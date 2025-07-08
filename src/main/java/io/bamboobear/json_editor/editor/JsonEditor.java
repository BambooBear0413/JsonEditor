package io.bamboobear.json_editor.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.bamboobear.json_editor.ErrorReport;
import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.ResourceImageLoader;
import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.json.JsonBooleanComponent;
import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonCompositeComponent;
import io.bamboobear.json_editor.component.json.JsonNumberComponent;
import io.bamboobear.json_editor.component.json.JsonObjectComponent;
import io.bamboobear.json_editor.component.json.JsonPrimitiveComponent;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.util.OptionPaneDialogUtilities;

@SuppressWarnings("serial")
public class JsonEditor extends JPanel{
	private JsonFile file;
	
	private JScrollPane body;
	
	private final ChangesRecord changesRecord;
	
	private long lastModified;

	private final Button undoButton;
	private final Button redoButton;

	private static final int ICON_SIZE = 16;
	private static final int HINTS = Image.SCALE_SMOOTH;
	
	private static final ImageIcon UNDO_ICON = ResourceImageLoader.getImageIcon("undo.png", ICON_SIZE, ICON_SIZE, HINTS);
	private static final ImageIcon REDO_ICON = ResourceImageLoader.getImageIcon("redo.png", ICON_SIZE, ICON_SIZE, HINTS);
	
	private static final Executor LOAD_FILE_THREAD = Executors.newSingleThreadExecutor();

	public JsonEditor() {
		super(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		undoButton = new Button(UNDO_ICON);
		undoButton.addActionListener(e -> undo());
		toolBar.add(undoButton);
		
		redoButton = new Button(REDO_ICON);
		redoButton.addActionListener(e -> redo());
		toolBar.add(redoButton);

		add(toolBar, BorderLayout.NORTH);

		changesRecord = new ChangesRecord();

		body = new JScrollPane();
		body.getVerticalScrollBar().setUnitIncrement(10);
		add(body, BorderLayout.CENTER);
	}

	public void openFile() {
		if(!canBeClosed()) return;
		
		File selectedFile = chooseFileToBeOpened();
		if(selectedFile == null || !selectedFile.exists()) return;
		
		LOAD_FILE_THREAD.execute(() -> load(new JsonFile(selectedFile)));
	}
	
	public void newFile() {
		if(!canBeClosed()) return;
		
		load(null);
	}
	
	private void load(JsonFile file) {
		Main.getMainWindow().setTitle(TranslatableText.create("json_editor.loading"));

		JsonCompositeComponent<?> root = null;

		if(file != null) {
			try {
				JsonElement json = file.load();
				if(json.isJsonArray() || json.isJsonObject()) {
					root = (JsonCompositeComponent<?>)JsonComponent.createDefaultJsonComponent(json);
				} else {
					OptionPaneDialogUtilities.showWarningMessageDialog("json_editor.error.invalid_root_element");
				}
			} catch(JsonIOException e) {
				if(e.getCause() instanceof IOException ioException) ioExceptionDuringLoading(ioException, file);
			} catch(JsonSyntaxException e) {
				OptionPaneDialogUtilities.showErrorMessageDialog("json_editor.error.json_syntax_error", file, e.getMessage());
			} catch(IOException e) {
				ioExceptionDuringLoading(e, file);
			} catch(OutOfMemoryError e) {
				OptionPaneDialogUtilities.showErrorMessageDialog("json_editor.error.file_too_large", file);
			} catch(Exception e) {
				ErrorReport.output(e);
			}
		} else {
			root = (JsonCompositeComponent<?>)JsonComponent.createDefaultJsonComponent(JsonObjectComponent.TYPE_ID);
		}

		if(root == null) { // root is null if there is any error occurs during loading
			updateTitle();
			return;
		}

		setRootComponent(file, root);
		body.revalidate();
		body.repaint();
		removeAllChange();
		
		root.requestFocus();
		
		updateTitle();
	}

	private void ioExceptionDuringLoading(IOException e, JsonFile file) {
		OptionPaneDialogUtilities.showErrorMessageDialog("json_editor.error.io_exception_while_loading", file, e);
	}
	
	private void updateTitle() {
		if(this.file != null) {
			Main.getMainWindow().setTitle(TranslatableText.literal(file.getFilePath()));
		} else {
			Main.getMainWindow().removeTitle();
		}
	}
	
	public boolean saveFile()      { return save(false); }
	public boolean saveAsNewFile() { return save(true);  }
	
	private boolean save(boolean saveAsNewFile) {
		JsonElement root = getRootComponent().getJsonElement();
		
		try {
			if(this.file == null || saveAsNewFile) {
				File f = chooseFileToBeSaved();
				if(f == null) return false;
				
				this.file = new JsonFile(f);
				updateTitle();
			}
			
			file.save(root);
			changesRecord.save();
			lastModified = file.lastModified();
			return true;
		} catch (Exception e) {
			OptionPaneDialogUtilities.showErrorMessageDialog("json_editor.error.failed_to_save_file", file);
			ErrorReport.output(e);
			return false;
		}
	}
	
	public File chooseFileToBeOpened() { return chooseFile(JFileChooser::showOpenDialog); }
	public File chooseFileToBeSaved()  { return chooseFile(JFileChooser::showSaveDialog); }
	
	private File chooseFile(BiFunction<JFileChooser, Component, Integer> func) {
		JFileChooser chooser = new JFileChooser();
		FileFilter jsonFileFilter = new FileNameExtensionFilter("JSON File", "json");
		
		if(this.file != null) {
			File file = this.file.file;
			chooser.setCurrentDirectory(file.getParentFile());
		} else {
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		}
		chooser.setFileFilter(jsonFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int result = func.apply(chooser, Main.getMainWindow());
		
		if (result != JFileChooser.APPROVE_OPTION) return null;
		
		File selectedFile = chooser.getSelectedFile();
		if(chooser.getFileFilter() == jsonFileFilter && !selectedFile.getName().endsWith(".json")) {
			return new File(selectedFile.getPath() + ".json");
		}
		return selectedFile;
	}
	
	public boolean canBeClosed() {
		if(hasSaved()) return true;
		
		int result = JOptionPane.showConfirmDialog(Main.getMainWindow(),
				((this.file == null) ? TranslatableText.create("json_editor.save_file.no_file") : TranslatableText.create("json_editor.save_file.file", file.getFilePath())).getDisplayText(), 
				TranslatableText.create("json_editor.save_file.title").getDisplayText(), JOptionPane.YES_NO_CANCEL_OPTION);
		return switch(result) {
		case JOptionPane.CANCEL_OPTION -> false;
		case JOptionPane.YES_OPTION -> saveFile();
		default -> true;
		};
	}
	
	private boolean hasSaved() {
		boolean hasSaved = changesRecord.hasUnsavedChanges();
		boolean lastModified = (file == null || this.lastModified == file.lastModified());
		return (hasSaved && lastModified);
	}

	private void setRootComponent(JsonFile file, JsonCompositeComponent<?> root) {
		if(root == getRootComponent()) return;

		if(file != this.file) {
			this.file = file;
			this.lastModified = (file == null) ? 0 : file.lastModified();
		}

		body.setViewportView(Objects.requireNonNull(root, "root component is null"));
	}
	
	public JsonCompositeComponent<?> getRootComponent() {
		Component c = body.getViewport().getView();
		if(c == null) return null;
		if(c instanceof JsonCompositeComponent<?> jcc) return jcc;
		
		throw new IllegalStateException();
	}
	
	public void undo() { changesRecord.undo(); }
	public void redo() { changesRecord.redo(); }
	
	public boolean canDoUndo() { return changesRecord.canDoUndo(); }
	public boolean canDoRedo() { return changesRecord.canDoRedo(); }
	
	public void removeAllChange() { changesRecord.removeAllRecord(); }
	
	public void addKeyFieldChange(JsonComponent<?> json, String before, String after) {
		changesRecord.addChange(ChangeType.KEY_FIELD_CHANGE, json, before, after);
	}
	
	public void addValueFieldChange(JsonPrimitiveComponent<?> json, String before, String after) {
		changesRecord.addChange(ChangeType.VALUE_FIELD_CHANGE, json, before, after);
	}
	
	public void addAddElementChange(JsonComponent<?> json, JsonCompositeComponent<?> parent, int index) {
		changesRecord.addChange(ChangeType.ADD_ELEMENT_CHANGE, json, parent, index);
	}
	
	public void addRemoveElementChange(JsonComponent<?> json, JsonCompositeComponent<?> parent, int index) {
		changesRecord.addChange(ChangeType.REMOVE_ELEMENT_CHANGE, json, parent, index);
	}
	
	private class ChangesRecord {
		private ArrayList<Change> undoChanges = new ArrayList<>();
		private ArrayList<Change> redoChanges = new ArrayList<>();
		
		private Change savingPoint;
		
		private static final int LIMIT = 50;
		
		private ChangesRecord() {
			updateButtons();
		}
		
		private void save() {
			savingPoint = (undoChanges.isEmpty()) ? null : undoChanges.getLast();
		}
		
		public boolean hasUnsavedChanges() {
			return (savingPoint == null) ? undoChanges.isEmpty() : (savingPoint == undoChanges.getLast());
		}
		
		public synchronized void addChange(ChangeType type, Object...args) throws IllegalArgumentException {
			Change cr = checkArguments(type, args);
			
			if(undoChanges.size() == LIMIT) undoChanges.removeFirst();
			
			undoChanges.add(cr);
			redoChanges.clear();
			updateButtons();
		}
		
		public synchronized void undo() {
			if(canDoUndo()) {
				undoChanges.getLast().undo();
				redoChanges.add(undoChanges.removeLast());
			}
			updateButtons();
		}
		
		public synchronized void redo() {
			if(canDoRedo()) {
				redoChanges.getLast().redo();
				undoChanges.add(redoChanges.removeLast());
			}
			updateButtons();
		}
		
		private boolean canDoUndo() { return !undoChanges.isEmpty(); }
		private boolean canDoRedo() { return !redoChanges.isEmpty(); }
		
		public void removeAllRecord() {
			undoChanges.clear();
			redoChanges.clear();
			savingPoint = null;
			updateButtons();
		}
		
		private void updateButtons() {
			undoButton.setEnabled(canDoUndo());
			redoButton.setEnabled(canDoRedo());
		}
		
		private Change checkArguments(ChangeType type, Object[] args) throws IllegalArgumentException {
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(args, "args is null");
			
			Class<?>[] argumentTypes = type.argumentTypes;
			
			if(args.length != argumentTypes.length) throw new IllegalArgumentException(type.name() + " accepts " + argumentTypes.length + " argument(s), but there are " + args.length + " argument(s)");
			
			for(int i = 0; i < args.length; i++) {
				Object o = args[i];
				Class<?> argumentType = argumentTypes[i];
				if(o == null) throw new IllegalArgumentException(String.format("args[%d] is null", i));
				if(!argumentType.isInstance(o)) throw new IllegalArgumentException(String.format("args[%d] is not an instance of %s", i, argumentType.getCanonicalName()));
			}
			
			return new Change(type, args);
		}
	}
	
	public record Change(ChangeType type, Object[] args) {
		private void undo() { type.undo(args); }
		private void redo() { type.redo(args); }
	}
	
	private enum ChangeType {
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain JsonComponent} - the JsonComponent whose corresponding key has changed</li>
		 * <li>{@linkplain String} - the key before the change</li>
		 * <li>{@linkplain String} - the key after the change</li>
		 * </ol>
		 * */
		KEY_FIELD_CHANGE((args) -> {
			JsonComponent<?> json = (JsonComponent<?>)args[0];
			json.setKey(args[1].toString());
		}, (args) -> {
			JsonComponent<?> json = (JsonComponent<?>)args[0];
			json.setKey(args[2].toString());
		}, JsonComponent.class, String.class, String.class),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain JsonPrimitiveComponent} - the JsonPrimitiveComponent whose value has changed</li>
		 * <li>{@linkplain String} - the value before the change</li>
		 * <li>{@linkplain String} - the value after the change</li>
		 * </ol>
		 * */
		VALUE_FIELD_CHANGE((args) -> {
			JsonPrimitiveComponent<?> jpc = (JsonPrimitiveComponent<?>)args[0];
			doValueChange(jpc, args[1].toString());
		}, (args) -> {
			JsonPrimitiveComponent<?> jpc = (JsonPrimitiveComponent<?>)args[0];
			doValueChange(jpc, args[2].toString());
		}, JsonPrimitiveComponent.class, String.class, String.class),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain JsonComponent} - the JsonComponent which was added</li>
		 * <li>{@linkplain JsonCompositeComponent} - the JsonCompositeComponent which is the parent of JsonComponent</li>
		 * <li>{@linkplain Integer} - the index</li>
		 * </ol>*/
		ADD_ELEMENT_CHANGE(
				ChangeType::doRemove,
				ChangeType::doAdd,
				JsonComponent.class, JsonCompositeComponent.class, Integer.class
		),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain JsonComponent} - the JsonComponent which was removed.</li>
		 * <li>{@linkplain JsonCompositeComponent} - the JsonCompositeComponent which is the parent of JsonComponent before the change</li>
		 * <li>{@linkplain Integer} - the index</li>
		 * </ol>*/
		REMOVE_ELEMENT_CHANGE(
				ChangeType::doAdd,
				ChangeType::doRemove,
				JsonComponent.class, JsonCompositeComponent.class, Integer.class
		),
		
		//TODO ELEMENT_POSITION_CHANGE
		
		//TODO ELEMENT_REPLACEMENT_CHANGE
		
		;
		
		private final Consumer<Object[]> undo;
		private final Consumer<Object[]> redo;
		private final Class<?>[] argumentTypes;
		
		ChangeType(Consumer<Object[]> undo, Consumer<Object[]> redo, Class<?>...argumentTypes) {
			this.undo = Objects.requireNonNull(undo);
			this.redo = Objects.requireNonNull(redo);
			this.argumentTypes = Objects.requireNonNull(argumentTypes);
		}
		
		public void undo(Object[] args) { undo.accept(args); }
		public void redo(Object[] args) { redo.accept(args); }
		
		private static void doRemove(Object[] args) {
			JsonCompositeComponent<?> parent = (JsonCompositeComponent<?>)args[1];
			parent.removeElement((JsonComponent<?>)args[0]);
			parent.refresh();
		}
		
		private static void doAdd(Object[] args) {
			JsonCompositeComponent<?> parent = (JsonCompositeComponent<?>)args[1];
			JsonComponent<?> c = (JsonComponent<?>)args[0];
			int index = (int)args[2];
			
			parent.addElement(c, index);
			c.requestFocus();
			parent.refresh();
		}
		
		private static void doValueChange(JsonPrimitiveComponent<?> json, String value) {
			switch(json) {
			case JsonBooleanComponent jbc -> jbc.setValue(Boolean.valueOf(value));
			case JsonNumberComponent  jnc -> jnc.setValueFromString(value);
			
			default -> json.setValue(new JsonPrimitive(value));
			}
		}
	}
}

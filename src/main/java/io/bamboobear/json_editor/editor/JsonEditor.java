package io.bamboobear.json_editor.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
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

import io.bamboobear.json_editor.ErrorReport;
import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.ResourceImageLoader;
import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorComboBox;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonCompositeComponent;
import io.bamboobear.json_editor.component.json.JsonObjectComponent;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.util.OptionPaneDialogUtilities;

@SuppressWarnings("serial")
public class JsonEditor extends JPanel{
	private JsonFile file;
	
	private static JToolBar toolBar;
	private static Button undoButton;
	private static Button redoButton;
	
	private JScrollPane body;
	
	private final ChangesRecord changesRecord;
	
	private long lastModified;
	
	private static final int ICON_SIZE = 16;
	private static final int HINTS = Image.SCALE_SMOOTH;
	
	private static final ImageIcon undoIcon = ResourceImageLoader.getImageIcon("undo.png", ICON_SIZE, ICON_SIZE, HINTS);
	private static final ImageIcon redoIcon = ResourceImageLoader.getImageIcon("redo.png", ICON_SIZE, ICON_SIZE, HINTS);
	
	static {
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		undoButton = new Button(undoIcon);
		undoButton.addActionListener(e -> Main.getEditor().undo());
		toolBar.add(undoButton);
		
		redoButton = new Button(redoIcon);
		redoButton.addActionListener(e -> Main.getEditor().redo());
		toolBar.add(redoButton);
	}
	
	public JsonEditor() {
		super(new BorderLayout());
		
		JsonComponent<?> rootComponent;
		rootComponent = JsonComponent.createDefaultJsonComponent(JsonObjectComponent.TYPE_ID);
		
		add(toolBar, BorderLayout.NORTH);
		
		changesRecord = new ChangesRecord();

		body = new JScrollPane();
		body.getVerticalScrollBar().setUnitIncrement(10);
		add(body, BorderLayout.CENTER);
		body.setViewportView(rootComponent);
	}
	
	public void load() {
		load(null);
	}
	
	public void openFile() {
		if(!close()) {
			return;
		}
		File selectedFile = chooseFileToBeOpened();
		if(selectedFile == null || !selectedFile.exists()) {
			return;
		}
		Thread loadFile = new Thread(() -> load(new JsonFile(selectedFile)));
		loadFile.start();
	}
	
	public void newFile() {
		if(!close()) {
			return;
		}
		load();
	}
	
	private void load(JsonFile file) {
		Main.getMainWindow().setTitle(TranslatableText.of("json_editor.loading"));
		
		this.file = file;
		this.lastModified = (this.file == null) ? 0 : this.file.lastModified();
		
		if(file != null) {
			try {
				JsonElement root = this.file.load();
				if(root.isJsonArray() || root.isJsonObject()) {
					body.setViewportView(JsonComponent.createDefaultJsonComponent(root));
				} else {
					OptionPaneDialogUtilities.showErrorMessageDialog(TranslatableText.of("json_editor.error.invalid_root_element"), TranslatableText.of("json_editor.error.invalid_root_element.title"));
					body.setViewportView(JsonComponent.createDefaultJsonComponent(JsonObjectComponent.TYPE_ID));
					this.file = null;
					this.lastModified = 0;
				}
			} catch (Exception e) {
			}
		} else {
			body.setViewportView(JsonComponent.createDefaultJsonComponent(JsonObjectComponent.TYPE_ID));
		}
		
		JsonCompositeComponent<?> root = getRootComponent();
		root.updateGUI();
		body.revalidate();
		body.repaint();
		removeAllChange();
		
		root.requestFocus();
		
		updateTitle();
	}
	
	private void updateTitle() {
		if(this.file != null) {
			Main.getMainWindow().setTitle(TranslatableText.literal(file.getFilePath()));
		} else {
			Main.getMainWindow().removeTitle();
		}
	}
	
	public boolean saveFile() {
		return save(false);
	}
	
	public boolean saveAsNewFile() {
		return save(true);
	}
	
	private boolean save(boolean saveAsNewFile) {
		boolean result = false;
		
		JsonElement root = getRootComponent().getAsJsonElement();
		
		try {
			if(this.file == null || saveAsNewFile) {
				File f = chooseFileToBeSaved();
				if(f == null) {
					return result;
				}
				this.file = new JsonFile(f);
				updateTitle();
			}
			
			file.save(root);
			changesRecord.save();
			lastModified = file.lastModified();
			result = true;
		} catch (Exception e) {
			OptionPaneDialogUtilities.showErrorMessageDialog(TranslatableText.of("json_editor.error.failed_to_save_file", file.getFilePath()), TranslatableText.of("json_editor.error.failed_to_save_file.title"));
			ErrorReport.output(e);
		}
		
		return result;
	}
	
	public File chooseFileToBeOpened() {
		return chooseFile(JFileChooser::showOpenDialog);
	}
	
	public File chooseFileToBeSaved() {
		return chooseFile(JFileChooser::showSaveDialog);
	}
	
	private File chooseFile(BiFunction<JFileChooser, Component, Integer> func) {
		JFileChooser chooser = new JFileChooser();
		FileFilter jsonFileFilter = new FileNameExtensionFilter("JSON File", "json");
		
		if(this.file != null) {
			File file = new File(this.file.getFilePath());
			chooser.setCurrentDirectory(file.getParentFile());
		} else {
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		}
		chooser.setFileFilter(jsonFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int result = func.apply(chooser, Main.getMainWindow());
		
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = chooser.getSelectedFile();
		if(chooser.getFileFilter() == jsonFileFilter && !selectedFile.getName().endsWith(".json")) {
			return new File(selectedFile.getPath() + ".json");
		}
		return selectedFile;
	}
	
	public boolean close() {
		if(hasSaved()) {
			return true;
		}
		
		int result = JOptionPane.showConfirmDialog(Main.getMainWindow(), ((this.file == null) ? TranslatableText.of("json_editor.save_file.no_file") : TranslatableText.of("json_editor.save_file.file", file.getFilePath())).getDisplayText()
				, TranslatableText.of("json_editor.save_file.title").getDisplayText(), JOptionPane.YES_NO_CANCEL_OPTION);
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
	
	public JsonCompositeComponent<?> getRootComponent() {
		Component c = body.getViewport().getView();
		if(c instanceof JsonCompositeComponent<?> jcc) {
			return jcc;
		}
		throw new IllegalStateException();
	}
	
	public void undo() {
		changesRecord.undo();
	}
	
	public void redo() {
		changesRecord.redo();
	}
	
	public boolean canDoUndo() {
		return changesRecord.canDoUndo();
	}
	
	public boolean canDoRedo() {
		return changesRecord.canDoRedo();
	}
	
	public void removeAllChange() {
		changesRecord.removeAllRecord();
	}
	
	public void addTextFieldValueChange(EditorTextField textField, String before, String after) {
		changesRecord.addChange(ChangeType.TEXT_FIELD_VALUE_CHANGE, textField, before, after);
	}
	
	public void addComboBoxValueChange(EditorComboBox comboBox, String before, String after) {
		changesRecord.addChange(ChangeType.COMBO_BOX_VALUE_CHANGE, comboBox, before, after);
	}
	
	public void addAddElementChange(EditorTextField keyField, JsonComponent<?> json, JsonCompositeComponent<?> parent, int index) {
		changesRecord.addChange(ChangeType.ADD_ELEMENT_CHANGE, keyField, json, parent, index);
	}
	
	public void addRemoveElementChange(EditorTextField keyField, JsonComponent<?> json, JsonCompositeComponent<?> parent, int index) {
		changesRecord.addChange(ChangeType.REMOVE_ELEMENT_CHANGE, keyField, json, parent, index);
	}
	
	private class ChangesRecord {
		private ArrayList<Change> undoChanges = new ArrayList<Change>();
		private ArrayList<Change> redoChanges = new ArrayList<Change>();
		
		private Change savingPoint;
		
		private static final int LIMIT = 50;
		
		private ChangesRecord() {
			updateButtons();
		}
		
		private void save() {
			if(!undoChanges.isEmpty()) {
				savingPoint = undoChanges.getLast();
			} else {
				savingPoint = null;
			}
		}
		
		public boolean hasUnsavedChanges() {
			if(savingPoint == null) {
				return undoChanges.isEmpty();
			}
			return savingPoint == undoChanges.getLast();
		}
		
		public synchronized void addChange(ChangeType type, Object...args) throws IllegalArgumentException {
			Change cr = checkArguments(type, args);
			if(undoChanges.size() == LIMIT) {
				undoChanges.removeFirst();
			}
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
		
		private boolean canDoUndo() {
			return !undoChanges.isEmpty();
		}
		
		private boolean canDoRedo() {
			return !redoChanges.isEmpty();
		}
		
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
			
			if(args.length != argumentTypes.length) {
				throw new IllegalArgumentException("the of argument count does not match the argument count required for " + type.name() + ".");
			}
			
			for(int i = 0; i < args.length; i++) {
				Object o = args[i];
				Class<?> argumentType = argumentTypes[i];
				if(o == null) {
					throw new IllegalArgumentException(new NullPointerException(String.format("args[%d] is null", i)));
				}
				if(!argumentType.isInstance(o)) {
					throw new IllegalArgumentException(String.format("args[%d] is not an instance of %s", 
							i, argumentType.getCanonicalName()));
				}
			}
			
			return new Change(type, args);
		}
	}
	
	public static record Change(ChangeType type, Object[] args) {
		private void undo() {
			type.undo(args);
		}
		
		private void redo() {
			type.redo(args);
		}
	}
	
	private static enum ChangeType {
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain EditorTextField} - the EditorTextField whose value has changed</li>
		 * <li>{@linkplain String} - the value before the change</li>
		 * <li>{@linkplain String} - the value after the change</li>
		 * </ol>
		 * */
		TEXT_FIELD_VALUE_CHANGE((args) -> {
			EditorTextField textField = (EditorTextField)args[0];
			String text = args[1].toString();
			doTextChange(textField, text);
		}, (args) -> {
			EditorTextField textField = (EditorTextField)args[0];
			String text = args[2].toString();
			doTextChange(textField, text);
		}, EditorTextField.class, String.class, String.class),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain EditorComboBox} - the EditorComboBox whose value has changed</li>
		 * <li>{@linkplain String} - the value before the change</li>
		 * <li>{@linkplain String} - the value after the change</li>
		 * </ol>*/
		COMBO_BOX_VALUE_CHANGE((args) -> {
			EditorComboBox comboBox = (EditorComboBox)args[0];
			comboBox.setValue(args[1].toString());
			comboBox.requestFocus();
		}, (args) -> {
			EditorComboBox comboBox = (EditorComboBox)args[0];
			comboBox.setValue(args[2].toString());
			comboBox.requestFocus();
		}, EditorComboBox.class, String.class, String.class),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain EditorTextField} - the EditorTextField.</li>
		 * <li>{@linkplain JsonComponent} - the JsonComponent which was added</li>
		 * <li>{@linkplain JsonCompositeComponent} - the JsonCompositeComponent which is the parent of JsonComponent</li>
		 * <li>{@linkplain Integer} - the index</li>
		 * </ol>*/
		ADD_ELEMENT_CHANGE((args) -> {
			doRemove(args);
		}, (args) -> {
			doAdd(args);
		}, EditorTextField.class, JsonComponent.class, JsonCompositeComponent.class, Integer.class),
		
		/**
		 * arguments:
		 * <ol start=0>
		 * <li>{@linkplain EditorTextField} - the EditorTextField.</li>
		 * <li>{@linkplain JsonComponent} - the JsonComponent which was removed.</li>
		 * <li>{@linkplain JsonCompositeComponent} - the JsonCompositeComponent which is the parent of JsonComponent before the change</li>
		 * <li>{@linkplain Integer} - the index</li>
		 * </ol>*/
		REMOVE_ELEMENT_CHANGE((args) -> {
			doAdd(args);
		}, (args) -> {
			doRemove(args);
		}, EditorTextField.class, JsonComponent.class, JsonCompositeComponent.class, Integer.class),
		
		//TODO ELEMENT_POSITION_CHANGE
		
		//TODO ELEMENT_REPLACEMENT_CHANGE
		
		;
		
		private final Consumer<Object[]> undo;
		private final Consumer<Object[]> redo;
		private final Class<?>[] argumentTypes;
		
		private ChangeType(Consumer<Object[]> undo, Consumer<Object[]> redo, Class<?>...argumentTypes) {
			this.undo = Objects.requireNonNull(undo);
			this.redo = Objects.requireNonNull(redo);
			this.argumentTypes = Objects.requireNonNull(argumentTypes);
		}
		
		public void undo(Object[] args) {
			undo.accept(args);
		}
		
		public void redo(Object[] args) {
			redo.accept(args);
		}
		
		private static void doRemove(Object[] args) {
			JsonCompositeComponent<?> parent = (JsonCompositeComponent<?>)args[2];
			parent.removeElement((JsonComponent<?>)args[1]);
			parent.getRootElement().refresh();
		}
		
		private static void doAdd(Object[] args) {
			EditorTextField keyField = (EditorTextField)args[0];
			JsonCompositeComponent<?> parent = (JsonCompositeComponent<?>)args[2];
			JsonComponent<?> c = (JsonComponent<?>)args[1];
			int index = (int)args[3];
			
			parent.addElement(keyField, c, index);
			c.requestFocus();
			parent.getRootElement().refresh();
		}
		
		private static void doTextChange(EditorTextField textField, String text) {
			textField.setText(text);
			textField.requestFocus();
			switch(textField.getType()) {
			case KEY -> {
				JsonCompositeComponent<?> parent = textField.getJsonComponent().getParentElement();
				if(parent != null) {
					parent.repaint();
				}
			}
			case VALUE -> {
				textField.repaint();
			}
			}
		}
	}
}

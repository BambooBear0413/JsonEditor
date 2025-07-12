package io.bamboobear.json_editor.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

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
import com.google.gson.JsonSyntaxException;
import io.bamboobear.json_editor.ErrorReport;
import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.ResourceImageLoader;
import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonCompositeComponent;
import io.bamboobear.json_editor.component.json.JsonObjectComponent;
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
					OptionPaneDialogUtilities.showWarningMessageDialog("json_editor.warning.invalid_root_element");
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
	
	public void undo() {
		beforeUndoRedo();
		changesRecord.undo();
	}

	public void redo() {
		beforeUndoRedo();
		changesRecord.redo();
	}

	private void beforeUndoRedo() {
		var c = Main.getMainWindow().getFocusOwner();
		if(c instanceof EditorInputField field) field.onUndoRedoRequested();
	}
	
	public boolean canDoUndo() { return changesRecord.canDoUndo(); }
	public boolean canDoRedo() { return changesRecord.canDoRedo(); }
	
	public void removeAllChange() { changesRecord.removeAllRecord(); }

	public void addChange(Change change) { changesRecord.addChange(change); }

	private class ChangesRecord {
		private Change currentPoint;
		private Change firstChange;
		private int undoableCount;

		private Change savingPoint;
		
		private static final int LIMIT = 50;
		
		private ChangesRecord() {
			updateButtons();
		}
		
		private void save() { savingPoint = currentPoint; }
		
		public boolean hasUnsavedChanges() { return savingPoint == currentPoint; }
		
		public synchronized void addChange(Change change) {
			Objects.requireNonNull(change, "change is null");

			if (undoableCount == LIMIT) {
				Change next = firstChange.next;
				next.previous = null;
				firstChange = next;
				undoableCount--;
			}

			if(currentPoint != null) {
				currentPoint.next = change;
				change.previous = currentPoint;
			} else { // there is no change that can undo
				firstChange = change;
			}
			currentPoint = change;
			undoableCount++;

			updateButtons();
		}
		
		public synchronized void undo() {
			if(canDoUndo()) {
				currentPoint.undoChanges();
				currentPoint = currentPoint.previous;
				undoableCount--;
			}
			updateButtons();
		}
		
		public synchronized void redo() {
			if(canDoRedo()) {
				currentPoint = (currentPoint != null) ? currentPoint.next : firstChange;
				currentPoint.redoChanges();
				undoableCount++;
			}
			updateButtons();
		}
		
		private boolean canDoUndo() { return currentPoint != null; }
		private boolean canDoRedo() { return (currentPoint != null) ? (currentPoint.next != null) : (firstChange != null); }
		
		public void removeAllRecord() {
			currentPoint = null;
			savingPoint = null;
			firstChange = null;
			undoableCount = 0;
			updateButtons();
		}
		
		private void updateButtons() {
			undoButton.setEnabled(canDoUndo());
			redoButton.setEnabled(canDoRedo());
		}
	}
}

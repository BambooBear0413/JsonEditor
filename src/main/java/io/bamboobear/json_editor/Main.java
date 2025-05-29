package io.bamboobear.json_editor;

import java.awt.AWTEvent;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.bamboobear.json_editor.component.AboutDialog;
import io.bamboobear.json_editor.component.Frame;
import io.bamboobear.json_editor.component.Menu;
import io.bamboobear.json_editor.component.MenuItem;
import io.bamboobear.json_editor.component.PluginListDialog;
import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.editor.JsonEditor;
import io.bamboobear.json_editor.lang.Language;
import io.bamboobear.json_editor.lang.Languages;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.plugin.LookAndFeelLoader;
import io.bamboobear.json_editor.plugin.Plugin;
import io.bamboobear.json_editor.plugin.PluginLoader;
import io.bamboobear.json_editor.plugin.Plugins;
import io.bamboobear.json_editor.settings.Settings;

public class Main{
	public static final TranslatableText NAME = TranslatableText.create("json_editor.name");
	public static final String VERSION = "0.1.0";
	
	private static boolean isExperimentalFeaturesEnabled;
	private static HashSet<Font> fontCache = new HashSet<Font>();
	private static LookAndFeelInfo defaultLookAndFellInfo;
	
	private static MainWindow mainWindow;
	private static JsonEditor editor;
	
	private Main() {}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { ErrorReport.output(e); }
		
		defaultLookAndFellInfo = getDefaultAndFeelInfo(UIManager.getLookAndFeel());
		
		ErrorReport.init();
		
		Thread.setDefaultUncaughtExceptionHandler(ErrorReport::output);
		
		LoadingDialog dialog = new LoadingDialog();
		
		load(Languages::loadLanguage, null, dialog, "Loading vanilla language files...");
		load(ResourceImageLoader::loadImages, dialog, "Loading resource images...");
		load(PluginLoader::loadPlugins, dialog, "Loading plugins...");
		for(Plugin plugin : Plugins.getPlugins()) {
			load(Languages::loadLanguage, plugin, dialog, "Loading language files in plugin \"%s\"...".formatted(plugin.id()));
			load(Plugin::loadLookAndFeels, plugin, dialog, "Loading looks and feels in plugin \"%s\"...".formatted(plugin.id()));
		}
		load(Settings::loadSettings, dialog, "Loading settings...");
		
		isExperimentalFeaturesEnabled = Settings.EXPERIMENTAL_FEATURES.getValue();
		
		setLAF: try {
			LookAndFeelInfo info = Settings.LOOK_AND_FEEL.getValue();
			String className = info.getClassName();
			if(className.equals(defaultLookAndFellInfo.getClassName())) break setLAF;
			Main.setLookAndFeel(className);
		} catch (Exception e) { ErrorReport.output(e); }
		
		SwingUtilities.invokeLater(() -> {
			mainWindow = new MainWindow();
			
			setEditor(new JsonEditor());
			
			mainWindow.setVisible(true);
			
			dialog.dispose();
		});
	}
	
	private static void load(Runnable action, LoadingDialog dialog, String loadingText) {
		dialog.setText(loadingText);
		action.run();
	}
	
	private static <T> void load(Consumer<T> action, T arg, LoadingDialog dialog, String loadingText) {
		dialog.setText(loadingText);
		action.accept(arg);
	}
	
	public synchronized static void setEditor(JsonEditor newEditor) {
		Objects.requireNonNull(newEditor, "newEditor is null");
		
		if (editor != null) {
			if (!editor.close()) return;
			
			mainWindow.remove(editor);
		}
		
		editor = newEditor;
		mainWindow.add(editor);
		editor.load();
		mainWindow.revalidate();
		mainWindow.repaint();
	}
	
	public static JsonEditor getEditor() {
		if(editor == null) throw new IllegalStateException("the editor is null");
		return editor;
	}
	
	public static MainWindow getMainWindow() { return mainWindow; }
	
	public static Language getLanguage() { return Settings.LANGUAGE.getValue(); }
	
	public static boolean isExperimentalFeaturesEnabled() { return isExperimentalFeaturesEnabled; }
	
	public static Font getFont() { return Settings.FONT.getValue(); }
	
	public static Font getFont(String fontFamily, int style, int size) {
		for(Font f : fontCache) {
			if(f.getFamily().equals(fontFamily) && f.getStyle() == style && f.getSize() == size) {
				return f;
			}
		}
		
		Font f = new Font(fontFamily, style, size);
		fontCache.add(f);
		return f;
	}

	public static JsonReader createJsonReader(Reader reader) {
		JsonReader jr = new JsonReader(reader);
		
		return jr;
	}
	
	public static JsonWriter createJsonWriter(Writer writer) throws IOException{
		JsonWriter jw = new JsonWriter(writer);
		
		if(Settings.PRETTY_PRINTING.getValue()) {
			jw.setIndent("    ");;
		}
		if(!Settings.HTML_ESCAPING.getValue()) {
			jw.setHtmlSafe(false);
		}
		
		return jw;
	}
	
	private static LookAndFeelInfo getDefaultAndFeelInfo(LookAndFeel laf) {
		String className = laf.getClass().getCanonicalName();
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo info : infos) {
			if(info.getClassName().equals(className)) return info;
		}
		throw new IllegalStateException("Unknown Look & Feel: " + className);
	}
	
	public static LookAndFeelInfo getDefaultLookAndFeelInfo() { return defaultLookAndFellInfo; }
	
	public static boolean setLookAndFeel(String className) {
		Class<?> clazz = LookAndFeelLoader.getLookAndFeelClass(className);
		if(clazz == null) {
			try {
				UIManager.setLookAndFeel(className);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		try {
			LookAndFeel laf = (LookAndFeel) clazz.getConstructor().newInstance();
			UIManager.setLookAndFeel(laf);

			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			if(defaults.get("ClassLoader") == null) defaults.put("ClassLoader", clazz.getClassLoader());
			
			return true;
		} catch (Throwable e) { // TODO show warning dialog
			e.printStackTrace();
			return false;
		}
	}
	
	public static void browse(String uri) {
		try {
			browse(new URI(uri));
		} catch (URISyntaxException e) {
			ErrorReport.output(e);
		}
	}
	
	public static void browse(URI uri) {
		int result = JOptionPane.showConfirmDialog(mainWindow, TranslatableText.create("json_editor.warning.browse", uri).getDisplayText(),
				TranslatableText.create("json_editor.warning.browse.title").getDisplayText(), JOptionPane.YES_NO_OPTION);
		
		if(result != JOptionPane.YES_OPTION) return;
		
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			ErrorReport.output(e);
		}
	}
	
	public static void open(String filePath) { open(new File(filePath)); }
	
	public static void open(File file) {
		int result = JOptionPane.showConfirmDialog(mainWindow, TranslatableText.create("json_editor.warning.open_file", file.getAbsolutePath()).getDisplayText(),
				TranslatableText.create("json_editor.warning.open_file.title").getDisplayText(), JOptionPane.YES_NO_OPTION);
		
		if(result != JOptionPane.YES_OPTION) return;
		
		try {
			Desktop.getDesktop().open(file);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(mainWindow, TranslatableText.create("json_editor.warning.open_file.file_not_exist", file.getAbsolutePath()).getDisplayText(),
					TranslatableText.create("json_editor.warning.open_file.file_not_exist.title").getDisplayText(), JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			ErrorReport.output(e);
		}
	}
	
	@SuppressWarnings("serial")
	private static class LoadingDialog extends JDialog {
		private final JLabel label;
		
		private LoadingDialog() {
			super((JFrame)null, "Loading");
			setSize(new Dimension(300, 150));
			setLocationRelativeTo(null);
			label = new JLabel("", JLabel.LEFT);
			add(label);
			setVisible(true);
		}
		
		private void setText(String text) { label.setText(text); }
	}
	
	@SuppressWarnings("serial")
	public static class MainWindow extends Frame {
		private TranslatableText title;
		
		private final MenuBar menuBar;
		
		private MainWindow() {
			super.setTitle(NAME);
			setSize(new Dimension(960, 540));
			setMinimumSize(new Dimension(480, 270));
			setLocationRelativeTo(null);
			setIconImage(ResourceImageLoader.getIconImage("icon.png"));
			
			menuBar = new MenuBar();
			setJMenuBar(menuBar);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					boolean canWindowBeClosed = editor == null || editor.close();
					setDefaultCloseOperation(canWindowBeClosed ? EXIT_ON_CLOSE : DO_NOTHING_ON_CLOSE);
				}
			});
			
			getToolkit().addAWTEventListener(new AWTEventListener() {
				@Override
				public void eventDispatched(AWTEvent event) {
					if(event instanceof KeyEvent keyEvent) {
						handleKeyEvent(keyEvent);
					}
				}
				
				private void handleKeyEvent(KeyEvent event) {
					boolean hasEditor = editor != null;
					boolean focusOnMainWindow = Main.getMainWindow().getFocusOwner() != null;
					
					if(event.getID() == KeyEvent.KEY_PRESSED) {
						if(event.isControlDown() && event.isShiftDown()) {
							switch(event.getKeyCode()) {
							case KeyEvent.VK_S -> {
								if(hasEditor && focusOnMainWindow) editor.saveAsNewFile();
							}
							}
						} else if(event.isControlDown()) {
							switch(event.getKeyCode()) {
							case KeyEvent.VK_Z -> {
								if(hasEditor && focusOnMainWindow) editor.undo();
							}
							case KeyEvent.VK_Y -> {
								if(hasEditor && focusOnMainWindow) editor.redo();
							}
							case KeyEvent.VK_S -> {
								if(hasEditor && focusOnMainWindow) editor.saveFile();
							}
							case KeyEvent.VK_O -> {
								if(hasEditor && focusOnMainWindow) editor.openFile();
							}
							case KeyEvent.VK_N -> {
								if(hasEditor && focusOnMainWindow) editor.newFile();
							}
							}
						}
					}
				}
			}, AWTEvent.KEY_EVENT_MASK);
		}
		
		public void setTitle(TranslatableText title) {
			this.title = title;
			super.refreshTitle();
		}
		
		public void removeTitle() {
			this.title = TranslatableText.EMPTY;
			super.refreshTitle();
		}
		
		@Override
		public String getTitle() {
			String title = super.getTitle();
			return (this.title == null || this.title.isEmptyKey()) ? title : String.format("%2$s - %1$s", title, this.title.getDisplayText());
		}
	}
	
	@SuppressWarnings("serial")
	private static class MenuBar extends JMenuBar {
		private MenuBar() {
			add(createFileMenu());
			add(createEditMenu());
			add(createSettingsMenu());
			add(createAboutMenu());
		}
		
		private static MenuItem createMenuItem(TranslatableText displayName, ActionListener listener) {
			MenuItem item = new MenuItem(displayName);
			item.addActionListener(listener);
			return item;
		}
		
		private Menu createFileMenu() {
			Menu fileMenu = new Menu(TranslatableText.create("json_editor.menu.file"));
			
			fileMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.file.new_file"), e -> {if(editor != null) editor.newFile();}));
			fileMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.file.open_file"), e -> {if(editor != null) editor.openFile();}));
			fileMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.file.save_file"), e -> {if(editor != null) editor.saveFile();}));
			fileMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.file.save_as"), e -> {if(editor != null) editor.saveAsNewFile();}));
			
			return fileMenu;
		}
		
		private Menu createEditMenu() {
			Menu editMenu = new Menu(TranslatableText.create("json_editor.menu.edit"));
			
			var undoMenuItem = editMenu.add(createMenuItem(TranslatableText.create("json_editor.undo"), e -> {if(editor != null) editor.undo();}));
			var redoMenuItem = editMenu.add(createMenuItem(TranslatableText.create("json_editor.redo"), e -> {if(editor != null) editor.redo();}));
			
			editMenu.addMenuListener(new MenuListener() {
				@Override
				public void menuSelected(MenuEvent e) {
					boolean hasEditor = editor != null;
					undoMenuItem.setEnabled(hasEditor ? editor.canDoUndo() : false);
					redoMenuItem.setEnabled(hasEditor ? editor.canDoRedo() : false);
				}
				
				@Override
				public void menuDeselected(MenuEvent e) {}
				
				@Override
				public void menuCanceled(MenuEvent e) {}
			});
			
			return editMenu;
		}
		
		private Menu createSettingsMenu() {
			Menu settingsMenu = new Menu(TranslatableText.create("json_editor.menu.settings"));
			
			settingsMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.settings"), e -> SettingsDialog.showDialog()));
			
			return settingsMenu;
		}
		
		private Menu createAboutMenu() {
			Menu aboutMenu = new Menu(TranslatableText.create("json_editor.menu.about"));
			
			final String JSON_ORG = "https://www.json.org/json-en.html";
			aboutMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.about.editor"), e -> AboutDialog.showDialog()));
			aboutMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.about.json"), e -> browse(JSON_ORG)));
			aboutMenu.addSeparator();
			aboutMenu.add(createMenuItem(TranslatableText.create("json_editor.menu.about.plugin_list"), e -> PluginListDialog.showDialog()));
			
			return aboutMenu;
		}
	}
}

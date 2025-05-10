package io.bamboobear.json_editor.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.settings.Setting;
import io.bamboobear.json_editor.settings.Settings;

@SuppressWarnings("serial")
public class SettingsDialog extends Dialog{
	private static volatile SettingsDialog dialog;
	private Map<String, String> changeSettings = new HashMap<String, String>();
	private JScrollPane body;
	
	private SettingsDialog() {
		super(Main.getMainWindow(), TranslatableText.create("json_editor.settings.title"));
				
		init();
		
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) { dispose(); }
		});
		
		setSize(800, 400);
		setLocationRelativeTo(getParent());
	}
	
	private void init() {
		body = new JScrollPane();
		body.getVerticalScrollBar().setUnitIncrement(10);
		
		JPanel componentsPanel = new JPanel(new GridBagLayout());
		componentsPanel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			
		int y = 0;
		int inset = 1;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(inset, 0, inset, 0);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		
		for(Setting<?> setting : Settings.getSettings()) {
			if(!setting.isEnabled()) {
				continue;
			}
			
			Component component = setting.createSettingComponent();
			
			GridBagConstraints _gbc = (GridBagConstraints)gbc.clone();
			_gbc.gridy = y;
			_gbc.gridheight = 1;
			y += 1;
			
			componentsPanel.add(component, _gbc);
		}
		gbc.gridy = y;
		gbc.weighty = 1;
		Component c = Box.createGlue();
		componentsPanel.add(c, gbc);
		
		body.setViewportView(componentsPanel);
		
		JPanel buttons = new JPanel(new GridLayout(1, 4, 1, 1));
		buttons.setPreferredSize(new Dimension(0, 40));
		
		Button applyButton = new Button(TranslatableText.create("json_editor.settings.apply"));
		applyButton.addActionListener(e -> saveSettings());
		
		Button applyAndCloseButton = new Button(TranslatableText.create("json_editor.settings.apply_and_close"));
		applyAndCloseButton.addActionListener(e -> {
			saveSettings();
			dispose();
		});
		
		Button restoreDefaultsButton = new Button(TranslatableText.create("json_editor.settings.restore_defaults"));
		restoreDefaultsButton.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(dialog, TranslatableText.create("json_editor.warning.restore_defaults").getDisplayText(),
					TranslatableText.create("json_editor.warning.restore_defaults.title").getDisplayText(), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.OK_OPTION) {
				Settings.restoreDefaults();
				getContentPane().removeAll();
				init();
				removeAllChanges();
				saveSettings();
			}
		});
		
		Button cancelButton = new Button(TranslatableText.create("json_editor.cancel"));
		cancelButton.addActionListener(e -> dispose());
		
		buttons.add(applyButton);
		buttons.add(applyAndCloseButton);
		buttons.add(restoreDefaultsButton);
		buttons.add(cancelButton);
		
		add(body, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
	}
	
	public static void showDialog() {
		if(dialog != null) {
			dialog.requestFocus();
			return;
		}
		
		dialog = new SettingsDialog();
		dialog.setVisible(true);
	}
	
	public static void addSettingChange(String key, String value) {
		if(dialog != null) {
			dialog.changeSettings.put(key, value);
		}
	}
	
	public static SettingsDialog getDialog() {
		return dialog;
	}

	@Override
	public void dispose() {
		dialog = null;
		super.dispose();
	}
	
	private void saveSettings() {
		Settings.save(changeSettings);
		
		var mainWindow = Main.getMainWindow();
		mainWindow.refreshTitle();
		mainWindow.getJMenuBar().revalidate();
		mainWindow.revalidate();
		mainWindow.repaint();
		
		var windows = mainWindow.getOwnedWindows();
		for(var window : windows) {
			if (window instanceof Dialog dialog) dialog.refreshTitle();
			window.revalidate();
			window.repaint();
		}
		
		removeAllChanges();
	}
	
	private void removeAllChanges() {
		changeSettings.clear();
	}
}

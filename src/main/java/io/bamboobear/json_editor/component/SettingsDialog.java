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
		super(Main.getMainWindow(), TranslatableText.of("json_editor.settings.title"));
				
		init();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		
		setSize(800, 400);
		setLocationRelativeTo(getParent());
		setVisible(true);
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
			if(setting.getIsExperimentalFeature() && !Main.isExperimentalFeaturesEnabled()) {
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
		
		Button applyButton = new Button(TranslatableText.of("json_editor.settings.apply"));
		applyButton.addActionListener(e -> {
			saveSettings();
		});
		
		Button applyAndCloseButton = new Button(TranslatableText.of("json_editor.settings.apply_and_close"));
		applyAndCloseButton.addActionListener(e -> {
			saveSettings();
			closeDialog();
		});
		
		Button restoreDefaultsButton = new Button(TranslatableText.of("json_editor.settings.restore_defaults"));
		restoreDefaultsButton.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(dialog, TranslatableText.of("json_editor.warning.restore_defaults").getDisplayText(),
					TranslatableText.of("json_editor.warning.restore_defaults.title").getDisplayText(), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.OK_OPTION) {
				Settings.restoreDefaults();
				getContentPane().removeAll();
				init();
				removeAllChanges();
				saveSettings();
			}
		});
		
		Button cancelButton = new Button(TranslatableText.of("json_editor.cancel"));
		cancelButton.addActionListener(e -> {
			closeDialog();
		});
		
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
	}
	
	public static void addSettingChange(String key, String value) {
		if(dialog != null) {
			dialog.changeSettings.put(key, value);
		}
	}
	
	public static SettingsDialog getDialog() {
		return dialog;
	}
	
	private void closeDialog() {
		dialog = null;
		dispose();
	}
	
	private void saveSettings() {
		Settings.save(changeSettings);
		
		var frame = Main.getMainWindow();
		var dialogs = new Dialog[] {this, AboutDialog.getDialog(), PluginListDialog.getDialog()};
		
		frame.refreshTitle();
		frame.revalidate();
		frame.getJMenuBar().revalidate();
		frame.repaint();
		
		for(Dialog dialog : dialogs) {
			if(dialog == null) {
				continue;
			}
			dialog.refreshTitle();
			dialog.revalidate();
			dialog.repaint();
		}
		
		removeAllChanges();
	}
	
	private void removeAllChanges() {
		changeSettings.clear();
	}
}

package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JPanel;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent;
import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.component.TextField;
import io.bamboobear.json_editor.lang.TranslatableText;

public class FontSetting extends Setting<Font>{
	public static final int MAX_FONT_SIZE = 28;
	public static final int MIN_FONT_SIZE = 8;
	
	private static final String FONT_FAMILY = "fontFamily";
	private static final String FONT_SIZE = "fontSize";
	
	FontSetting(Font defaultValue) {
		super(defaultValue);
	}
	
	@Override
	public Font getValue() {
		if(!Main.isExperimentalFeaturesEnabled()) {
			return Main.getFont(defaultValue.getFamily(), 0, value.getSize());
		}
		return super.getValue();
	}

	@Override
	protected String[] loadValue(Properties properties) {
		String fontFamily = properties.getProperty(FONT_FAMILY);
		String fontSize = properties.getProperty(FONT_SIZE);
		
		Font f = getFont(fontFamily, fontSize);
		if(f != null) {
			value = f;
		}
		
		return new String[] {FONT_FAMILY, FONT_SIZE};
	}

	@Override
	protected void changeValue(Map<String, String> changes) {
		String fontFamily = changes.get(FONT_FAMILY);
		String fontSize = changes.get(FONT_SIZE);
		
		Font f = getFont(fontFamily, fontSize);
		if(f != null) {
			setValue(f); 
		}
	}
	
	private Font getFont(String fontFamily, String fontSize) {
		if(fontFamily == null && fontSize == null) {
			return null;
		}
		
		fontFamily = (fontFamily == null) ? value.getFamily() : fontFamily;
		fontSize = (fontSize == null) ? String.valueOf(value.getSize()) : fontSize;
		
		try {
			int size = correctFontSizeValue(Integer.parseInt(fontSize));
			return Main.getFont(fontFamily, 0, size);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected Map<String, String> saveValue() {
		var map = new HashMap<String, String>();
		map.put(FONT_FAMILY, value.getFamily());
		map.put(FONT_SIZE, String.valueOf(value.getSize()));
		return map;
	}

	@SuppressWarnings("serial")
	@Override
	public Component createSettingComponent() {
		var c = new SettingComponent(TranslatableText.create("json_editor.settings.font")) {
			@Override
			protected Component createValueComponent() {
				JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
				
				ComboBox comboBox = new ComboBox(createComboBoxItems());
				comboBox.setValue(value.getFamily());
				comboBox.addItemListener(e -> {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						SettingsDialog.addSettingChange(FONT_FAMILY, comboBox.getValue());
					}
				});
				panel.add(comboBox);
				if(!Main.isExperimentalFeaturesEnabled()) {
					comboBox.setEnabled(false);
					comboBox.setToolTipText(TranslatableText.create("json_editor.experimental_feature").getDisplayText());
				}
				
				TextField textField = new TextField(String.valueOf(value.getSize()));
				textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						String text = textField.getText();
						try {
							String size = String.valueOf(correctFontSizeValue(Integer.parseInt(text)));
							textField.setText(size);
							SettingsDialog.addSettingChange(FONT_SIZE, size);
						} catch (NumberFormatException exception) {
						}
					}
				});
				panel.add(textField);
				
				return panel;
			}
		};
		
		return c;
	}
	
	private ComboBoxItem[] createComboBoxItems() {
		String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		ComboBoxItem[] items = new ComboBoxItem[fontFamilyNames.length];
		for(int i = 0; i < fontFamilyNames.length; i++) {
			String name = fontFamilyNames[i];
			items[i] = new ComboBoxItem(TranslatableText.literal(name), name);
		}
		
		return items;
	}

	private int correctFontSizeValue(int value) {
		if(value > MAX_FONT_SIZE) {
			return MAX_FONT_SIZE;
		}
		if(value < MIN_FONT_SIZE) {
			return MIN_FONT_SIZE;
		}
		return value;
	}
}

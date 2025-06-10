package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.swing.JPanel;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent;
import io.bamboobear.json_editor.lang.TranslatableText;

public final class FontSetting extends Setting<Font>{
	public static final int MAX_FONT_SIZE = 28;
	public static final int MIN_FONT_SIZE = 8;
	
	private static final String FONT_FAMILY = "fontFamily";
	private static final String FONT_SIZE = "fontSize";
	
	private FontSetting(FontSettingBuilder builder) { super(builder); }
	
	@Override
	public Font getValue() {
		return Main.isExperimentalFeaturesEnabled() ? super.getValue() : Main.getFont(defaultValue.getFamily(), 0, value.getSize());
	}

	@Override
	protected String[] loadValue(Properties properties) {
		String fontFamily = properties.getProperty(FONT_FAMILY);
		String fontSize = properties.getProperty(FONT_SIZE);
		
		Font f = getFont(fontFamily, fontSize);
		if(f != null) value = f;
		
		return new String[] {FONT_FAMILY, FONT_SIZE};
	}

	@Override
	protected void changeValue(Map<String, String> changes) {
		String fontFamily = changes.get(FONT_FAMILY);
		String fontSize = changes.get(FONT_SIZE);
		
		Font f = getFont(fontFamily, fontSize);
		if(f != null) setValue(f);
	}
	
	private Font getFont(String fontFamily, String fontSize) {
		if(fontFamily == null && fontSize == null) return null;
		
		fontFamily = (fontFamily == null) ? value.getFamily() : fontFamily;
		fontSize = (fontSize == null) ? String.valueOf(value.getSize()) : fontSize;
		
		try {
			int size = correctFontSizeValue(Integer.parseInt(fontSize));
			return Main.getFont(fontFamily, 0, size);
		} catch (NumberFormatException e) { return null; }
	}

	@Override
	protected Map<String, String> saveValue() {
		return Map.of(
				FONT_FAMILY, value.getFamily(),
				FONT_SIZE, String.valueOf(value.getSize())
		);
	}

	@SuppressWarnings("serial")
	@Override
	public SettingComponent createSettingComponent() {
		var c = new SettingComponent(label) {
			@Override
			protected Component createValueComponent() {
				JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
				
				SettingComboBox fontFamily = new SettingComboBox(createComboBoxItems(), FONT_FAMILY, value.getFamily());
				panel.add(fontFamily);
				if(!Main.isExperimentalFeaturesEnabled()) {
					fontFamily.setEnabled(false);
					fontFamily.setToolTipText(TranslatableText.create("json_editor.experimental_feature").getDisplayText());
				}
				
				SettingTextField fontSize = new SettingTextField(FONT_SIZE, String.valueOf(value.getSize()),
						value -> {
							String text;
							try {
								text = String.valueOf(correctFontSizeValue(Integer.parseInt(value)));
							} catch (NumberFormatException e) {
								text = String.valueOf(FontSetting.this.value.getSize());
							}
							return text;
						}, SettingTextField::setText
				);
				panel.add(fontSize);
				
				return panel;
			}
		};
		
		return c;
	}
	
	private ComboBoxItem[] createComboBoxItems() {
		return Stream.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
				.map(ComboBoxItem::new).toArray(ComboBoxItem[]::new);
	}

	private int correctFontSizeValue(int value) {
		if(value > MAX_FONT_SIZE) return MAX_FONT_SIZE;
		if(value < MIN_FONT_SIZE) return MIN_FONT_SIZE;
		return value;
	}
	
	public static FontSettingBuilder builder(TranslatableText label, Font defaultValue) {
		return new FontSettingBuilder(label, defaultValue);
	}
	
	public static final class FontSettingBuilder extends SettingBuilder<Font, FontSetting, FontSettingBuilder> {
		private FontSettingBuilder(TranslatableText label, Font defaultValue) { super(label, defaultValue); }

		@Override protected FontSettingBuilder getThis() { return this; }

		@Override protected FontSetting build() { return new FontSetting(this); }
	}
}

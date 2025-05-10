package io.bamboobear.json_editor.settings;

import java.awt.Component;

import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent.SettingComboBox;
import io.bamboobear.json_editor.lang.TranslatableText;

public class BooleanSetting extends SimpleSetting<Boolean>{
	public static final TranslatableText DEFAULT_TRUE_TEXT = TranslatableText.create("json_editor.settings.enable");
	public static final TranslatableText DEFAULT_FALSE_TEXT = TranslatableText.create("json_editor.settings.disable");
	
	private final TranslatableText trueText;
	private final TranslatableText falseText;
	
	BooleanSetting(String key, SettingProperties<Boolean> properties) {
		this(key, properties, DEFAULT_TRUE_TEXT, DEFAULT_FALSE_TEXT);
	}
	
	
	BooleanSetting(String key, SettingProperties<Boolean> properties, TranslatableText trueText, TranslatableText falseText) {
		super(key, properties);
		
		this.trueText = trueText;
		this.falseText = falseText;
	}

	@Override
	protected Boolean getValueFromString(String stringValue) {
		if(stringValue == null) {
			return defaultValue;
		}
		
		return switch(stringValue.toLowerCase()) {
		case "true" -> Boolean.TRUE;
		case "false" -> Boolean.FALSE;
		default -> defaultValue;
		};
	}
	
	@Override
	protected String getStringValue() {
		return value.toString();
	}

	@Override
	protected Component createValueComponent() {
		SettingComboBox comboBox = new SettingComboBox(new ComboBoxItem[] {
				new ComboBoxItem(trueText, "true"),
				new ComboBoxItem(falseText, "false")
		}, getKey(), value.toString());
		
		return comboBox;
	}
}

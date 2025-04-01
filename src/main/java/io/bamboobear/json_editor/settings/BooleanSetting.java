package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.awt.event.ItemEvent;

import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.lang.TranslatableText;

public class BooleanSetting extends SimpleSetting<Boolean>{
	public static final TranslatableText DEFAULT_TRUE_TEXT = TranslatableText.of("json_editor.settings.enable");
	public static final TranslatableText DEFAULT_FALSE_TEXT = TranslatableText.of("json_editor.settings.disable");
	
	private final TranslatableText trueText;
	private final TranslatableText falseText;
	
	BooleanSetting(String key, Boolean defaultValue, TranslatableText text) {
		this(key, defaultValue, text, false);
	}
	
	BooleanSetting(String key, Boolean defaultValue, TranslatableText text, boolean needToRestart) {
		this(key, defaultValue, text, needToRestart, DEFAULT_TRUE_TEXT, DEFAULT_FALSE_TEXT);
	}
	
	BooleanSetting(String key, Boolean defaultValue, TranslatableText text, TranslatableText trueText, TranslatableText falseText) {
		this(key, defaultValue, text, false, trueText, falseText);
	}
	
	BooleanSetting(String key, Boolean defaultValue, TranslatableText text, boolean needToRestart, TranslatableText trueText, TranslatableText falseText) {
		super(key, defaultValue, text, needToRestart);
		
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
		ComboBox comboBox = new ComboBox(new ComboBoxItem[] {
			new ComboBoxItem(trueText, "true"),
			new ComboBoxItem(falseText, "false")
		});
		
		comboBox.setValue(value.toString());
		comboBox.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				SettingsDialog.addSettingChange(getKey(), comboBox.getValue());
			}
		});
		
		return comboBox;
	}
}

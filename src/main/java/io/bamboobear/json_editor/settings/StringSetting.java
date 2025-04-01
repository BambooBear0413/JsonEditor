package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.component.TextField;
import io.bamboobear.json_editor.lang.TranslatableText;

public class StringSetting extends SimpleSetting<String>{

	StringSetting(String key, String defaultValue, TranslatableText text) {
		this(key, defaultValue, text, false);
	}
	
	StringSetting(String key, String defaultValue, TranslatableText text, boolean needToRestart) {
		super(key, defaultValue, text, needToRestart);
	}

	@Override
	protected String getValueFromString(String stringValue) {
		return stringValue;
	}
	
	@Override
	protected String getStringValue() {
		return value;
	}

	@Override
	protected Component createValueComponent() {
		TextField textField = new TextField(value);
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String text = textField.getText();
				SettingsDialog.addSettingChange(getKey(), text);
			}
		});
		return textField;
	}
}

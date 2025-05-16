package io.bamboobear.json_editor.settings;

import java.awt.Component;

import io.bamboobear.json_editor.component.SettingComponent.SettingTextField;

public class StringSetting extends SimpleSetting<String>{
	
	StringSetting(String key, SettingProperties<String> properties) { super(key, properties); }

	@Override
	protected String getValueFromString(String stringValue) {
		return (stringValue != null) ? stringValue : defaultValue;
	}
	
	@Override protected String getStringValue() { return value; }

	@Override
	protected Component createValueComponent() {
		SettingTextField textField = new SettingTextField(getKey(), value);
		return textField;
	}
}

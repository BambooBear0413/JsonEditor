package io.bamboobear.json_editor.settings;

import java.awt.Component;

import io.bamboobear.json_editor.component.SettingComponent.SettingTextField;
import io.bamboobear.json_editor.lang.TranslatableText;

public class StringSetting extends SimpleSetting<String>{
	
	private StringSetting(StringSettingBuilder builder) { super(builder); }
	
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
	
	public static StringSettingBuilder builder(String key, TranslatableText label, String defaultValue) {
		return new StringSettingBuilder(key, label, defaultValue);
	}
	
	public static final class StringSettingBuilder extends SimpleSettingBuilder<String, StringSetting, StringSettingBuilder> {
		private StringSettingBuilder(String key, TranslatableText label, String defaultValue) { super(key, label, defaultValue); }

		@Override protected StringSettingBuilder getThis() { return this; }

		@Override protected StringSetting build() { return new StringSetting(this); }
	}
}

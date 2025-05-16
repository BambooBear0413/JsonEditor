package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.Map;
import java.util.Properties;

import io.bamboobear.json_editor.component.SettingComponent;
import io.bamboobear.json_editor.lang.TranslatableText;

public abstract class SimpleSetting<T> extends Setting<T>{
	protected final String key;
	
	<S extends SimpleSetting<T>, B extends SimpleSettingBuilder<T, S, B>> SimpleSetting(SimpleSettingBuilder<T, S ,B> builder) {
		super(builder);
		this.key = builder.key;
	}
	
	@Override
	protected final String[] loadValue(Properties properties) {
		String value = properties.getProperty(key);
		
		super.value = (value != null) ? getValueFromString(value) : defaultValue;
		
		return new String[] {key};
	}
	
	@Override
	protected void changeValue(Map<String, String> changes) {
		String value = changes.get(key);
		
		if(value != null) setValue(getValueFromString(value));
	}
	
	protected abstract T getValueFromString(String stringValue);
	
	@Override protected Map<String, String> saveValue() { return Map.of(key, getStringValue()); }
	
	protected abstract String getStringValue();
	
	@SuppressWarnings("serial")
	@Override
	public SettingComponent createSettingComponent() {
		var c = new SettingComponent(label) {
			@Override protected Component createValueComponent() { return SimpleSetting.this.createValueComponent(); }
		};
		return c;
	}
	
	protected abstract Component createValueComponent();
	
	public String getKey() { return key; }
	
	public static abstract class SimpleSettingBuilder<T, S extends SimpleSetting<T>, B extends SimpleSettingBuilder<T, S, B>> extends SettingBuilder<T, S, B> {
		private String key;
		
		SimpleSettingBuilder(String key, TranslatableText label, T defaultValue) {
			super(label, defaultValue);
			this.key = key;
		}
	}
}

package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.bamboobear.json_editor.component.SettingComponent;

public abstract class SimpleSetting<T> extends Setting<T>{
	protected final String key;
	
	SimpleSetting(String key, SettingProperties<T> properties) {
		super(properties);
		
		this.key = key;
	}
	
	@Override
	protected final String[] loadValue(Properties properties) {
		String value = properties.getProperty(key);
		
		if(value != null) {
			super.value = getValueFromString(value);
		} else {
			super.value = defaultValue;
		}
		
		return new String[] {key};
	}
	
	@Override
	protected void changeValue(Map<String, String> changes) {
		String value = changes.get(key);
		if(value != null) {
			setValue(getValueFromString(value));
		}
	}
	
	protected abstract T getValueFromString(String stringValue);
	
	@Override
	protected Map<String, String> saveValue() {
		var map = new HashMap<String, String>();
		map.put(key, getStringValue());
		return map;
	}
	
	protected abstract String getStringValue();
	
	@SuppressWarnings("serial")
	@Override
	public SettingComponent createSettingComponent() {
		var c = new SettingComponent(label) {
			@Override
			protected Component createValueComponent() {
				return SimpleSetting.this.createValueComponent();
			}
		};
		return c;
	}
	
	protected abstract Component createValueComponent();
	
	public String getKey() {
		return key;
	}
}

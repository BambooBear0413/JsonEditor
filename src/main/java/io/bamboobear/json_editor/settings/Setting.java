package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public abstract class Setting<T> {
	
	protected final T defaultValue;
	protected T value;
	private boolean experimentalFeature;
	
	Setting(T defaultValue) {
		this.defaultValue = Objects.requireNonNull(defaultValue, "default value is null");
		this.value = this.defaultValue;
	}
	
	public T getValue() {
		return value;
	}
	
	/**
	 * @return the array of string which contains the keys of the properties that just be read.*/
	protected abstract String[] loadValue(Properties properties);
	
	protected final void restoreDefault() {
		setValue(defaultValue);
	}
	
	protected abstract void changeValue(Map<String, String> changes);
	
	public final void setValue(T value) {		
		if(!(this.value.equals(value)) && showWarningDialog()) {
			this.value = (value == null) ? defaultValue : value;
			doAfterChangeValue();
		}
	}
	
	protected boolean showWarningDialog() {
		return true;
	}
	
	/**
	 * When invoking the method {@linkplain #setValue(Object)}, this method is invoked
	 * after the value changes.<br>
	 * <br>
	 * <b>WARNING</b><br>
	 * It is not recommended to perform repainting in this method because
	 * if the user changes the value of the setting via {@linkplain SettingsDialog},
	 * it will usually repaint after saving the setting.
	 */
	protected void doAfterChangeValue() {
	}
	
	protected abstract Map<String, String> saveValue();
	
	public abstract Component createSettingComponent();
	
	@SuppressWarnings("unchecked")
	public final <S extends Setting<T>> S isExperimentalFeature() {
		experimentalFeature = true;
		return (S) this;
	}
	
	public final boolean getIsExperimentalFeature() {
		return experimentalFeature;
	}
}

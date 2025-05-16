package io.bamboobear.json_editor.settings;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.SettingComponent;
import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.lang.TranslatableText;

public abstract class Setting<T> {
	protected T value;
	
	protected final TranslatableText label;
	protected final T defaultValue;
	private final boolean experimentalFeature;
	private final boolean requiresRestart;
	private final ValueChangeHandler<? super T> valueChangeHandler;
	private final Consumer<? super T> afterValueChange;
	private final BooleanSupplier isEnabled;
	
	Setting(SettingProperties<T> properties) {
		this.label = properties.label;
		this.defaultValue = Objects.requireNonNull(properties.defaultValue, "default value is null.");
		this.experimentalFeature = properties.experimentalFeature;
		this.requiresRestart = properties.requiresRestart;
		this.valueChangeHandler = properties.valueChangeHandler;
		this.afterValueChange = properties.afterValueChange;
		this.isEnabled = properties.isEnabled;
	}
	
	public T getValue() { return isEnabled() ? value : defaultValue; }
	
	/**
	 * @return the array of string which contains the keys of the properties that just be read.*/
	protected abstract String[] loadValue(Properties properties);
	
	protected final void restoreDefault() { setValue(defaultValue); }
	
	protected abstract void changeValue(Map<String, String> changes);
	
	public final void setValue(T value) {
		if(this.value.equals(value) || !canApplyChange(value)) return;
		
		this.value = (value != null) ? value : defaultValue;
		doAfterChangeValue();
	}
	
	private final boolean canApplyChange(T newValue) { return valueChangeHandler.shouldApply(newValue); }
	
	private final void doAfterChangeValue() { afterValueChange.accept(this.value); }
	
	protected abstract Map<String, String> saveValue();
	
	public abstract SettingComponent createSettingComponent();
	
	public final boolean isExperimentalFeature() { return experimentalFeature; }
	public final boolean requiresRestart()       { return requiresRestart;     }
	
	/**
	 * WARNING: Don't use this method during the loading phase.
	 * */
	public final boolean isEnabled() {
		return isEnabled.getAsBoolean() && (!isExperimentalFeature() || Main.isExperimentalFeaturesEnabled());
	}
	
	public static final class SettingProperties<T> {
		private static final TranslatableText REQUIRES_RESTART = TranslatableText.create("json_editor.settings.restart");
		
		private TranslatableText label;
		private T defaultValue;
		private boolean experimentalFeature;
		private boolean requiresRestart;
		private ValueChangeHandler<? super T> valueChangeHandler = newValue -> true;
		private Consumer<? super T> afterValueChange = value -> {};
		private BooleanSupplier isEnabled = () -> true;
		
		public SettingProperties(TranslatableText label, T defaultValue) {
			this.label = Objects.requireNonNull(label, "label is null");
			this.defaultValue = defaultValue;
		}
		
		public SettingProperties<T> isExperimentalFeature() {
			if(!experimentalFeature) {
				this.experimentalFeature = true;
				this.label.isExperimentalFeature();
			}
			return this;
		}
		
		public SettingProperties<T> valueChangeHandler(ValueChangeHandler<? super T> valueChangeHandler) {
			this.valueChangeHandler = Objects.requireNonNullElse(valueChangeHandler, this.valueChangeHandler);
			return this;
		}
		
		/**
		 * WARNING: It is not recommended to perform repaint operations within the {@link Consumer},
		 * as the {@link SettingsDialog} will typically repaint after the settings are saved.
		 * */
		public SettingProperties<T> afterValueChange(Consumer<? super T> afterValueChange) {
			this.afterValueChange = Objects.requireNonNullElse(afterValueChange, this.afterValueChange);
			return this;
		}
		
		public SettingProperties<T> requiresRestart() {
			if(!requiresRestart) {
				this.requiresRestart = true;
				label.append(TranslatableText.literal(" (%s)", REQUIRES_RESTART));
			}
			return this;
		}
		
		public SettingProperties<T> isEnabled(BooleanSupplier isEnabled) {
			this.isEnabled = Objects.requireNonNullElse(isEnabled, this.isEnabled);
			return this;
		}
	}
	
	@FunctionalInterface
	public static interface ValueChangeHandler<T> {
		boolean shouldApply(T newValue);
	}
}

package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.Objects;

import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent.SettingComboBox;
import io.bamboobear.json_editor.lang.TranslatableText;

public final class BooleanSetting extends SimpleSetting<Boolean>{
	public static final TranslatableText DEFAULT_TRUE_TEXT = TranslatableText.create("json_editor.settings.enable");
	public static final TranslatableText DEFAULT_FALSE_TEXT = TranslatableText.create("json_editor.settings.disable");
	
	private final TranslatableText trueText;
	private final TranslatableText falseText;
	
	private BooleanSetting(BooleanSettingBuilder builder) {
		super(builder);
		this.trueText  = builder.trueText;
		this.falseText = builder.falseText;
	}

	@Override
	protected Boolean getValueFromString(String stringValue) {
		if(stringValue == null) return defaultValue;
		
		return switch(stringValue.toLowerCase()) {
		case "true" -> Boolean.TRUE;
		case "false" -> Boolean.FALSE;
		default -> defaultValue;
		};
	}
	
	@Override protected String getStringValue() { return value.toString(); }

	@Override
	protected Component createValueComponent() {
		SettingComboBox comboBox = new SettingComboBox(new ComboBoxItem[] {
				new ComboBoxItem(trueText, "true"),
				new ComboBoxItem(falseText, "false")
		}, getKey(), value.toString());
		
		return comboBox;
	}
	
	public static BooleanSettingBuilder builder(String key, TranslatableText label, Boolean defaultValue) {
		return new BooleanSettingBuilder(key, label, defaultValue);
	}
	
	public static final class BooleanSettingBuilder extends SimpleSettingBuilder<Boolean, BooleanSetting, BooleanSettingBuilder> {
		private TranslatableText trueText  = DEFAULT_TRUE_TEXT;
		private TranslatableText falseText = DEFAULT_FALSE_TEXT;
		
		private BooleanSettingBuilder(String key, TranslatableText label, Boolean defaultValue) { super(key, label, defaultValue); }
		
		@Override protected BooleanSettingBuilder getThis() { return this; }
		
		@Override protected BooleanSetting build() { return new BooleanSetting(this); }
		
		public BooleanSettingBuilder text(TranslatableText trueText, TranslatableText falseText) {
			this.trueText  = Objects.requireNonNullElse(trueText,  this.trueText );
			this.falseText = Objects.requireNonNullElse(falseText, this.falseText);
			return this;
		}
	}
}

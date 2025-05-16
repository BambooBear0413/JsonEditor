package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.stream.Stream;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent.SettingComboBox;
import io.bamboobear.json_editor.lang.TranslatableText;

public class LookAndFeelSetting extends SimpleSetting<LookAndFeelInfo>{
	
	private LookAndFeelSetting(LookAndFeelSettingBuilder builder) { super(builder.isExperimentalFeature()); }
	
	@Override
	protected LookAndFeelInfo getValueFromString(String stringValue) {
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo info : infos) {
			if(info.getClassName().equals(stringValue)) return info;
		}
		return defaultValue;
	}

	@Override protected String getStringValue() { return value.getClassName(); }

	@Override
	protected Component createValueComponent() {
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		ComboBoxItem[] items = Stream.of(infos)
				.map(info -> new ComboBoxItem(TranslatableText.literal(info.getName()), info.getClassName()))
				.toArray(ComboBoxItem[]::new);
		SettingComboBox comboBox = new SettingComboBox(items, getKey(), value.getClassName());
		return comboBox;
	}
	
	public static LookAndFeelSettingBuilder builder(String key, TranslatableText label, LookAndFeelInfo defaultValue) {
		return new LookAndFeelSettingBuilder(key, label, defaultValue);
	}

	public static final class LookAndFeelSettingBuilder extends SimpleSettingBuilder<LookAndFeelInfo, LookAndFeelSetting, LookAndFeelSettingBuilder> {
		private LookAndFeelSettingBuilder(String key, TranslatableText label, LookAndFeelInfo defaultValue) { super(key, label, defaultValue); }

		@Override protected LookAndFeelSettingBuilder getThis() { return this; }

		@Override protected LookAndFeelSetting build() { return new LookAndFeelSetting(this); }
	}
}

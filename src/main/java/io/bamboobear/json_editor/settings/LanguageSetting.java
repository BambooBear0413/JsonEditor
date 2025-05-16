package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.util.stream.Stream;

import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingComponent.SettingComboBox;
import io.bamboobear.json_editor.lang.Language;
import io.bamboobear.json_editor.lang.LanguageLoadingException;
import io.bamboobear.json_editor.lang.Languages;
import io.bamboobear.json_editor.lang.TranslatableText;

public class LanguageSetting extends SimpleSetting<Language>{

	LanguageSetting(String key, SettingProperties<Language> properties) { super(key, properties); }

	@Override
	protected Language getValueFromString(String stringValue) {
		if(stringValue == null) return defaultValue;
		
		Language lang = null;
		try {
			lang = Languages.getLanguage(stringValue.toLowerCase());
		} catch (LanguageLoadingException e) {}
		return (lang == null) ? defaultValue : lang;
	}
	
	@Override protected String getStringValue() { return value.id(); }

	@Override
	protected Component createValueComponent() {
		SettingComboBox comboBox = new SettingComboBox(
				Stream.of(Languages.getLanguages())
						.map(lang -> new ComboBoxItem(TranslatableText.literal(lang.getName()), lang.id()).showID())
						.toArray(ComboBoxItem[]::new),
				getKey(), value.id());
		return comboBox;
	}
}

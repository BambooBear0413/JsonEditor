package io.bamboobear.json_editor.settings;

import java.awt.Component;
import java.awt.event.ItemEvent;

import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.SettingsDialog;
import io.bamboobear.json_editor.lang.Language;
import io.bamboobear.json_editor.lang.LanguageLoadingException;
import io.bamboobear.json_editor.lang.Languages;
import io.bamboobear.json_editor.lang.TranslatableText;

public class LanguageSetting extends SimpleSetting<Language>{

	LanguageSetting(String key, Language defaultValue, TranslatableText text) {
		super(key, defaultValue, text, false);
	}

	@Override
	protected Language getValueFromString(String stringValue) {
		if(stringValue == null) {
			return defaultValue;
		}
		stringValue = stringValue.toLowerCase();
		Language lang;
		try {
			lang = Languages.getLanguage(stringValue);
		} catch (LanguageLoadingException e) {
			return defaultValue;
		}
		return lang == null ? defaultValue : lang;
	}
	
	@Override
	protected String getStringValue() {
		return value.id();
	}

	@Override
	protected Component createValueComponent() {
		ComboBox comboBox = new ComboBox(createComboBoxItems());
		comboBox.setValue(value.id());
		comboBox.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				SettingsDialog.addSettingChange(getKey(), comboBox.getValue());
			}
		});
		return comboBox;
	}
	
	private ComboBoxItem[] createComboBoxItems() {
		Language[] languages = Languages.getLanguages();
		ComboBoxItem[] items = new ComboBoxItem[languages.length];
		for(int i = 0; i < languages.length; i++) {
			Language lang = languages[i];
			items[i] = new ComboBoxItem(TranslatableText.literal(lang.getName()), lang.id()).showID();
		}
		return items;
	}

}

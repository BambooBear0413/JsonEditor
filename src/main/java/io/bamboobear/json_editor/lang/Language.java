package io.bamboobear.json_editor.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.bamboobear.json_editor.ErrorReport;
import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.util.JsonArrayUtilities;
import io.bamboobear.json_editor.util.JsonObjectUtilities;

public final class Language {
	private static final String UNKNOWN = "Unknown Language";
	
	private static final Map<String, String> REPLACEMENTS = new HashMap<>();

	private final String id;
	private final Map<String, String> translations = new HashMap<>();
	private String readme;
	private String name;
	
	private LinkedHashSet<String> alternativeLanguages = new LinkedHashSet<>();
	
	private static final String LANGUAGE_ID_REGEX = "[a-z][a-z0-9_]{1,63}";
	
	static {
		try {
			JsonElement element = JsonFile.loadResource("lang/key-mappings.json");
			if(!(element instanceof JsonObject object)) {
				throw new IllegalStateException("Unexpected JSON type: " + element.getClass().getSimpleName());
			}
			object.asMap().forEach((key, value) -> {
				if(REPLACEMENTS.containsKey(key)) return;
				if(!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) return;

				String stringValue = value.getAsString();
				String remappedValue = REPLACEMENTS.getOrDefault(stringValue, stringValue);
				if(key.equals(remappedValue)) return;

				for (var entry : REPLACEMENTS.entrySet()) {
					/*
					 * remappedValue never equals to entry.getKey() ("k") because when value equals to "k",
					 * the result of remappedValue will be entry.getValue().
					 */

					String v = entry.getValue();
					if (v.equals(key)) entry.setValue(remappedValue);
				}

				REPLACEMENTS.put(key, remappedValue);
			});
		} catch (Exception e) {
			ErrorReport.output(e);
		}
	}

	/**
	 * @param id the ID of the language
	 * @param readme the path of the corresponding README file 
	 *
	 * @throws LanguageLoadingException
	 */
	Language(String id, String readme) throws LanguageLoadingException {
		if(id == null) throw new LanguageLoadingException("\"id\" is null");
		if(!id.matches("^" + LANGUAGE_ID_REGEX + "$")) throw new LanguageLoadingException("id=" + id + ", id must start with a lowercase letter (a-z) and can only contain lowercase letters (a-z), numbers (0-9), and underscores (_). It must be between 2 and 64 characters long.");
		
		this.id = id;
		this.readme = readme;
	}
	
	void load(JsonElement element) {
		switch(element){
			case JsonObject object -> load(object);
			default -> {} // TODO loading warning;
		}
	}

	private void load(JsonObject object) {
		JsonObject translations = JsonObjectUtilities.getJsonObject(object, "translations");
		if(translations == null) {
			loadTranslations(object);
			return;
		}
		loadTranslations(translations);

		JsonArray alternativeLanguages = JsonObjectUtilities.getJsonArray(object, "alternative_languages");
		if(alternativeLanguages != null) {
			String[] languages = JsonArrayUtilities.getStrings(alternativeLanguages, (str) -> str.matches("^" + LANGUAGE_ID_REGEX + "$"));
			this.alternativeLanguages.addAll(Arrays.asList(languages));
		}
	}

	private void loadTranslations(JsonObject object) {
		if(name == null) name = JsonObjectUtilities.getString(object, "lang.name", null);

		object.asMap().forEach((key, value) -> {
			if(value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				add(REPLACEMENTS.getOrDefault(key, key), value.getAsString());
			}
		});
	}
	
	private void add(String key, String value) {
		if(!key.isEmpty()) translations.putIfAbsent(key, value);
	}
	
	public boolean hasReadmePath() { return readme != null; }

	public String id() { return id;     }

	public String getName() { return (name == null) ? UNKNOWN : name; }

	public String getReadmePath() { return readme; }

	String getDisplayText(String key) {
		if(key == null || key.isEmpty()) return "";
		
		key = REPLACEMENTS.getOrDefault(key, key);

		String value = translations.get(key);
		if(isValidValue(value)) return value;
		
		for(String languageName : alternativeLanguages) {
			Language lang = Languages.getLanguage(languageName);
			value = lang.translations.get(key);
			if(isValidValue(value)) return value;
		}
		
		value = Languages.DEFAULT_LANGUAGE.translations.get(key);
		if(isValidValue(value)) return value;
		
		return key;
	}
	
	private boolean isValidValue(String value) { return value != null && !value.isEmpty(); }
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Language lang)) return false;
		return this.id.equals(lang.id);
	}
	
	@Override public int hashCode() { return id.hashCode(); }
	
	@Override
	public String toString() {
		return "id:" + id + ", translation:" + translations + ", alternativeLanguages" + alternativeLanguages;
	}
}

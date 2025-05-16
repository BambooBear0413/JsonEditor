package io.bamboobear.json_editor.lang;

import java.util.HashMap;
import java.util.LinkedHashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.bamboobear.json_editor.util.JsonArrayUtilities;
import io.bamboobear.json_editor.util.JsonObjectUtilities;

public final class Language {
	private static final String UNKNOWN = "Unknown Language";
	
	private final String id;
	private final HashMap<String, String> translations = new HashMap<String, String>();
	private String readme;
	private String html;
	private String name = UNKNOWN;
	
	private LinkedHashSet<String> alternativeLanguages = new LinkedHashSet<>();
	
	public static final String LANGUAGE_ID_REGEX = "[a-z][a-z0-9_]{1,63}";
	
	/**
	 * @param id the ID of the language
	 * @param readme the path of the corresponding README file 
	 * @param html the path of the corresponding HTML attachment file
	 * 
	 * @throws LanguageLoadingException
	 */
	Language(String id, String readme, String html) throws LanguageLoadingException {
		if(id == null) throw new LanguageLoadingException("\"id\" is null");
		if(!id.matches("^" + LANGUAGE_ID_REGEX + "$")) throw new LanguageLoadingException("id=" + id + ", id must start with a lowercase letter (a-z) and can only contain lowercase letters (a-z), numbers (0-9), and underscores (_). It must be between 2 and 64 characters long.");
		
		this.id = id;
		this.readme = readme;
		this.html = html;
	}
	
	void load(JsonElement element) {
		switch(element) {
		case JsonObject object -> {
			if(name == UNKNOWN) name = JsonObjectUtilities.getString(object, "lang.name", UNKNOWN);
			
			object.asMap().forEach((key, value) -> {
				if(value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
					add(key, value.getAsString());
				}
			});
		}
		case JsonArray array -> {
			String[] languages = JsonArrayUtilities.getStrings(array, (string) -> string.matches("^" + LANGUAGE_ID_REGEX + "$"));
			for(String lang : languages) {
				alternativeLanguages.add(lang);
			}
			
			JsonObject[] objects = JsonArrayUtilities.getJsonObjects(array);
			for(JsonObject object : objects) {
				load(object);
			}
		}
		default -> {} // TODO loading warning
		}
	}
	
	private void add(String key, String value) {
		if(!key.isEmpty()) translations.putIfAbsent(key, value);
	}
	
	public boolean hasReadmePath() { return readme != null; }
	public boolean hasHtmlPath()   { return html   != null; }
	
	public String id()            { return id;     }
	public String getName()       { return name;   }
	public String getReadmePath() { return readme; }
	public String getHtmlPath()   { return html;   }
	
	String getDisplayText(String key) {
		if(key == null || key.isEmpty()) return "";
		
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

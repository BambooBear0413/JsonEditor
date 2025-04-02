package io.bamboobear.json_editor.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	private ArrayList<String> alternativeLanguages = new ArrayList<String>();
	
	public static final String LANGUAGE_ID_REGEX = "[a-z][a-z0-9_]{1,63}";
	
	/**
	 * @param id the ID of the language
	 * @param readme the path of the corresponding README file 
	 * @param html the path of the corresponding HTML attachment file
	 * 
	 * @throws LanguageLoadingException
	 */
	Language(String id, String readme, String html) throws LanguageLoadingException {
		if(id == null) {
			throw new LanguageLoadingException("\"id\" is null", new NullPointerException());
		}
		if(!id.matches("^" + LANGUAGE_ID_REGEX + "$")) {
			throw new LanguageLoadingException(new IllegalArgumentException("\"id=\"" + id + ", \"id\" must start with a lowercase letter (a-z) and can only contain lowercase letters (a-z), numbers (0-9), and underscores (_). It must be between 2 and 64 characters long."));
		}
		this.id = id;
		this.readme = readme;
		this.html = html;
	}
	
	void load(JsonElement element) {
		if(element instanceof JsonObject object) { //TODO java-21
			if(name == UNKNOWN) {
				name = JsonObjectUtilities.getString(object, "lang.name", UNKNOWN);
			}
			
			Map<String, JsonElement> map = object.asMap();
			map.forEach((key, value) -> {
				if(value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
					add(key, value.getAsString());
				}
			});
		} else if(element instanceof JsonArray array) {
			String[] languages = JsonArrayUtilities.getStrings(array, (string) -> string.matches("^" + LANGUAGE_ID_REGEX + "$"));
			for(String s : languages) {
				if(!alternativeLanguages.contains(s)) {
					alternativeLanguages.add(s);
				}
			}
			
			JsonObject[] objects = JsonArrayUtilities.getJsonObjects(array);
			for(JsonObject object : objects) {
				load(object);
			}
		}
	}
	
	private void add(String key, String value) {
		if(!key.isEmpty() && !translations.containsKey(key)) { // avoid replacing the previously loaded value
			translations.put(key, value);
		}
	}
	
	public String id() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasReadmePath() {
		return readme != null;
	}
	
	public String getReadmePath() {
		return readme;
	}
	
	public boolean hasHtmlPath() {
		return html != null;
	}
	
	public String getHtmlPath() {
		return html;
	}
	
	String getDisplayText(String key) {
		if(key == null || key.isEmpty()) {
			return "";
		}
		
		String value = translations.get(key);
		int i = 0;
		int size = alternativeLanguages.size();
		while(value == null || value.isEmpty()) {
			if(i < size) {
				Language lang = Languages.getLanguage(alternativeLanguages.get(i));
				value = lang.translations.get(key);
			} else if(i == size) {
				value = Languages.DEFAULT_LANGUAGE.translations.get(key);
			} else {
				value = key;
				break;
			}
			i++;
		}
		
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Language lang) {
			return this.id.equals(lang.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id + ": " +translations.toString();
	}
}

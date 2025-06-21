package io.bamboobear.json_editor.lang;

import java.util.ArrayList;

import com.google.gson.JsonElement;

import io.bamboobear.json_editor.ErrorReport;
import io.bamboobear.json_editor.JsonFile;

public final class Languages {
	private static final ArrayList<Language> LANGUAGES = new ArrayList<>();
	
	public static final Language ENGLISH = getLanguage("en_us", "README.md", "Bamboo Bear's JSON Editor.html");
	public static final Language TRADITIONAL_CHINESE = getLanguage("zh_tw", "README (zh_tw).md", "竹子熊的JSON編輯器 (繁體中文).html");
	public static final Language SIMPLIFIED_CHINESE = getLanguage("zh_cn", "README (zh_cn).md", "竹子熊的JSON编辑器 (简体中文).html");
	
	public static final Language DEFAULT_LANGUAGE = ENGLISH;
	
	private Languages() {}
	
	public static void loadLanguage() {		
		for(Language lang : LANGUAGES) {
			try {
				String fileName = String.format("lang/%s.json", lang.id());
				JsonElement root = JsonFile.loadResource(fileName);
				lang.load(root);
			} catch (Exception e) {
				ErrorReport.output(new LanguageLoadingException("An error occurred when loading the language file for \"" + lang.id() + "\".", e));
			}
		}
	}
	
	public static Language getLanguage(String id) throws LanguageLoadingException {
		return getLanguage(id, null, null);
	}
	
	private static Language getLanguage(String id, String readme, String html) {
		for(Language lang : LANGUAGES) {
			if(lang.id().equals(id)) return lang;
		}
		Language lang = new Language(id, readme, html);
		LANGUAGES.add(lang);
		return lang;
	}
		
	public static Language[] getLanguages() { return LANGUAGES.toArray(new Language[LANGUAGES.size()]); }
	
	public static int getLanguageCount() { return LANGUAGES.size(); }
}

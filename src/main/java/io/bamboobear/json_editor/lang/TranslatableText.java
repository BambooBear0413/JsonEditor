package io.bamboobear.json_editor.lang;

import java.util.HashMap;
import java.util.IllegalFormatException;

import io.bamboobear.json_editor.Main;

public final class TranslatableText{	
	private final String key;
	private final Object[] objects;
	private boolean isTranslatable = true;
	private boolean isExperimentalFeature;
	
	private static final HashMap<String, TranslatableText> cache = new HashMap<String, TranslatableText>();
	private static final HashMap<String, TranslatableText> literals = new HashMap<String, TranslatableText>();
	
	public static final TranslatableText EMPTY = TranslatableText.of("");
	
	private static final TranslatableText EXPERIMENTAL_FEATURE = TranslatableText.of("json_editor.experimental_feature");
	
	private TranslatableText(String key, Object... objects) {
		this.key = (key == null) ? "" : key;
		this.objects = objects;
	}
	
	private TranslatableText(TranslatableText text) {
		this.key = text.key;
		this.objects = text.objects;
		this.isTranslatable = text.isTranslatable;
		this.isExperimentalFeature = text.isExperimentalFeature;
	}
	
	public static TranslatableText of(String key) {
		TranslatableText text = cache.get(key);
		if(text != null) {
			return text;
		}
		
		text = new TranslatableText(key);
		cache.put(key, text);
		return text;
	}
	
	public static TranslatableText of(String key, Object... objects) {
		if(objects == null || objects.length == 0) {
			return TranslatableText.of(key);
		}
		return new TranslatableText(key, objects);
	}
	
	public static TranslatableText literal(String key) {
		TranslatableText text = literals.get(key);
		if(text != null) {
			return text;
		}
		
		text = new TranslatableText(key);
		text.isTranslatable = false;
		literals.put(key, text);
		return text;
	}
	
	public static TranslatableText literal(String key, Object...objects) {
		if(objects == null || objects.length == 0) {
			return TranslatableText.literal(key);
		}
		TranslatableText text = new TranslatableText(key, objects);
		text.isTranslatable = false;
		return text;
	}
	
	public TranslatableText isExperimentalFeature() {
		TranslatableText text = new TranslatableText(this);
		text.isExperimentalFeature = true;
		return text;
	}
	
	public String getDisplayText() {
		return getDisplayText(Main.getLanguage());
	}
	
	public String getDisplayText(Language lang) {
		String displayText;
		
		displayText = isTranslatable ? getValue(lang, key, objects) : key;
		
		if(isExperimentalFeature) {
			displayText = String.format("%s (%s)", displayText, EXPERIMENTAL_FEATURE.getDisplayText());
		}
		
		return displayText;
	}
	
	private String getValue(Language lang, String key, Object[] objects) {
		try {
			String str = lang.getDisplayText(key);
			try {
				return String.format(str, objects);
			} catch (IllegalFormatException e) {
				return str;
			}
		} catch (NullPointerException e) {
			return key;
		}
	}
	
	public boolean isTranslatable() {
		return isTranslatable;
	}
	
	public boolean getIsExperimentalFeature() {
		return isExperimentalFeature;
	}
	
	public boolean isEmpty() {
		return key.isEmpty();
	}
	
	@Override
	public String toString() {
		return String.format("key = %s, objects = %s, isTranslatable = %b, isExperimentalFeature = %b", 
				key, arrayToString(objects), isTranslatable, isExperimentalFeature);
	}
	
	private String arrayToString(Object[] objects) {
		if(objects == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(objects.getClass().getCanonicalName());
		sb.append("{");
		for(int i = 0; i < objects.length; i++) {
			if(i != 0) {
				sb.append(", ");
			}
			
			Object object = objects[i];
			if(object.getClass().isArray()) {
				sb.append(arrayToString((Object[])object));
			} else {
				sb.append(object);
			}
		}
		sb.append("}");
		return sb.toString();
	}
}

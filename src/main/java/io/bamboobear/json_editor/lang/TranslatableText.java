package io.bamboobear.json_editor.lang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Map;

import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.Main;

public final class TranslatableText{	
	private final String key;
	private final Object[] arguments;
	private final ArrayList<TranslatableText> siblings;
	
	private boolean nonTranslatable;
	
	public static final TranslatableText EMPTY = TranslatableText.create("");
	
	private static final TranslatableText EXPERIMENTAL_FEATURE = TranslatableText.create("json_editor.experimental_feature");
	
	private TranslatableText(String key, Object... args) {
		this.key = (key == null) ? "" : key;
		this.arguments = args;
		this.siblings = new ArrayList<>();
	}
	
	public static TranslatableText create(String key) { return new TranslatableText(key); }
	
	public static TranslatableText create(String key, Object... args) {
		if(args == null || args.length == 0) return create(key);
		return new TranslatableText(key, args);
	}
	
	public static TranslatableText literal(String key) {
		TranslatableText text = new TranslatableText(key);
		text.nonTranslatable = true;
		return text;
	}
	
	public static TranslatableText literal(String key, Object... args) {
		if(args == null || args.length == 0) return TranslatableText.literal(key);
		
		TranslatableText text = new TranslatableText(key, args);
		text.nonTranslatable = true;
		return text;
	}
	
	public TranslatableText append(TranslatableText text) {
		if(text != null) siblings.add(text);
		return this;
	}
	
	public TranslatableText append(String text) {
		if(text == null) return this;
		return append(literal(text));
	}
	
	public TranslatableText isExperimentalFeature() {
		return append(TranslatableText.create(" (%s)", EXPERIMENTAL_FEATURE));
	}
	
	public String getDisplayText() { return getDisplayText(Main.getLanguage()); }
	
	public String getDisplayText(Language lang) {
		String displayText = (!nonTranslatable) ? getValue(lang, key) : key;
		
		String[] translatedArguments = new String[arguments.length];
		for(int i = 0; i < arguments.length; i++) {
			translatedArguments[i] = translateArgument(arguments[i]);
		}
		
		try {
			displayText = displayText.formatted((Object[])translatedArguments);
		} catch (IllegalFormatException e) {}
		
		for(TranslatableText text : siblings) {
			displayText = displayText.concat(text.getDisplayText());
		}
		
		return displayText;
	}
	
	private String getValue(Language lang, String key) {
		String str = lang.getDisplayText(key);
		return (str != null) ? str : key;
	}
	
	private String translateArgument(Object argument) {
		return switch(argument) {
		case TranslatableText text -> text.getDisplayText();
		case JsonFile file -> file.getFilePath();
		
		case File file -> {
			try                   { yield file.getCanonicalPath(); }
			catch (IOException e) { yield file.getAbsolutePath(); }
		}
		
		case Collection<?> collection -> {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for(Object element : collection) {
				if(builder.length() > 1) builder.append(", ");
				builder.append(translateArgument(element));
			}
			builder.append("}");
			yield builder.toString();
		}
		
		case Map<?, ?> map -> {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			map.forEach((key, value) -> {
				if(builder.length() > 1) builder.append(", ");
				builder.append("%s=%s".formatted(translateArgument(key), translateArgument(value)));
			});
			builder.append("}");
			yield builder.toString();
		}
		
		case Object[] objects -> {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for(int i = 0; i < objects.length; i++) {
				if(i != 0) builder.append(", ");
				Object object = objects[i];
				builder.append(translateArgument(object));
			}
			builder.append("}");
			yield builder.toString();
		}
		
		case null -> "null";
		default -> argument.toString();
		};
	}
	
	public boolean isNonTranslatable() { return nonTranslatable; }
	public boolean isEmptyKey()        { return key.isEmpty();   }
	
	@Override
	public String toString() {
		return String.format("{key = \"%s\", args = %s, isNonTranslatable = %b, siblings = %s}", 
				key, Arrays.deepToString(arguments), nonTranslatable, siblings);
	}
}

package io.bamboobear.json_editor.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Map;
import java.util.Properties;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.Languages;
import io.bamboobear.json_editor.lang.TranslatableText;

public class Settings {
	private static final File file = new File("settings.properties");
	private static final ArrayList<Setting<?>> settings = new ArrayList<Setting<?>>();
	private static final Properties unknownProperties = new Properties();
	
	public static final LanguageSetting LANGUAGE = registerSetting(new LanguageSetting("language", Languages.DEFAULT_LANGUAGE, createTranslatableText("language")));
	public static final BooleanSetting EXPERIMENTAL_FEATURE = registerSetting(new BooleanSetting("isExperimentalFeaturesEnabled", false, createTranslatableText("experimental_features"), true));
	public static final FontSetting FONT = registerSetting(new FontSetting(Main.getFont("Default", 0, 15)));
	public static final BooleanSetting PRETTY_PRINTING = registerSetting(new BooleanSetting("enablesPrettyPrinting", false, createTranslatableText("pretty_printing")));
	public static final BooleanSetting HTML_ESCAPING = registerSetting(new BooleanSetting("enablesHtmlEscaping", false, createTranslatableText("html_escaping")));
	public static final BooleanSetting UNICODE_ESCAPING = registerSetting(new BooleanSetting("enablesUnicodeEscaping", false, createTranslatableText("unicode_escaping").isExperimentalFeature()).isExperimentalFeature());
	
	public static void loadSettings() {
		try(FileReader fr = new FileReader(file)) {
			Properties properties = new Properties();
			properties.load(fr);
			
			ArrayList<String> knownProperties = new ArrayList<String>();
			
			for(Setting<?> setting : settings) {
				var keys = setting.loadValue(properties);
				
				for(String key : keys) {
					knownProperties.add(key);
				}
			}
			
			for(String key : knownProperties) {
				properties.remove(key);
			}
			
			properties.forEach((key, value) -> {
				unknownProperties.put(key, value);
			});
		} catch (IOException e) {
		}
		
		for(Setting<?> setting : settings) {
			if(!EXPERIMENTAL_FEATURE.getValue() && setting.getIsExperimentalFeature()) {
				setting.restoreDefault();
			}
		}
	}
	
	public static boolean save(Map<String, String> changes) {
		for(Setting<?> setting : settings) {
			setting.changeValue(changes);
		}
		
		return save();
	}
	
	/**
	 * @return {@code true} if saved the settings successfully, {@code false} otherwise.*/
	public static boolean save() {
		try {
			if(!file.isFile()) {
				file.createNewFile();
			}
			
			try (FileWriter fw = new FileWriter(file)) {
				write(fw);
				return true;
			} catch (IOException e) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}
	
	static <T extends Setting<?>> T registerSetting(T setting) {
		settings.add(setting);
		return setting;
	}
	
	public static Setting<?>[] getSettings() {
		return settings.toArray(new Setting[settings.size()]);
	}
	
	public static void restoreDefaults() {
		for(Setting<?> setting : settings) {
			setting.restoreDefault();
		}
	}
	
	private static void write(FileWriter fw) throws IOException {
		for(Setting<?> setting : settings) {
			var map = setting.saveValue();
			write(fw, map);
		}
		
		if(unknownProperties.size() == 0) {
			return;
		}
		
		fw.write("\n#Unknown Properties\n\n");
		write(fw, unknownProperties);
	}
	
	private static void write(FileWriter fw, Map<?, ?> map) throws IOException {
		try {
			map.forEach((key, value) -> {
				try {
					String convertedKey = convert(key.toString(), true, true);
					String convertedValue = convert(value.toString(), false, true);
					fw.write(convertedKey + "=" + convertedValue + "\n");
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}
	
	private static String convert(String string, boolean escapeSpace, boolean escapeUnicode) {
		char[] chars = string.toCharArray();
		StringBuffer sb = new StringBuffer();
		HexFormat hf = HexFormat.of().withUpperCase();
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i];
			switch(c) {
			case '\\' -> sb.append("\\\\");
			case ' ' -> {
				if (i == 0 && escapeSpace) {
					sb.append("\\ ");
				} else {
					sb.append(' '); }
				}
			case '\t' -> sb.append("\\t");
			case '\n' -> sb.append("\\n");
			case '\r' -> sb.append("\\r");
			case '\f' -> sb.append("\\f");
			case '=', ':', '#', '!' -> sb.append('\\').append(c);
			default -> {
				if((c < 0x0020 || c > 0x007E) && escapeUnicode) {
					sb.append("\\u").append(hf.toHexDigits(c));
				} else {
					sb.append(c);
				}
			}}
		}
		return sb.toString();
	}
	
	private static TranslatableText createTranslatableText(String settingId) {
		return TranslatableText.of("json_editor.settings." + settingId);
	}
}

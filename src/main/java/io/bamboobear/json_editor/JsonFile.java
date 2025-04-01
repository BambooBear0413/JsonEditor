package io.bamboobear.json_editor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.bamboobear.json_editor.settings.Settings;

public final class JsonFile {
	private final File file;
	
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	public JsonFile(File file){
		Objects.requireNonNull(file, "\"file\" is null");
		this.file = file;
	}

	public JsonFile(String fileName){
		this(new File(fileName));
	}

	public JsonElement load() throws IOException, JsonParseException {
		JsonElement je;
		try (FileReader fr = new FileReader(file, UTF_8);
			JsonReader jr = Main.createJsonReader(fr)) {
			je = JsonParser.parseReader(jr);
		}

		return je;
	}
	
	public static JsonElement loadResource(String name) throws IOException, JsonParseException {
		try (InputStream is = ClassLoader.getSystemResourceAsStream(name)){
			if(is == null) {
				throw new NullPointerException("Resource \"" + name + "\" was not found.");
			}
			return loadByInputStream(is);
		}
	}
	
	public static JsonElement loadByInputStream(InputStream is) throws IOException, JsonParseException {
		JsonElement je;
		try(InputStreamReader isr = new InputStreamReader(is, UTF_8);
			JsonReader jr = Main.createJsonReader(isr)) {
			je = JsonParser.parseReader(jr);
		}
		
		return je;
	}

	public void save(JsonElement root) throws IOException {
		if (!file.isFile()) {
			file.createNewFile();
		}
		
		try (FileWriter fw = new FileWriter(file, UTF_8);
			JsonWriter jw = Main.createJsonWriter(fw)) {
			writeJsonElement(jw, root);
		}
	}
	
	private void writeJsonElement(JsonWriter jw, JsonElement je) throws IOException {
		try {
			if(je instanceof JsonPrimitive jp) {
				if(jp.isBoolean()) {
					jw.value(jp.getAsBoolean());
				} else if(jp.isNumber()) {
					jw.value(jp.getAsNumber());
				} else {
					if(Settings.UNICODE_ESCAPING.getValue()) {
						writeStringWithUnicodeEscape(jw, je.getAsString());
					} else {
						jw.value(jp.getAsString());
					}
				}
			} else if (je instanceof JsonObject jo) {
				Map<String, JsonElement> map = jo.asMap();
				
				jw.beginObject();
				
				map.forEach((key, value) -> {
					try {
						jw.name(key);
						writeJsonElement(jw, value);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
				
				jw.endObject();
			} else if (je instanceof JsonArray ja) {
				List<JsonElement> list = ja.asList();
				
				jw.beginArray();
				
				list.forEach((e) -> {
					try {
						writeJsonElement(jw, e);
					} catch (IOException exception) {
						throw new UncheckedIOException(exception);
					}
				});
				
				jw.endArray();
			} else {
				jw.nullValue();
			}
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}
	
	private void writeStringWithUnicodeEscape(JsonWriter jw, String str) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] charArray = str.toCharArray();
		boolean htmlEscaping = Settings.HTML_ESCAPING.getValue();
		
		sb.append('"');
		for(char c : charArray) {
			if(c >= '\u007F') {
				sb.append("\\u%04x".formatted((int)c));
				continue;
			}
			
			if(c < ' ') {
				sb.append(switch(c) {
				case '\b' -> "\\b";
				case '\t' -> "\\t";
				case '\n' -> "\\n";
				case '\f' -> "\\f";
				case '\r' -> "\\r";
				default -> "\\u%04x".formatted((int)c);
				});
				continue;
			}
			
			sb.append(switch(c) {
			case '"' -> "\\\"";
			case '\\' -> "\\\\";
			case '<', '>', '&', '=', '\'' -> htmlEscaping ? "\\u%04x".formatted((int)c) : c;
			default -> c;
			});
		}
		sb.append('"');
		
		jw.jsonValue(sb.toString());
	}
	
	public String getFilePath() {
		return file.getAbsolutePath();
	}
	
	public long lastModified() {
		return file.lastModified();
	}
}

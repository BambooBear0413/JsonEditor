package io.bamboobear.json_editor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
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

import static java.nio.charset.StandardCharsets.UTF_8;

public final class JsonFile {
	public final File file;
	
	public JsonFile(String filePath){ this(new File(filePath)); }

	public JsonFile(File file){
		this.file = Objects.requireNonNull(file, "file is null");
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
			if(is == null) throw new NullPointerException("Resource \"" + name + "\" was not found.");
			
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
		try (FileWriter fw = new FileWriter(file, UTF_8);
			JsonWriter jw = Main.createJsonWriter(fw)) {
			writeJsonElement(jw, root);
		}
	}
	
	private void writeJsonElement(JsonWriter writer, JsonElement json) throws IOException {
		try {
			switch(json) {
			case JsonPrimitive primitive when primitive.isBoolean() -> writer.value(primitive.getAsBoolean());
			case JsonPrimitive primitive when primitive.isNumber() -> writer.value(primitive.getAsNumber());
			case JsonPrimitive primitive -> {
				if(Settings.UNICODE_ESCAPING.getValue()) {
					writeStringWithUnicodeEscape(writer, json.getAsString());
				} else {
					writer.value(json.getAsString());
				}
			}
			
			case JsonObject object -> {
				Map<String, JsonElement> map = object.asMap();
				
				writer.beginObject();
				
				map.forEach((key, value) -> {
					try {
						writer.name(key);
						writeJsonElement(writer, value);
					} catch (IOException e) { throw new UncheckedIOException(e); }
				});
				
				writer.endObject();
			}
			
			case JsonArray array -> {
				writer.beginArray();
				
				for(JsonElement value : array) {
					writeJsonElement(writer, value);
				}
				
				writer.endArray();
			}
			
			default -> writer.nullValue();
			}
		} catch (UncheckedIOException e) { throw e.getCause(); }
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
		try                   { return file.getCanonicalPath(); }
		catch (IOException e) { return file.getAbsolutePath(); }
	}
	
	public long lastModified() { return file.lastModified(); }
}

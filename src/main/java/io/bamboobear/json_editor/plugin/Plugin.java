package io.bamboobear.json_editor.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.lang.Language;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.util.JsonArrayUtilities;
import io.bamboobear.json_editor.util.JsonObjectUtilities;

public abstract class Plugin {
	public static final int LATEST_FORMAT_VERSION = 1;
	
	private final String id;
	private final TranslatableText name;
	private final TranslatableText description;
	private final String[] authors;
	private final String[] credits;
	private final int formatVersion;
	
	public static final String PLUGIN_ID_REGEX = "[a-z][a-z0-9_]{1,63}";
	
	/**
	 * @param id the ID of the plugin
	 * 
	 * @throws PluginLoadingException 
	 */
	protected Plugin(String id, JsonObject pluginJson) throws PluginLoadingException{
		if(id == null) {
			throw new PluginLoadingException("\"id\" is null", new NullPointerException());
		}
		if(!id.matches("^" + PLUGIN_ID_REGEX + "$")) {
			throw new PluginLoadingException(new IllegalArgumentException("\"id=\"" + id + ", \"id\" must start with a lowercase letter (a-z) and can only contain lowercase letters (a-z), numbers (0-9), and underscores (_). It must be between 2 and 64 characters long."));
		}
		this.id = id;
		
		name = TranslatableText.of(JsonObjectUtilities.getString(pluginJson, "display_name", String.format("%s.name", id)));
		authors = loadArrayOrString(pluginJson, "authors");
		credits = loadArrayOrString(pluginJson, "credits");
		
		String desc = JsonObjectUtilities.getString(pluginJson, "description", null);
		description = desc == null ? TranslatableText.EMPTY : TranslatableText.of(desc);
		
		formatVersion = JsonObjectUtilities.getInteger(pluginJson, "plugin_format", Plugin.LATEST_FORMAT_VERSION);
	}
	
	public String id() {
		return id;
	}
	
	public TranslatableText getName() {
		return name;
	}
	
	public TranslatableText getDescription() {
		return description;
	}
	
	public String[] getAuthors() {
		return authors;
	}
	
	public String[] getCredits() {
		return credits;
	}
	
	public int getFormatVersion() {
		return formatVersion;
	}
	
	private String[] loadArrayOrString(JsonObject object, String name) {
		var array = JsonObjectUtilities.getJsonArray(object, name);
		if(array == null) {
			String str = JsonObjectUtilities.getString(object, name, null);
			return str == null ? new String[0] : new String[] {str};
		}
		
		if(array.size() == 0) {
			return new String[0];
		}
		
		return JsonArrayUtilities.getStrings(array);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Plugin plugin) {
			return this.id.equals(plugin.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public abstract File getFile();
	
	public abstract Map<String, JsonElement> loadLanguages();
	
	static final Plugin createDirectoryPlugin(String id, JsonObject pluginJson, File file) {
		return new DirectoryPlugin(id, pluginJson, file);
	}
	
	static final Plugin createZipPlugin(String id, JsonObject pluginJson, File zipFile) {
		return new ZipPlugin(id, pluginJson, zipFile);
	}
	
	public static enum PluginType {
		DIRECTORY("directory", Plugin::createDirectoryPlugin),
		ZIP("zip", Plugin::createZipPlugin);
		
		private final String name;
		private final PluginCreator creator;
		
		private PluginType(String name, PluginCreator creator) {
			this.name = name;
			this.creator = creator;
		}
		
		public String getName() {
			return name;
		}
		
		public Plugin createPlugin(String id, JsonObject pluginJson, File file) {
			return creator.create(id, pluginJson, file);
		}
		
		@FunctionalInterface
		public interface PluginCreator{
			Plugin create(String id, JsonObject pluginJson, File file);
		}
	}
	
	private static class DirectoryPlugin extends Plugin {
		private final File dir;
		
		protected DirectoryPlugin(String id, JsonObject pluginJson, File dir) {
			super(id, pluginJson);
			if(!dir.isDirectory()) { //TODO loading warning
				throw new PluginLoadingException(String.format("%s was not found.", dir.getAbsolutePath()));
			}
			this.dir = dir;
		}

		@Override
		public File getFile() {
			return dir;
		}

		@Override
		public Map<String, JsonElement> loadLanguages() {
			Map<String, JsonElement> map = new HashMap<String, JsonElement>();
			
			File langDir = new File(dir, "lang");
			if(langDir.isDirectory()) {
				File[] files = langDir.listFiles();
				
				Pattern pattern = Pattern.compile("^(" + Language.LANGUAGE_ID_REGEX + ")\\.json$");
				
				for(File file : files) {
					Matcher matcher = pattern.matcher(file.getName());
					
					if(file.isFile() && matcher.find()) {
						String id = matcher.group(1);
						JsonFile jsonFile = new JsonFile(file);
						
						try {
							JsonElement root = jsonFile.load();
							map.put(id, root);
						} catch (Exception e) { //TODO loading warning
							e.printStackTrace();
						}
					}
				}
			}
			return map;
		}
	}
	
	private static class ZipPlugin extends Plugin {
		private final File file;
		
		protected ZipPlugin(String id, JsonObject pluginJson, File zipFile) {
			super(id, pluginJson);
			this.file = zipFile;
			try (ZipFile test = new ZipFile(this.file)){
			} catch (Exception e) { //TODO loading warning
				throw new PluginLoadingException(e);
			}
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public Map<String, JsonElement> loadLanguages() {
			Map<String, JsonElement> map = new HashMap<String, JsonElement>();
			
			try (ZipFile zipFile = new ZipFile(file)) {
				Pattern pattern = Pattern.compile(".*[/](" + Language.LANGUAGE_ID_REGEX + ")\\.json");
				
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				ZipEntry entry;
				while(entries.hasMoreElements()) {
					entry = entries.nextElement();
					if(entry.getName().startsWith("lang/")) {
						Matcher matcher = pattern.matcher(entry.getName());
						if(matcher.find()) {
							String id = matcher.group(1);
							try {
								JsonElement root = JsonFile.loadByInputStream(getEntryInputStream(zipFile, entry));
								map.put(id, root);
							} catch (Exception e) { //TODO loading warning
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e) { //TODO loading warning
				e.printStackTrace();
			}
			
			return map;
		}
		
		private InputStream getEntryInputStream(ZipFile zipFile, ZipEntry entry) throws IOException {
			return zipFile.getInputStream(entry);
		}
	}
}

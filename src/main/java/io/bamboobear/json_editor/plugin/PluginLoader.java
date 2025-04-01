package io.bamboobear.json_editor.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonObject;

import io.bamboobear.json_editor.JsonFile;
import io.bamboobear.json_editor.plugin.Plugin.PluginType;

public class PluginLoader {
	private static final File pluginDir = new File("plugins");
	
	private static final String PLUGIN_JSON_NAME = "plugin.json";
	
	private PluginLoader() {
	}
	
	public static synchronized void loadPlugins() {
		if(!pluginDir.isDirectory()) {
			pluginDir.mkdir();
			return;
		}
		
		findPlugins(pluginDir);
	}
	
	private static void findPlugins(File dir) {
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				checkDirectory(file);
			} else if(file.isFile() && file.getName().endsWith(".zip")) {
				checkZipFile(file);
			}
		}
	}
	
	private static void checkDirectory(File dir) {
		File pluginJsonFile = new File(dir, PLUGIN_JSON_NAME);
		if(pluginJsonFile.isFile()) {
			try {
				JsonFile pluginJson = new JsonFile(pluginJsonFile);
				JsonObject root = pluginJson.load().getAsJsonObject();
				String id = root.get("id").getAsString();
				Plugins.addPlugin(PluginType.DIRECTORY, id, root, dir);
			} catch (Exception e) { //TODO loading warning
				e.printStackTrace();
			}
		} else {
			findPlugins(dir);
		}
	}
	
	private static void checkZipFile(File file) {
		try (ZipFile zip = new ZipFile(file)){
			ZipEntry pluginJson = zip.getEntry(PLUGIN_JSON_NAME);
			InputStream is = zip.getInputStream(pluginJson);
			JsonObject root = JsonFile.loadByInputStream(is).getAsJsonObject();
			String id = root.get("id").getAsString();
			Plugins.addPlugin(PluginType.ZIP, id, root, file);
		} catch (Exception e) { //TODO loading warning
			e.printStackTrace();
		}
	}
}

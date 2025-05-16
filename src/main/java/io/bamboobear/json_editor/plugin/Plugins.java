package io.bamboobear.json_editor.plugin;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import io.bamboobear.json_editor.plugin.Plugin.PluginType;

public final class Plugins {
	private static final ArrayList<Plugin> PLUGINS = new ArrayList<>();
	
	private Plugins() {}
	
	static Plugin addPlugin(PluginType type, String id, JsonObject pluginJson, File file) {
		return addPlugin(type.createPlugin(id, pluginJson, file));
	}
	
	private static Plugin addPlugin(Plugin plugin) {
		if(PLUGINS.contains(plugin)) throw new PluginLoadingException(String.format("Plugin ID conflict encountered: %s", plugin.id()));
		PLUGINS.add(plugin);
		return plugin;
	}
	
	/**
	 * @return the plugin; {@code null} if not found*/
	public static Plugin getPlugin(String id) {
		for(Plugin plugin : PLUGINS) {
			if(id.equals(plugin.id())) return plugin;
		}
		
		return null;
	}

	public static Plugin[] getPlugins() { return PLUGINS.toArray(new Plugin[PLUGINS.size()]); }
	
	public static int getPluginCount() { return PLUGINS.size(); }
}

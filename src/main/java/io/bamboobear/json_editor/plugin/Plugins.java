package io.bamboobear.json_editor.plugin;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import io.bamboobear.json_editor.plugin.Plugin.PluginType;

public final class Plugins {
	private static final ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	
	private Plugins() {
	}
	
	static Plugin addPlugin(PluginType type, String id, JsonObject pluginJson, File file) {
		return addPlugin(type.createPlugin(id, pluginJson, file));
	}
	
	private static Plugin addPlugin(Plugin plugin) {
		if(plugins.contains(plugin)) {
			throw new PluginLoadingException(String.format("Plugin ID conflict encountered: %s", plugin.id()));
		}
		plugins.add(plugin);
		return plugin;
	}
	
	public static Plugin[] getPlugins() {
		return plugins.toArray(new Plugin[plugins.size()]);
	}
	
	/**
	 * @return the plugin; {@code null} if not found*/
	public static Plugin getPlugin(String id) {
		for(Plugin plugin : plugins) {
			if(id.equals(plugin.id())) {
				return plugin;
			}
		}
		
		return null;
	}
	
	public static int getPluginCount() {
		return plugins.size();
	}
}

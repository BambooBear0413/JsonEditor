package io.bamboobear.json_editor.plugin;

public class PluginLoadingException extends RuntimeException {
	private static final long serialVersionUID = 5528561968878718778L;

	public PluginLoadingException() {}
	
	public PluginLoadingException(String message) { super(message); }
	
	public PluginLoadingException(Throwable cause) { super(cause); }
	
	public PluginLoadingException(String message, Throwable cause) { super(message, cause); }
}

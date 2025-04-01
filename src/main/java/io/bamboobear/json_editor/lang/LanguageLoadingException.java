package io.bamboobear.json_editor.lang;

public class LanguageLoadingException extends RuntimeException {
	private static final long serialVersionUID = -2203838893530920692L;

	public LanguageLoadingException() {
		super();
	}
	
	public LanguageLoadingException(String message) {
		super(message);
	}
	
	public LanguageLoadingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LanguageLoadingException(Throwable cause) {
		super(cause);
	}
}

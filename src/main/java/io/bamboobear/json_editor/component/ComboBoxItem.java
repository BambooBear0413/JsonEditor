package io.bamboobear.json_editor.component;

import io.bamboobear.json_editor.lang.TranslatableText;

public class ComboBoxItem {
	private TranslatableText text;
	private String id;
	
	private boolean showID;
	
	public ComboBoxItem(TranslatableText text, String id) {
		this.text = text;
		this.id = id;
	}
	
	public String getDisplayText() {
		String displayText = text.getDisplayText();
		
		if(showID) {
			displayText = String.format("%s (%s)", displayText, id);
		}
		
		return displayText;
	}
	
	public TranslatableText getText() {
		return text;
	}
	
	public ComboBoxItem showID() {
		showID = true;
		return this;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return getDisplayText();
	}
}

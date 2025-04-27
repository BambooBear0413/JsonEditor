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
	
	public TranslatableText getText() {
		return text;
	}
	
	public ComboBoxItem showID() {
		if(showID) {
			return this;
		}
		
		showID = true;
		text.append(" (%s)".formatted(id));
		return this;
	}
	
	public String getId() {
		return id;
	}
}

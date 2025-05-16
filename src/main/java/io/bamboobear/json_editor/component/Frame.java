package io.bamboobear.json_editor.component;

import javax.swing.JFrame;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	private TranslatableText title;
	
	public Frame() { this(null); }
	
	public Frame(TranslatableText title) {
		setTitle(title);
	}
	
	public void setTitle(TranslatableText title) {
		this.title = title;
		refreshTitle();
	}
	
	public void refreshTitle() { super.setTitle(getTitle()); }
	
	@Override public String getTitle() { return (title == null) ? super.getTitle() : title.getDisplayText(); }
}

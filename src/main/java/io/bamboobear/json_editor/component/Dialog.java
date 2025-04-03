package io.bamboobear.json_editor.component;

import java.awt.Frame;

import javax.swing.JDialog;

import io.bamboobear.json_editor.lang.TranslatableText;

public class Dialog extends JDialog{
	private static final long serialVersionUID = 2535283717998499406L;
	
	private TranslatableText title;
	
	public Dialog() {
	}
	
	public Dialog(Frame owner) {
		super(owner);
	}
	
	public Dialog(Frame owner, boolean modal) {
		super(owner, modal);
	}
	
	public Dialog(Frame owner, TranslatableText title) {
		super(owner);
		setTitle(title);
	}
	
	public Dialog(Frame owner, TranslatableText title, boolean modal) {
		super(owner, modal);
		setTitle(title);
	}
	
	public void setTitle(TranslatableText title) {
		this.title = title;
		refreshTitle();
	}
	
	public void refreshTitle() {
		super.setTitle(getTitle());
	}
	
	@Override
	public String getTitle() {
		return title == null ? super.getTitle() : title.getDisplayText();
	}
}

package io.bamboobear.json_editor.component;

import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

import io.bamboobear.json_editor.lang.TranslatableText;

public class Frame extends JFrame {
	private static final long serialVersionUID = -4845753413738634748L;
	
	private TranslatableText title;
	
	public Frame() {
		super();
	}
	
	public Frame(GraphicsConfiguration gc) {
		super(gc);
	}
	
	public Frame(TranslatableText title) {
		super();
		setTitle(title);
	}
	
	public Frame(TranslatableText title, GraphicsConfiguration gc) {
		super(gc);
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
		return (title == null) ? super.getTitle() : title.getDisplayText();
	}
}

package io.bamboobear.json_editor.component;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import io.bamboobear.json_editor.lang.TranslatableText;

public class MenuItem extends JMenuItem{
	private static final long serialVersionUID = -1873885405693561439L;

	private TranslatableText text;

	public MenuItem() {
	}
	
	public MenuItem(TranslatableText text) {
		setText(text);
	}
	
	public MenuItem(Icon icon) {
		super(icon);
	}
	
	public MenuItem(Action action) {
		super(action);
	}
	
	public MenuItem(TranslatableText text, Icon icon) {
		super(icon);
		setText(text);
	}
	
	public MenuItem(TranslatableText text, int mnemonic) {
		super("", mnemonic);
		setText(text);
	}
	
	@Override
	public String getText() {
		return (text == null) ? super.getText() : text.getDisplayText();
	}
	
	public void setText(TranslatableText text) {
		this.text = text;
	}
}

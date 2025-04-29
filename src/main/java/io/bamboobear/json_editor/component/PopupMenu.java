package io.bamboobear.json_editor.component;

import javax.swing.JPopupMenu;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class PopupMenu extends JPopupMenu{
	private TranslatableText label;
	
	public PopupMenu() { this(null); }
	
	public PopupMenu(TranslatableText label) {
		setLabel(label);
	}
	
	public void setLabel(TranslatableText label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label == null ? super.getLabel() : label.getDisplayText();
	};
}

package io.bamboobear.json_editor.component;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class TextArea extends JTextArea{
	private boolean useCustomFont = false;
	private TranslatableText displayText;
	private boolean updateTextWhenRepaint;
	
	public TextArea()                      { this(""); }
	public TextArea(String text)           { this(text, true); }
	public TextArea(TranslatableText text) { this(text, true); }
	
	protected TextArea(TranslatableText text, boolean popupMenu) {
		this(text.getDisplayText(), popupMenu);
		this.displayText = text;
		this.updateTextWhenRepaint = true;
		setEditable(false);
	}
	
	protected TextArea(String text, boolean popupMenu) {
		super(text);
		if(popupMenu) {
			setComponentPopupMenu(createPopupMenu());
		}
	}
	
	private PopupMenu createPopupMenu() {
		PopupMenu popupMenu = new PopupMenu();
		
		MenuItem copyItem = new MenuItem(TranslatableText.create("json_editor.copy"));
		copyItem.addActionListener(e -> copy());
		popupMenu.add(copyItem);
		
		MenuItem cutItem = new MenuItem(TranslatableText.create("json_editor.cut"));
		cutItem.addActionListener(e -> cut());
		popupMenu.add(cutItem);
		
		MenuItem pasteItem = new MenuItem(TranslatableText.create("json_editor.paste"));
		pasteItem.addActionListener(e -> paste());
		popupMenu.add(pasteItem);
		
		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				boolean b = (getSelectionStart() != getSelectionEnd());
				copyItem.setEnabled(b);
				cutItem.setEnabled(b);
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		
		return popupMenu;
	}
	
	public void setText(String t) {
		updateTextWhenRepaint = false;
		displayText = null;
		super.setText(t);
	}
	
	public void setEditable(boolean b) {
		super.setEditable(b);
		if(displayText != null) {
			updateTextWhenRepaint = !isEditable();
		}
	}
	
	public void setDisplayText(TranslatableText text) {
		super.setText(text.getDisplayText());
		updateTextWhenRepaint = !isEditable();
	}
	
	public TranslatableText getDisplayText() {
		return displayText;
	}
	
	@Override
	public void setFont(Font f) {
		useCustomFont = true;
		super.setFont(f);
	}
	
	@Override
	public Font getFont() {
		return useCustomFont ? super.getFont() : Main.getFont();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if(this.displayText != null && updateTextWhenRepaint) {
			String displayText = this.displayText.getDisplayText();
			if(!super.getText().equals(displayText)) {
				super.setText(displayText);
			}
		}
		super.paintComponent(g);
	}
}

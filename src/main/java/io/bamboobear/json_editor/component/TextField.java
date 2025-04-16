package io.bamboobear.json_editor.component;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class TextField extends JTextField{
	private boolean useCustomFont = false;
	private TranslatableText displayText;
	private boolean updateTextWhenRepaint;
	
	public TextField()                      { this(""); }
	public TextField(String text)           { this(text, true); }
	public TextField(TranslatableText text) { this(text, true); }
	
	protected TextField(TranslatableText text, boolean popupMenu) {
		this(text.getDisplayText(), popupMenu);
		this.displayText = text;
		this.updateTextWhenRepaint = true;
		setEditable(false);
	}
	
	protected TextField(String text, boolean popupMenu) {
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
	
	@Override
	public void setText(String t) {
		updateTextWhenRepaint = false;
		super.setText(t);
	}
	
	@Override
	public void setEditable(boolean b) {
		super.setEditable(b);
		displayText = null;
	}
	
	public void setDisplayText(TranslatableText text) {
		super.setText(text.getDisplayText());
		this.displayText = text;
		setEditable(false);
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

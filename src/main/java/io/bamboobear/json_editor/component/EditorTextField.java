package io.bamboobear.json_editor.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonComponent.State;
import io.bamboobear.json_editor.component.json.JsonNullComponent;
import io.bamboobear.json_editor.component.json.JsonObjectComponent;
import io.bamboobear.json_editor.component.json.JsonPrimitiveComponent;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public final class EditorTextField extends TextField implements EditorInputField{
	private volatile String beforeChange;
	
	private String value;
	
	private final JsonComponent<?> json;
	private final Type type;
	
	public EditorTextField(JsonComponent<?> json, Type type) {
		this("", json, type);
	}
	
	public EditorTextField(String value, JsonComponent<?> json, Type type) {
		super(value == null ? "" : value);
		beforeChange = getText();
		
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				beforeChange = getValue();
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				fireTextChange(false);
			}
		});
		this.json = Objects.requireNonNull(json, "json component is null");
		this.type = Objects.requireNonNull(type, "type is null");
		checkArgument(json, type);
	}
	
	public EditorTextField(String value, TranslatableText text, JsonComponent<?> json, Type type) {
		super(text);
		this.value = value;
		this.json = Objects.requireNonNull(json, "json component is null");;
		this.type = Objects.requireNonNull(type, "type is null");;
		checkArgument(json, type);
	}
	
	private void checkArgument(JsonComponent<?> json, Type type) {
		if(type == Type.VALUE) {
			if(!(json instanceof JsonPrimitiveComponent<?> || json instanceof JsonNullComponent)) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	@Override
	public JComponent getAsComponent() {
		return this;
	}
	
	@Override
	public void paste() {
		fireTextChange(false);
		super.paste();
		fireTextChange(true);
	}
	
	@Override
	public void cut() {
		fireTextChange(false);
		super.cut();
		fireTextChange(true);
	}
	
	@Override
	public void setText(String t) {
		super.setText(translateInputValue(t));
	}
	
	@Override
	public void setValue(String value) {
		this.setText(value);
	}
	
	@Override
	public String getValue() {
		String str = (getDisplayText() == null) ? getText() : value;
		return translateOutputValue(str);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public JsonComponent<?> getJsonComponent() {
		return json;
	}
	
	@Override
	public Color getBackground() {
		try {
			return getState().getBackground();
		} catch (Exception e) {
			return super.getBackground();
		}
	}
	
	@Override
	public Color getForeground() {
		try {
			return getState().getForeground();
		} catch (Exception e) {
			return super.getForeground();
		}
	}
	
	@Override
	public Color getCaretColor() {
		return getForeground();
	}
	
	private State getState() {
		return switch(type) {
		case KEY -> {
			if(json.getParentElement() instanceof JsonObjectComponent object && isEditable()) {
				yield object.checkKey(getValue()) ? State.WARNING : State.NORMAL;
			}
			yield State.NON_EDITABLE;
		}
		case VALUE -> {
			if(json instanceof JsonPrimitiveComponent<?> primitive) {
				yield primitive.getValueComponentState();
			}
			throw new IllegalStateException();
		}
		};
	}
	
	private void doParentRepaint() {
		Container parent = getParent();
		if(parent != null) {
			parent.repaint();
		}
	}
	
	private void fireTextChange(boolean repaint) {
		String afterChange = getValue();
		if(!beforeChange.equals(afterChange)) {
			addChange(json, beforeChange, afterChange);
			beforeChange = afterChange;
			
			if(repaint) {
				SwingUtilities.invokeLater(() -> {
					doParentRepaint();
				});
			}
		}
	}
}

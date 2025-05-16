package io.bamboobear.json_editor.component;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComponent;

import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonComponent.State;
import io.bamboobear.json_editor.component.json.JsonPrimitiveComponent;

@SuppressWarnings("serial")
public final class EditorComboBox extends ComboBox implements EditorInputField{
	private volatile String beforeChange;
	
	private final JsonComponent<?> json;

	public EditorComboBox(ComboBoxItem[] items, JsonComponent<?> json) {
		this(items, false, json);
	}
	
	private EditorComboBox(ComboBoxItem[] items, boolean isEditable, JsonComponent<?> json) {
		super(items);
		
		this.json = json;
		
		setEditable(isEditable);
		
		addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) { beforeChange = getValue(); }
			@Override public void focusLost(FocusEvent e)   { fireValueChange(); }
		});
	}
	
	@Override public JComponent getAsComponent() { return this; }
	
	@Override public void setValue(String str) { super.setValue(translateInputValue(str)); }
	
	@Override public String getValue() { return translateOutputValue(super.getValue()); }

	private void fireValueChange() {
		String afterChange = getValue();
		if(beforeChange != null && !beforeChange.equals(afterChange)) {
			addChange(json, beforeChange, afterChange);
			beforeChange = afterChange;
		}
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
	
	private State getState() {
		if(json instanceof JsonPrimitiveComponent<?> primitive) {
			return primitive.getValueComponentState();
		}
		throw new IllegalStateException();
	}
	
	@Override public Type getType() { return Type.VALUE; }
}

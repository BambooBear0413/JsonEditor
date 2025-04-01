package io.bamboobear.json_editor.component.json;

import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorComboBox;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.EditorTextField.Type;

@SuppressWarnings("serial")
public final class JsonNumberComponent extends JsonPrimitiveComponent<Number>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("number");
	
	public static final String TYPE_ID = "number";
	
	JsonNumberComponent() {
		super(ICON);
	}
	
	@Override
	protected JComponent createValueComponent() {
		return new EditorTextField("0", this, Type.VALUE);
	}

	@Override
	public void setValue(Number value) {
		if(valueComponent instanceof EditorTextField textField) {
			if(textField.isEditable()) {
				textField.setText(String.valueOf(value));
			}
		} else if(valueComponent instanceof EditorComboBox comboBox) {
			comboBox.setValue(String.valueOf(value));
		}
	}

	@Override
	public boolean setValue(JsonPrimitive value) {
		if(value.isNumber()) {
			setValue(value.getAsNumber());
			return true;
		}
		return false;
	}
	
	private static Number getAsNumber(String value) throws NumberFormatException{
		return new BigDecimal(value);
	}

	@Override
	public Number getValue() throws NumberFormatException {
		if(valueComponent instanceof EditorTextField textField) {
			try {
				return getAsNumber(textField.getValue());
			} catch (NumberFormatException e) {
				return 0; // default value
			}
		} else if(valueComponent instanceof EditorComboBox comboBox) {
			try {
				return getAsNumber(comboBox.getValue());
			} catch (NumberFormatException e) {
				return 0; // default value
			}
		}
		throw new IllegalStateException("It's shouldn't happen :(");
	}

	@Override
	public JsonPrimitive getAsJsonElement() {
		return new JsonPrimitive(getValue());
	}
	
	@Override
	public State getValueComponentState() {		
		try {
			if(valueComponent instanceof EditorTextField textField && textField.isEditable()) {
				getAsNumber(textField.getValue());
			} else if(valueComponent instanceof EditorComboBox comboBox && comboBox.isEditable()) {
				getAsNumber(comboBox.getValue());
			}
		} catch (NumberFormatException e) {
			return State.ERROR;
		}
		
		return State.NORMAL;
	}
	
	@Override
	public String getTypeID() {
		return TYPE_ID;
	}
}

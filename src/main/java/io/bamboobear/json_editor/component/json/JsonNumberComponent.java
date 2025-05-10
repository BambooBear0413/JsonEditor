package io.bamboobear.json_editor.component.json;

import java.math.BigDecimal;

import javax.swing.ImageIcon;

import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.component.EditorInputField.Type;
import io.bamboobear.json_editor.component.EditorTextField;

@SuppressWarnings("serial")
public final class JsonNumberComponent extends JsonPrimitiveComponent<Number>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("number");
	
	public static final String TYPE_ID = "number";
	
	JsonNumberComponent() {
		super(ICON);
	}
	
	@Override
	protected EditorInputField createValueComponent() {
		return new EditorTextField("0", this, Type.VALUE);
	}

	public void setValue(Number value) {
		valueComponent.setValue(String.valueOf(value));
	}
	
	public void setValueFromString(String value) {
		valueComponent.setValue(value);
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
		try {
			return getAsNumber(valueComponent.getValue());
		} catch (NumberFormatException e) {
			return 0; // default value
		}
	}

	@Override
	public JsonPrimitive getJsonElement() {
		return new JsonPrimitive(getValue());
	}
	
	@Override
	public State getValueComponentState() {		
		try {
			if(valueComponent.isEditable()) {
				getAsNumber(valueComponent.getValue());
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

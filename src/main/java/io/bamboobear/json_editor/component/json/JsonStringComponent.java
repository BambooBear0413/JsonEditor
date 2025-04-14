package io.bamboobear.json_editor.component.json;

import javax.swing.ImageIcon;

import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.component.EditorInputField.Type;
import io.bamboobear.json_editor.component.EditorTextField;

@SuppressWarnings("serial")
public final class JsonStringComponent extends JsonPrimitiveComponent<String>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("string");
	
	public static final String TYPE_ID = "string";
	
	JsonStringComponent() {
		super(ICON);
	}
	
	@Override
	protected EditorInputField createValueComponent() {
		return new EditorTextField("", this, Type.VALUE);
	}

	@Override
	public void setValue(String value) {
		valueComponent.setValue(value);
	}

	@Override
	public boolean setValue(JsonPrimitive value) {
		if(value.isString()) {
			setValue(value.getAsString());
			return true;
		}
		return false;
	}

	@Override
	public String getValue() {
		return valueComponent.getValue();
	}

	@Override
	public JsonPrimitive getJsonElement() {
		return new JsonPrimitive(getValue());
	}
	
	@Override
	public String getTypeID() {
		return TYPE_ID;
	}
}

package io.bamboobear.json_editor.component.json;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorComboBox;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.EditorTextField.Type;

@SuppressWarnings("serial")
public final class JsonStringComponent extends JsonPrimitiveComponent<String>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("string");
	
	public static final String TYPE_ID = "string";
	
	JsonStringComponent() {
		super(ICON);
	}
	
	@Override
	protected JComponent createValueComponent() {
		return new EditorTextField("", this, Type.VALUE);
	}

	@Override
	public void setValue(String value) {
		if(valueComponent instanceof EditorTextField textField) {
			if(textField.isEditable()) {
				textField.setText(value);
			}
		} else if (valueComponent instanceof EditorComboBox comboBox) {
			comboBox.setValue(value);
		}
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
		if(valueComponent instanceof EditorTextField textField) {
			return textField.getValue();
		} else if (valueComponent instanceof EditorComboBox comboBox) {
			return comboBox.getValue();
		}
		throw new IllegalStateException("It's shouldn't happen :(");
	}

	@Override
	public JsonPrimitive getAsJsonElement() {
		return new JsonPrimitive(getValue());
	}
	
	@Override
	public String getTypeID() {
		return TYPE_ID;
	}
}

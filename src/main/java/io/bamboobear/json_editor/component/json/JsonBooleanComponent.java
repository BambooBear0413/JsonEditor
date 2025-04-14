package io.bamboobear.json_editor.component.json;

import javax.swing.ImageIcon;

import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.component.EditorComboBox;
import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public final class JsonBooleanComponent extends JsonPrimitiveComponent<Boolean>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("boolean");
	
	public static final String TYPE_ID = "boolean";
	
	JsonBooleanComponent() {
		super(ICON);
	}
	
	@Override
	protected EditorInputField createValueComponent() {
		EditorComboBox comboBox = new EditorComboBox(new ComboBoxItem[] {
			new ComboBoxItem(TranslatableText.literal("False"), "false"),
			new ComboBoxItem(TranslatableText.literal("True"), "true")
		}, this);
		
		return comboBox;
	}

	@Override
	public void setValue(Boolean value) {
		valueComponent.setValue(value.toString());
	}
	
	@Override
	public boolean setValue(JsonPrimitive value) {
		if(value.isBoolean()) {
			setValue(value.getAsBoolean());
			return true;
		}
		return false;
	}

	@Override
	public Boolean getValue() {
		return Boolean.valueOf(valueComponent.getValue());
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

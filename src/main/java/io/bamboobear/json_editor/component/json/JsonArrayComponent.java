package io.bamboobear.json_editor.component.json;

import javax.swing.ImageIcon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.AddElementDialog;
import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;

@SuppressWarnings({"serial"})
public final class JsonArrayComponent extends JsonCompositeComponent<JsonArray> {
	private static final ImageIcon ICON = JsonComponent.getImageIcon("array");
	
	public static final String TYPE_ID = "array";
	
	JsonArrayComponent() {
		super(ICON);
	}

	@Override
	public JsonArray getJsonElement() {
		JsonArray array = new JsonArray();
		for(JsonComponent<?> element : getElements()) {
			array.add(element.getJsonElement());
		}
		return array;
	}

	@Override
	public synchronized boolean setValue(JsonElement value) {
		if(!value.isJsonArray()) {
			return false;
		}
		
		JsonArray array = value.getAsJsonArray();

		removeAllElements();
		
		for(JsonElement e : array) {
			JsonComponent<?> c = JsonComponent.createDefaultJsonComponent(e);
			addElement("", c.getTypeDisplayName(), c);
		}
		refresh();
		return true;
	}
	
	@Override
	public synchronized void fireAddElement() {
		ComboBoxItem[] items = new ComboBoxItem[] {
			createComboBoxItem(JsonArrayComponent.TYPE_ID),
			createComboBoxItem(JsonBooleanComponent.TYPE_ID),
			createComboBoxItem(JsonNumberComponent.TYPE_ID),
			createComboBoxItem(JsonObjectComponent.TYPE_ID),
			createComboBoxItem(JsonStringComponent.TYPE_ID),
			createComboBoxItem(JsonNullComponent.TYPE_ID)
		};
			
		AddElementDialog dialog = new AddElementDialog(new ComboBox(items));
			
		String typeID = dialog.showDialog();
		if(typeID != null) {
			JsonComponent<?> c = JsonComponent.createDefaultJsonComponent(typeID);
			addElement("", c.getTypeDisplayName(), c);
			int index = indexOf(c);
			Main.getEditor().addAddElementChange(c, this, index);
			refresh();
		}
	}
	
	private static ComboBoxItem createComboBoxItem(String typeID) {
		return new ComboBoxItem(JsonComponent.getTypeDisplayName(typeID), typeID);
	}
	
	@Override
	public String getTypeID() {
		return TYPE_ID;
	}
}

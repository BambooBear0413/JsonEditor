package io.bamboobear.json_editor.component.json;

import java.util.Map;

import javax.swing.ImageIcon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.AddElementDialog;
import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;
import io.bamboobear.json_editor.editor.Changes;

@SuppressWarnings({"serial"})
public final class JsonObjectComponent extends JsonCompositeComponent<JsonObject>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("object");
	
	public static final String TYPE_ID = "object";
	
	public JsonObjectComponent() { super(ICON); }

	@Override
	public JsonObject getJsonElement() {
		JsonObject object = new JsonObject();
		for(JsonComponent<?> element : getElements()) {
			object.add(element.getKey(), element.getJsonElement());
		}
		return object;
	}

	@Override
	public boolean setValue(JsonElement value) {
		if(!value.isJsonObject()) return false;
		
		removeAllElements();
		
		Map<String, JsonElement> map = value.getAsJsonObject().asMap();
		map.forEach((key, json) -> {
			JsonComponent<?> c = JsonComponent.createDefaultJsonComponent(json);
			addElement(key, c);
		});
		
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
			addElement("", c);
			int index = indexOf(c);
			Main.getEditor().addChange(new Changes.AddElementChange(c, this, index));
			refresh();
		}
	}
	
	private static ComboBoxItem createComboBoxItem(String typeID) {
		return new ComboBoxItem(JsonComponent.getTypeDisplayName(typeID), typeID);
	}
	
	public boolean checkKey(String key) {
		boolean hasFound = false;
		for(JsonComponent<?> element : getElements()) {
			if(element.getKey().equals(key)) {
				if(hasFound) return true;
				
				hasFound = true;
			}
		}
		return false;
	}
	
	@Override public String getTypeID() { return TYPE_ID; }
	
	public boolean containsElement(String key) { return indexOf(key) != -1; }
	
	public int indexOf(String key) {
		for(int i = 0; i < getElementCount(); i++) {
			if(getElement(i).getKey().equals(key)) return i;
		}
		return -1;
	}
	
	public JsonComponent<?> getElement(String key) {
		int i = indexOf(key);
		return (i == -1) ? null : getElement(i);
	}
	
	public String getKey(JsonComponent<?> element) {
		int i = indexOf(element);
		return (i == -1) ? null : getElement(i).getKey();
	}
}

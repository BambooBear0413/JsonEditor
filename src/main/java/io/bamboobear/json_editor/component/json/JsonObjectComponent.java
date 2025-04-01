package io.bamboobear.json_editor.component.json;

import java.util.Map;

import javax.swing.ImageIcon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.AddElementDialog;
import io.bamboobear.json_editor.component.ComboBox;
import io.bamboobear.json_editor.component.ComboBoxItem;

@SuppressWarnings({"serial"})
public final class JsonObjectComponent extends JsonCompositeComponent<JsonObject>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("object");
	
	public static final String TYPE_ID = "object";
	
	public JsonObjectComponent() {
		super(ICON);
	}

	@Override
	public JsonObject getAsJsonElement() {
		JsonObject object = new JsonObject();
		for(KeyValuePair kvp : content) {
			object.add(kvp.getKey(), kvp.getJsonComponent().getAsJsonElement());
		}
		return object;
	}

	@Override
	public boolean setValue(JsonElement value) {
		if(!value.isJsonObject()) {
			return false;
		}
		
		content.clear();
		
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
			createComboBoxItem(JsonStringComponent.TYPE_ID)
		};
			
		AddElementDialog dialog = new AddElementDialog(new ComboBox(items));
			
		String typeID = dialog.showDialog();
		if(typeID != null) {
			JsonComponent<?> c = JsonComponent.createDefaultJsonComponent(typeID);
			addElement("", c);
			int index = indexOf(c);
			KeyValuePair kvp = content.get(index);
			Main.getEditor().addAddElementChange(kvp.getKeyField(), kvp.getJsonComponent(), this, index);
			refresh();
		}
	}
	
	private static ComboBoxItem createComboBoxItem(String typeID) {
		return new ComboBoxItem(JsonComponent.getTypeDisplayName(typeID), typeID);
	}
	
	public State checkKey(String key) {
		boolean hasFound = false;
		for(KeyValuePair pair : content) {
			if(pair.getKey().equals(key)) {
				if(hasFound) {
					return State.WARNING;
				} else {
					hasFound = true;
				}
			}
		}
		return State.NORMAL;
	}
	
	@Override
	public String getTypeID() {
		return TYPE_ID;
	}
	
	public boolean containsElement(String key) {
		return indexOf(key) != -1;
	}
	
	public int indexOf(String key) {
		for(int i = 0; i < content.size(); i++) {
			if(content.get(i).getKey().equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	public JsonComponent<?> getElement(String key) {
		int i = indexOf(key);
		return (i == -1) ? null : content.get(i).getJsonComponent();
	}
	
	public String getKey(JsonComponent<?> element) {
		int i = indexOf(element);
		return (i == -1) ? null : content.get(i).getKey();
	}
}

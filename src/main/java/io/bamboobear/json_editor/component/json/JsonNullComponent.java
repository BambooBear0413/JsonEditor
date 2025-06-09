package io.bamboobear.json_editor.component.json;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import io.bamboobear.json_editor.component.EditorInputField.Type;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public final class JsonNullComponent extends JsonComponent<JsonNull>{
	private static final ImageIcon ICON = JsonComponent.getImageIcon("null");
	
	public static final String TYPE_ID = "null";
	
	private final EditorTextField valueComponent;
	
	JsonNullComponent() {
		super(ICON);
		
		this.valueComponent = new EditorTextField("null", TranslatableText.literal("Null"), this, Type.VALUE);
		
		this.valueComponent.setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));
		
		setLayout(new ComponentLayout(valueComponent));
	}
	
	@Override public JsonNull getJsonElement() { return JsonNull.INSTANCE; }
	
	@Override public boolean setValue(JsonElement value) { return true; }
	
	@Override public String getTypeID() { return TYPE_ID; }
}

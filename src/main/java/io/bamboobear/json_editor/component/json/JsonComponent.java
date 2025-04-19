package io.bamboobear.json_editor.component.json;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.ResourceImageLoader;
import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.Label;
import io.bamboobear.json_editor.component.json.JsonCompositeComponent.KeyValuePair;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public sealed abstract class JsonComponent<T extends JsonElement> extends JPanel permits JsonCompositeComponent, JsonPrimitiveComponent{
	protected Label icon;
	
	protected final Button removeButton;
	
	private JsonCompositeComponent<?> parent;
	
	protected static final int DEFAULT_HEIGHT = 40;
	
	JsonComponent(Icon icon) {
		this.icon = new Label(icon);
		this.icon.setBorder(new LineBorder(Color.GRAY, 1));
		
		removeButton = new Button(TranslatableText.literal("-"));
		removeButton.setForeground(Color.RED);
		removeButton.setFont(new Font("Default", Font.BOLD, 20));
		removeButton.setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));
		removeButton.addActionListener(e -> remove());
	}
	
	public abstract void updateGUI();
	
	public void remove() {
		JsonCompositeComponent<?> oldParent = getParentElement();
		int index = oldParent.indexOf(this);
		KeyValuePair kvp = oldParent.removeElement(this);
		if(kvp != null) {
			Main.getEditor().addRemoveElementChange(kvp.getKeyField(), this, oldParent, index);
		}
		oldParent.refresh();
	}
	
	public final void refresh() {
		updateGUI();
		revalidate();
		repaint();
	}
	
	public final Label getIconLabel() {
		return icon;
	}
	
	public abstract T getAsJsonElement();
	
	public abstract boolean setValue(JsonElement value);
	
	protected abstract int getGridHeight();
	
	public abstract String getTypeID();
	
	public TranslatableText getTypeDisplayName() {
		return getTypeDisplayName(getTypeID());
	}
	
	public static TranslatableText getTypeDisplayName(String typeID) {
		return TranslatableText.of("json_editor.type." + typeID);
	}
	
	public void setRemovable(boolean removable) {
		removeButton.setEnabled(removable);
	}
	
	public boolean isRemovable() {
		return removeButton.isEnabled();
	}
	
	protected void setParentElement(JsonCompositeComponent<?> parent) {
		this.parent = parent;
	}
	
	public JsonCompositeComponent<?> getParentElement() {
		return parent;
	}
	
	public abstract JsonCompositeComponent<?> getRootElement();
	
	protected static final ImageIcon getImageIcon(String name) {
		return ResourceImageLoader.getImageIcon(String.format("json/%s.png", name), DEFAULT_HEIGHT, DEFAULT_HEIGHT, Image.SCALE_SMOOTH);
	}
	
	public static final JsonComponent<?> createDefaultJsonComponent(String typeID) { //TODO java-21
		return switch(typeID) {
		case JsonArrayComponent.TYPE_ID -> new JsonArrayComponent();
		case JsonBooleanComponent.TYPE_ID -> new JsonBooleanComponent();
		case JsonNumberComponent.TYPE_ID -> new JsonNumberComponent();
		case JsonObjectComponent.TYPE_ID -> new JsonObjectComponent();
		default -> new JsonStringComponent();
		};
	}
	
	public static final JsonComponent<?> createDefaultJsonComponent(JsonElement value) {
		JsonComponent<?> c;
		String typeID = getTypeIDFromJsonElement(value);
		if(typeID == null) {
			c = createDefaultJsonComponent(JsonStringComponent.TYPE_ID);
			c.setValue(new JsonPrimitive("null"));
		} else {
			c = createDefaultJsonComponent(typeID);
			c.setValue(value);
		}
		return c;
	}
	
	public static final String getTypeIDFromJsonElement(JsonElement json) {
		if(json.isJsonObject()) return JsonObjectComponent.TYPE_ID;
		if(json.isJsonArray()) return JsonArrayComponent.TYPE_ID;
		
		if(json.isJsonPrimitive()) {
			JsonPrimitive jp = json.getAsJsonPrimitive();
			if(jp.isBoolean()) return JsonBooleanComponent.TYPE_ID;
			if(jp.isNumber()) return JsonNumberComponent.TYPE_ID;
		}
		return JsonStringComponent.TYPE_ID;
	}
	
	public static enum State {
		NORMAL(Color.BLACK, Color.WHITE),
		WARNING(Color.BLACK, Color.YELLOW),
		ERROR(Color.WHITE, Color.RED),
		NON_EDITABLE(Color.BLACK, Color.LIGHT_GRAY);
		
		private Color foreground;
		private Color background;
		
		State(Color foreground, Color background) {
			this.foreground = foreground;
			this.background = background;
		}
		
		public Color getForeground() {
			return foreground;
		}
		
		public Color getBackground() {
			return background;
		}
	}
}

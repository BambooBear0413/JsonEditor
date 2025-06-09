package io.bamboobear.json_editor.component.json;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.ResourceImageLoader;
import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.component.EditorInputField.Type;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.Label;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public sealed abstract class JsonComponent<T extends JsonElement> extends JPanel permits JsonCompositeComponent, JsonPrimitiveComponent, JsonNullComponent{
	protected final Label icon;
	
	protected final Button removeButton;
	protected final EditorTextField keyField = new EditorTextField(this, Type.KEY);
	
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
	
	public void remove() {
		JsonCompositeComponent<?> oldParent = getParentElement();
		int index = oldParent.indexOf(this);
		if(index < 0) return;
		
		oldParent.removeElement(this);
		Main.getEditor().addRemoveElementChange(this, oldParent, index);
		oldParent.refresh();
	}
	
	public final void refresh() {
		revalidate();
		repaint();
	}
	
	public final Label getIconLabel() { return icon; }
	
	public final String getKey() { return keyField.getValue(); }
	
	public abstract T getJsonElement();
	
	public final void setKey(String key) { keyField.setValue(key); }
	
	public final void setKeyDisplayText(TranslatableText text) { keyField.setDisplayText(text); }
	
	public abstract boolean setValue(JsonElement value);
	
	public abstract String getTypeID();
	
	public final TranslatableText getTypeDisplayName() { return getTypeDisplayName(getTypeID()); }
	
	public static TranslatableText getTypeDisplayName(String typeID) { return TranslatableText.create("json_editor.type." + typeID); }
	
	public void setRemovable(boolean removable) { removeButton.setEnabled(removable); }
	
	public boolean isRemovable() { return removeButton.isEnabled(); }
	
	protected void setParentElement(JsonCompositeComponent<?> parent) {
		this.parent = parent;
		
		keyField.setVisible(parent != null);
		removeButton.setVisible(parent != null);
		icon.setVisible(parent != null);
		
		if(parent instanceof JsonObjectComponent)     keyField.setValue(keyField.getValue());
		else if(parent instanceof JsonArrayComponent) keyField.setDisplayText(getTypeDisplayName());
	}
	
	public final JsonCompositeComponent<?> getParentElement() { return parent; }
	
	public final JsonCompositeComponent<?> getRootElement() {
		JsonComponent<?> component = this;
		JsonCompositeComponent<?> parent = getParentElement(); // is equivalent to component.getParentElement();
		
		while(parent != null) {
			component = parent;
			parent = component.getParentElement();
		}
		
		if(component instanceof JsonCompositeComponent<?> root) return root;
		throw new IllegalStateException("unexpected root element: " + component.getClass().getCanonicalName());
	}
	
	protected static final ImageIcon getImageIcon(String name) {
		return ResourceImageLoader.getImageIcon(String.format("json/%s.png", name), DEFAULT_HEIGHT, DEFAULT_HEIGHT, Image.SCALE_SMOOTH);
	}
	
	public static final JsonComponent<?> createDefaultJsonComponent(String typeID) {
		return switch(typeID) {
		case JsonArrayComponent.TYPE_ID -> new JsonArrayComponent();
		case JsonBooleanComponent.TYPE_ID -> new JsonBooleanComponent();
		case JsonNumberComponent.TYPE_ID -> new JsonNumberComponent();
		case JsonObjectComponent.TYPE_ID -> new JsonObjectComponent();
		case JsonStringComponent.TYPE_ID -> new JsonStringComponent();
		default -> new JsonNullComponent();
		};
	}
	
	public static final JsonComponent<?> createDefaultJsonComponent(JsonElement value) {
		JsonComponent<?> c;
		String typeID = getTypeIDFromJsonElement(value);
		if(typeID == null) {
			c = createDefaultJsonComponent(JsonNullComponent.TYPE_ID);
		} else {
			c = createDefaultJsonComponent(typeID);
			c.setValue(value);
		}
		return c;
	}
	
	public static final String getTypeIDFromJsonElement(JsonElement json) {
		return switch(json) {
		case JsonObject object -> JsonObjectComponent.TYPE_ID;
		case JsonArray  array  -> JsonArrayComponent.TYPE_ID;
		
		case JsonPrimitive jp when jp.isBoolean() -> JsonBooleanComponent.TYPE_ID;
		case JsonPrimitive jp when jp.isNumber()  -> JsonNumberComponent.TYPE_ID;
		case JsonPrimitive jp                     -> JsonStringComponent.TYPE_ID;
		
		default -> JsonNullComponent.TYPE_ID;
		};
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
		
		public Color getForeground() { return foreground; }
		public Color getBackground() { return background; }
	}
	
	class ComponentLayout implements LayoutManager {
		final int gap = 1;
		
		private final Label icon;
		private final EditorTextField key;
		private final EditorInputField value;
		private final Button removeButton;
		
		ComponentLayout(EditorInputField value) {
			this.icon = Objects.requireNonNull(JsonComponent.this.icon, "icon is null");
			this.key = Objects.requireNonNull(JsonComponent.this.keyField, "key is null");
			this.value = Objects.requireNonNull(value, "value is null");
			this.removeButton = Objects.requireNonNull(JsonComponent.this.removeButton, "remove button is null");
			
			JsonComponent.this.add(icon);
			JsonComponent.this.add(key);
			JsonComponent.this.add(value.getAsComponent());
			JsonComponent.this.add(removeButton);
		}
		
		@Override public void addLayoutComponent(String name, Component comp) { throw new UnsupportedOperationException(); }
		@Override public void removeLayoutComponent(Component comp) { throw new UnsupportedOperationException();}
		
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getMinimumSize); }
		}
		
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getPreferredSize); }
		}
		
		private Dimension createDimension(Container parent, Function<Component, Dimension> factory) {
			int iconSize = DEFAULT_HEIGHT;
			
			int width = iconSize * 2 + gap * 3 + factory.apply(key).width + factory.apply(value.getAsComponent()).width;
			int height = iconSize;
			
			Insets insets = parent.getInsets();
			width += (insets.left + insets.right);
			height += (insets.top + insets.bottom);
			return new Dimension(width, height);
		}
		
		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				
				int top = insets.top;
				int left = insets.left;
				int right = parent.getWidth() - insets.right;
				
				int iconSize = parent.getHeight() - (insets.top + insets.bottom);
				
				icon.setBounds(left, top, iconSize, iconSize);
				left += iconSize + gap;
				
				removeButton.setBounds(right - iconSize, top, iconSize, iconSize);
				right -= iconSize + gap;
				
				int width = (right - left - gap) / 2;
				value.getAsComponent().setBounds(right - width, top, width, iconSize);
				key.setBounds(left, top, width, iconSize);
			}
		}
	}
}

package io.bamboobear.json_editor.component.json;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.Icon;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorInputField;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.Label;

@SuppressWarnings("serial")
public sealed abstract class JsonPrimitiveComponent<T> extends JsonComponent<JsonPrimitive> permits JsonBooleanComponent, JsonNumberComponent, JsonStringComponent{
	protected final EditorInputField valueComponent;
	
	JsonPrimitiveComponent(Icon icon) {
		super(icon);
		this.valueComponent = createValueComponent();
		
		this.valueComponent.getAsComponent().setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));
		this.valueComponent.getAsComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				repaint();
			}
		});
		
		setLayout(new ComponentLayout(this, this.icon, this.keyField, this.valueComponent, this.removeButton));
	}
	
	public abstract void setValue(T value);
	
	public abstract boolean setValue(JsonPrimitive value);
	
	@Override
	public boolean setValue(JsonElement value) {
		if(!value.isJsonPrimitive()) {
			return false;
		}
		JsonPrimitive jp = value.getAsJsonPrimitive();
		return setValue(jp);
	}
	
	public abstract T getValue();
	
	@Override
	public final JsonCompositeComponent<?> getRootElement() {
		JsonCompositeComponent<?> parent = getParentElement();
		if (parent == null) {
			throw new IllegalStateException("parent is null");
		} else {
			return parent.getRootElement();
		}
	}

	protected abstract EditorInputField createValueComponent();
	
	public State getValueComponentState() {
		return State.NORMAL;
	}
	
	private static class ComponentLayout implements LayoutManager {
		final int gap = 1;
		
		private final Label icon;
		private final EditorTextField key;
		private final EditorInputField value;
		private final Button removeButton;
		
		private ComponentLayout(JsonPrimitiveComponent<?> json, Label icon, EditorTextField key, EditorInputField value, Button removeButton) {
			this.icon = Objects.requireNonNull(icon, "icon is null");
			this.key = Objects.requireNonNull(key, "key is null");
			this.value = Objects.requireNonNull(value, "value is null");
			this.removeButton = Objects.requireNonNull(removeButton, "remove button is null");
			
			json.add(icon);
			json.add(key);
			json.add(value.getAsComponent());
			json.add(removeButton);
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

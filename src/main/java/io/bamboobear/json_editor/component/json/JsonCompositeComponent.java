package io.bamboobear.json_editor.component.json;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.gson.JsonElement;

import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.Label;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public sealed abstract class JsonCompositeComponent<T extends JsonElement> extends JsonComponent<T> permits JsonArrayComponent, JsonObjectComponent{
	protected final Button addButton;
	private final JPanel elementsPanel;
	
	JsonCompositeComponent(Icon icon) {
		super(icon);

		addButton = new Button(TranslatableText.literal("+"));
		addButton.setFont(new Font("Default", Font.BOLD, 20));
		addButton.setForeground(new Color(0x005C00));
		addButton.addActionListener(e -> fireAddElement());
		addButton.setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
		
		elementsPanel = new JPanel(new ElementsPanelLayout());
		
		setLayout(new ComponentLayout());
		setBorder(new LineBorder(Color.GRAY, 5));
	}
	
	public final void addElement(JsonComponent<?> element)            { addElement(null, element);        }
	public final void addElement(JsonComponent<?> element, int index) { addElement(null, element, index); }
	
	public final void addElement(String key, JsonComponent<?> element)            { addElement(key, element, -1);          }
	public final void addElement(String key, JsonComponent<?> element, int index) { addElement(key, null, element, index); }

	public final void addElement(String key, TranslatableText text, JsonComponent<?> element) { addElement(key, text, element, -1); }
	
	public synchronized final void addElement(String key, TranslatableText text, JsonComponent<?> element, int index) {
		if(element.getParentElement() != null) return;
		
		if(key != null) element.setKey(key);
		if(text != null) element.setKeyDisplayText(text);
		
		element.setParentElement(this);
		elementsPanel.add(element, index);
	}
	
	public synchronized final void removeElement(JsonComponent<?> element) {
		int i = indexOf(element);
		if(i < 0) return;
		
		element.setParentElement(null);
		elementsPanel.remove(i);
	}
	
	public synchronized final void removeAllElements() {
		for(JsonComponent<?> element : getElements()) {
			removeElement(element);
		}
	}
	
	public synchronized final JsonComponent<?>[] getElements() {
		JsonComponent<?>[] elements = new JsonComponent<?>[getElementCount()];
		Component[] components = elementsPanel.getComponents();
		for(int i = 0; i < elements.length; i++) {
			elements[i] = (JsonComponent<?>)components[i];
		}
		return elements;
	}
	
	public final boolean containsElement(JsonComponent<?> element) { return indexOf(element) > 0; }
	
	public final int indexOf(JsonComponent<?> element) {
		int elementsCount = elementsPanel.getComponentCount();
		for(int i = 0; i < elementsCount; i++) {
			if(element == elementsPanel.getComponent(i)) return i;
		}
		return -1;
	}
	
	public final JsonComponent<?> getElement(int index) { return (JsonComponent<?>)elementsPanel.getComponent(index); }
	
	public final int getElementCount() { return elementsPanel.getComponentCount(); }
	
	public abstract void fireAddElement();
	
	@Override public Dimension getPreferredSize() { return getLayout().preferredLayoutSize(this); }
	
	private class ComponentLayout implements LayoutManager {
		final int gap = 1;
		
		private final Label icon;
		private final EditorTextField key;
		private final JPanel elements;
		private final Button addButton;
		private final Button removeButton;
		
		private ComponentLayout() {
			this.icon = JsonCompositeComponent.this.icon;
			this.key = JsonCompositeComponent.this.keyField;
			this.elements = JsonCompositeComponent.this.elementsPanel;
			this.addButton = JsonCompositeComponent.this.addButton;
			this.removeButton = JsonCompositeComponent.this.removeButton;
			
			JsonCompositeComponent.this.add(icon);
			JsonCompositeComponent.this.add(key);
			JsonCompositeComponent.this.add(elements);
			JsonCompositeComponent.this.add(addButton);
			JsonCompositeComponent.this.add(removeButton);
			
			if(JsonCompositeComponent.this.getParentElement() == null) {
				icon.setVisible(false);
				key.setVisible(false);
				removeButton.setVisible(false);
			}
		}

		@Override public void addLayoutComponent(String name, Component comp) { throw new UnsupportedOperationException(); }
		@Override public void removeLayoutComponent(Component comp) { throw new UnsupportedOperationException(); }

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getPreferredSize); }
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getMinimumSize); }
		}
		
		private Dimension createDimension(Container parent, Function<Component, Dimension> factory) {
			boolean isRootElement = !key.isVisible();
			
			int iconSize = DEFAULT_HEIGHT;
			
			int width = 0, height = 0;
			
			if(!isRootElement) {
				height += iconSize + gap;
				width += iconSize * 2 + gap + factory.apply(key).width;
			}
			
			Dimension d1 = factory.apply(elements);
			Dimension d2 = factory.apply(addButton);
			
			width = Math.max(width, d1.width + (isRootElement ? 0 : (iconSize + gap) * 2));
			height += d1.height + gap + d2.height;
			
			Insets insets = parent.getInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;
			
			return new Dimension(width, height);
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				boolean isRootElement = !key.isVisible();
				Insets insets = parent.getInsets();
				int iconSize = DEFAULT_HEIGHT;
				
				int top = insets.top;
				int left = insets.left;
				int right = parent.getWidth() - insets.right;
				
				int elementsHeight = elements.getPreferredSize().height;
				
				if(!isRootElement) {
					icon.setBounds(left, top, iconSize, iconSize);
					left += iconSize + gap;
					
					key.setBounds(left, top, right - left, iconSize);
					top += iconSize + gap;
					
					removeButton.setBounds(right - iconSize, top, iconSize, elementsHeight + gap + iconSize);
					right -= iconSize + gap;
				}
				
				elements.setBounds(left, top, right - left, elementsHeight);
				top += elementsHeight + gap;
				
				addButton.setBounds(left, top, right - left, iconSize);
			}
		}
	}
	
	private static class ElementsPanelLayout implements LayoutManager {
		final int gap = 1;
		
		private ElementsPanelLayout() {}
				
		@Override public void addLayoutComponent(String name, Component comp) {}
		@Override public void removeLayoutComponent(Component comp) {}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getPreferredSize); }
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) { return createDimension(parent, Component::getMinimumSize); }
		}
		
		public Dimension createDimension(Container parent, Function<Component, Dimension> factory) {
			int width = 0, height = 0;
			
			for(Component c : parent.getComponents()) {
				if(!c.isVisible()) continue;
				
				Dimension d = factory.apply(c);
				width = Math.max(width, d.width);
				height += d.height + ((height == 0) ? 0 : gap);
			}
			
			Insets insets = parent.getInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;
			
			return new Dimension(width, height);
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int x = insets.left;
				int y = insets.top;
				
				int parentWidth = parent.getWidth();
				
				for(Component c : parent.getComponents()) {
					if(!c.isVisible()) continue;
					Dimension d = c.getPreferredSize();
					c.setBounds(x, y, Math.max(parentWidth, d.width), d.height);
					y += d.height + gap;
				}
			} 
		}
	}
}

package io.bamboobear.json_editor.component.json;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.gson.JsonElement;

import io.bamboobear.json_editor.component.Button;
import io.bamboobear.json_editor.component.EditorTextField;
import io.bamboobear.json_editor.component.EditorTextField.Type;
import io.bamboobear.json_editor.component.GridBagConstraintsBuilder;
import io.bamboobear.json_editor.component.GridBagConstraintsBuilder.Anchor;
import io.bamboobear.json_editor.component.GridBagConstraintsBuilder.Fill;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public abstract class JsonCompositeComponent<T extends JsonElement> extends JsonComponent<T>{
	protected ArrayList<KeyValuePair> content = new ArrayList<KeyValuePair>();
	
	protected Button addButton;
	
	JsonCompositeComponent(Icon icon) {
		super(icon);
		setLayout(new GridBagLayout());
		
		addButton = new Button(TranslatableText.literal("+"));
		addButton.setFont(new Font("Default", Font.BOLD, 20));
		addButton.setForeground(new Color(0x005C00));
		addButton.addActionListener(e -> {
			fireAddElement();
		});
		addButton.setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
	}

	public final void addElement(String key, JsonComponent<?> element) {
		addElement(new EditorTextField(key, element, Type.KEY), element);
	}
	
	public final void addElement(String key, TranslatableText text, JsonComponent<?> element) {
		addElement(new EditorTextField(key, text, element, Type.KEY), element);
	}
	
	public final void addElement(EditorTextField key, JsonComponent<?> element) {
		addElement(key, element, -1);
	}
	
	public synchronized final void addElement(EditorTextField key, JsonComponent<?> element, int index) {
		if(element.getParentElement() != null) {
			return;
		}
		
		element.setParentElement(this);
		KeyValuePair kvp = new KeyValuePair(key, element);
		content.add(kvp);
		if(index >= 0 && index < content.size()) {
			for(int i = index; i < content.size(); i++) {
				kvp = content.set(i, kvp);
			}
		}
	}
	
	/**remove the element even if it is not removable.*/
	public synchronized KeyValuePair removeElement(JsonComponent<?> element) {
		int i = indexOf(element);
		if(i == -1) {
			return null;
		}
		
		element.setParentElement(null);
		return content.remove(i);
	}
	
	public boolean containsElement(JsonComponent<?> element) {
		return indexOf(element) != -1;
	}
	
	public int indexOf(JsonComponent<?> element) {
		for(int i = 0; i < content.size(); i++) {
			if(content.get(i).getJsonComponent().equals(element)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public final JsonCompositeComponent<?> getRootElement() {
		JsonCompositeComponent<?> parent = getParentElement();
		return (parent == null) ? this : parent.getRootElement();
	}
	
	@Override
	public void updateGUI() {
		removeAll();
		
		int y = 0;
		int height = 1;
		int gap = 2;
		GridBagConstraintsBuilder gbcb = new GridBagConstraintsBuilder().setGridLocation(0, y).setGridSize(1, 1).setWeight(1.0, 0.0).setAnchor(Anchor.NORTH).setFill(Fill.HORIZONTAL).setInsets(gap, gap, gap, gap);
		
		for(KeyValuePair pair : content) {
			height = pair.value.getGridHeight();
			pair.updateGUI();
			add(pair, gbcb.clone().setGridy(y).setGridheight(height).build());
			y += height;
		}
		add(addButton, gbcb.clone().setGridy(y).setWeighty(1.0).build());
	}
	
	@Override
	protected int getGridHeight() {
		int i = 2;
		for(KeyValuePair pair : content) {
			i += pair.getJsonComponent().getGridHeight();
		}
		return i;
	}
	
	protected JsonComponent<?> getElement(String text) {
		for(KeyValuePair pair : content) {
			if(pair.getKey() == text) {
				return pair.getJsonComponent();
			}
		}
		
		return null;
	}
	
	public abstract void fireAddElement();
	
	protected static class KeyValuePair extends JPanel {
		private final EditorTextField key;
		private final JsonComponent<?> value;
		
		public KeyValuePair(EditorTextField key, JsonComponent<?> value) {
			super(new BorderLayout(2, 2));
			
			this.key = key;
			this.value = value;
			
			this.key.setPreferredSize(new Dimension(0, JsonComponent.DEFAULT_HEIGHT));
			this.key.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					value.getParentElement().repaint();
				}
			});
			
			if(value instanceof JsonCompositeComponent) {
				setBorder(new LineBorder(Color.GRAY, 5));
				
				JPanel keyPanel = new JPanel(new BorderLayout());
				keyPanel.add(key, BorderLayout.CENTER);
				keyPanel.add(value.icon, BorderLayout.WEST);
				
				add(keyPanel, BorderLayout.NORTH);
				add(value, BorderLayout.CENTER);
				add(Box.createRigidArea(new Dimension(JsonComponent.DEFAULT_HEIGHT, 0)), BorderLayout.WEST);
				add(value.removeButton, BorderLayout.EAST);
			} else {
				JPanel keyValue = new JPanel(new GridLayout(1, 2, 4, 0));
				keyValue.add(key);
				keyValue.add(value);
				
				add(keyValue, BorderLayout.CENTER);
				add(value.icon, BorderLayout.WEST);
				add(value.removeButton, BorderLayout.EAST);
			}
			
		}
		
		public EditorTextField getKeyField() {
			return key;
		}
		
		public String getKey() {
			return key.getValue();
		}
		
		public JsonComponent<?> getJsonComponent() {
			return value;
		}
		
		public void updateGUI() {
			value.updateGUI();
		}
	}
}

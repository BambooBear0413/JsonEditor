package io.bamboobear.json_editor.component.json;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorComboBox;
import io.bamboobear.json_editor.component.EditorTextField;

@SuppressWarnings("serial")
public abstract class JsonPrimitiveComponent<T> extends JsonComponent<JsonPrimitive>{
	protected JComponent valueComponent;
	
	JsonPrimitiveComponent(Icon icon) {
		super(icon);
		this.valueComponent = createValueComponent();
		if(!(valueComponent instanceof EditorTextField || valueComponent instanceof EditorComboBox)) {
			throw new IllegalArgumentException("Type of this.valueComponent: " + valueComponent.getClass().getCanonicalName());
		}
		this.valueComponent.setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
		
		this.valueComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				repaint();
			}
		});
		
		setLayout(new GridLayout(1, 1));
		add(this.valueComponent);
	}
	
	public abstract void setValue(T value);
	
	protected abstract boolean setValue(JsonPrimitive value);
	
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
	protected int getGridHeight() {
		return 1;
	}
	
	@Override
	public final JsonCompositeComponent<?> getRootElement() {
		JsonCompositeComponent<?> parent = getParentElement();
		if (parent == null) {
			throw new IllegalStateException(new NullPointerException("parent is null"));
		} else {
			return parent.getRootElement();
		}
	}

	@Override
	public void updateGUI() {}
	
	protected abstract JComponent createValueComponent();
	
	public State getValueComponentState() {
		return State.NORMAL;
	}
}

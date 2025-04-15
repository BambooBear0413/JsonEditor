package io.bamboobear.json_editor.component.json;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Icon;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.bamboobear.json_editor.component.EditorInputField;

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
		
		setLayout(new ComponentLayout(valueComponent));
	}
	
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
}

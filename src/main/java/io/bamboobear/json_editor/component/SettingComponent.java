package io.bamboobear.json_editor.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JTextField;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public abstract class SettingComponent extends JComponent{
	protected static final int HEIGHT = 40;

	protected TranslatableText labelText;
	protected Component valueComponent;
	
	public SettingComponent(TranslatableText text) {
		this.labelText = text;
		valueComponent = createValueComponent();
		
		initialize();
	}
	
	protected void initialize() {
		setLayout(new GridLayout(1, 2, 1, 1));
		add(new Label(labelText));
		add(valueComponent);
		setPreferredSize(new Dimension(0, HEIGHT));
	}
	
	protected abstract Component createValueComponent();
	
	public static final class SettingTextField extends TextField {
		private String key;
		
		public SettingTextField(String key, String value) {
			this(key, value, str -> str);
		}
		
		public SettingTextField(String key, String value, Function<String, String> valueHandler) {
			this(key, value, valueHandler, (textField, newValue) -> {});
		}
		
		public SettingTextField(String key, String value, Function<String, String> valueHandler, BiConsumer<SettingTextField, String> updater) {
			super(value);
			this.key = key;
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					String value = valueHandler.apply(SettingTextField.this.getText());
					SettingsDialog.addSettingChange(SettingTextField.this.key, value);
					updater.accept(SettingTextField.this, value);
				}
			});
		}
	}
	
	public static final class SettingComboBox extends ComboBox {
		private String key;
		
		public SettingComboBox(ComboBoxItem[] items, String key, String value) {
			this(items, key, value, str -> str);
		}
		
		public SettingComboBox(ComboBoxItem[] items, String key, String value, Function<String, String> valueHandler) {
			this(items, key, value, valueHandler, (comboBox, newValue) -> {});
		}
		
		public SettingComboBox(ComboBoxItem[] items,
				String key, String value, Function<String, String> valueHandler, BiConsumer<SettingComboBox, String> updater) {
			
			super(items);
			this.key = key;
			setValue(value);
			addItemListener(e -> {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					SettingsDialog.addSettingChange(SettingComboBox.this.key, valueHandler.apply(SettingComboBox.this.getValue()));
				}
			});
			
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if(SettingComboBox.this.isEditable()) {
						String value = valueHandler.apply(SettingComboBox.this.getValue());
						SettingsDialog.addSettingChange(SettingComboBox.this.key, value);
						((JTextField)(SettingComboBox.this.getEditor().getEditorComponent())).setText(value);
					}
				}
			});
		}
	}
}

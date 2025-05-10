package io.bamboobear.json_editor.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.function.Function;

import javax.swing.JComponent;

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
	
	public static class SettingTextField extends TextField {
		private String key;
		
		public SettingTextField(String key, String value) {
			this(key, value, SettingTextField::getText);
		}
		
		public SettingTextField(String key, String value, Function<SettingTextField, String> valueGetter) {
			super(value);
			this.key = key;
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					SettingsDialog.addSettingChange(SettingTextField.this.key, valueGetter.apply(SettingTextField.this));
				}
			});
		}
	}
	
	public static class SettingComboBox extends ComboBox {
		private String key;
		
		public SettingComboBox(ComboBoxItem[] items, String key, String value) {
			this(items, key, value, SettingComboBox::getValue);
		}
		
		public SettingComboBox(ComboBoxItem[] items, String key, String value, Function<SettingComboBox, String> valueGetter) {
			super(items);
			this.key = key;
			setValue(value);
			addItemListener(e -> {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					SettingsDialog.addSettingChange(SettingComboBox.this.key, valueGetter.apply(SettingComboBox.this));
				}
			});
		}
	}
}

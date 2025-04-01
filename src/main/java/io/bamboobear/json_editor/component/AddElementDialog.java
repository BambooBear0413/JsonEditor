package io.bamboobear.json_editor.component;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class AddElementDialog extends Dialog{
	private String result;
	
//	public AddElementDialog(JsonCompositeComponent<?> component) {
//	}
	
//	@Deprecated
	public AddElementDialog(ComboBox type) {
		super(Main.getMainWindow(), TranslatableText.of("json_editor.add_element.title"), true);
		
		JPanel contentPanel = new JPanel(new GridLayout(2, 2, 2, 2));
		Label typeLabel = new Label(TranslatableText.of("json_editor.add_element.type"), Label.CENTER);
		ComboBox typeField = Objects.requireNonNull(type);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			};
		});

		contentPanel.add(typeLabel);
		contentPanel.add(typeField);
		
		contentPanel.add(createButton(TranslatableText.of("json_editor.finish"), e -> {
			result = typeField.getValue();
			dispose();
		}));
		contentPanel.add(createButton(TranslatableText.of("json_editor.cancel"), e -> dispose()));
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel);

		pack();
		setLocationRelativeTo(getParent());
		setResizable(false);
	}

	public String showDialog() {
		setVisible(true);
		return result;
	}
	
	private Button createButton(TranslatableText text, ActionListener listener) {
		Button button = new Button(text);
		button.addActionListener(listener);
		
		return button;
	}
}

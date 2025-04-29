package io.bamboobear.json_editor.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class RGBColorPicker extends JComponent{
	private final Label redText;
	private final Label greenText;
	private final Label blueText;
	
	private final JSlider redSlider;
	private final JSlider greenSlider;
	private final JSlider blueSlider;
	
	private final TextField redTextField;
	private final TextField greenTextField;
	private final TextField blueTextField;
	
	private final Component preview;
	
	public RGBColorPicker() {
		this(Color.WHITE);
	}
	
	public RGBColorPicker(Color color) {
		setLayout(new GridLayout(1, 2, 1, 1));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel rgb = new JPanel(new GridBagLayout());
		
		redText = new Label(TranslatableText.literal("R"));
		greenText = new Label(TranslatableText.literal("G"));
		blueText = new Label(TranslatableText.literal("B"));
		
		redTextField = new TextField(String.valueOf(color.getRed()));
		greenTextField = new TextField(String.valueOf(color.getGreen()));
		blueTextField = new TextField(String.valueOf(color.getBlue()));
		
		redSlider = new JSlider(0, 0xFF, color.getRed());
		greenSlider = new JSlider(0, 0xFF, color.getGreen());
		blueSlider = new JSlider(0, 0xFF, color.getBlue());
		
		redTextField.addFocusListener(getTextFieldFocusListener(redSlider, redTextField));
		greenTextField.addFocusListener(getTextFieldFocusListener(greenSlider, greenTextField));
		blueTextField.addFocusListener(getTextFieldFocusListener(blueSlider, blueTextField));
		
		redTextField.addKeyListener(getTextFieldKeyListener(redSlider, redTextField));
		greenTextField.addKeyListener(getTextFieldKeyListener(greenSlider, greenTextField));
		blueTextField.addKeyListener(getTextFieldKeyListener(blueSlider, blueTextField));
		
		redTextField.setPreferredSize(new Dimension(20, 0));
		greenTextField.setPreferredSize(new Dimension(20, 0));
		blueTextField.setPreferredSize(new Dimension(20, 0));
		
		redSlider.addChangeListener(getChangeListener(redSlider, redTextField));
		greenSlider.addChangeListener(getChangeListener(greenSlider, greenTextField));
		blueSlider.addChangeListener(getChangeListener(blueSlider, blueTextField));
		
		redSlider.setMinorTickSpacing(1);
		greenSlider.setMinorTickSpacing(1);
		blueSlider.setMinorTickSpacing(1);
		
		preview = new JPanel();
		preview.setBackground(color);
		
		int i = 1;
		Insets insets = new Insets(i, i, i, i);
		int anchor = GridBagConstraints.CENTER;
		int fill = GridBagConstraints.BOTH;
		
		rgb.add(redText, 		new GridBagConstraints(0, 0, 1, 1, 0.25, 1.0, anchor, fill, insets, 0, 0));
		rgb.add(greenText, 		new GridBagConstraints(0, 1, 1, 1, 0.25, 1.0, anchor, fill, insets, 0, 0));
		rgb.add(blueText, 		new GridBagConstraints(0, 2, 1, 1, 0.25, 1.0, anchor, fill, insets, 0, 0));
		
		rgb.add(redSlider, 		new GridBagConstraints(1, 0, 1, 1, 1.0,	1.0, anchor, fill, insets, 0, 0));
		rgb.add(greenSlider, 	new GridBagConstraints(1, 1, 1, 1, 1.0,	1.0, anchor, fill, insets, 0, 0));
		rgb.add(blueSlider, 	new GridBagConstraints(1, 2, 1, 1, 1.0,	1.0, anchor, fill, insets, 0, 0));
		
		rgb.add(redTextField, 	new GridBagConstraints(2, 0, 1, 1, 0.375, 1.0, anchor, fill, insets, 0, 0));
		rgb.add(greenTextField, new GridBagConstraints(2, 1, 1, 1, 0.375, 1.0, anchor, fill, insets, 0, 0));
		rgb.add(blueTextField, 	new GridBagConstraints(2, 2, 1, 1, 0.375, 1.0, anchor, fill, insets, 0, 0));
		
		add(preview);
		add(rgb);
	}
	
	public Color getColor() {
		return new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
	}
	
	private ChangeListener getChangeListener(JSlider slider, TextField textField) {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				textField.setText(String.valueOf(slider.getValue()));
				preview.setBackground(getColor());
				preview.repaint();
			}
		};
	}
	
	private FocusListener getTextFieldFocusListener(JSlider slider, TextField textField) {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setValue(slider, textField);
			}
		};
	}
	
	private KeyListener getTextFieldKeyListener(JSlider slider, TextField textField) {
		return new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> {
					setValue(slider, textField);
				});
			}
		};
	}
	
	private void setValue(JSlider slider, TextField textField) {
		try {
			int value = Integer.parseInt(textField.getText());
			if(value < 0) {
				value = 0;
			} else if(value > 0xFF) {
				value = 0xff;
			}
			slider.setValue(value);
		} catch (NumberFormatException exception) {
			slider.setValue(0);
			textField.setText("0");
		}
	}
}

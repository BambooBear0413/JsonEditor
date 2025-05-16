package io.bamboobear.json_editor.component;

import java.awt.Font;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import io.bamboobear.json_editor.Main;

@SuppressWarnings("serial")
public class ComboBox extends JComboBox<ComboBoxItem>{
	private boolean useCustomFont = false;
	
	public ComboBox(ComboBoxItem[] items) {
		super(filterInputArray(items));
		setRenderer((list, item, index, isSelected, cellHasFocus) -> {
			Label render = new Label(item.getText());
			render.setOpaque(true);
			
			render.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
			render.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			
			return render;
		});
	}
	
	public String getValue() {
		if(!isEditable()) return getItemAt(getSelectedIndex()).getId();
		
		return ((JTextField) getEditor().getEditorComponent()).getText();
	}
	
	public void setValue(String id) {
		int itemCount = getItemCount();
		for(int i = 0; i < itemCount; i++) {
			if(getItemAt(i).getId().equals(id)) {
				setSelectedIndex(i);
				break;
			}
		}
		
		if(isEditable()) ((JTextField) getEditor().getEditorComponent()).setText(id);
	}
	
	private static ComboBoxItem[] filterInputArray(ComboBoxItem[] items) {
		LinkedHashSet<ComboBoxItem> set = new LinkedHashSet<>(items.length);
		set.addAll(List.of(items));
		return set.toArray(new ComboBoxItem[set.size()]);
	}
	
	@Override
	public void setFont(Font font) {
		useCustomFont = true;
		super.setFont(font);
	}
	
	@Override public Font getFont() { return useCustomFont ? super.getFont() : Main.getFont(); }
}

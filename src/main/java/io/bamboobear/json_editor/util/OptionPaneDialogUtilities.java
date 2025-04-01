package io.bamboobear.json_editor.util;

import javax.swing.JOptionPane;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

public class OptionPaneDialogUtilities {
	private OptionPaneDialogUtilities() {
	}
	
	public static void showErrorMessageDialog(TranslatableText content, TranslatableText title) {
		JOptionPane.showMessageDialog(Main.getMainWindow(), content.getDisplayText(), title.getDisplayText(), JOptionPane.ERROR_MESSAGE);
	}
}

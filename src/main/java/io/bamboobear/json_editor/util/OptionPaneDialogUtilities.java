package io.bamboobear.json_editor.util;

import javax.swing.JOptionPane;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

public class OptionPaneDialogUtilities {
	private OptionPaneDialogUtilities() {}

	public static void showErrorMessageDialog(String content, Object... args) {
		showErrorMessageDialog(content + ".title", content, args);
	}

	public static void showErrorMessageDialog(String title, String content, Object... args) {
		showMessageDialog(JOptionPane.ERROR_MESSAGE, title, content, args);
	}

	public static void showWarningMessageDialog(String content, Object... args) {
		showWarningMessageDialog(content + ".title", content, args);
	}

	public static void showWarningMessageDialog(String title, String content, Object... args) {
		showMessageDialog(JOptionPane.WARNING_MESSAGE, title, content, args);
	}

	@SuppressWarnings("MagicConstant")
	public static void showMessageDialog(int type, String title, String content, Object... args) {
		JOptionPane.showMessageDialog(
				Main.getMainWindow(),
				TranslatableText.create(content, args).getDisplayText(),
				TranslatableText.create(title).getDisplayText(),
				type
		);
	}
}

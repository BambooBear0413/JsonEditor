package io.bamboobear.json_editor.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.Language;
import io.bamboobear.json_editor.lang.Languages;
import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class AboutDialog extends Dialog{
	private static volatile AboutDialog dialog;
	
	private AboutDialog() {
		super(Main.getMainWindow(), TranslatableText.of("json_editor.about_editor.title"));
		
		init();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		
		pack();
		if(getWidth() < 500) setSize(500, getHeight());
		setLocationRelativeTo(getParent());
		setResizable(false);
		setVisible(true);
	}
	
	private void init() {
		JPanel northPanel = new JPanel(new GridLayout(0, 1));
		
		Label name = new Label(TranslatableText.of("json_editor.name"), Label.CENTER);
		name.setFont(new Font("Default", Font.BOLD, 20));
		northPanel.add(name);
		
		Label version = new Label(TranslatableText.literal(Main.VERSION), Label.CENTER);
		northPanel.add(version);
		
		add(northPanel, BorderLayout.NORTH);
		
		JPanel centeralPanel = new JPanel(new GridLayout(0, 1));
		
		centeralPanel.add(createFilePanel("README", Language::hasReadmePath, Language::getReadmePath));
		centeralPanel.add(createFilePanel("HTML", Language::hasHtmlPath, Language::getHtmlPath));
		
		add(centeralPanel);
	}
	
	private Button createButton(Language lang, String filePath) {
		Button button = new Button(TranslatableText.literal(lang.getName()));
		button.addActionListener(e -> Main.open(filePath));
		return button;
	}
	
	private TitledBorder createTitledBorder(String title) {
		return new TitledBorder(new LineBorder(Color.BLACK, 2), title, TitledBorder.CENTER, TitledBorder.TOP, Main.getFont());
	}
	
	private JPanel createFilePanel(String title, Predicate<Language> pathChecker, Function<Language, String> pathGetter) {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		
		panel.setBorder(createTitledBorder(title));
		Language[] languages = Languages.getLanguages();
		for(Language lang : languages) {
			if(pathChecker.test(lang)) {
				panel.add(createButton(lang, pathGetter.apply(lang)));
			}
		}
		
		return panel;
	}
	
	public static void showDialog() {
		if(dialog != null) {
			dialog.requestFocus();
			return;
		}
		dialog = new AboutDialog();
	}
	
	public static AboutDialog getDialog() {
		return dialog;
	}
	
	private void closeDialog() {
		dialog = null;
		dispose();
	}
} 

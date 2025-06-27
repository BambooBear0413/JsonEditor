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
		super(Main.getMainWindow(), TranslatableText.create("json_editor.about_editor.title"));
		
		init();
		
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) { dispose(); }
		});
		
		pack();
		if(getWidth() < 500) setSize(500, getHeight());
		setLocationRelativeTo(getParent());
		setResizable(false);
	}
	
	private void init() {
		JPanel northPanel = new JPanel(new GridLayout(0, 1));
		
		Label name = new Label(TranslatableText.create("json_editor.name"), Label.CENTER);
		name.setFont(new Font("Default", Font.BOLD, 20));
		northPanel.add(name);
		
		Label version = new Label(TranslatableText.literal("%s", Main.VERSION), Label.CENTER);
		northPanel.add(version);
		
		JPanel centeralPanel = new JPanel(new GridLayout(0, 1));
		
		centeralPanel.add(createFilePanel("README", Language::hasReadmePath, Language::getReadmePath));
		centeralPanel.add(createFilePanel("HTML", Language::hasHtmlPath, Language::getHtmlPath));
		
		Button issueTrackerButton = new Button(TranslatableText.create("json_editor.about_editor.issue_tracker"));
		issueTrackerButton.addActionListener(e -> Main.browse("https://github.com/BambooBear0413/JsonEditor/issues"));
		
		add(northPanel, BorderLayout.NORTH);
		add(centeralPanel);
		add(issueTrackerButton, BorderLayout.SOUTH);
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
		dialog.setVisible(true);
	}
	
	public static AboutDialog getDialog() { return dialog; }
	
	@Override
	public void dispose() {
		dialog = null;
		super.dispose();
	}
} 

package io.bamboobear.json_editor.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public abstract class SettingComponent extends JComponent{
	private static final int HEIGHT = 40;

	protected SettingLabel displayText;
	protected Component valueComponent;
	
	public SettingComponent(TranslatableText text) {
		this(text, false);
	}
	
	public SettingComponent(TranslatableText text, boolean needToRestart) {
		setLayout(new GridLayout(1, 2, 1, 1));
		setDoubleBuffered(true);
		
		displayText = new SettingLabel(text, needToRestart);
		
		add(displayText);
		
		this.valueComponent = createValueComponent();
		add(valueComponent);
		
		setPreferredSize(new Dimension(0, HEIGHT));
	}
	
	protected abstract Component createValueComponent();
	
	class SettingLabel extends Label {
		private final boolean needToRestart;
		private static final TranslatableText NEED_TO_RESTART = TranslatableText.create("json_editor.settings.restart");
		
		public SettingLabel(TranslatableText text) {
			this(text, false);
		}
		
		public SettingLabel(TranslatableText text, boolean needToRestart) {
			super(text);
			this.needToRestart = needToRestart;
		}
		
		@Override
		public String getText() {
			String displayText = super.getText();
			if(needToRestart) {
				displayText = String.format("%s (%s)", displayText, NEED_TO_RESTART.getDisplayText());
			}
			return displayText;
		}
	}
}

package io.bamboobear.json_editor.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.component.GridBagConstraintsBuilder.Anchor;
import io.bamboobear.json_editor.component.GridBagConstraintsBuilder.Fill;
import io.bamboobear.json_editor.lang.TranslatableText;
import io.bamboobear.json_editor.plugin.Plugin;
import io.bamboobear.json_editor.plugin.Plugins;

@SuppressWarnings("serial")
public class PluginListDialog extends Dialog {
	private static volatile PluginListDialog dialog;
	
	private PluginListDialog() {
		super(Main.getMainWindow(), TranslatableText.of("json_editor.plugin_list.title"));
		
		init();
		
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) { dispose(); }
		});
		
		setSize(800, 400);
		setLocationRelativeTo(getParent());
		setResizable(false);
	}
	
	private void init() {
		var plugins = Plugins.getPlugins();
		if(plugins.length == 0) {
			Label noPlugin = new Label(TranslatableText.of("json_editor.plugin_list.no_plugin"), Label.CENTER);
			int insets = 10;
			noPlugin.setBorder(new EmptyBorder(insets, insets, insets, insets));
			add(noPlugin);
			return;
		}
		
		var pluginList = new JList<Plugin>(plugins);
		var pluginInfo = new PluginInfoPanel();
		var pluginContentScrollPane = new JScrollPane(pluginInfo);
		
		
		pluginList.setCellRenderer((list, plugin, index, isSelected, cellHasFocus) -> {
			Label displayName = new Label(plugin.getName());
			displayName.setOpaque(true);
			
			if(isSelected) {
				displayName.setBackground(list.getSelectionBackground());
				displayName.setForeground(list.getSelectionForeground());
			} else {
				displayName.setBackground(list.getBackground());
				displayName.setForeground(list.getForeground());
			}
			
			displayName.setEnabled(list.isEnabled());
			
			return displayName;
		});
		
		pluginList.addListSelectionListener(e -> {
			Plugin plugin = pluginList.getSelectedValue();
			pluginInfo.setPlugin(plugin);
			pluginInfo.revalidate();
			pluginInfo.repaint();
		});
		pluginList.setPreferredSize(new Dimension(100, 0));
		
		pluginList.setSelectedIndex(0);
		
		add(pluginList, BorderLayout.WEST);
		add(pluginContentScrollPane);
	}
	
	public static void showDialog() {
		if(dialog != null) {
			dialog.requestFocus();
			return;
		}
		
		dialog = new PluginListDialog();
		dialog.setVisible(true);
	}
	
	public static PluginListDialog getDialog() {
		return dialog;
	}
	
	@Override
	public void dispose() {
		dialog = null;
		super.dispose();
	}
	
	private class PluginInfoPanel extends JPanel {
		private Label id;
		private Label pluginFormat;
		private Label authors;
		private Label credits;
		private Button pluginFileButton;
		private TextArea pluginDescription;
		
		private File pluginFile;
		
		public PluginInfoPanel() {
			super(new GridBagLayout());
			
			int gridxForLabel = 0;
			int gridxForComponent = 1;
			int gridy = 1;
			
			int gridweightForPluginFileButton = 2;
			
			double weightXForLabel = 1.0;
			double weightXForComponent = 1.0;
			double weightY = 0.0;
			double weightYForDescription = 1.0;
			
			GridBagConstraintsBuilder gbcb = new GridBagConstraintsBuilder().setGridLocation(gridxForLabel, gridy).setWeight(weightXForLabel, weightY).setInsets(2, 2, 2, 2).setAnchor(Anchor.NORTHWEST).setFill(Fill.HORIZONTAL);
			
			/*======================= Plugin ID =======================*/
			Label idLabel = new Label(TranslatableText.of("json_editor.plugin_list.plugin_id"));
			add(idLabel, gbcb.clone().setGridy(gridy).build());
			
			id = new Label();
			add(id, gbcb.clone().setGridLocation(gridxForComponent, gridy++).setWeightx(weightXForComponent).build());
			
			/*======================= Plugin Format =======================*/
			Label pluginFormatLabel = new Label(TranslatableText.of("json_editor.plugin_list.plugin_format"));
			add(pluginFormatLabel, gbcb.clone().setGridy(gridy).build());
			
			pluginFormat = new Label();
			add(pluginFormat, gbcb.clone().setGridLocation(gridxForComponent, gridy++).setWeightx(weightXForComponent).build());
			
			/*======================= Plugin Author =======================*/
			Label authorsLabel = new Label(TranslatableText.of("json_editor.plugin_list.plugin_authors"));
			add(authorsLabel, gbcb.clone().setGridy(gridy).build());
			
			authors = new Label();
			add(authors, gbcb.clone().setGridLocation(gridxForComponent, gridy++).setWeightx(weightXForComponent).build());
			
			/*======================= Plugin Credits =======================*/
			Label creditsLabel = new Label(TranslatableText.of("json_editor.plugin_list.plugin_credits"));
			add(creditsLabel, gbcb.clone().setGridy(gridy).build());
			
			credits = new Label();
			add(credits, gbcb.clone().setGridLocation(gridxForComponent, gridy++).setWeightx(weightXForComponent).build());
			
			/*======================= Plugin File =======================*/
			pluginFileButton = new Button(TranslatableText.of("json_editor.plugin_list.plugin_file"));
			pluginFileButton.addActionListener(e -> Main.open(pluginFile));
			add(pluginFileButton, gbcb.clone().setGridy(gridy++).setGridwidth(gridweightForPluginFileButton).setWeightx(weightXForComponent).build());
			
			/*======================= Plugin Description =======================*/
			Label pluginDescriptionLabel = new Label(TranslatableText.of("json_editor.plugin_list.plugin_description"));
			add(pluginDescriptionLabel, gbcb.clone().setGridy(gridy).setWeighty(weightYForDescription).build());
			
			pluginDescription = new TextArea();
			pluginDescription.setBackground(getBackground());
			pluginDescription.setLineWrap(true);
			add(pluginDescription, gbcb.clone().setGridLocation(gridxForComponent, gridy++).setWeight(weightXForComponent, weightYForDescription).build());
		}
		
		public void setPlugin(Plugin plugin) {
			id.setText(TranslatableText.literal(plugin.id()));
			pluginFormat.setText(TranslatableText.literal(String.valueOf(plugin.getFormatVersion())));
			if(plugin.getFormatVersion() != Plugin.LATEST_FORMAT_VERSION) {
				pluginFormat.setForeground(Color.RED);
			} else {
				pluginFormat.setForeground(Color.BLACK);
			}
			authors.setText(TranslatableText.literal(getStringFromArray(plugin.getAuthors())));
			credits.setText(TranslatableText.literal(getStringFromArray(plugin.getCredits())));
			pluginFile = plugin.getFile();
			pluginDescription.setDisplayText(plugin.getDescription());
		}
		
		public String getStringFromArray(String[] array) {
			StringBuilder sb = new StringBuilder();
			for(String str : array) {
				if(sb.length() != 0) {
					sb.append(", ");
				}
				sb.append(str);
			}
			return sb.toString();
		}
	}
}

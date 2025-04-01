package io.bamboobear.json_editor;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceImageLoader {
	private static final Map<URL, Image> cache = new HashMap<URL, Image>();
	private static final String IMAGE_DIR = "textures/";
	private static final String IMAGE_LIST = IMAGE_DIR + "list.txt";
	
	private ResourceImageLoader() {
	}
	
	public static void loadImages() {
		try(InputStream is = ClassLoader.getSystemResourceAsStream(IMAGE_LIST);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr)) {
			
			String name;
			while((name = br.readLine()) != null) {
				loadImage(ClassLoader.getSystemResource(IMAGE_DIR + name));
			}
		} catch (IOException | NullPointerException ignored) {
		}
	}
	
	private static Image loadImage(URL url) {
		try {
			Image image = ImageIO.read(url.openStream());
			cache.put(url, image);
			return image;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Image getImage(String name) {
		URL url = ClassLoader.getSystemResource(IMAGE_DIR + name);
		Image image = cache.get(url);
		if(image == null) {
			image = loadImage(url);
		}
		
		return image;
	}
	
	public static Image getIconImage(String name) {
		return getImage("icon/" + name);
	}
	
	public static ImageIcon getImageIcon(String name, int width, int height, int hints) {
		Image image = null;
		try {
			Image originImage = getIconImage(name);
			image = originImage.getScaledInstance(width, height, hints);
		} catch (NullPointerException e) {
			image = null;
		}
		return image == null ? new ImageIcon() : new ImageIcon(image);
	}
}

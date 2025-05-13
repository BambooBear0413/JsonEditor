package io.bamboobear.json_editor.plugin;

import java.lang.reflect.AccessFlag;
import java.util.HashMap;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public class LookAndFeelLoader {
	private static final HashMap<String, Class<?>> MAP = new HashMap<>();
	
	private LookAndFeelLoader() {}
	
	public static Class<?> getLookAndFeelClass(String className) {
		return MAP.get(className);
	}
	
	static void installLookAndFeel(String name, String className, ClassLoader loader) {
		if(MAP.containsKey(className)) return; // TODO loading warning
		
		try {
			Class<?> clazz = Class.forName(className, true, loader);
			if(!LookAndFeel.class.isAssignableFrom(clazz)) return; // TODO loading warning
			if(clazz.accessFlags().contains(AccessFlag.ABSTRACT)) return; // TODO loading warning
			
			clazz.getConstructor();
			
			UIManager.installLookAndFeel(name, className);
			MAP.put(className, clazz);
		} catch(ClassNotFoundException e) { //TODO loading warning
			return;
		} catch(NoSuchMethodException e) { // TODO loading warning
			return;
		} catch (Throwable e) { // TODO loading warning
			return;
		}
	}
}

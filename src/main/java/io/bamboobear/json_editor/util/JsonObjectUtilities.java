package io.bamboobear.json_editor.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonObjectUtilities {
	private JsonObjectUtilities() {
	}
	
	public static JsonPrimitive getJsonPrimitive(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonPrimitive jp) {
			return jp;
		}
		return null;
	}
	
	public static JsonObject getJsonObject(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonObject jo) {
			return jo;
		}
		return null;
	}
	
	public static JsonArray getJsonArray(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonArray ja) {
			return ja;
		}
		return null;
	}
	
	public static boolean getBoolean(JsonObject object, String key, boolean defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isBoolean()) {
			return jp.getAsBoolean();
		}
		return defaultValue;
	}
	
	public static Number getNumber(JsonObject object, String key, Number defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsNumber();
		}
		return defaultValue;
	}
	
	public static byte getByte(JsonObject object, String key, byte defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsByte();
		}
		return defaultValue;
	}
	
	public static short getShort(JsonObject object, String key, short defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsShort();
		}
		return defaultValue;
	}
	
	public static int getInteger(JsonObject object, String key, int defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsInt();
		}
		return defaultValue;
	}
	
	public static long getLong(JsonObject object, String key, long defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsLong();
		}
		return defaultValue;
	}
	
	public static float getFloat(JsonObject object, String key, float defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsFloat();
		}
		return defaultValue;
	}
	
	public static double getDouble(JsonObject object, String key, double defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsDouble();
		}
		return defaultValue;
	}
	
	public static BigDecimal getBigDecimal(JsonObject object, String key, BigDecimal defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			try {
				return jp.getAsBigDecimal();
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public static BigInteger getBigInteger(JsonObject object, String key, BigInteger defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			try {
				return jp.getAsBigInteger();
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public static String getString(JsonObject object, String key, String defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isString()) {
			return jp.getAsString();
		}
		return defaultValue;
	}
	
	public static boolean isJsonPrimitive(JsonObject object, String key) {
		return getJsonPrimitive(object, key) != null;
	}
	
	public static boolean isJsonObject(JsonObject object, String key) {
		return getJsonObject(object, key) != null;
	}
	
	public static boolean isJsonArray(JsonObject object, String key) {
		return getJsonArray(object, key) != null;
	}
	
	public static boolean isBoolean(JsonObject object, String key) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		return (jp != null) && (jp.isBoolean());
	}
	
	public static boolean isNumber(JsonObject object, String key) {
		return getNumber(object, key, null) != null;
	}
	
	public static boolean isString(JsonObject object, String key) {
		return getString(object, key, null) != null;
	}
}

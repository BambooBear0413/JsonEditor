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
	
	public static JsonPrimitive getAsJsonPrimitive(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonPrimitive jp) {
			return jp;
		}
		return null;
	}
	
	public static JsonObject getAsJsonObject(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonObject jo) {
			return jo;
		}
		return null;
	}
	
	public static JsonArray getAsJsonArray(JsonObject object, String key) {
		JsonElement element = object.get(key);
		if(element instanceof JsonArray ja) {
			return ja;
		}
		return null;
	}
	
	public static boolean getAsBoolean(JsonObject object, String key, boolean defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isBoolean()) {
			return jp.getAsBoolean();
		}
		return defaultValue;
	}
	
	public static Number getAsNumber(JsonObject object, String key, Number defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsNumber();
		}
		return defaultValue;
	}
	
	public static byte getAsByte(JsonObject object, String key, byte defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsByte();
		}
		return defaultValue;
	}
	
	public static short getAsShort(JsonObject object, String key, short defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsShort();
		}
		return defaultValue;
	}
	
	public static int getAsInteger(JsonObject object, String key, int defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsInt();
		}
		return defaultValue;
	}
	
	public static long getAsLong(JsonObject object, String key, long defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsLong();
		}
		return defaultValue;
	}
	
	public static float getAsFloat(JsonObject object, String key, float defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsFloat();
		}
		return defaultValue;
	}
	
	public static double getAsDouble(JsonObject object, String key, double defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			return jp.getAsDouble();
		}
		return defaultValue;
	}
	
	public static BigDecimal getAsBigDecimal(JsonObject object, String key, BigDecimal defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			try {
				return jp.getAsBigDecimal();
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public static BigInteger getAsBigInteger(JsonObject object, String key, BigInteger defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) {
			try {
				return jp.getAsBigInteger();
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public static String getAsString(JsonObject object, String key, String defaultValue) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		if(jp != null && jp.isString()) {
			return jp.getAsString();
		}
		return defaultValue;
	}
	
	public static boolean isJsonPrimitive(JsonObject object, String key) {
		return getAsJsonPrimitive(object, key) != null;
	}
	
	public static boolean isJsonObject(JsonObject object, String key) {
		return getAsJsonObject(object, key) != null;
	}
	
	public static boolean isJsonArray(JsonObject object, String key) {
		return getAsJsonArray(object, key) != null;
	}
	
	public static boolean isBoolean(JsonObject object, String key) {
		JsonPrimitive jp = getAsJsonPrimitive(object, key);
		return (jp != null) && (jp.isBoolean());
	}
	
	public static boolean isNumber(JsonObject object, String key) {
		return getAsNumber(object, key, null) != null;
	}
	
	public static boolean isString(JsonObject object, String key) {
		return getAsString(object, key, null) != null;
	}
}

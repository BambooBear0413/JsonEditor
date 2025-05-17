package io.bamboobear.json_editor.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonObjectUtilities {
	private JsonObjectUtilities() {}
	
	public static JsonPrimitive getJsonPrimitive(JsonObject object, String key) {
		if(isJsonPrimitive(object, key)) return object.get(key).getAsJsonPrimitive();
		return null;
	}
	
	public static JsonObject getJsonObject(JsonObject object, String key) {
		if(isJsonObject(object, key)) return object.get(key).getAsJsonObject();
		return null;
	}
	
	public static JsonArray getJsonArray(JsonObject object, String key) {
		if(isJsonArray(object, key)) return object.get(key).getAsJsonArray();
		return null;
	}
	
	public static boolean getBoolean(JsonObject object, String key, boolean defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isBoolean()) return jp.getAsBoolean();
		return defaultValue;
	}
	
	public static Number getNumber(JsonObject object, String key, Number defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) jp.getAsNumber();
		return defaultValue;
	}
	
	public static int getInteger(JsonObject object, String key, int defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) return jp.getAsInt();
		return defaultValue;
	}
	
	public static long getLong(JsonObject object, String key, long defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) return jp.getAsLong();
		return defaultValue;
	}
	
	public static double getDouble(JsonObject object, String key, double defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) return jp.getAsDouble();
		return defaultValue;
	}
	
	public static BigDecimal getBigDecimal(JsonObject object, String key, BigDecimal defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) return jp.getAsBigDecimal();
		return defaultValue;
	}
	
	public static BigInteger getBigInteger(JsonObject object, String key, BigInteger defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isNumber()) return jp.getAsBigDecimal().toBigInteger();
		return defaultValue;
	}
	
	public static String getString(JsonObject object, String key, String defaultValue) {
		JsonPrimitive jp = getJsonPrimitive(object, key);
		if(jp != null && jp.isString()) return jp.getAsString();
		return defaultValue;
	}
	
	public static boolean isJsonPrimitive(JsonObject object, String key) {
		return object.has(key) && object.get(key).isJsonPrimitive();
	}
	
	public static boolean isJsonObject(JsonObject object, String key) {
		return object.has(key) && object.get(key).isJsonObject();
	}
	
	public static boolean isJsonArray(JsonObject object, String key) {
		return object.has(key) && object.get(key).isJsonArray();
	}
	
	public static boolean isBoolean(JsonObject object, String key) {
		return isJsonPrimitive(object, key) && getJsonPrimitive(object, key).isBoolean();
	}
	
	public static boolean isNumber(JsonObject object, String key) {
		return isJsonPrimitive(object, key) && getJsonPrimitive(object, key).isNumber();
	}
	
	public static boolean isString(JsonObject object, String key) {
		return isJsonPrimitive(object, key) && getJsonPrimitive(object, key).isString();
	}
}

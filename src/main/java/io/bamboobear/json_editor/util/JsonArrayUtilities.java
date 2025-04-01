package io.bamboobear.json_editor.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonArrayUtilities {
	private JsonArrayUtilities() {
	}
	
	public static JsonPrimitive[] getJsonPrimitives(JsonArray array) {
		return getJsonPrimitives(array, (jp) -> true);
	}
	
	public static JsonObject[] getJsonObjects(JsonArray array) {
		return getJsonObjects(array, (jo) -> true);
	}
	
	public static JsonArray[] getJsonArrays(JsonArray array) {
		return getJsonArrays(array, (ja) -> true);
	}
	
	public static boolean[] getBooleans(JsonArray array) {
		return getBooleans(array, (b) -> true);
	}
	
	public static Number[] getNumbers(JsonArray array) {
		return getNumbers(array, (n) -> true);
	}
	
	public static byte[] getBytes(JsonArray array) {
		return getBytes(array, (b) -> true);
	}
	
	public static short[] getShorts(JsonArray array) {
		return getShorts(array, (s) -> true);
	}
	
	public static int[] getIntegers(JsonArray array) {
		return getIntegers(array, (i) -> true);
	}
	
	public static long[] getLongs(JsonArray array) {
		return getLongs(array, (l) -> true);
	}
	
	public static float[] getFloats(JsonArray array) {
		return getFloats(array, (f) -> true);
	}
	
	public static double[] getDoubles(JsonArray array) {
		return getDoubles(array, (d) -> true);
	}
	
	public static BigDecimal[] getBigDecimals(JsonArray array) {
		return getBigDecimals(array, (bd) -> true);
	}
	
	public static BigInteger[] getBigIntegers(JsonArray array) {
		return getBigIntegers(array, (bi) -> true);
	}
	
	public static String[] getStrings(JsonArray array) {
		return getStrings(array, (s) -> true);
	}
	
	public static JsonElement[] getJsonElements(JsonArray array, Function<JsonElement, Boolean> condition) {
		ArrayList<JsonElement> list = new ArrayList<JsonElement>();
		for(JsonElement element : array) {
			if(condition.apply(element)) {
				list.add(element);
			}
		}
		return list.toArray(new JsonPrimitive[list.size()]);
	}
	
	public static JsonPrimitive[] getJsonPrimitives(JsonArray array, Function<JsonPrimitive, Boolean> condition) {
		ArrayList<JsonPrimitive> list = new ArrayList<JsonPrimitive>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && condition.apply(jp)) {
				list.add(jp);
			}
		}
		return list.toArray(new JsonPrimitive[list.size()]);
	}
	
	public static JsonObject[] getJsonObjects(JsonArray array, Function<JsonObject, Boolean> condition) {
		ArrayList<JsonObject> list = new ArrayList<JsonObject>();
		for(JsonElement element : array) {
			if(element instanceof JsonObject jo && condition.apply(jo)) {
				list.add(jo);
			}
		}
		return list.toArray(new JsonObject[list.size()]);
	}
	
	public static JsonArray[] getJsonArrays(JsonArray array, Function<JsonArray, Boolean> condition) {
		ArrayList<JsonArray> list = new ArrayList<JsonArray>();
		for(JsonElement element : array) {
			if(element instanceof JsonArray ja && condition.apply(ja)) {
				list.add(ja);
			}
		}
		return list.toArray(new JsonArray[list.size()]);
	}
	
	public static boolean[] getBooleans(JsonArray array, Function<Boolean, Boolean> condition) {
		ArrayList<Boolean> list = new ArrayList<Boolean>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isBoolean()) {
				Boolean b = jp.getAsBoolean();
				if(condition.apply(b)) {
					list.add(b);
				}
			}
		}
		boolean[] booleans = new boolean[list.size()];
		for(int i = 0; i < booleans.length; i++) {
			booleans[i] = list.get(i);
		}
		return booleans;
	}
	
	public static Number[] getNumbers(JsonArray array, Function<Number, Boolean> condition) {
		ArrayList<Number> list = new ArrayList<Number>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Number num = jp.getAsNumber();
				if(condition.apply(num)) {
					list.add(num);
				}
			}
		}
		return list.toArray(new Number[list.size()]);
	}
	
	public static byte[] getBytes(JsonArray array, Function<Byte, Boolean> condition) {
		ArrayList<Byte> list = new ArrayList<Byte>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Byte b = jp.getAsByte();
				if(condition.apply(b)) {
					list.add(b);
				}
			}
		}
		byte[] bytes = new byte[list.size()];
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = list.get(i);
		}
		return bytes;
	}
	
	public static short[] getShorts(JsonArray array, Function<Short, Boolean> condition) {
		ArrayList<Short> list = new ArrayList<Short>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Short s = jp.getAsShort();
				if(condition.apply(s)) {
					list.add(s);
				}
			}
		}
		short[] shorts = new short[list.size()];
		for(int i = 0; i < shorts.length; i++) {
			shorts[i] = list.get(i);
		}
		return shorts;
	}
	
	public static int[] getIntegers(JsonArray array, Function<Integer, Boolean> condition) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Integer i = jp.getAsInt();
				if(condition.apply(i)) {
					list.add(i);
				}
			}
		}
		int[] integers = new int[list.size()];
		for(int i = 0; i < integers.length; i++) {
			integers[i] = list.get(i);
		}
		return integers;
	}
	
	public static long[] getLongs(JsonArray array, Function<Long, Boolean> condition) {
		ArrayList<Long> list = new ArrayList<Long>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Long l = jp.getAsLong();
				if(condition.apply(l)) {
					list.add(l);
				}
			}
		}
		long[] longs = new long[list.size()];
		for(int i = 0; i < longs.length; i++) {
			longs[i] = list.get(i);
		}
		return longs;
	}
	
	public static float[] getFloats(JsonArray array, Function<Float, Boolean> condition) {
		ArrayList<Float> list = new ArrayList<Float>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Float f = jp.getAsFloat();
				if(condition.apply(f)) {
					list.add(f);
				}
			}
		}
		float[] floats = new float[list.size()];
		for(int i = 0; i < floats.length; i++) {
			floats[i] = list.get(i);
		}
		return floats;
	}
	
	public static double[] getDoubles(JsonArray array, Function<Double, Boolean> condition) {
		ArrayList<Double> list = new ArrayList<Double>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				Double d = jp.getAsDouble();
				if(condition.apply(d)) {
					list.add(d);
				}
			}
		}
		double[] doubles = new double[list.size()];
		for(int i = 0; i < doubles.length; i++) {
			doubles[i] = list.get(i);
		}
		return doubles;
	}
	
	public static BigDecimal[] getBigDecimals(JsonArray array, Function<BigDecimal, Boolean> condition) {
		ArrayList<BigDecimal> list = new ArrayList<BigDecimal>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				try {
					BigDecimal bd = jp.getAsBigDecimal();
					if(condition.apply(bd)) {
						list.add(bd);
					}
				} catch (NumberFormatException e) {
				}
			}
		}
		return list.toArray(new BigDecimal[list.size()]);
	}
	
	public static BigInteger[] getBigIntegers(JsonArray array, Function<BigInteger, Boolean> condition) {
		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isNumber()) {
				try {
					BigInteger bi = jp.getAsBigInteger();
					if(condition.apply(bi)) {
						list.add(bi);
					}
				} catch (NumberFormatException e) {
				}
			}
		}
		return list.toArray(new BigInteger[list.size()]);
	}
	
	public static String[] getStrings(JsonArray array, Function<String, Boolean> condition) {
		ArrayList<String> list = new ArrayList<String>();
		for(JsonElement element : array) {
			if(element instanceof JsonPrimitive jp && jp.isString()) {
				String s = jp.getAsString();
				if(condition.apply(s)) {
					list.add(s);
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}
}

package io.bamboobear.json_editor.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonArrayUtilities {
	private JsonArrayUtilities() {}
	
	public static JsonPrimitive[] getJsonPrimitives(JsonArray array) { return getJsonPrimitives(array, jp -> true); }
	public static JsonObject[]    getJsonObjects   (JsonArray array) { return getJsonObjects(array, jo -> true);    }
	public static JsonArray[]     getJsonArrays    (JsonArray array) { return getJsonArrays(array, ja -> true);     }
	public static Number[]        getNumbers       (JsonArray array) { return getNumbers(array, n -> true);         }
	public static int[]           getIntegers      (JsonArray array) { return getIntegers(array, i -> true);        }
	public static long[]          getLongs         (JsonArray array) { return getLongs(array, l -> true);           }
	public static double[]        getDoubles       (JsonArray array) { return getDoubles(array, d -> true);         }
	public static BigDecimal[]    getBigDecimals   (JsonArray array) { return getBigDecimals(array, bd -> true);    }
	public static BigInteger[]    getBigIntegers   (JsonArray array) { return getBigIntegers(array, bi -> true);    }
	public static String[]        getStrings       (JsonArray array) { return getStrings(array, s -> true);         }
	
	public static JsonElement[] getJsonElements(JsonArray array, Predicate<JsonElement> predicate) {
		return array.asList().stream()
				.filter(predicate).toArray(JsonElement[]::new);
	}
	
	public static JsonPrimitive[] getJsonPrimitives(JsonArray array, Predicate<JsonPrimitive> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(predicate).toArray(JsonPrimitive[]::new);
	}
	
	public static JsonObject[] getJsonObjects(JsonArray array, Predicate<JsonObject> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
				.filter(predicate).toArray(JsonObject[]::new);
	}
	
	public static JsonArray[] getJsonArrays(JsonArray array, Predicate<JsonArray> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonArray).map(JsonElement::getAsJsonArray)
				.filter(predicate).toArray(JsonArray[]::new);
	}
	
	public static Number[] getNumbers(JsonArray array, Predicate<Number> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsNumber)
				.filter(predicate).toArray(Number[]::new);
	}
	
	public static int[] getIntegers(JsonArray array, IntPredicate predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).mapToInt(JsonPrimitive::getAsInt)
				.filter(predicate).toArray();
	}
	
	public static long[] getLongs(JsonArray array, LongPredicate predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).mapToLong(JsonPrimitive::getAsLong)
				.filter(predicate).toArray();
	}
	
	public static double[] getDoubles(JsonArray array, DoublePredicate predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).mapToDouble(JsonPrimitive::getAsDouble)
				.filter(predicate).toArray();
	}
	
	public static BigDecimal[] getBigDecimals(JsonArray array, Predicate<BigDecimal> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsBigDecimal)
				.filter(predicate).toArray(BigDecimal[]::new);
	}
	
	public static BigInteger[] getBigIntegers(JsonArray array, Predicate<BigInteger> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsBigDecimal)
				.map(BigDecimal::toBigInteger)
				.filter(predicate).toArray(BigInteger[]::new);
	}
	
	public static String[] getStrings(JsonArray array, Predicate<String> predicate) {
		return array.asList().stream()
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isString).map(JsonPrimitive::getAsString)
				.filter(predicate).toArray(String[]::new);
	}
}

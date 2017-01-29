package com.dalvik.utils.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.dalvik.utils.date.DateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JSONHelper {

	private static Gson gsonInstance;
	private static Gson gsonInstanceNullSerializer;

	private static class ExceptionSerializer implements JsonSerializer<Exception>, JsonDeserializer<Exception> {

		@Override
		public Exception deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new Exception(json.toString());
		}

		@Override
		public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src.getMessage());
		}

	}

	private static class ObjectSerializer implements JsonDeserializer<Object> {

		@Override
		public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			return jsonElementToJavaObject(json);
		}

	}

	private static class DateSerializer implements JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String asString = json.getAsString();
			return DateUtils.getValidDate(asString);
		}

	}

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Exception.class, new ExceptionSerializer());
		builder.registerTypeAdapter(Object.class, new ObjectSerializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonInstance = builder.create();
		builder = new GsonBuilder();
		builder.registerTypeAdapter(Exception.class, new ExceptionSerializer());
		builder.registerTypeAdapter(Object.class, new ObjectSerializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonInstanceNullSerializer = builder.serializeNulls().create();
	}

	public static String convertBeanToJson(Object o) throws SerializationException {
		try {
			return convertBeanToJson(o, false);
		} catch (RuntimeException e) {
			throw new SerializationException("Exception coverting bean to Json: " + o, e);
		}
	}

	public static String convertBeanToJson(Object o, boolean serializeNull) throws SerializationException {
		try {
			if (serializeNull) {
				return gsonInstanceNullSerializer.toJson(o);
			} else {
				return gsonInstance.toJson(o);
			}
		} catch (RuntimeException e) {
			throw new SerializationException("Exception coverting bean to Json: " + o, e);
		}
	}

	public static <T> T convertJsonStringToBean(String json, Class<T> targetClass) throws SerializationException {
		try {
			return gsonInstance.fromJson(json, targetClass);
		} catch (RuntimeException e) {
			throw new SerializationException("Runtime exception found while converting json to bean\nSrc json: " + json,
					e);
		}
	}

	public static <T> T convertJsonStringToBean(String json, Type type) throws SerializationException {
		try {
			return gsonInstance.fromJson(json, type);
		} catch (RuntimeException e) {
			throw new SerializationException("Runtime exception found while converting json to bean\nSrc json: " + json,
					e);
		}
	}

	public static final Map<String, Object> stringToMap(String jsonString) throws SerializationException {
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(jsonString);
			return (Map<String, Object>) jsonElementToJavaObject(jsonElement);
		} catch (RuntimeException e) {
			throw new SerializationException(
					"Runtime Exception while converting json to Map\n Src Json : " + jsonString, e);

		}

	}

	private static final Object jsonElementToJavaObject(JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			return jsonPrimitiveToJavaObject(jsonPrimitive);
		} else if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return jsonObjectToMap(jsonObject);
		} else if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			return jsonArrayToList(jsonArray);
		} else if (jsonElement.isJsonNull()) {
			return null;
		}
		return null;
	}

	private static final Object jsonPrimitiveToJavaObject(JsonPrimitive jsonPrimitive) {
		if (jsonPrimitive.isBoolean()) {
			Boolean value = jsonPrimitive.getAsBoolean();
			return value;
		} else if (jsonPrimitive.isNumber()) {
			Number value = jsonPrimitive.getAsNumber();
			return value;
		} else if (jsonPrimitive.isString()) {
			String value = jsonPrimitive.getAsString();
			return value;
		}
		return null;
	}

	private static final Map<String, Object> jsonObjectToMap(JsonObject jsonObject) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
		for (Entry<String, JsonElement> entry : entrySet) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (value instanceof JsonObject) {
				Map<String, Object> jsonObjectToMap = jsonObjectToMap((JsonObject) value);
				map.put(key, jsonObjectToMap);
			} else if (value instanceof JsonArray) {
				List<Object> jsonArrayToList = jsonArrayToList((JsonArray) value);
				map.put(key, jsonArrayToList);
			} else if (value instanceof JsonNull) {
				map.put(key, null);
			} else if (value instanceof JsonPrimitive) {
				Object primitive = jsonPrimitiveToJavaObject((JsonPrimitive) value);
				map.put(key, primitive);
			} else {
				map.put(key, value.getAsString());
			}
		}
		return map;

	}

	private static final List<Object> jsonArrayToList(JsonArray jsonArray) {
		List<Object> list = new ArrayList<Object>();
		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement instanceof JsonArray) {
				List<Object> jsonArrayToList = jsonArrayToList((JsonArray) jsonElement);
				list.add(jsonArrayToList);
			} else if (jsonElement instanceof JsonObject) {
				Map<String, Object> jsonObjectToMap = jsonObjectToMap((JsonObject) jsonElement);
				list.add(jsonObjectToMap);
			} else if (jsonElement instanceof JsonNull) {
				list.add(null);
			} else if (jsonElement instanceof JsonPrimitive) {
				Object primitive = jsonPrimitiveToJavaObject((JsonPrimitive) jsonElement);
				list.add(primitive);
			} else {
				list.add(jsonElement.getAsString());
			}
		}
		return list;
	}

	public static String mapToString(Map<String, Object> map, boolean formatted) {
		String objectString = buildObjectString(map, "");
		if (!formatted) {
			objectString = trimString(objectString);
		}
		return objectString;
	}

	public static String trimString(String s) {
		StringBuilder sBuilder = new StringBuilder();

		int numberOfQuotes = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == ' ' || ch == '\t' || ch == '\n') {
				if (numberOfQuotes % 2 != 0) {
					sBuilder.append(ch);
				}
			} else {
				if (ch == '"') {
					if (i > 0 && s.charAt(i - 1) != '\\') {
						numberOfQuotes++;
					}
				}
				sBuilder.append(ch);
			}
		}
		return sBuilder.toString();
	}

	private static String buildObjectString(Map<String, Object> map, String lineStart) {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append(lineStart + "{\n");

		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String name = iterator.next();
			outputBuilder.append(lineStart).append('"').append(name).append("\" : ");
			Object object = map.get(name);
			if (object instanceof Map) {
				String printObj = buildObjectString((Map<String, Object>) object, lineStart + "\t");
				outputBuilder.append(printObj);
			} else if (object instanceof List) {
				String arrayString = buildArrayString((List) object, lineStart);
				outputBuilder.append(arrayString);
			} else {
				if (object instanceof String) {
					outputBuilder.append('"').append(object).append('"');
				} else {
					outputBuilder.append(object);
				}
			}
			outputBuilder.append(",\n");
		}
		int lastComma = outputBuilder.lastIndexOf(",");
		if (lastComma != -1) {
			outputBuilder.replace(lastComma, lastComma + 2, "\n" + lineStart + "}");
		} else {
			outputBuilder.append("}");
		}
		return outputBuilder.toString();
	}

	private static String buildArrayString(List list, String lineStart) {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append('[');

		for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof Map) {
				String objectString = buildObjectString((Map) object, lineStart + "\t");
				outputBuilder.append(objectString);
			} else if (object instanceof List) {
				String arrayString = buildArrayString((List) object, lineStart);
				outputBuilder.append(arrayString);
			} else {
				if (object instanceof String) {
					outputBuilder.append('"').append(object).append('"');
				} else {
					outputBuilder.append(object);
				}
			}
			outputBuilder.append(", ");
		}
		int lastComma = outputBuilder.lastIndexOf(",");
		if (lastComma != -1) {
			outputBuilder.replace(lastComma, lastComma + 2, "]");
		} else {
			outputBuilder.append("]");
		}
		return outputBuilder.toString();
	}
}

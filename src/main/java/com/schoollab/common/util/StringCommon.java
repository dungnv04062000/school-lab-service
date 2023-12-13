package com.schoollab.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class StringCommon {
    protected static final Gson gson = new Gson();
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public static String getStringInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || jsonObject.get(key).isJsonNull()) ? null : jsonObject.get(key).getAsString();
    }

    public static JsonArray getArrayInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || !jsonObject.get(key).isJsonArray()) ? null : jsonObject.get(key).getAsJsonArray();
    }

    public static JsonObject getObjectInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || !jsonObject.get(key).isJsonObject()) ? null : jsonObject.get(key).getAsJsonObject();
    }

    public static int getIntInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || jsonObject.get(key).isJsonNull()) ? -1 : jsonObject.get(key).getAsInt();
    }

    public static double getDoubleInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || jsonObject.get(key).isJsonNull()) ? -1 : jsonObject.get(key).getAsDouble();
    }

    public static long getLongInJsonObject(JsonObject jsonObject, String key) {
        return (!jsonObject.has(key) || jsonObject.get(key).isJsonNull()) ? -1 : jsonObject.get(key).getAsLong();
    }

    public static boolean isJson(String Json) {
        try {
            gson.fromJson(Json, Object.class);
            return true;
        } catch (JsonSyntaxException var2) {
            return false;
        }
    }
}

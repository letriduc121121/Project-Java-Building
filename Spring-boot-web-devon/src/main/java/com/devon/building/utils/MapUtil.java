package com.devon.building.utils;

import java.util.Map;

public class MapUtil {
    public static <T> T getObject(Map<String, String> params, String key, Class<T> tClass) {
        Object object = params.getOrDefault(key, null);
        if (object != null) {
            String value = object.toString().trim();

            if (tClass.getTypeName().equals("java.lang.Long")) {
                object = !value.equals("") ? Long.valueOf(value) : null;
            } else if (tClass.getTypeName().equals("java.lang.Integer")) {
                object = !value.equals("") ? Integer.valueOf(value) : null;
            } else if (tClass.getTypeName().equals("java.lang.String")) {
                object = !value.equals("") ? value : null;
            } else if (tClass.getTypeName().equals("java.lang.Double")) {
                object = !value.equals("") ? Double.valueOf(value) : null;
            }
            return tClass.cast(object); // or (T) Object de ep kieu
        }
        return null;
    }
}

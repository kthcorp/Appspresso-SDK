package com.appspresso.core.runtime.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginResult;

public class JsonUtil {
    private final static Log L = AxLog.getLog(JsonUtil.class);

    private static Object convert(Object value) throws JSONException {
        Object result = null;
        if (value == null && JSONObject.NULL.equals(value)) {
            result = null;
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            int length = array.length();

            Object[] resultArray = new Object[length];
            result = resultArray;
            for (int i = 0; i < length; i++) {
                resultArray[i] = convert(array.get(i));
            }
        }
        else if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            Map<String, Object> map = new HashMap<String, Object>(object.length());
            result = map;

            Iterator<?> it = object.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                map.put(key, convert(object.get(key)));
            }
        }
        else if (value instanceof String) {
            result = value;
        }
        else if (value instanceof Integer || value instanceof Long) {
            result = Long.valueOf(value.toString());
        }
        else if (value instanceof Double) {
            result = Double.valueOf(value.toString());
        }
        else if (value instanceof Boolean) {
            result = value;
        }

        return result;
    }

    public static Object fromJson(String json) {
        Object result;
        try {
            JSONTokener tokener = new JSONTokener(json);

            // JSONTokener.nextValue()
            // return JSONObject, JSONArray, String, Boolean, Integer, Long,
            // Double or JSONObject.NULL.
            Object value = tokener.nextValue();
            result = convert(value);
        }
        catch (Exception e) {
            return null;
        }

        return result;
    }

    public static String toJson(Object object) {
        return process(object).toString();
    }

    private static Object process(Object object) {
        if (object == null) { return JSONObject.NULL; }

        if (object instanceof Map) {
            return processMap((Map<?, ?>) object);
        }
        else if (object instanceof Collection) {
            return processList((Collection<?>) object);
        }
        else if (object.getClass().isArray()) {
            return processArray(object);
        }
        else if (object instanceof AxPluginResult) { return process(((AxPluginResult) object)
                .getPluginResult()); }
        if (!(object instanceof Boolean || object instanceof Number || object instanceof String || object instanceof Character)) {
            String o = object.toString();
            try {
                return new JSONObject(o);
            }
            catch (JSONException e) {
                return o;
            }
            catch (NullPointerException e) {
                if (L.isErrorEnabled()) {
                    L.error("invalid parameter", e);
                }
                return null;
            }
        }

        return object;
    }

    private static JSONArray processArray(Object array) {
        JSONArray jsonArray = new JSONArray();

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(array, i);
            jsonArray.put(process(value));
        }
        return jsonArray;
    }

    private static JSONArray processList(Collection<?> collection) {
        JSONArray arr = new JSONArray();
        for (Object value : collection) {
            arr.put(process(value));
        }
        return arr;
    }

    private static JSONObject processMap(Map<?, ?> map) {
        JSONObject obj = new JSONObject();
        try {
            Iterator<?> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();

                Object key = entry.getKey();
                if (key != null) {
                    obj.put(key.toString(), process(entry.getValue()));
                }
            }
        }
        catch (JSONException e) {
            if (L.isErrorEnabled()) {
                L.error("key must be not null", e);
            }
        }

        return obj;
    }
}

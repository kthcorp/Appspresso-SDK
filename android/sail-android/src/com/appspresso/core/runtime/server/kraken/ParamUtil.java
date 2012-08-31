package com.appspresso.core.runtime.server.kraken;

import java.lang.reflect.Array;
import java.util.Map;

import com.appspresso.api.AxError;

/**
 * This class provides convinient methods to access
 * {@link com.appspresso.api.AxPluginContext#getParams()}.
 * 
 */
class ParamUtil {

    static class ParamTypeError extends AxError {
        private static final long serialVersionUID = 1L;

        public ParamTypeError(String message) {
            super(TYPE_MISMATCH_ERR, message);
        }
    }

    static class ParamIndexError extends AxError {
        private static final long serialVersionUID = 1L;

        public ParamIndexError(int paramIndex) {
            super(TYPE_MISMATCH_ERR, paramIndex + ": parameter required");
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T getNamedParam(Object[] params, int index, String name,
            Class<? extends Object> klass) {
        try {
            Object param = params[index];
            if (null == param) return null;

            Map<String, Object> map = (Map<String, Object>) param;
            Object value = map.get(name);

            if (value == null) { return null; }

            if (!klass.isInstance(value)) { throw new ParamTypeError(
                    "parameter type does not match"); }

            return (T) value;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ParamIndexError(index);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T get(Object[] params, int index, Class<? extends Object> klass) {
        try {
            Object param = params[index];
            if (null == param) return null;

            if (!klass.isInstance(param)) { throw new ParamTypeError(
                    "parameter type does not match"); }

            return (T) param;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ParamIndexError(index);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T[] getNamedParamAsArray(Object[] params, int index, String name, Class<T> klass) {
        try {
            Object param = params[index];
            if (null == param) return null;

            Object[] array = null;
            Map<String, Object> map = (Map<String, Object>) param;
            array = (Object[]) map.get(name);

            int length = array.length;
            T[] result = (T[]) Array.newInstance(klass, length);
            for (int i = 0; i < length; i++) {
                Object v = array[i];
                result[i] = v == null ? null : (T) v;
            }

            return result;
        }
        catch (ArrayStoreException e) {
            throw new ParamTypeError(name + ": parameter type does not match");
        }
        catch (ClassCastException e) {
            throw new ParamTypeError(name + ": parameter type does not match");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ParamIndexError(index);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T[] getArray(Object[] params, int index, Class<T> klass) {
        try {
            Object param = params[index];
            if (null == param) return null;

            Object[] array = (Object[]) param;

            int length = array.length;
            T[] result = (T[]) Array.newInstance(klass, length);
            for (int i = 0; i < length; i++) {
                Object v = array[i];
                result[i] = v == null ? null : (T) v;
            }

            return result;
        }
        catch (ArrayStoreException e) {
            throw new ParamTypeError("parameter type does not match");
        }
        catch (ClassCastException e) {
            throw new ParamTypeError("parameter type does not match");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ParamIndexError(index);
        }
    }

    @SuppressWarnings("unchecked")
    static <K, V> Map<K, V> getMap(Object[] params, int index) {
        try {
            return (Map<K, V>) params[index];
        }
        catch (Exception e) {
            throw new ParamIndexError(index);
        }
    }

}

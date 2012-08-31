package com.appspresso.waikiki.deviceapis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.appspresso.api.AxPluginResult;
import com.appspresso.w3.Feature;

public class DeviceapisFeature implements AxPluginResult, Feature {
    public static final String ATTR_URI = "uri";
    public static final String ATTR_REQUIRED = "required";
    public static final String ATTR_PARAMS = "params";

    private final String uri;
    private boolean required;
    private Map<String, String> params;
    private boolean configured;

    public DeviceapisFeature(String uri) throws NullPointerException, IllegalArgumentException {
        if (uri == null) { throw new NullPointerException("The uri must be not null."); }
        if (uri.length() == 0) { throw new IllegalArgumentException(""); // TODO Error message
        }

        this.uri = uri;
    }

    public DeviceapisFeature(Feature feature) throws NullPointerException {
        if (feature == null) { throw new NullPointerException("The feature must be not null."); }

        this.uri = feature.getUri();
        this.required = feature.isRequired();
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Map<String, String> getParams() {
        return (params == null) ? new HashMap<String, String>() : params;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setParams(Map<String, String> params) {
        if (this.params == null) {
            this.params = new HashMap<String, String>();
        }
        else {
            this.params.clear();
        }

        this.params.putAll(params);
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    @Override
    public Object getPluginResult() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(ATTR_URI, getUri());
        result.put(ATTR_REQUIRED, isRequired());
        result.put(ATTR_PARAMS, convertToParameters(getParams()));

        return result;
    }

    public static Parameter[] convertToParameters(Map<String, String> map) {
        Parameter[] params = new Parameter[map.size()];

        int i = 0;
        for (Entry<String, String> param : map.entrySet()) {
            try {
                params[i++] = new Parameter(param.getKey(), param.getValue());
            }
            catch (IllegalArgumentException ignore) {}
        }

        return params;
    }

    public static class Parameter implements AxPluginResult {
        public static final String ATTR_NAME = "name";
        public static final String ATTR_VALUE = "value";

        private String name;
        private String value;

        public Parameter(String name, String value) throws NullPointerException,
                IllegalArgumentException {
            if (name == null) { throw new NullPointerException(
                    "The parameter name must not be null."); }

            if (name.length() == 0) { throw new IllegalArgumentException(); }

            this.name = name;
            this.value = value;
        }

        public Parameter(Parameter param) throws NullPointerException, IllegalArgumentException {
            this(param.getName(), param.getValue());
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public Object getPluginResult() {
            Map<String, String> result = new HashMap<String, String>();
            result.put(ATTR_NAME, name);
            result.put(ATTR_VALUE, value);

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (super.equals(o)) return true;
            if (o == null) return false;

            Parameter param = (Parameter) o;

            if (!getName().equals(param.getName())) return false;
            if (getValue() == null) return param.getValue() == null;

            return getValue().equals(param.getValue());
        }
    }
}

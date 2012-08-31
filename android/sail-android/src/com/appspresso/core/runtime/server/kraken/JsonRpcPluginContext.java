package com.appspresso.core.runtime.server.kraken;

import java.util.HashMap;
import java.util.Map;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.core.runtime.server.kraken.BridgeSessionManager.BridgeSession;

/**
 * This class implements {@link AxPluginContext} for {@link Kraken} webserver.
 * 
 */
class JsonRpcPluginContext implements AxPluginContext {

    private int id;
    private String prefix;
    private String method;
    private Object[] params;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private Map<String, Object> result;
    private BridgeSession session;

    JsonRpcPluginContext(Map<?, ?> requestJson, BridgeSession session) {
        this.session = session;
        id = ((Number) requestJson.get("id")).intValue();
        String method = (String) requestJson.get("method");
        int idx = method.lastIndexOf('.');
        this.prefix = method.substring(0, idx);
        this.method = method.substring(idx + 1);
        this.params = (Object[]) requestJson.get("params");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Object[] getParams() {
        return params;
    }

    public Map<String, Object> getResult() {
        return this.result;
    }

    @Override
    public void sendResult() {
        this.sendResult(null);
    }

    @Override
    public void sendResult(final Object object) {
        makeSuccessResult(object);
    }

    @Override
    public void sendError(final int code, final String message) {
        makeErrorResult(code, message);
    }

    protected BridgeSession getSession() {
        return session;
    }

    protected void makeSuccessResult(final Object object) {
        this.result = new HashMap<String, Object>();
        this.result.put("id", id);
        this.result.put("result", object);
        this.result.put("error", null);
    }

    protected void makeErrorResult(final int code, final String message) {
        Map<String, Object> errorJson = new HashMap<String, Object>();
        errorJson.put("code", code);
        errorJson.put("message", message);

        this.result = new HashMap<String, Object>();
        this.result.put("id", id);
        this.result.put("error", errorJson);
        this.result.put("result", null);
    }

    @Override
    public void sendError(AxError error) {
        sendError(error.getCode(), error.getMessage());
    }

    @Override
    public void sendError(int code) {
        sendError(code, "");
    }

    @Override
    public void sendWatchResult(Object object) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR,
                "sendWatchResult() is not supported in non watch jsonrpc context.");
    }

    @Override
    public void sendWatchError(int code, String message) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR,
                "sendWatchResult() is not supported in non watch jsonrpc context.");
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Object
    @Override
    public Object getParam(int index) {
        return ParamUtil.get(this.params, index, Object.class);
    }

    @Override
    public Object getParam(int index, Object defaultValue) {
        try {
            Object value = getParam(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Object getNamedParam(int index, String name) {
        return ParamUtil.getNamedParam(this.params, index, name, Object.class);
    }

    @Override
    public Object getNamedParam(int index, String name, Object defaultValue) {
        try {
            Object value = getNamedParam(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Boolean

    @Override
    public Boolean getParamAsBoolean(int index) {
        return ParamUtil.get(params, index, Boolean.class);
    }

    @Override
    public Boolean getParamAsBoolean(int index, Boolean defaultValue) {
        try {
            Boolean value = getParamAsBoolean(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean getNamedParamAsBoolean(int index, String name) {
        return ParamUtil.getNamedParam(this.params, index, name, Boolean.class);
    }

    @Override
    public Boolean getNamedParamAsBoolean(int index, String name, Boolean defaultValue) {
        try {
            Boolean value = getNamedParamAsBoolean(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Number
    @Override
    public Number getParamAsNumber(int index) {
        return ParamUtil.get(params, index, Number.class);
    }

    @Override
    public Number getParamAsNumber(int index, Number defaultValue) {
        try {
            Number value = getParamAsNumber(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Number getNamedParamAsNumber(int index, String name) {
        return ParamUtil.getNamedParam(params, index, name, Number.class);
    }

    @Override
    public Number getNamedParamAsNumber(int index, String name, Number defaultValue) {
        try {
            Number value = getNamedParamAsNumber(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // String
    @Override
    public String getParamAsString(int index) {
        return ParamUtil.get(params, index, String.class);
    }

    @Override
    public String getParamAsString(int index, String defaultValue) {
        try {
            String value = getParamAsString(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getNamedParamAsString(int index, String name) {
        return ParamUtil.getNamedParam(params, index, name, String.class);
    }

    @Override
    public String getNamedParamAsString(int index, String name, String defaultValue) {
        try {
            String value = getNamedParamAsString(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Boolean[]
    @Override
    public Boolean[] getParamAsBooleanArray(int index) {
        return ParamUtil.getArray(params, index, Boolean.class);
    }

    @Override
    public Boolean[] getParamAsBooleanArray(int index, Boolean[] defaultValue) {
        try {
            Boolean[] value = getParamAsBooleanArray(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean[] getNamedParamAsBooleanArray(int index, String name) {
        return ParamUtil.getNamedParamAsArray(params, index, name, Boolean.class);
    }

    @Override
    public Boolean[] getNamedParamAsBooleanArray(int index, String name, Boolean[] defaultValue) {
        try {
            Boolean[] value = getNamedParamAsBooleanArray(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // String[]
    @Override
    public String[] getParamAsStringArray(int index) {
        return ParamUtil.getArray(params, index, String.class);
    }

    @Override
    public String[] getParamAsStringArray(int index, String[] defaultValue) {
        try {
            String[] value = getParamAsStringArray(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String[] getNamedParamAsStringArray(int index, String name) {
        return ParamUtil.getNamedParamAsArray(this.params, index, name, String.class);
    }

    @Override
    public String[] getNamedParamAsStringArray(int index, String name, String[] defaultValue) {
        try {
            String[] value = getNamedParamAsStringArray(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Number[]
    @Override
    public Number[] getParamAsNumberArray(int index) {
        return ParamUtil.getArray(params, index, Number.class);
    }

    @Override
    public Number[] getParamAsNumberArray(int index, Number[] defaultValue) {
        try {
            Number[] value = getParamAsNumberArray(index);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Number[] getNamedParamAsNumberArray(int index, String name) {
        return ParamUtil.getNamedParamAsArray(params, index, name, Number.class);
    }

    @Override
    public Number[] getNamedParamAsNumberArray(int index, String name, Number[] defaultValue) {
        try {
            Number[] value = getNamedParamAsNumberArray(index, name);
            return value != null ? value : defaultValue;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Map
    @Override
    public <K, V> Map<K, V> getParamAsMap(int index) {
        return ParamUtil.getMap(params, index);
    }

}

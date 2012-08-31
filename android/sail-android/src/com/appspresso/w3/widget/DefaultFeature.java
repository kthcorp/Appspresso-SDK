package com.appspresso.w3.widget;

import java.util.HashMap;
import java.util.Map;

import com.appspresso.w3.Feature;

class DefaultFeature implements Feature {

    private String uri;
    private boolean required;
    private Map<String, String> params;

    public DefaultFeature(final String name, final boolean required) {
        this.uri = name;
        this.required = required;
        this.params = new HashMap<String, String>(0);
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public Map<String, String> getParams() {
        return this.params;
    }

    public void putParam(final String name, final String value) {
        this.params.put(name, value);
    }

}

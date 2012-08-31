package com.appspresso.core.runtime.plugin;

import java.util.ArrayList;
import java.util.List;

public class DefaultAxPluginXml implements AxPluginXml {

    private String id;
    private String className;
    private List<String> features = new ArrayList<String>(1);

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getScript() {
        return this.id;
    }

    @Override
    public String getPrefix() {
        return this.id;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addFeature(String featureUri) {
        features.add(featureUri);
    }

    public List<String> getFeatures() {
        return features;
    }
}

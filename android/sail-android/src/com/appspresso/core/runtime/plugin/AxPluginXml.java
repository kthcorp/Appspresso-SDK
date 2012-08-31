package com.appspresso.core.runtime.plugin;

import java.util.List;

interface AxPluginXml {

    public String getId();

    public String getScript();

    public String getClassName();

    public String getPrefix();

    public List<String> getFeatures();

}

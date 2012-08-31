package com.appspresso.core.runtime.plugin;

import java.util.List;

import com.appspresso.api.AxPlugin;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.ActivityListener;

public interface PluginManager extends ActivityListener {

    AxPlugin requirePlugin(String pluginId);

    AxPlugin requirePluginWithFeature(String featureUri);

    List<String> getPluginLoadedOrder();

    AxRuntimeContext getRuntimeContext();

}

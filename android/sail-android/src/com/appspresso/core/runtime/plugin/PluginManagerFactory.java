package com.appspresso.core.runtime.plugin;

import com.appspresso.api.AxRuntimeContext;

public class PluginManagerFactory {

    public static PluginManager newPluginManager(AxRuntimeContext runtimeContext) {
        return new DefaultPluginManager(runtimeContext);
        // return new MockPluginManager(axContext);
    }

}

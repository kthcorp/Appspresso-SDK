package com.appspresso.core.runtime.server.kraken;

import java.util.Map;

import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.core.runtime.server.kraken.BridgeSessionManager.BridgeSession;

public class PluginContextFactory {

    public static JsonRpcPluginContext newPluginContext(Map<?, ?> requestJson, BridgeSession session) {
        return new JsonRpcPluginContext(requestJson, session);
    }

    public static JsonRpcAsyncPluginContext newAsyncPluginContext(Map<?, ?> requestJson,
            AxRuntimeContext runtimeContext, BridgeSession session) {
        JsonRpcAsyncPluginContext context = new JsonRpcAsyncPluginContext(requestJson, session);
        context.setRuntimeContext(runtimeContext);
        return context;
    }

    public static AxPluginContext newWatchPluginContext(Map<?, ?> requestJson,
            AxRuntimeContext runtimeContext, BridgeSession session) {
        JsonRpcWatchPluginContext context = new JsonRpcWatchPluginContext(requestJson, session);
        context.setRuntimeContext(runtimeContext);
        return context;
    }

}

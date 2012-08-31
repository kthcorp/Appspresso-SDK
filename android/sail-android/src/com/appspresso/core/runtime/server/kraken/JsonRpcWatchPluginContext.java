package com.appspresso.core.runtime.server.kraken;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.appspresso.api.AxLog;
import com.appspresso.core.runtime.server.kraken.BridgeSessionManager.BridgeSession;
import com.appspresso.core.runtime.util.JsonUtil;

public class JsonRpcWatchPluginContext extends JsonRpcAsyncPluginContext {

    private final static Log L = AxLog.getLog("JsonRpcWatchPluginContext");

    private static final String WATCH_SAMPLE_METHOD = "ax.watch.sample";

    public JsonRpcWatchPluginContext(Map<?, ?> requestJson, BridgeSession session) {
        super(requestJson, session);
    }

    @Override
    public void sendResult(Object object) {
        if (L.isTraceEnabled()) {
            L.trace(String
                    .format("sendResult(%s) called on JsonRpcWatchPluginContext(%d). it will be discarded.",
                            JsonUtil.toJson(object), getId()));
        }
    }

    @Override
    public void sendError(int code, String message) {
        sendWatchError(code, message);
    }

    @Override
    public void sendWatchResult(Object object) {
        super.makeSuccessResult(object);
        sendRpcNotify(WATCH_SAMPLE_METHOD, super.getResult());
    }

    @Override
    public void sendWatchError(int code, String message) {
        super.makeErrorResult(code, message);
        sendRpcNotify(WATCH_SAMPLE_METHOD, super.getResult());
    }

    private void sendRpcNotify(String method, Object... params) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", null);
        map.put("method", method);
        map.put("params", params);
        sendRpcResponse(map);
    }

}

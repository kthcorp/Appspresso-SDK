package com.appspresso.core.runtime.server.kraken;

import java.util.Map;
import org.apache.commons.logging.Log;

import com.appspresso.api.AxLog;
import com.appspresso.api.AxErrorHandler;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.core.runtime.server.kraken.BridgeSessionManager.BridgeSession;

public class JsonRpcAsyncPluginContext extends JsonRpcPluginContext {

    private final static Log L = AxLog.getLog("JsonRpcAsyncPluginContext");

    private static final String BRIDGE_JSONRPC_METHOD = "ax.bridge.jsonrpc";
    private AxRuntimeContext runtimeContext;

    public JsonRpcAsyncPluginContext(Map<?, ?> requestJson, BridgeSession session) {
        super(requestJson, session);
    }

    public void setRuntimeContext(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public void sendResult(Object object) {
        super.makeSuccessResult(object);
        sendRpcResponse(super.getResult());
    }

    @Override
    public void sendError(int code, String message) {
        super.makeErrorResult(code, message);
        sendRpcResponse(super.getResult());
    }

    protected void sendRpcResponse(final Object obj) {
        final BridgeSession session = getSession();
        if (!session.getJavaScriptEvaluationEnabled()) {
            JsonRpcPollHandler.instance().push(session.getSessionID(), obj);
            return;
        }

        AxErrorHandler onError = new AxErrorHandler() {
            @Override
            public void onError(Exception e) {
                if (L.isDebugEnabled()) {
                    L.debug("exception occured while evaluating javascript by loadUrl", e);
                }
                // fall back to long-poll
                session.setJavaScriptEvaluationEnabled(false);
                JsonRpcPollHandler.instance().push(session.getSessionID(), obj);
            }
        };
        runtimeContext.invokeJavaScriptFunction(onError, BRIDGE_JSONRPC_METHOD, obj);
    }

}

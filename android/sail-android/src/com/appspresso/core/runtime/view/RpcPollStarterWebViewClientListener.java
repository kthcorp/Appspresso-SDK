package com.appspresso.core.runtime.view;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebView;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.view.WebViewClientAdapter;

public class RpcPollStarterWebViewClientListener extends WebViewClientAdapter {

    private AxRuntimeContext context;

    public RpcPollStarterWebViewClientListener(AxRuntimeContext context) {
        this.context = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        triggerEvent("startrpcpoll");
    }

    private void triggerEvent(String type) {
        Map<String, Object> event = new HashMap<String, Object>(2);
        event.put("type", type);

        Map<String, Object> notify = new HashMap<String, Object>(3);
        notify.put("id", null);
        notify.put("method", "ax.event.trigger");
        notify.put("params", new Object[] {event});
        context.invokeJavaScriptFunction("ax.bridge.jsonrpc", notify);
    }

}

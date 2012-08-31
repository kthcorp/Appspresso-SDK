package com.appspresso.core.runtime.server.kraken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import android.net.Uri;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPlugin;
import com.appspresso.api.AxPluginContext;
import com.appspresso.core.runtime.plugin.PluginManager;
import com.appspresso.core.runtime.server.kraken.BridgeSessionManager.BridgeSession;
import com.appspresso.core.runtime.util.JsonUtil;

/**
 * This handler executes jsonrpc request with plugin.
 * 
 */
class JsonRpcHandler extends AxRequestHandler {
    private static final String DEF_ENCODING = "UTF-8";

    private static final String MIME_TYPE_JSON = "application/json";

    private final Log L = AxLog.getLog("Plugin");

    private final PluginManager pluginManager;

    private final Map<Integer, HttpResponse> runningContexts = new HashMap<Integer, HttpResponse>();

    public JsonRpcHandler(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public void specificHandler(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Uri uri = getParsedUri(request);
        BridgeSession session = getSessionFromID(getSessionID(uri), request);
        if (isAsync(uri)) {
            handleAsync(request, response, context, isWatch(uri), session);
            return;
        }

        handleSync(request, response, context, session);
    }

    private BridgeSession getSessionFromID(String sessionID, HttpRequest request) {
        BridgeSession session = BridgeSessionManager.lookup(sessionID);
        // need mutex to prevent multi-initialization of same session... but
        // it's *harmless* ;)
        if (!session.getInitialized()) {
            session.setInitialized();
            session.setJavaScriptEvaluationEnabled(isLocalConnection(request));
        }
        return session;
    }

    private String getSessionID(Uri uri) {
        return uri.getQueryParameter("session");
    }

    private void handleSync(HttpRequest request, HttpResponse response, HttpContext context,
            BridgeSession session) throws IOException {
        JsonRpcPluginContext pluginContext =
                PluginContextFactory.newPluginContext(getRequestJson(request), session);

        Integer requestId = pluginContext.getId();

        if (runningContexts.containsKey(requestId)) {
            if (L.isDebugEnabled()) {
                L.debug("@@@@@@@@@@@@@ duplicate request: id=" + requestId + " @@@@@@@@@@@@@");
            }
            synchronized (runningContexts) {
                if (runningContexts.containsKey(requestId)) {
                    runningContexts.put(requestId, response);
                }
            }
            waitResponse(response);
            return;
        }

        runningContexts.put(requestId, response);
        try {
            AxPlugin plugin = pluginManager.requirePlugin(pluginContext.getPrefix());
            if (plugin != null) {
                plugin.execute(pluginContext);
            }
            else {
                pluginContext.sendError(AxError.NOT_FOUND_ERR, "plugin not found");
            }
        }
        catch (Exception e) {
            // XXX Log level
            if (L.isWarnEnabled()) {
                L.warn(e);
            }
            pluginContext.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
        finally {
            sendReponse(runningContexts.get(requestId), pluginContext.getResult());
            synchronized (runningContexts) {
                runningContexts.remove(requestId);
            }
        }
    }

    private void handleAsync(HttpRequest request, HttpResponse response, HttpContext context,
            boolean isWatch, BridgeSession session) throws IOException {
        sendEmptyResponse(response);

        final AxPluginContext pluginContext;
        if (isWatch) {
            pluginContext =
                    PluginContextFactory.newWatchPluginContext(getRequestJson(request),
                            pluginManager.getRuntimeContext(), session);
        }
        else {
            pluginContext =
                    PluginContextFactory.newAsyncPluginContext(getRequestJson(request),
                            pluginManager.getRuntimeContext(), session);
        }

        final AxPlugin plugin = pluginManager.requirePlugin(pluginContext.getPrefix());

        if (plugin == null) {
            pluginContext.sendError(AxError.NOT_FOUND_ERR, "plugin not found");
            return;
        }

        // TODO 비동기 jsonrpc 처리하는 proxy를 만들어서 거기서 스레드 풀로 관리?
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.execute(pluginContext);
                }
                catch (Exception e) {
                    pluginContext.sendError(AxError.UNKNOWN_ERR, e.getMessage());
                }
            }
        }).start();
    }

    private void sendEmptyResponse(HttpResponse response) throws UnsupportedEncodingException {
        sendReponse(response, "");
    }

    private Map<?, ?> getRequestJson(HttpRequest request) {
        HttpEntityEnclosingRequest request1 = (HttpEntityEnclosingRequest) request;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            request1.getEntity().writeTo(baos);
            return (Map<?, ?>) JsonUtil.fromJson(baos.toString(DEF_ENCODING));
        }
        catch (IOException e) {
            return null;
        }
        finally {
            try {
                baos.close();
            }
            catch (IOException ignore) {}
        }
    }

    private Uri getParsedUri(HttpRequest request) {
        return Uri.parse(request.getRequestLine().getUri());
    }

    private boolean isAsync(Uri uri) {
        String async = uri.getQueryParameter("async");

        if (async != null && async.equals("true")) return true;

        return false;
    }

    private boolean isWatch(Uri uri) {
        String watch = uri.getQueryParameter("watch");

        if (watch != null && watch.equals("true")) return true;

        return false;
    }

    private void sendReponse(HttpResponse response, Object result)
            throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(JsonUtil.toJson(result), DEF_ENCODING);
        entity.setContentType(MIME_TYPE_JSON);
        response.setEntity(entity);

        synchronized (response) {
            response.notify();
        }
    }

    private void waitResponse(HttpResponse response) {
        synchronized (response) {
            try {
                response.wait();
            }
            catch (InterruptedException e) {}
        }
    }

}

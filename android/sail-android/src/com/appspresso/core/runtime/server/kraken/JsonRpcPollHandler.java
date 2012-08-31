package com.appspresso.core.runtime.server.kraken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.net.Uri;

import com.appspresso.api.AxLog;
import com.appspresso.core.runtime.util.JsonUtil;

/**
 * jsonrpc polling 요청을 담당. query parameter로 전달되는 브릿지 세션에 따라 각각 json rpc result queue를 관리해 준다. http
 * 처리 도중 결과가 여러개 쌓였을 경우 ax.bridge.jsonrpc.plural notification 메시지로 감싸서 한번에 보낸다.
 */
public class JsonRpcPollHandler implements HttpRequestHandler {

    private static final Log L = AxLog.getLog(JsonRpcPollHandler.class);

    private static JsonRpcPollHandler theInstance;
    private static final long WAIT_FOR_EMPTY_RESPONSE = 1000 * 10;
    private static final String DEF_ENCODING = "UTF-8";
    private static final String JSONRPC_BRIDGE_PLURAL_METHOD = "ax.bridge.jsonrpc.plural";

    private Map<String, Queue<Object>> sessionResults;

    private JsonRpcPollHandler() {
        sessionResults = new HashMap<String, Queue<Object>>(1);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {

        Uri uri = Uri.parse(request.getRequestLine().getUri());
        String session = uri.getQueryParameter("session");

        try {
            Queue<Object> q = getQueue(session);

            synchronized (q) {
                if (q.isEmpty()) {
                    q.wait(WAIT_FOR_EMPTY_RESPONSE);
                }
            }

            List<Object> results = drainQueue(q);
            sendResponse(response, results, session);

        }
        catch (InterruptedException e) {
            throw new IOException();
        }
    }

    private void sendResponse(HttpResponse response, List<Object> results, String session) {
        String json = serialize(results);
        try {
            StringEntity entity = new StringEntity(json, DEF_ENCODING);
            entity.setContentType(MimeTypeUtils.MIME_TYPE_JSON);
            response.setEntity(entity);
        }
        catch (UnsupportedEncodingException e) {
            if (L.isErrorEnabled()) {
                L.error(String.format(
                        "error during respond to request with response [%s] at session [%s]", json,
                        session), e);
            }
        }
    }

    private String serialize(List<Object> results) {
        int len = results.size();
        if (len == 0) { return ""; // 결과 없을 때 보내는 빈 응답. long-poll 스크립트가 다시 접속을 시도하게 된다.
        }
        if (len == 1) { return JsonUtil.toJson(results.get(0)); }

        Map<String, Object> wrap = new HashMap<String, Object>();
        wrap.put("id", null);
        wrap.put("method", JSONRPC_BRIDGE_PLURAL_METHOD);
        wrap.put("params", results);

        return JsonUtil.toJson(wrap);
    }

    private List<Object> drainQueue(Queue<Object> queue) {
        synchronized (queue) {
            List<Object> ret = new ArrayList<Object>(queue.size());
            while (true) {
                Object obj = queue.poll();
                if (obj == null) break;

                ret.add(obj);
            }
            return ret;
        }
    }

    public void push(String session, Object result) {
        Queue<Object> q = getQueue(session);
        synchronized (q) {
            if (!q.add(result)) {
                L.error(String.format("cannot push a result(%s) to session(%s) queue",
                        JsonUtil.toJson(result), session));
            }
            q.notifyAll();
        }
    }

    private Queue<Object> getQueue(String session) {
        synchronized (sessionResults) {
            Queue<Object> q = sessionResults.get(session);
            if (q == null) {
                q = new LinkedList<Object>();
                sessionResults.put(session, q);
            }
            return q;
        }
    }

    public static JsonRpcPollHandler instance() {
        if (theInstance == null) {
            theInstance = new JsonRpcPollHandler();
        }

        return theInstance;
    }

}

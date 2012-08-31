package com.appspresso.screw.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;

import android.text.TextUtils;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxPluginResult;
import com.appspresso.api.DefaultAxPlugin;

/**
 * ax.ext.net
 */
public class NetPlugin extends DefaultAxPlugin {

    private static final Log L = AxLog.getLog(NetPlugin.class);

    public void curl(AxPluginContext ctx) {
        String method = ctx.getNamedParamAsString(0, "method");
        String uri = ctx.getNamedParamAsString(0, "url");
        Map<String, String> headers =
                toMap(ctx.getNamedParam(0, "headers", new HashMap<String, String>(0)));
        Map<String, Object> params =
                toMap(ctx.getNamedParam(0, "params", new HashMap<String, Object>(0)));
        Map<String, String> files =
                toMap(ctx.getNamedParam(0, "files", new HashMap<String, String>(0)));
        String download = ctx.getNamedParamAsString(0, "download", null);
        final String encoding = ctx.getNamedParamAsString(0, "encoding", "UTF-8");
        final boolean sent = ctx.getNamedParamAsBoolean(0, "sent", false);
        final boolean received = ctx.getNamedParamAsBoolean(0, "received", false);

        // closed under anonymous class instance
        final AxPluginContext closedContext = ctx;

        // upload: files 가상 경로를 네이티브 경로로 변환
        Map<String, String> filesNative = new HashMap<String, String>(files.size());
        for (Entry<String, String> entry : files.entrySet()) {
            String fileNative =
                    runtimeContext.getFileSystemManager().toNativePath(entry.getValue());
            if (fileNative == null) { throw new AxError(AxError.INVALID_VALUES_ERR,
                    "invalid upload file path: " + entry.getValue()); }
            filesNative.put(entry.getKey(), fileNative);
        }

        // download: download 가상 경로를 네이티브 경로로 변환
        String downloadNative = null;
        if (!TextUtils.isEmpty(download)) {
            downloadNative = runtimeContext.getFileSystemManager().toNativePath(download);
            if (downloadNative == null) { throw new AxError(AxError.INVALID_VALUES_ERR,
                    "invalid download file path: " + download); }
        }

        // HTTP 요청을 시작하기 전에 그냥~ 리턴~
        // 이후로는 결과를 watch listener를 통해서 전달~
        ctx.sendResult();// ax.nop

        HttpUtils.IHttpProgressListener httpListener = new HttpUtils.IHttpProgressListener() {

            @Override
            public void onSuccess(int status, String data, Map<String, String> headers) {
                if (L.isTraceEnabled()) {
                    L.trace("curl success: status=" + status);
                }
                Map<String, Object> result = new HashMap<String, Object>(3);
                result.put("status", status);
                result.put("data", data);
                result.put("headers", headers);

                closedContext.sendWatchResult(new CurlEvent("success", result));
            }

            @Override
            public void onError(int code, String message) {
                if (L.isTraceEnabled()) {
                    L.trace("curl error: code=" + code + ",message=" + message);
                }

                closedContext.sendWatchError(code, message);
            }

            @Override
            public void onSent(int sentBytes, int totalBytes) {
                if (L.isTraceEnabled()) {
                    L.trace("curl sent: sentBytes=" + sentBytes + " totalBytes = " + totalBytes);
                }

                if (sent) {
                    closedContext.sendWatchResult(new CurlEvent("sent", new int[] {sentBytes,
                            totalBytes}));
                }
            }

            @Override
            public void onReceived(int receivedBytes, int totalBytes) {
                if (L.isTraceEnabled()) {
                    L.trace("curl recevied: receivedBytes=" + receivedBytes + " totalBytes = "
                            + totalBytes);
                }

                if (received) {
                    closedContext.sendWatchResult(new CurlEvent("received", new int[] {
                            receivedBytes, totalBytes}));
                }
            }
        };

        try {
            HttpUtils.execute(runtimeContext, method, uri, headers, params, filesNative,
                    downloadNative, encoding, httpListener);
        }
        catch (AxError e) {
            if (L.isDebugEnabled()) {
                L.debug("curl error", e);
            }
            closedContext.sendWatchError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                L.debug("curl error", e);
            }
            closedContext.sendWatchError(AxError.IO_ERR, e.getMessage());
        }
    }

    public void __removeContext(AxPluginContext ctx) {
        int contextId = ctx.getParamAsNumber(0).intValue();
        if (L.isDebugEnabled()) {
            L.debug("__removeContext id: " + contextId);
        }

        // curl 컨텍스트 객체들을 따로 맵에 넣어서 관리하지 않고 클로저로 그냥 처리하고 있다.
        // 그래서 스크립트로부터의 이 호출은 그냥 무시..
        ctx.sendResult();
    }

    private class CurlEvent implements AxPluginResult {
        private String kind;
        private Object payload;

        public CurlEvent(String kind, Object payload) {
            this.kind = kind;
            this.payload = payload;
        }

        @Override
        public Object getPluginResult() {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("kind", kind);
            result.put("payload", payload);
            return result;
        }
    }

    public void sendMail(AxPluginContext ctx) {
        final String subject = ctx.getNamedParamAsString(0, "subject");
        final String message = ctx.getNamedParamAsString(0, "message", "");
        final String[] to = ctx.getNamedParamAsStringArray(0, "to", new String[0]);
        final String[] cc = ctx.getNamedParamAsStringArray(0, "cc", new String[0]);
        final String[] bcc = ctx.getNamedParamAsStringArray(0, "bcc", new String[0]);
        final String[] attachments =
                ctx.getNamedParamAsStringArray(0, "attachments", new String[0]);
        final long listener = ctx.getNamedParamAsNumber(0, "listener", -1L).longValue();

        String[] nativePathAttachments = null;
        if (attachments != null && attachments.length > 0) {
            nativePathAttachments = new String[attachments.length];
            for (int i = 0; i < attachments.length; i++) {
                nativePathAttachments[i] =
                        runtimeContext.getFileSystemManager().toNativePath(attachments[i]);
            }
        }

        ctx.sendResult();// ax.nop

        try {
            MailUtils.sendMail(runtimeContext.getActivity(), to, cc, bcc, subject, message,
                    nativePathAttachments);
            if (listener != -1) {
                runtimeContext.invokeWatchSuccessListener(listener, true);
            }
        }
        catch (AxError e) {
            if (listener != -1) {
                runtimeContext.invokeWatchErrorListener(listener, e.getCode(), e.getMessage());
            }
        }
        catch (Exception e) {
            if (L.isWarnEnabled()) {
                L.warn("failed to sendMail!", e);
            }
            if (listener != -1) {
                runtimeContext.invokeWatchErrorListener(listener, AxError.IO_ERR, e.getMessage());
            }
        }
    }

    /**
     * TODO: 이 함수는 AxPluginContext에 getParamAsMap(int), getParamAsMap(int, Map),
     * getNamedParamAsMap(int, String), getNamedParamAsMap(int, String, Map)등으로 추가되어야 할 듯...
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> toMap(Object obj) {
        return (Map<K, V>) obj;
    }

}

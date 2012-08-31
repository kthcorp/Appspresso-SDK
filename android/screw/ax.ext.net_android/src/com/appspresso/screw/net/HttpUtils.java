package com.appspresso.screw.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.webkit.CookieManager;

import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;

/**
 * "net" plugin helper methods to support "http".
 * 
 */
public class HttpUtils {

    private static final Log L = AxLog.getLog(HttpUtils.class);

    private static final int DOWNLOAD_BUF_SIZE = 8 * 1024;

    public interface IHttpProgressListener {
        void onSuccess(int status, String data, Map<String, String> headers);

        void onError(int code, String message);

        void onSent(int sentBytes, int totalBytes);

        void onReceived(int receivedBytes, int totalBytes);
    }

    public static void execute(AxRuntimeContext runtimeContext, String method, String uri,
            Map<String, String> headers, Map<String, Object> params, Map<String, String> files,
            String download, String encoding, IHttpProgressListener listener) throws Exception {
        if (L.isTraceEnabled()) {
            L.trace("execute: method=" + method + ",uri=" + uri + ",headers=" + headers
                    + ",params=" + params + ",files=" + files);
        }

        HttpRequest request = HttpClientUtils.newHttpRequest(method, uri);

        HttpProtocolParams.setUserAgent(request.getParams(), runtimeContext.getWebView()
                .getSettings().getUserAgentString());

        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                request.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(uri);
        if (cookie != null && cookie.length() != 0) {
            request.addHeader("Cookie", cookie);
        }

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = null;
            if (files == null || files.isEmpty()) {
                // POST/PUT with params
                // --> application/x-www-form-urlencoded
                List<NameValuePair> nameValuePairs = createNameValuePairs(params);
                entity = new UrlEncodedFormEntity(nameValuePairs, encoding);

                // TODO: support raw data
                // entity = new StringEntity(params.toString(), encoding);
                // entity = new ByteArrayEntity((byte[])params);
                // ...
            }
            else {
                // POST/PUT with files
                // multipart/form-data

                // XXX: upload method bug fix... when setEntity method call,
                // 'Content-Type' auto-set...
                request.removeHeaders("Content-Type");

                entity = createMultipartEntity(params, files, encoding);
            }

            ((HttpEntityEnclosingRequest) request).setEntity(entity);
        }
        else {
            // if ((params != null && !params.isEmpty())
            // || (files != null && !files.isEmpty())) {
            // throw new AxError(AxError.INVALID_VALUES_ERR,
            // "entity is not allowed for " + method + " request.");
            // }
        }

        // XXX: 업로드 진행 상황 통보...
        // listener.onHttpSent(sentBytes, size);

        HttpResponse response = HttpClientUtils.executeHttpRequest(request);

        int responseStatus = response.getStatusLine().getStatusCode();
        Map<String, String> responseHeaders = new HashMap<String, String>();
        for (Header h : response.getAllHeaders()) {
            responseHeaders.put(h.getName(), h.getValue());

            if (h.getName().equals("Set-Cookie")) {
                cookieManager.setCookie(uri, h.getValue());
            }
        }

        HttpEntity responseEntity = response.getEntity();

        // download mode
        if (!TextUtils.isEmpty(download)) {
            File downloadFile = new File(download);
            if (!downloadFile.getParentFile().exists()) {
                downloadFile.getParentFile().mkdirs();
            }

            InputStream responseContentIn = null;
            OutputStream downloadFileOut = null;
            try {
                downloadFileOut = new FileOutputStream(downloadFile);

                // 다운로드 전체 크기... 응답에 Content-Length 헤더가 없으면 -1
                int totalBytes = (int) responseEntity.getContentLength();

                responseContentIn =
                        new BufferedInputStream(responseEntity.getContent(), DOWNLOAD_BUF_SIZE);

                // 다운로드 결과를 한 번에 쓰기...
                // responseEntity.writeTo(downloadFileOut);

                // 다운로드 진행상황을 통보하면서... 조금씩 쓰기...
                int receivedBytes = 0;
                int readBytes;
                byte buf[] = new byte[DOWNLOAD_BUF_SIZE];
                while ((readBytes = responseContentIn.read(buf)) != -1) {
                    downloadFileOut.write(buf, 0, readBytes);
                    receivedBytes += readBytes;
                    listener.onReceived(receivedBytes, totalBytes);
                }

                // TODO: 다운로드 결과 정보를 어떻게 전달할까? 지금은 쌩까~ -_-;;
                // responseEntity.getContentType().getElements()[0].getValue(););
                listener.onSuccess(responseStatus, "", responseHeaders);
            }
            finally {
                if (downloadFileOut != null) {
                    try {
                        downloadFileOut.close();
                        downloadFileOut = null;
                    }
                    catch (IOException ignored) {}
                }
            }
        }
        else {
            String responseData = null;
            if (responseEntity != null) {
                // 응답에 지정된 인코딩이 우선. 지정된 인코딩이 없으면 사용자가 지정한 인코딩...
                String responseEncoding = EntityUtils.getContentCharSet(responseEntity);
                if (responseEncoding == null) {
                    responseEncoding = encoding;
                }

                // FIXME: 큰 데이터, 특히 바이너리 처리... 지금은 OOM~ -_-;
                responseData = EntityUtils.toString(responseEntity, responseEncoding);
            }
            listener.onSuccess(responseStatus, responseData, responseHeaders);
        }
    }

    private static List<NameValuePair> createNameValuePairs(Map<String, Object> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry
                        .getValue().toString()));
            }
        }
        return nameValuePairs;
    }

    // TODO: httpmime/mime4j 의존성 제거!
    private static MultipartEntity createMultipartEntity(Map<String, Object> params,
            Map<String, String> files, String encoding) throws Exception {
        MultipartEntity multipartEntity = new MultipartEntity();
        if (params != null) {
            for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                StringBody paramBody =
                        new StringBody(paramEntry.getValue().toString(), Charset.forName(encoding));
                multipartEntity.addPart(paramEntry.getKey(), paramBody);
            }
        }
        if (files != null) {
            for (Map.Entry<String, String> fileEntry : files.entrySet()) {
                FileBody fileBody = new FileBody(new File(fileEntry.getValue()));
                multipartEntity.addPart(fileEntry.getKey(), fileBody);
            }
        }
        return multipartEntity;
    }

}

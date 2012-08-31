package com.appspresso.core.runtime.server.kraken;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.appspresso.api.AxLog;

/**
 * This class provides a simplest transparent http proxy with <a href="http://hc.apache.org">apache
 * httpcomponents</a>.
 * 
 */
class XDProxyHandler implements HttpRequestHandler {
    private static final Log L = AxLog.getLog(XDProxyHandler.class);

    public static final String CONTEXT_PATH = "/xdproxy";

    private static final Set<String> NO_PROXY_HEADERS = new HashSet<String>() {

        private static final long serialVersionUID = -1L;

        {
            add("connection");
            add("host");
            add("keep-alive");
            add("proxy-authenticate");
            add("proxy-authorization");
            add("proxy-connection");
            add("te");
            add("transfer-encoding");
            add("trailer");
            add("upgrade");
            add("referer");// XXX:
        }

    };

    private static final Set<String> OVERRIDE_PROXY_HEADERS = new HashSet<String>() {

        private static final long serialVersionUID = -1L;

        {
            add("date");
            add("server");
            add("content-type");
            add("content-length");
        }

    };

    private static final String HOST_HEADER = "Host";

    private static final String CONNECTION_HEADER = "Connection";

    private static final String CONNECTION_CLOSE = "close";

    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        String remoteUri = request.getRequestLine().getUri().substring(CONTEXT_PATH.length() + 1);
        L.trace("proxying... remoteUri=" + remoteUri);

        try {
            HttpClient remoteHttpClient = HttpClientUtils.newHttpClient();
            HttpHost remoteHost = HttpClientUtils.newHttpHost(remoteUri);
            HttpRequest remoteRequest =
                    HttpClientUtils.newHttpRequest(request.getRequestLine().getMethod(), remoteUri);

            // check connection header
            String connectionHeader = null;
            if (request.containsHeader(CONNECTION_HEADER)) {
                connectionHeader =
                        request.getFirstHeader(CONNECTION_HEADER).getValue().toLowerCase();
                if (CONNECTION_KEEP_ALIVE.equals(connectionHeader)
                        || CONNECTION_CLOSE.equals(connectionHeader)) {
                    connectionHeader = null;
                }
            }

            // rewrite host request header
            remoteRequest.addHeader(HOST_HEADER, remoteHost.toHostString());

            // set(copy) request headers
            for (Header header : request.getAllHeaders()) {
                String headerNameLower = header.getName().toLowerCase();
                if (NO_PROXY_HEADERS.contains(headerNameLower)) {
                    continue;
                }
                // XXX: skip connection header with keep alive or close
                if (connectionHeader != null && connectionHeader.indexOf(headerNameLower) >= 0) {
                    continue;
                }
                remoteRequest.addHeader(header);
                L.trace("proxy request header: " + header);
            }

            // set(copy) request content
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                ((HttpEntityEnclosingRequest) remoteRequest).setEntity(entity);
            }

            // execute remote request(real request)
            HttpResponse remoteResponse = remoteHttpClient.execute(remoteHost, remoteRequest);

            // set(copy) response status
            // XXX: how can set status message?
            // response.setStatus(code, message) is deprecated!
            // response.sendError(code, message) generates html content!
            response.setStatusLine(remoteResponse.getStatusLine());

            // set(copy) response headers
            for (Header header : remoteResponse.getAllHeaders()) {
                String headerNameLower = header.getName().toLowerCase();
                if (NO_PROXY_HEADERS.contains(headerNameLower)) {
                    continue;
                }
                if (OVERRIDE_PROXY_HEADERS.contains(headerNameLower)) {
                    // override (servlet) with remote response header
                    response.setHeader(header);
                }
                else {
                    response.addHeader(header);
                }
                if (L.isTraceEnabled()) {
                    L.trace("proxy response header: " + header);
                }
            }

            // emit(copy) response content
            HttpEntity remoteResponseEntity = remoteResponse.getEntity();
            if (remoteResponseEntity != null) {
                response.setEntity(remoteResponseEntity);
            }
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                L.debug("proxying failed: remoteUri=" + remoteUri, e);
            }
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        finally {}
    }

}

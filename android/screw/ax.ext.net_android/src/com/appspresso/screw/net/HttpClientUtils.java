package com.appspresso.screw.net;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;

/**
 * This class provides some utility methods to make easy to use apache httpcore.
 * <p/>
 * NOTE: this class is designed for internal use only.
 * 
 */
public class HttpClientUtils {

    /**
     * create a {@link HttpClient} that supports both http and https.
     * 
     * @return a HttpClient object
     */
    public static HttpClient newHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        // XXX: apache httpcore in android doesn't support "https"
        // we need some tricky code to support "https"
        SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
        if (schemeRegistry.get("https") == null) {
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        }
        return httpClient;
    }

    /**
     * create a {@link HttpRequest} for the specified method and uri.
     * 
     * @param method the http method string, such as "GET", "POST", "PUT", and "DELETE".
     * @param uri the request uri string
     * @return a HttpRequest object
     * @throws HttpException
     */
    public static HttpRequest newHttpRequest(String method, String uri) throws HttpException {
        return new DefaultHttpRequestFactory().newHttpRequest(method, uri);
    }

    /**
     * create a {@link HttpHost} for the specified uri.
     * 
     * @param uri an uri string that contains host and port infomation.
     * @return a HttpHost object
     */
    public static HttpHost newHttpHost(String uri) {
        URI remoteURI = URI.create(uri);
        return new HttpHost(remoteURI.getHost(), remoteURI.getPort(), remoteURI.getScheme());
    }

    /**
     * execute the specified request to the specified host using the specified client.
     * 
     * @param client a http client to use
     * @param host a remote(destination server) http host
     * @param request a http request to execute
     * @return a HttpResponse object
     * @throws IOException
     */
    public static HttpResponse executeHttpRequest(HttpClient client, HttpHost host,
            HttpRequest request) throws IOException {
        return client.execute(host, request);
    }

    /**
     * execute the specified request using the specified client.
     * 
     * @param client a http client to use
     * @param request a http request to execute, that contains host and port information to
     *        designate a http host.
     * @return a HttpResponse object
     * @throws IOException
     */
    public static HttpResponse executeHttpRequest(HttpClient client, HttpRequest request)
            throws IOException {
        return executeHttpRequest(client, newHttpHost(request.getRequestLine().getUri()), request);
    }

    /**
     * execute the specified request using the default http client.
     * 
     * @param request a http request to execute, that contains host and port information to
     *        designate a http host.
     * @return a HttpResponse object
     * @throws IOException
     */
    public static HttpResponse executeHttpRequest(HttpRequest request) throws IOException {
        return executeHttpRequest(newHttpClient(), request);
    }

}

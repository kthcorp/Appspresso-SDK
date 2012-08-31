package com.appspresso.core.runtime.server.kraken;

import org.apache.http.HttpResponse;

public class HttpHeaderUtils {
    public static void setCacheHeader(HttpResponse response) {
        // 1 year expire date :)
        response.addHeader("Cache-Control", "public, max-age=31557600");
    }

    public static void setNoCacheHeader(HttpResponse response) {
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    }

    // TODO Last-Modified? ETag?
}

package com.appspresso.core.runtime.server.kraken;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.appspresso.core.runtime.util.AxSessionKeyHolder;
import com.appspresso.internal.AxConfig;

/**
 * AuthenticateWebChromeClientListener 에서 발급한 AXSESSIONID 쿠키 값과 리퀘스트의 쿠키 값을 비교해서 컨텐츠를 주거나, 403 에러
 * 처리를 한다. 릴리즈 모드에서만 동작하며, 외부 앱이 kraken에 접속해서 정보를 얻지 못하게 한다.
 * 
 */
public abstract class AxRequestHandler implements HttpRequestHandler {

    private boolean developMode;

    public AxRequestHandler() {
        developMode = AxConfig.getAttributeAsBoolean("app.devel", false);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {

        if (developMode || getClientSessionId(request).equals(AxSessionKeyHolder.instance().key())) {
            specificHandler(request, response, context);
            return;
        }

        Kraken.sendErrorPage(response, HttpStatus.SC_FORBIDDEN, "403 Forbidden");
    }

    private String getClientSessionId(HttpRequest request) {
        Header[] headers = request.getHeaders("Cookie");
        for (Header header : headers) {
            String[] cookies = header.getValue().split(";");
            for (String cookie : cookies) {
                String[] pair = cookie.split("=");
                if (pair[0].trim().equals("AXSESSIONID")) return pair[1].trim();
            }
        }
        return "[not exist]";
    }

    // widgetView.configureSettings() 에서 설정하는 Appspresso/[ver] 값이 UA 문자열에 있는지
    // 확인해서 로컬 접속인지 외부 접속인지 판단한다.
    protected boolean isLocalConnection(HttpRequest request) {
        Header[] headers = request.getHeaders("User-Agent");
        for (Header header : headers) {
            String ua = header.getValue();
            if (ua.indexOf("Appspresso/") < 0) return false;
        }

        return true;
    }

    protected void setCacheHeader(HttpRequest request, HttpResponse response) {
        if (isLocalConnection(request)) {
            HttpHeaderUtils.setCacheHeader(response);
            return;
        }

        // ADE 모드에서 크롬이 접속할 경우 캐쉬하지 못하게 막는다.
        HttpHeaderUtils.setNoCacheHeader(response);
    }

    abstract protected void specificHandler(HttpRequest request, HttpResponse response,
            HttpContext context) throws HttpException, IOException;

}

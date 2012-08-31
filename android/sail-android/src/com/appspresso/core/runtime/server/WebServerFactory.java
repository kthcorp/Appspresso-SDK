package com.appspresso.core.runtime.server;

import com.appspresso.core.runtime.server.kraken.KrakenWebServer;
import com.appspresso.core.runtime.widget.WidgetAgent;
import com.appspresso.internal.AxConfig;

/**
 * This class provides a "factory" for {@link IWebServer}.
 * <p/>
 * <code>
 * in  appspress-config.properties:
 * webServerClass=YourWebServer_FQCN
 * </code> <code>
 * IWebServer ws = WebServerFactory.newWebServer(widgetAgent);
 * </code>
 * 
 */
public class WebServerFactory {

    // use httpcomponents based webserver
    private static final String DEF_WEB_SERVER_CLASS_NAME = AxConfig.getAttribute(
            "android.config.server.class", KrakenWebServer.class.getName());

    public static IWebServer newWebServer(WidgetAgent widgetAgent) {
        try {
            return (IWebServer) Class.forName(DEF_WEB_SERVER_CLASS_NAME)
                    .getConstructor(WidgetAgent.class).newInstance(widgetAgent);
        }
        catch (Exception e) {
            // throw new RuntimeException("failed to create web server!");
            // fallback
            return new KrakenWebServer(widgetAgent);
        }
    }

}

package com.appspresso.core.runtime.server.kraken;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.os.Bundle;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxLog;
import com.appspresso.api.activity.ActivityAdapter;
import com.appspresso.core.runtime.plugin.PluginManager;
import com.appspresso.core.runtime.server.IWebServer;
import com.appspresso.core.runtime.widget.WidgetAgent;
import com.appspresso.internal.AxConfig;

/**
 * This class implements {@link com.appspresso.core.runtime.server.IWebServer} using {@link Kraken}.
 * 
 */
public class KrakenWebServer extends ActivityAdapter implements IWebServer {

    private static final String DEF_HOST = AxConfig.getAttribute("kraken.host", "localhost");
    private static final int DEF_PORT = AxConfig.getAttributeAsInteger("kraken.port", 0); // 0 for
                                                                                          // auto
    private static final String ANY_LOCAL_HOST = "0.0.0.0";

    static final String DEF_SERVER_PORT_KEY = "kraken";

    private static final Log L = AxLog.getLog(KrakenWebServer.class);

    private final WidgetAgent widgetAgent;
    private final Kraken server;

    private AssetsHandler assetsHandler;

    public KrakenWebServer(WidgetAgent widgetAgent) {
        this.widgetAgent = widgetAgent;

        int port = -1;
        if (DEF_PORT == 0) {
            port =
                    SameOriginRestrictionResolver.restoreWebServerPort(widgetAgent
                            .getAxRuntimeContext().getActivity(), DEF_SERVER_PORT_KEY);
            if (port == -1) {
                port = DEF_PORT;
            }
        }
        else {
            port = DEF_PORT;
        }

        String host =
                AxConfig.getAttributeAsBoolean("app.devel", false) ? ANY_LOCAL_HOST : DEF_HOST;
        this.server = new Kraken(host, port);
    }

    @Override
    public String getHost() {
        return server.getHost();
    }

    @Override
    public int getPort() {
        return server.getPort();
    }

    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {
        L.trace("create");

        AxRuntimeContext runtimeContext = widgetAgent.getAxRuntimeContext();
        PluginManager pluginManager = widgetAgent.getPluginManager();

        server.addHandler("/appspresso/plugin/*", new JsonRpcHandler(pluginManager));
        server.addHandler("/appspresso/rpcpoll/*", JsonRpcPollHandler.instance());

        // for new filesystem
        server.addHandler("/appspresso/file/*",
                new AxFileHandler(runtimeContext.getFileSystemManager()));

        server.addHandler("/appspresso/contact/*", new ContactPhotoURIHandler(runtimeContext)); // XXX
                                                                                                // for
                                                                                                // Contact.photoURI

        // server.addHandler(XDProxyHandler.CONTEXT_PATH + "/*", new
        // XDProxyHandler());
        server.addHandler("/appspresso/appspresso.js", new AxScriptHandler(runtimeContext,
                pluginManager));

        // TODO: separate on-the-fly support into different class to avoid
        // redundant class on release mode
        String nessieProject = AxConfig.getAttribute("nessie.project");
        if (nessieProject != null) {
            String nessieHost = AxConfig.getAttribute("nessie.host");
            int nessiePort = AxConfig.getAttributeAsInteger("nessie.port", -1);
            if (L.isInfoEnabled()) {
                L.info("on-the-fly mode is enabled using nessie: host=" + nessieHost + ",port="
                        + nessiePort + ",project=" + nessieProject);
            }
            server.addHandler("*", new OnTheFlyHandler(nessieHost, nessiePort, nessieProject));
        }
        else {
            this.assetsHandler = new AssetsHandler(runtimeContext, widgetAgent.getBaseDir());
            server.addHandler("*", this.assetsHandler);
        }

        int oldPort, newPort;

        oldPort = SameOriginRestrictionResolver.restoreWebServerPort(activity, DEF_SERVER_PORT_KEY);
        server.init();
        newPort = server.getPort();

        if (oldPort > 0 && oldPort != newPort) {
            SameOriginRestrictionResolver.resolve(activity, widgetAgent.getAxRuntimeContext()
                    .getWebView(), oldPort, newPort);
        }
        SameOriginRestrictionResolver.saveWebServerPort(activity, DEF_SERVER_PORT_KEY, newPort);

        // XXX:
        server.start();
    }

    @Override
    public void onActivityRestart(final Activity activity) {
        L.trace("restart");

        // XXX:
        // server.start();
    }

    @Override
    public void onActivityStart(final Activity activity) {
        L.trace("start");
    }

    @Override
    public void onActivityResume(final Activity activity) {
        L.trace("resume");

        // XXX:
        // server.start();
    }

    @Override
    public void onActivityPause(final Activity activity) {
        L.trace("pause");
    }

    @Override
    public void onActivityStop(final Activity activity) {
        L.trace("stop");

        // XXX:
        // server.stop();
    }

    @Override
    public void onActivityDestroy(Activity activity) {
        L.trace("destroy");

        // XXX:
        server.stop();

        server.destroy();
    }

}

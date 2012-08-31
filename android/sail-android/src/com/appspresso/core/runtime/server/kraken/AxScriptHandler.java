package com.appspresso.core.runtime.server.kraken;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import android.content.res.AssetManager;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxLog;
import com.appspresso.core.runtime.plugin.PluginManager;
import com.appspresso.internal.AxConfig;

class AxScriptHandler extends AxRequestHandler {
    private static final Log L = AxLog.getLog(AxScriptHandler.class.getSimpleName());

    // build/build-common.xml build.keel target
    private final String PLUGIN_JS_ASSET_PATH = "ax_scripts";
    private final String KEEL_JS_ASSET_PATH = PLUGIN_JS_ASSET_PATH + File.separator + "keel.js";

    private final String keelFinallyScript = "delete ax._;";

    private String keelScript = null;
    private String pluginScript = null;

    private final AxRuntimeContext runtimeContext;
    private final List<String> pluginOrder;

    public AxScriptHandler(AxRuntimeContext runtimeContext, PluginManager pluginManager) {
        this.runtimeContext = runtimeContext;
        this.pluginOrder = pluginManager.getPluginLoadedOrder();
    }

    @Override
    public void specificHandler(final HttpRequest request, HttpResponse response,
            HttpContext context) throws HttpException, IOException {

        final String script = getAppspressoScript();

        response.setStatusCode(HttpStatus.SC_OK);
        EntityTemplate entity = new EntityTemplate(new ContentProducer() {
            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outstream));
                writer.write(script);
                writer.flush();

                // TODO: override default log and console url
                String nessieProject = AxConfig.getAttribute("nessie.project");
                if (nessieProject != null && isLocalConnection(request)) {
                    String nessieHost = AxConfig.getAttribute("nessie.host");
                    int nessiePort = AxConfig.getAttributeAsInteger("nessie.port", -1);
                    String nessieUrl = String.format("http://%s:%d", nessieHost, nessiePort);
                    StringBuilder script = new StringBuilder();
                    script.append("window._APPSPRESSO_CONSOLE_URL=").append(
                            String.format("\"%s/appspresso/CON$/%s\";", nessieUrl, nessieProject));
                    script.append("window._APPSPRESSO_LOG_URL=").append(
                            String.format("\"%s/appspresso/LOG$/%s\";", nessieUrl, nessieProject));
                    script.append("window._APPSPRESSO_DEBUG_SESSION_ISSUE_URL=").append(
                            String.format("\"%s/appspresso/debug/session/issue/%s\";", nessieUrl,
                                    nessieProject));
                    script.append("ax.console.initDebugSession();");
                    script.append("ax.console.startRedirect();");
                    writer.write(script.toString());
                    writer.flush();
                }

                // ADE 등 외부에서 접속했을 경우 jsonrpc polling 시작하게 한다.
                if (!isLocalConnection(request)) {
                    writer.write("ax.bridge.rpcpoll.start();");
                    writer.flush();
                }
            }
        });

        entity.setContentType(MimeTypeUtils.MIME_TYPE_JS);
        setCacheHeader(request, response);
        response.setEntity(entity);
    }

    // widgetView.configureSettings() 에서 설정하는 Appspresso/[ver] 값이 UA 문자열에 있는지
    // 확인해서 로컬 접속인지 외부 접속인지 판단한다.
    protected boolean isLocalConnection(HttpRequest request) {
        return super.isLocalConnection(request);
    }

    public String getAppspressoScript() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(getKeelScript()).append("\n");

        builder.append(getPluginScripts()).append("\n");

        builder.append(this.keelFinallyScript);
        return builder.toString();
    }

    private String getKeelScript() throws IOException {
        if (keelScript == null) {
            InputStream in = null;
            try {
                in = this.runtimeContext.getActivity().getAssets().open(KEEL_JS_ASSET_PATH);
                keelScript = readAsString(in);

                if (L.isTraceEnabled()) {
                    L.trace("load keel script");
                }

            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        return keelScript;
    }

    private String getPluginScripts() throws IOException {
        if (this.pluginScript != null) { return this.pluginScript; }

        AssetManager assetManager = runtimeContext.getActivity().getAssets();
        StringBuilder builder = new StringBuilder();
        for (String name : this.pluginOrder) {
            InputStream in = null;

            try {
                in = assetManager.open(PLUGIN_JS_ASSET_PATH + File.separator + name + ".js");
                String script = readAsString(in);
                builder.append(script);

                if (L.isTraceEnabled()) {
                    L.trace("load plugin script : " + name);
                }
            }
            catch (FileNotFoundException e) {
                if (L.isTraceEnabled()) {
                    L.trace("fail to load plugin script : " + name);
                }
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        this.pluginScript = builder.toString();
        return this.pluginScript;
    }

    private String readAsString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
        byte[] bytes = new byte[1024 * 2];
        int length = -1;
        while ((length = in.read(bytes)) > -1) {
            out.write(bytes, 0, length);
        }
        return out.toString(/* utf-8 */);
    }
}

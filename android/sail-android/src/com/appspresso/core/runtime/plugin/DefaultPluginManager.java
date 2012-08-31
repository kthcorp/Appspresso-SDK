package com.appspresso.core.runtime.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPlugin;
import com.appspresso.api.activity.ActivityAdapter;

class DefaultPluginManager extends ActivityAdapter implements PluginManager {
    private static final Log L = AxLog.getLog(DefaultPluginManager.class);

    protected final Map<String, String> pluginClassName = new HashMap<String, String>();// axp
                                                                                        // id
                                                                                        // -
                                                                                        // class
                                                                                        // name
    protected Map<String, String> pluginForFeature = new HashMap<String, String>(); // feature
                                                                                    // uri
                                                                                    // -
                                                                                    // axp
                                                                                    // id
    protected Map<String, AxPlugin> pluginInstances = new HashMap<String, AxPlugin>(); // axp
                                                                                       // id
                                                                                       // -
                                                                                       // AxPlugin
    protected final List<String> pluginLoadedOrder = new ArrayList<String>();

    protected AxRuntimeContext runtimeContext;

    private final static String AX_PLUGINS = "ax_plugins";

    DefaultPluginManager(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public AxPlugin requirePlugin(String pluginId) {
        AxPlugin plugin = this.pluginInstances.get(pluginId);

        if (L.isTraceEnabled()) {
            L.trace("requirePlugin : " + pluginId);
        }

        if (plugin == null) {
            String className = this.pluginClassName.get(pluginId);

            plugin = newPluginInstance(className);

            if (plugin != null) {

                if (L.isTraceEnabled()) {
                    L.trace("new " + className + " instance");
                }

                pluginInstances.put(pluginId, plugin);
                pluginLoadedOrder.add(pluginId);
            }
        }

        return plugin;
    }

    @Override
    public AxPlugin requirePluginWithFeature(String featureUri) {
        String pluginId = this.pluginForFeature.get(featureUri);
        return requirePlugin(pluginId);
    }

    @Override
    public List<String> getPluginLoadedOrder() {
        return this.pluginLoadedOrder;
    }

    protected AxPlugin newPluginInstance(String className) {
        try {
            AxPlugin instance = (AxPlugin) Class.forName(className).newInstance();
            instance.activate(this.runtimeContext);
            return instance;
        }
        catch (Exception e) {
            if (L.isWarnEnabled()) {
                L.warn("Failed to create instance for of plugin : " + className);
            }
        }
        return null;
    }

    @Override
    public void onActivityCreate(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreate(activity, savedInstanceState);

        loadBuiltInPlugin();

        loadPlugins(activity.getAssets());

        for (String prefix : pluginClassName.keySet()) {
            requirePlugin(prefix);
        }
    }

    private void loadBuiltInPlugin() {
        AxPlugin widgetPlugin = new WidgetPreferencesPlugin();
        widgetPlugin.activate(this.runtimeContext);
        pluginInstances.put(WidgetPreferencesPlugin.NAME, widgetPlugin);

        AxPlugin basicDeviceStatus = new BuiltinDeviceStatusPlugin();
        basicDeviceStatus.activate(this.runtimeContext);
        pluginInstances.put(BuiltinDeviceStatusPlugin.NAME, basicDeviceStatus);
    }

    @Override
    public void onActivityDestroy(Activity activity) {
        Map<String, AxPlugin> plugins = this.pluginInstances;

        this.pluginInstances = null;
        Collection<AxPlugin> pluginInstances = plugins.values();
        for (AxPlugin plugin : pluginInstances) {
            if (plugin != null) {
                plugin.deactivate(runtimeContext);
            }
            // XXX
        }
        plugins.clear();

        super.onActivityDestroy(activity);
    }

    private void loadPlugins(AssetManager assetManager) {
        InputStream axPluginXmlStream = null;
        try {
            String[] axpluginXml = assetManager.list(AX_PLUGINS);
            for (int i = 0; i < axpluginXml.length; i++) {
                axPluginXmlStream = assetManager.open(AX_PLUGINS + File.separator + axpluginXml[i]);
                parsePluginInfo(axPluginXmlStream);

                if (L.isTraceEnabled()) {
                    L.trace("parse to " + axpluginXml[i]);
                }
            }
        }
        catch (IOException e) {
            if (L.isWarnEnabled()) {
                L.warn("cannot open plugin information file", e);
            }
        }
        finally {
            if (axPluginXmlStream != null) {
                try {
                    axPluginXmlStream.close();
                }
                catch (IOException ignore) {}
            }
        }
    }

    private void parsePluginInfo(InputStream in) {
        try {
            AxPluginXmlParser parser = AxPluginXmlParserFactory.newAxPluginXmlParser();
            AxPluginXml pluginXml = parser.parseAxPluginXml(in);
            String className = pluginXml.getClassName();
            if (!TextUtils.isEmpty(className)) {
                pluginClassName.put(pluginXml.getPrefix(), pluginXml.getClassName());
            }
            for (String featureUri : pluginXml.getFeatures()) {
                pluginForFeature.put(featureUri, pluginXml.getPrefix());
            }
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                L.debug("failed to parse plugin info!", e);
            }
        }
    }

    @Override
    public AxRuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

}

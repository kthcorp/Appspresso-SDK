package com.appspresso.core.runtime.widget;

import com.appspresso.api.AxPlugin;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.ActivityListener;
import com.appspresso.core.runtime.plugin.PluginManager;
import com.appspresso.core.runtime.view.WidgetView;
import com.appspresso.w3.Widget;

/**
 * This is the "core" of sail.
 * 
 * The widget agent injects "keel" to WebView, and accepts requests from "keel"(running on WebView),
 * and processes them using {@link AxPlugin}s, and returns responses.
 * 
 */
public interface WidgetAgent extends ActivityListener {

    WidgetView getWidgetView();

    Widget getWidget();

    AxRuntimeContext getAxRuntimeContext();

    PluginManager getPluginManager();

    String getBaseDir();

}

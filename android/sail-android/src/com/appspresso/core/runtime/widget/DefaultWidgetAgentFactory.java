package com.appspresso.core.runtime.widget;

import com.appspresso.core.runtime.server.IWebServer;
import com.appspresso.core.runtime.view.WidgetView;

/**
 * This class creates an instance of {@link DefaultWidgetAgent} as an implementaion of
 * {@link WidgetAgent}.
 * 
 * NOTE: The widget agent provides features using built-in {@link IWebServer}.
 * 
 * @see DefaultWidgetAgent
 */
public class DefaultWidgetAgentFactory extends WidgetAgentFactory {

    @Override
    public WidgetAgent newWidgetAgent(WidgetView widgetView) {
        // if(...) {
        // return new LoadUrlWidgetAgent();
        // } else if(...){
        // return new JavaScriptInterfaceWidgetAgent();
        // } else if(...){
        // return new WebServerWidgetAgent();
        // }
        // return
        // Class.forName(System.getPropety("com.kthcrop.wp.WidgetAgentImpl",
        // WidgetAgent.class.getName())).newInstance();
        return new DefaultWidgetAgent(widgetView);
    }

}

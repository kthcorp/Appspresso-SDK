package com.appspresso.core.runtime.widget;

import com.appspresso.core.runtime.view.WidgetView;

/**
 * This class creates an instance of {@link WidgetAgent}.
 * 
 * <code>
 * IWidgetView widgetView = WidgetViewFactory().newInstance().newWidgetView(activity);
 * IWidgetAgent widgetAgent = WidgetAgentFactory().newInstance().newWidgetAgent(widgetView);
 * </code>
 * 
 */
public abstract class WidgetAgentFactory {

    public static WidgetAgentFactory newInstance() {
        try {
            return (WidgetAgentFactory) Class.forName(
                    System.getProperty("com.kthcorp.wp.WidgetAgentFactoryImpl",
                            DefaultWidgetAgentFactory.class.getName())).newInstance();
        }
        catch (Exception e) {
            return new DefaultWidgetAgentFactory();
        }
    }

    public abstract WidgetAgent newWidgetAgent(WidgetView widgetView);

}

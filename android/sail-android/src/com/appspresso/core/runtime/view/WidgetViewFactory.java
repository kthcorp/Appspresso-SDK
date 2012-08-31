package com.appspresso.core.runtime.view;

import android.content.Context;

import com.appspresso.core.runtime.widget.WidgetAgent;

/**
 * This class creates an instance of {@link WidgetView} and/or its subclass as a view for
 * {@link WidgetAgent}.
 * <p/>
 * <code>
 * IWidgetView widgetView = WidgetViewFactory.newInstance().newWidgetView(activity);
 * </code>
 * <p/>
 * To use an alternative factory, set {@link #SYSTEM_PROPERTY_NAME} system property. <code>
 * System.setProperty(WidgetViewFactory.SYSTEM_PROPERTY_NAME, MyWidgetViewFactory.class.getName());
 * IWidgetView widgetView = WidgetViewFactory.newInstance().newWidgetView(activity);
 * </code>
 * 
 * @see WidgetAgent
 * @see <a href="http://en.wikipedia.org/wiki/Abstract_factory_pattern">Abstract Factory Pattern</a>
 */
public abstract class WidgetViewFactory {

    public static final String SYSTEM_PROPERTY_NAME = "com.appspresso.android.WidgetViewFactory";

    public static final String DEF_FACTORY_NAME = DefaultWidgetViewFactory.class.getName();

    public static WidgetViewFactory newInstance() {
        String factoryName = System.getProperty(SYSTEM_PROPERTY_NAME, DEF_FACTORY_NAME);
        try {
            return (WidgetViewFactory) Class.forName(factoryName).newInstance();
        }
        catch (Exception e) {
            // throw new
            // RuntimeException("failed to create widget view with factory:" +
            // factoryName, e);
            // fallback
            return new DefaultWidgetViewFactory();
        }
    }

    public abstract WidgetView newWidgetView(Context context);

}

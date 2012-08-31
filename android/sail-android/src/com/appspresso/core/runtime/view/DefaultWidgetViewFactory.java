package com.appspresso.core.runtime.view;

import android.content.Context;
import android.webkit.WebView;

import com.appspresso.core.runtime.widget.WidgetAgent;

/**
 * This class creates an instance of {@link WebView} as a view for {@link WidgetAgent}.
 * 
 */
public class DefaultWidgetViewFactory extends WidgetViewFactory {

    @Override
    public WidgetView newWidgetView(Context context) {
        return new DefaultWidgetView(context);
    }

}

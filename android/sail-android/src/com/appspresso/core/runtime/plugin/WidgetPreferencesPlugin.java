package com.appspresso.core.runtime.plugin;

import org.w3c.dom.DOMException;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPlugin;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.w3.Widget;

public class WidgetPreferencesPlugin implements AxPlugin {

    static final String NAME = "ax.w3.widget.preferences";

    private Widget widget;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        this.widget = runtimeContext.getWidget();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        this.widget = null;
    }

    @Override
    public void execute(AxPluginContext context) {
        String method = context.getMethod();
        Object result = null;
        try {
            if ("length".equals(method)) {
                result = widget.getPreferences().length();
            }
            else if ("key".equals(method)) {
                Number index = context.getParamAsNumber(0, 0);
                result = widget.getPreferences().key(index.longValue());
            }
            else if ("getItem".equals(method)) {
                String key = context.getParamAsString(0);
                result = widget.getPreferences().getItem(key);
            }
            else if ("setItem".equals(method)) {
                String key = context.getParamAsString(0);
                String value = context.getParamAsString(1);
                widget.getPreferences().setItem(key, value);
            }
            else if ("removeItem".equals(method)) {
                String key = context.getParamAsString(0);
                widget.getPreferences().removeItem(key);
            }
            else if ("clear".equals(method)) {
                widget.getPreferences().clear();
            }
        }
        catch (AxError e) {
            context.sendError(e);
            return;
        }
        catch (DOMException e) {
            context.sendError(e.code, e.getMessage());
            return;
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
            return;
        }

        context.sendResult(result);
    }

}

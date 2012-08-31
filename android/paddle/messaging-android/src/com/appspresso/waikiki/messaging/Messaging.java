package com.appspresso.waikiki.messaging;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;

public class Messaging extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/messaging";
    public static final String FEATURE_SEND = "http://wacapps.net/api/messaging.send";
    public static final String FEATURE_FIND = "http://wacapps.net/api/messaging.find";
    public static final String FEATURE_SUBSCRIBE = "http://wacapps.net/api/messaging.subscribe";
    public static final String FEATURE_WRITE = "http://wacapps.net/api/messaging.write";

    private MessageManager messageManager;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis");
        messageManager = new MessageManager(runtimeContext);
        runtimeContext.addActivityListener(messageManager);
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        if (messageManager != null) {
            runtimeContext.removeActivityListener(messageManager);
            messageManager = null;
        }

        super.deactivate(runtimeContext);
    }

    public void sendMessage(AxPluginContext context) {
        if (!runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                && !runtimeContext.isActivatedFeature(FEATURE_SEND)) { throw new AxError(
                AxError.SECURITY_ERR, "Innactivated feature: " + FEATURE_SEND); }
        messageManager.sendMessage(context);
    }

    public void findMessages(AxPluginContext context) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR, "This feature is not supported.");
    }

    public void onSMS(AxPluginContext context) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR, "This feature is not supported.");
    }

    public void onMMS(AxPluginContext context) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR, "This feature is not supported.");
    }

    public void onEmail(AxPluginContext context) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR, "This feature is not supported.");
    }

    public void unsubscribe(AxPluginContext context) {
        throw new AxError(AxError.NOT_SUPPORTED_ERR, "This feature is not supported.");
    }
}

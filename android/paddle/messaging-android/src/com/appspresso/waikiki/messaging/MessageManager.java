package com.appspresso.waikiki.messaging;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.ActivityAdapter;
import com.appspresso.waikiki.messaging.utils.JavaScriptCaller;

public class MessageManager extends ActivityAdapter {
    private AxRuntimeContext runtimeContext;
    private Map<Integer, MessageSender> senders;

    public MessageManager(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
        JavaScriptCaller.getInstance().init(runtimeContext.getActivity(),
                runtimeContext.getWebView());
        senders = new HashMap<Integer, MessageSender>();
    }

    public void sendMessage(AxPluginContext pluginContext) throws AxError {
        int type = pluginContext.getNamedParamAsNumber(0, "type").intValue();
        Components components = ComponentsList.getComponents(type, runtimeContext.getActivity());

        if (components == null) {
            pluginContext.sendError(AxError.INVALID_VALUES_ERR, "It is an unsupported type.");
            return;
        }

        if (!components.isSupported()) {
            pluginContext.sendError(AxError.NOT_SUPPORTED_ERR, "TYPE_MMS is an unsupported type.");
            return;
        }

        pluginContext.sendResult();

        Map<Object, Object> messageInfo = pluginContext.getParamAsMap(0);
        long handle = pluginContext.getParamAsNumber(1).longValue();
        boolean isIndividually = pluginContext.getParamAsBoolean(2);

        MessageSender messageSender = senders.get(type);
        if (messageSender == null) {
            messageSender = components.getMessageSender();
            senders.put(type, messageSender);
        }

        Message[] messages = components.createMessages(messageInfo);
        MessagePackage messagePackage =
                MessagePackageFactory.createMessageFactory(runtimeContext.getWebView(), handle,
                        isIndividually, messages);

        try {
            messageSender.sendMessages(messagePackage);
        }
        catch (AxError error) {

        }
    }

    @Override
    public void onActivityDestroy(Activity activity) {
        if (!senders.isEmpty()) {
            for (MessageSender sender : senders.values()) {
                sender.deactivate(activity);
            }
            senders.clear();
        }
    }

    @Override
    public void onActivityPause(Activity activity) {}

    @Override
    public void onActivityResume(Activity activity) {}
}
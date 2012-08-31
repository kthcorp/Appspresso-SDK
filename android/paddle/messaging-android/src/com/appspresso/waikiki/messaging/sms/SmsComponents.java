package com.appspresso.waikiki.messaging.sms;

import java.util.Map;
import com.appspresso.waikiki.messaging.Components;
import com.appspresso.waikiki.messaging.Message;
import com.appspresso.waikiki.messaging.MessageSender;
import android.app.Activity;

public class SmsComponents implements Components {
    private Activity activity;
    private MessageSender messageSender;

    @Override
    public void init(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public Message[] createMessages(Map<Object, Object> messageInfo) {
        String id = (String) messageInfo.get("id");
        String[] toArray = convertToStringArray((Object[]) messageInfo.get("to"));
        String body = (String) messageInfo.get("body");

        int length = toArray.length;
        Message[] messages = new Message[length];
        for (int i = 0; i < length; i++) {
            messages[i] = new SmsMessage(id, toArray[i], body);
        }

        return messages;
    }

    @Override
    public MessageSender getMessageSender() {
        if (messageSender == null) messageSender = new SmsMessageSender(activity);
        return messageSender;
    }

    private static String[] convertToStringArray(Object[] objectArray) {
        int length = objectArray.length;
        String[] stringArray = new String[length];
        for (int i = 0; i < length; i++) {
            stringArray[i] = (String) objectArray[i];
        }

        return stringArray;
    }
}

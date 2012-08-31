package com.appspresso.waikiki.messaging.email;

import java.util.Map;

import com.appspresso.waikiki.messaging.Components;
import com.appspresso.waikiki.messaging.Message;
import com.appspresso.waikiki.messaging.MessageSender;

import android.app.Activity;

public class EmailComponents implements Components {
    @Override
    public void init(Activity activity) {}

    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public Message[] createMessages(Map<Object, Object> messageInfo) {
        return null;
    }

    @Override
    public MessageSender getMessageSender() {
        return null;
    }
}

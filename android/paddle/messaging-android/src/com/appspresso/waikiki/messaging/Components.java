package com.appspresso.waikiki.messaging;

import java.util.Map;
import android.app.Activity;

public interface Components {
    public void init(Activity activity);

    public boolean isSupported();

    public Message[] createMessages(Map<Object, Object> messageInfo);

    public MessageSender getMessageSender();
}

package com.appspresso.waikiki.messaging;

import android.app.Activity;

public interface MessageSender {
    public void activate(Activity activity);

    public void deactivate(Activity activity);

    public void sendMessages(MessagePackage messagePackage);
}

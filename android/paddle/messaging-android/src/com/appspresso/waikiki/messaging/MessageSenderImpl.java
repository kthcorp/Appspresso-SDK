package com.appspresso.waikiki.messaging;

import java.util.Observable;
import java.util.Observer;
import android.app.Activity;

public abstract class MessageSenderImpl extends Observable implements MessageSender {
    protected Activity activity;

    public MessageSenderImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public final void sendMessages(MessagePackage messagePackage) {
        addObserver((Observer) messagePackage);
        sendMessagesImpl(messagePackage);
    }

    abstract protected void sendMessagesImpl(MessagePackage messagePackage);
}

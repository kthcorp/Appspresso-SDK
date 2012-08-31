package com.appspresso.waikiki.messaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import android.webkit.WebView;

public abstract class MessagePackageImpl implements MessagePackage, Observer {
    protected WebView webView;
    protected long handle;

    protected List<Message> messages;
    protected Set<Message> sentMessages;
    protected Set<Message> failedMessages;

    public MessagePackageImpl(WebView webView, long handle) {
        this.webView = webView;
        this.handle = handle;

        messages = new ArrayList<Message>();
        sentMessages = new HashSet<Message>();
        failedMessages = new HashSet<Message>();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void removeMessage(int index) {
        messages.remove(index);
    }

    @Override
    public Message[] getMessages() {
        return messages.toArray(new Message[] {});
    }

    @Override
    public Message[] getSentMessages() {
        return sentMessages.toArray(new Message[] {});
    }

    @Override
    public Message[] getFailedMessages() {
        return failedMessages.toArray(new Message[] {});
    }

    @Override
    public void setMessageisSucceed(int index) {
        Message message = messages.get(index);
        if (message == null) return;
        messages.set(index, null);

        sentMessages.add(message);
        setMessageisSucceed(message);
    }

    @Override
    public void setMessageFailed(int index) {
        Message message = messages.get(index);
        if (message == null) return;
        messages.set(index, null);

        failedMessages.add(message);
        setMessageFailed(message);
    }

    @Override
    public boolean isAllMessageSent() {
        return messages.size() == (sentMessages.size() + failedMessages.size());
    }

    @Override
    public List<Integer> getRemainingMessages() {
        List<Integer> indexes = new ArrayList<Integer>();

        for (int i = 0, count = messages.size(); i < count; i++) {
            if (sentMessages.contains(messages.get(i))) continue;
            if (failedMessages.contains(messages.get(i))) continue;

            indexes.add(i);
        }

        return indexes;
    }

    protected abstract void setMessageisSucceed(Message message);

    protected abstract void setMessageFailed(Message message);

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            Object[] result = (Object[]) data;

            if (this.handle != (Long) result[0]) return;
            int index = (Integer) result[1];
            boolean isSucceed = (Boolean) result[2];

            if (isSucceed) {
                setMessageisSucceed(index);
            }
            else {
                setMessageFailed(index);
            }
        }

        if (isAllMessageSent()) {
            observable.deleteObserver(this);
            webView = null;
        }
    }
}

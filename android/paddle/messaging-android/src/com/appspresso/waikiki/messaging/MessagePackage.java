package com.appspresso.waikiki.messaging;

import java.util.List;

public interface MessagePackage {
    public long getHandle();

    public void addMessage(Message message);

    public void removeMessage(int index);

    public Message[] getMessages();

    public Message[] getSentMessages();

    public Message[] getFailedMessages();

    public List<Integer> getRemainingMessages();

    public void setMessageisSucceed(int index);

    public void setMessageFailed(int index);

    public boolean isAllMessageSent();
}

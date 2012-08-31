package com.appspresso.waikiki.messaging;

public interface Message {
    public int TYPE_SMS = 1;
    public int TYPE_MMS = 2;
    public int TYPE_EMAIL = 3;
    public int FOLDER_INBOX = 1;
    public int FOLDER_OUTBOX = 2;
    public int FOLDER_DRAFTS = 3;
    public int FOLDER_SENTBOX = 4;

    public int getType();

    public String getId();

    public String getTo();

    public String getBody();
}

package com.appspresso.waikiki.messaging.sms;

import com.appspresso.waikiki.messaging.Message;

public class SmsMessage implements Message {
    private String id;
    private String to;
    private String body;

    public SmsMessage(String id, String to, String body) {
        this.to = to;
        this.body = body;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getType() {
        return TYPE_SMS;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public String getBody() {
        return body;
    }
}

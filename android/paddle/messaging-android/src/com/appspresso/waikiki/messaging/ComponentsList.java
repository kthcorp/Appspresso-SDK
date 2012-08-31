package com.appspresso.waikiki.messaging;

import java.util.HashMap;
import java.util.Map;

import com.appspresso.waikiki.messaging.email.EmailComponents;
import com.appspresso.waikiki.messaging.mms.MmsComponents;
import com.appspresso.waikiki.messaging.sms.SmsComponents;

import android.app.Activity;

public class ComponentsList {
    private static Map<Integer, Components> componentsMap = new HashMap<Integer, Components>();

    public static final Components getComponents(int type, Activity activity) {
        if (componentsMap.containsKey(type)) return componentsMap.get(type);

        Components components = null;
        switch (type) {
            case Message.TYPE_SMS:
                components = (Components) new SmsComponents();
                break;
            case Message.TYPE_MMS:
                components = new MmsComponents();
                break;
            case Message.TYPE_EMAIL:
                components = new EmailComponents();
                break;
        }

        if (components != null) {
            components.init(activity);
            componentsMap.put(type, components);
        }

        return components;
    }
}

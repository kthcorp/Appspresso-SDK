package com.appspresso.waikiki.contact;

import java.util.Map;
import com.appspresso.waikiki.contact.data.IContact;
import android.content.Context;

public interface IAddressBook {
    // beta 스펙
    // int SIM_ADDRESS_BOOK = 1;
    // int DEVICE_ADDRESS_BOOK = 0;

    // 2.0 스펙
    // int SIM_ADDRESS_BOOK = 0x0;
    // int DEVICE_ADDRESS_BOOK = 0x000f;
    // int PHONE_ADDRESS_BOOK = 0x00ff;

    /*
     * 각 Address book에 대한 정확한 정의가 없고 구현한 것도 DEVICE_ADDRESS_BOOK이므로 가장 기본 flag인 0으로 둔다.
     */
    int SIM_ADDRESS_BOOK = 1;
    int DEVICE_ADDRESS_BOOK = 0;
    int PHONE_ADDRESS_BOOK = 2;

    int getType();

    IContact addContact(Context context, IContact contact);

    void updateContact(Context context, IContact contact);

    void deleteContact(Context context, String id);

    IContact[] findContacts(Context context, Map<String, Object> filter);

    // extension
    int getHandle();
}

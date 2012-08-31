package com.appspresso.waikiki.contact.data;

import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.appspresso.api.AxPluginResult;

public interface IPhoneNumber extends AxPluginResult {
    String MIMETYPE = Phone.CONTENT_ITEM_TYPE;

    String COLUMN_NUMBER = Phone.DATA;
    String COLUMN_TYPE = Phone.TYPE;

    String ATTR_NUMBER = "number";
    String ATTR_TYPES = "types";

    String[] getTypes();

    String getNumber();

}

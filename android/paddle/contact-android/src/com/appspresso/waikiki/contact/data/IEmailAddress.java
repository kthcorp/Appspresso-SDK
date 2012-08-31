package com.appspresso.waikiki.contact.data;

import android.provider.ContactsContract.CommonDataKinds.Email;

import com.appspresso.api.AxPluginResult;

public interface IEmailAddress extends AxPluginResult {
    String MIMETYPE = Email.CONTENT_ITEM_TYPE;

    String COLUMN_EMAIL = Email.DATA;
    String COLUMN_TYPE = Email.TYPE;

    String ATTR_EMAIL = "email";
    String ATTR_TYPES = "types";

    String[] getTypes();

    String getEmail();

}

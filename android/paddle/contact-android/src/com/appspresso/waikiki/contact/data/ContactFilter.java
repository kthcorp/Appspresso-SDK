package com.appspresso.waikiki.contact.data;

public interface ContactFilter {
    String ATTR_ID = "id";
    String ATTR_FIRSTNAME = "firstName";
    String ATTR_MIDDLENAME = "middleName";
    String ATTR_LASTNAME = "lastName";
    String ATTR_NICKNAME = "nickName"; // XXX spec 오류
    String ATTR_PHONETICNAME = "phoneticName";
    String ATTR_PHONENUMBER = "phoneNumber";
    String ATTR_EMAILS = "emails"; // XXX spec 오류
    String ATTR_ADDRESS = "address";
}

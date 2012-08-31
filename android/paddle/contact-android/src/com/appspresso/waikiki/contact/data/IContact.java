package com.appspresso.waikiki.contact.data;

import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.appspresso.api.AxPluginResult;

public interface IContact extends AxPluginResult {
    String PREFIX_PHOTO_URI = "/appspresso/contact/";

    String MIMETYPE_NAME = StructuredName.CONTENT_ITEM_TYPE;
    String MIMETYPE_NICKNAME = Nickname.CONTENT_ITEM_TYPE;
    String MIMETYPE_PHOTOURI = Photo.CONTENT_ITEM_TYPE;

    String COLUMN_ID = ContactsContract.Data.RAW_CONTACT_ID;
    String COLUMN_FIRSTNAME = StructuredName.GIVEN_NAME;
    String COLUMN_MIDDLENAME = StructuredName.MIDDLE_NAME;
    String COLUMN_LASTNAME = StructuredName.FAMILY_NAME;
    String COLUMN_NICKNAME = Nickname.DATA;
    String COLUMN_PHONETICNAME = StructuredName.PHONETIC_GIVEN_NAME;
    String COLUMN_PHOTOURI = Photo.PHOTO;

    String ATTR_ID = "_id";
    String ATTR_FIRSTNAME = "firstName";
    String ATTR_MIDDLENAME = "middleName";
    String ATTR_LASTNAME = "lastName";
    String ATTR_NICKNAMES = "nicknames";
    String ATTR_PHONETICNAME = "phoneticName";
    String ATTR_ADDRESSES = "addresses";
    String ATTR_PHOTOURI = "photoURI";
    String ATTR_PHONENUMBERS = "phoneNumbers";
    String ATTR_EMAILS = "emails";

    String getId();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    String[] getNicknames();

    String getPhoneticName();

    IContactAddress[] getAddresses();

    IPhoneNumber[] getPhoneNumbers();

    String getPhotoURI();

    IEmailAddress[] getEmails();

    void setId(String id);

    void setFirstName(String firstName);

    void setMiddleName(String firstName);

    void setLastName(String lastName);

    void addPhoneNumber(String number, String[] types);

    void addEmailAddress(String email, String[] types);

    void addContactAddress(String country, String region, String county, String city,
            String streetAddress, String additionalInformation, String postalCode, String[] types);

    void addNickname(String string);

    void setPhotoURI(String string);

    void setPhoneticName(String value);
}

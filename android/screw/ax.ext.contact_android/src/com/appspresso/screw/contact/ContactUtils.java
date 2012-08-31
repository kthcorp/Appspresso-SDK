package com.appspresso.screw.contact;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import com.appspresso.api.AxLog;

class ContactUtils {
    private static Log L = AxLog.getLog("ContactPlugin");

    public static Object onPickContact(Activity activity, Intent data) {
        if (data == null) { return null; }

        String contactId = data.getData().getLastPathSegment();
        return ContactUtils.getContactWithContactId(activity, contactId);
    }

    static JSONObject getContactWithContactId(Activity activity, String contactId) {
        JSONObject contact = new JSONObject();

        Cursor cursor = null;
        Cursor rawContactIdsCursor = null;
        try {
            String[] rawContactIds = null;
            rawContactIdsCursor =
                    activity.getContentResolver().query(RawContacts.CONTENT_URI,
                            new String[] {RawContacts._ID}, RawContacts.CONTACT_ID + " = ?",
                            new String[] {contactId}, null);
            rawContactIds = new String[rawContactIdsCursor.getCount()];
            for (int i = 0; rawContactIdsCursor.moveToNext(); i++) {
                rawContactIds[i] = rawContactIdsCursor.getString(0);
            }

            String[] columns =
                    new String[] {StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME,
                            Phone.NUMBER, ContactsContract.Data.MIMETYPE};

            StringBuilder buf = new StringBuilder();
            if (rawContactIds != null && rawContactIds.length > 0) {
                buf.append("(").append(rawContactIds[0]);
                for (int i = 1; i < rawContactIds.length; i++) {
                    buf.append(",").append(rawContactIds[i]);
                }
                buf.append(")");
            }

            cursor =
                    activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, columns,
                            RawContacts.Data.RAW_CONTACT_ID + " IN " + buf, null, null);

            final int idx_firstName = cursor.getColumnIndex(StructuredName.GIVEN_NAME);
            final int idx_lastName = cursor.getColumnIndex(StructuredName.FAMILY_NAME);
            final int idx_phoneNumber = cursor.getColumnIndex(Phone.NUMBER);
            final int idx_mimetype = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);

            List<String> phoneNumbers = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String mimetype = cursor.getString(idx_mimetype);

                if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    contact.put("firstName", cursor.getString(idx_firstName));
                    contact.put("lastName", cursor.getString(idx_lastName));
                }
                else if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    phoneNumbers.add(cursor.getString(idx_phoneNumber));
                }
            }
            contact.put("phoneNumbers", new JSONArray(phoneNumbers));

            return contact;
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                L.debug(e);
            }
        }
        finally {
            if (rawContactIdsCursor != null) {
                rawContactIdsCursor.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}

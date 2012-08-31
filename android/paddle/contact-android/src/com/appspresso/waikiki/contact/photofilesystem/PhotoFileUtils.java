package com.appspresso.waikiki.contact.photofilesystem;

import java.io.InputStream;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class PhotoFileUtils {
    public static long getContactId(ContentResolver contentResolver, long rawId) {
        Cursor cursor =
                contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                        new String[] {ContactsContract.RawContacts.CONTACT_ID},
                        ContactsContract.RawContacts._ID + "=?",
                        new String[] {Long.toString(rawId)}, null);

        if (cursor == null) return -1;

        try {
            return cursor.moveToNext() ? cursor.getInt(0) : -1;
        }
        finally {
            cursor.close();
        }
    }

    public static InputStream getInputStream(ContentResolver contentResolver, long contactId) {
        if (contactId < 0) throw new IllegalArgumentException("Invalid ID");

        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        return ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
    }

    public static boolean updatePhotoFile(ContentResolver contentResolver, long rawId, byte[] data) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, data);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

        long photoRow = getPhotoRow(contentResolver, rawId);
        Log.i("Hello", rawId + ", " + photoRow);

        if (photoRow > -1) {
            return contentResolver.update(ContactsContract.Data.CONTENT_URI, values,
                    ContactsContract.Data._ID + "=?", new String[] {Long.toString(photoRow)}) > 0;
        }
        else {
            return contentResolver.insert(ContactsContract.Data.CONTENT_URI, values) != null;
        }
    }

    public static long getPhotoRow(ContentResolver contentResolver, long rawId) {
        String where =
                ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";

        Cursor cursor =
                contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        new String[] {ContactsContract.Data._ID}, where,
                        new String[] {Long.toString(rawId)}, null);
        if (cursor == null) return -1;

        int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
        try {
            return cursor.moveToNext() ? cursor.getInt(idIdx) : -1;
        }
        finally {
            cursor.close();
        }
    }
}

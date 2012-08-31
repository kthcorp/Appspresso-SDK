package com.appspresso.waikiki.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appspresso.api.AxError;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.waikiki.contact.data.ContactBuilder;
import com.appspresso.waikiki.contact.data.ContactFilter;
import com.appspresso.waikiki.contact.data.IContact;
import com.appspresso.waikiki.contact.data.IContactAddress;
import com.appspresso.waikiki.contact.data.IEmailAddress;
import com.appspresso.waikiki.contact.data.IPhoneNumber;
import com.appspresso.waikiki.contact.photofilesystem.PhotoFileSystem;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

class DeviceAddressBook extends AbstractAddressBook {
    private static final String NAME = "Contact";
    public static final String PHOTO_FILE_SYSTEM_PREFIX = "contact_photo/device";
    private AxFileSystemManager fileSystemManager;
    private PhotoFileSystem photoFileSystem;

    public DeviceAddressBook(int handle, AxFileSystemManager fileSystemManager) {
        super(AbstractAddressBook.DEVICE_ADDRESS_BOOK, DeviceAddressBook.NAME, handle);

        this.fileSystemManager = fileSystemManager;
        this.photoFileSystem =
                (PhotoFileSystem) fileSystemManager.getFileSystem(PHOTO_FILE_SYSTEM_PREFIX);
    }

    @Override
    public IContact addContact(Context context, IContact contact) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(1);

        // TODO temp code. set user account
        Cursor cursor =
                context.getContentResolver().query(
                        ContactsContract.Settings.CONTENT_URI,
                        new String[] {ContactsContract.Settings.UNGROUPED_VISIBLE},
                        ContactsContract.Settings.ACCOUNT_NAME + " = ? AND "
                                + ContactsContract.Settings.ACCOUNT_TYPE + " = ? ",
                        new String[] {"", ""}, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 0 || cursor.getInt(0) != 1) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Settings.CONTENT_URI)
                    .withValue(ContactsContract.Settings.UNGROUPED_VISIBLE, 1)
                    .withValue(ContactsContract.Settings.ACCOUNT_NAME, "")
                    .withValue(ContactsContract.Settings.ACCOUNT_TYPE, "").build());

            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }
            // 여기서 Ungrouped_visible 이 setting되지 않으면, 기본 Contact 어플에서 보이지 않게 된다.
            catch (Exception e) {}
        }

        ops = OperationBuilder.getAddContactOperationBuilder().build(contact);

        ContentProviderResult[] cpr = null;
        try {
            cpr = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (cpr == null || cpr.length < 1) { return null; }

            String id = ContactIDEncoder.encode((int) ContentUris.parseId(cpr[0].uri));
            return getContacts(context.getContentResolver(), new int[] {Integer.valueOf(id)})
                    .get(0);
        }
        catch (Exception e) {
            throw new AxError(AxError.UNKNOWN_ERR, "addContact operation failed");
        }
    }

    protected boolean existContactData(Context context, int rawId) {
        Cursor cursor = null;
        try {
            cursor =
                    context.getContentResolver().query(Data.CONTENT_URI,
                            new String[] {Data.RAW_CONTACT_ID}, Data.RAW_CONTACT_ID + "=?",
                            new String[] {String.valueOf(rawId)}, null);
            if (cursor == null || cursor.getCount() < 1) { return false; }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }

    @Override
    public void updateContact(Context context, IContact contact) {
        int rawid = ContactIDEncoder.decode(contact.getId());

        if (!existContactData(context, rawid)) { throw new AxError(AxError.INVALID_VALUES_ERR,
                "this contact was already deleted."); }

        ArrayList<ContentProviderOperation> ops =
                OperationBuilder.getUpdateOperationBuilder().build(contact);
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e) {
            throw new AxError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    @Override
    public void deleteContact(Context context, String id) {
        int rawId = ContactIDEncoder.decode(id);

        if (!existContactData(context, rawId)) { throw new AxError(AxError.INVALID_VALUES_ERR,
                "this contact was already deleted."); }

        Uri uri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawId);
        context.getContentResolver().delete(uri, null, null);
    }

    @Override
    public IContact[] findContacts(Context context, Map<String, Object> filter) {
        ContentResolver cr = context.getContentResolver();
        int[] rawIds = filtering(cr, filter);

        List<IContact> cs = getContacts(cr, rawIds);
        IContact[] contact = new IContact[cs.size()];
        return cs.toArray(contact);
    }

    private int[] filtering(ContentResolver cr, Map<String, Object> filter) {
        int[] rawIds = null;

        if (filter != null && filter.size() > 0) {
            Object id = filter.get(ContactFilter.ATTR_ID);
            if (id != null) {
                rawIds = query(cr, IContact.COLUMN_ID + "=?", new String[] {String.valueOf(id)});
                // rawIds = query(cr, IContact.COLUMN_ID + "=?",
                // new String[] {
                // String.valueOf(ContactIDEncoder.decode(String.valueOf(id)))
                // });
            }

            // firstName, lastName
            Object firstName = filter.get(ContactFilter.ATTR_FIRSTNAME);
            Object lastName = filter.get(ContactFilter.ATTR_LASTNAME);
            Object phoneticName = filter.get(ContactFilter.ATTR_PHONETICNAME);
            if (firstName != null || lastName != null || phoneticName != null) {
                StringBuffer buf = new StringBuffer("(");
                buf.append(Data.MIMETYPE).append("=?");

                ArrayList<String> args = new ArrayList<String>(3);
                args.add(IContact.MIMETYPE_NAME);
                if (firstName != null) {
                    buf.append(" AND ").append(IContact.COLUMN_FIRSTNAME).append(" LIKE ?");
                    args.add((String) firstName);
                }
                if (lastName != null) {
                    buf.append(" AND ").append(IContact.COLUMN_LASTNAME).append(" LIKE ?");
                    args.add((String) lastName);
                }
                if (phoneticName != null) {
                    buf.append(" AND ").append(IContact.COLUMN_PHONETICNAME).append(" LIKE ?");
                    args.add((String) phoneticName);
                }

                buf.append(")");

                // String selection =
                // buildSelectionStmt(StructuredName.GIVEN_NAME,
                // StructuredName.FAMILY_NAME, rawIds);
                rawIds = query(cr, buf.toString(), args.toArray(new String[args.size()]));
            }

            // nickName
            if (filter.get(ContactFilter.ATTR_NICKNAME) != null) {
                String selection = buildSeletionStatement(IContact.COLUMN_NICKNAME, rawIds);
                rawIds =
                        query(cr, selection, new String[] {IContact.MIMETYPE_NICKNAME,
                                (String) filter.get(ContactFilter.ATTR_NICKNAME)});
            }

            // phoneNumber
            if (filter.get(ContactFilter.ATTR_PHONENUMBER) != null) {
                String selection = buildSeletionStatement(IPhoneNumber.COLUMN_NUMBER, rawIds);
                rawIds =
                        query(cr,
                                selection,
                                new String[] {IPhoneNumber.MIMETYPE,
                                        (String) filter.get(ContactFilter.ATTR_PHONENUMBER)});
            }

            // emails
            if (filter.get(ContactFilter.ATTR_EMAILS) != null) {
                String selection = buildSeletionStatement(IEmailAddress.COLUMN_EMAIL, rawIds);
                rawIds =
                        query(cr,
                                selection,
                                new String[] {IEmailAddress.MIMETYPE,
                                        (String) filter.get(ContactFilter.ATTR_EMAILS)});
            }

            // TODO address

            // TODO exclude deleted data
        }

        return rawIds;
    }

    protected List<IContact> getContacts(ContentResolver cr, int[] rawIds) {
        List<IContact> contacts = new ArrayList<IContact>();

        String selection = null;
        if (rawIds != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(Data.RAW_CONTACT_ID).append(" IN (");
            for (int i = 0; i < rawIds.length; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(rawIds[i]);
            }
            builder.append(")");
            selection = builder.toString();
        }

        Cursor cursor = null;

        try {
            cursor =
                    cr.query(ContactsContract.Data.CONTENT_URI, new String[] {Data.RAW_CONTACT_ID,
                            Data.MIMETYPE, IContact.COLUMN_FIRSTNAME, IContact.COLUMN_MIDDLENAME,
                            IContact.COLUMN_LASTNAME, IContact.COLUMN_PHONETICNAME,
                            IContact.COLUMN_NICKNAME, IContactAddress.COLUMN_COUNTRY,
                            IContactAddress.COLUMN_REGION, IContactAddress.COLUMN_CITY,
                            IContactAddress.COLUMN_STREETADDRESS,
                            IContactAddress.COLUMN_POSTALCODE, IContactAddress.COLUMN_TYPE,
                            StructuredPostal.LABEL, IEmailAddress.COLUMN_EMAIL,
                            IEmailAddress.COLUMN_TYPE, Email.LABEL, IPhoneNumber.COLUMN_NUMBER,
                            IPhoneNumber.COLUMN_TYPE, Phone.LABEL, IContact.COLUMN_PHOTOURI},
                            selection, null, ContactsContract.Data.RAW_CONTACT_ID + " ASC");

            if (cursor == null) return null;

            int raw_contact_id_idx = cursor.getColumnIndex(Data.RAW_CONTACT_ID);
            int mimetype_idx = cursor.getColumnIndex(Data.MIMETYPE);

            int firstName_idx = cursor.getColumnIndex(IContact.COLUMN_FIRSTNAME);
            int middleName_idx = cursor.getColumnIndex(IContact.COLUMN_MIDDLENAME);
            int lastName_idx = cursor.getColumnIndex(IContact.COLUMN_LASTNAME);
            int phoneticName_idx = cursor.getColumnIndex(IContact.COLUMN_PHONETICNAME);

            int nickname_idx = cursor.getColumnIndex(IContact.COLUMN_NICKNAME);

            int address_custom_idx = cursor.getColumnIndex(StructuredPostal.LABEL);
            int address_postalcode_idx = cursor.getColumnIndex(IContactAddress.COLUMN_POSTALCODE);
            int address_country_idx = cursor.getColumnIndex(IContactAddress.COLUMN_COUNTRY);
            int address_region_idx = cursor.getColumnIndex(IContactAddress.COLUMN_REGION);
            int address_city_idx = cursor.getColumnIndex(IContactAddress.COLUMN_CITY);
            int address_street_idx = cursor.getColumnIndex(IContactAddress.COLUMN_STREETADDRESS);
            int address_types_idx = cursor.getColumnIndex(IContactAddress.COLUMN_TYPE);

            int email_custom_idx = cursor.getColumnIndex(Email.LABEL);
            int email_data_idx = cursor.getColumnIndex(IEmailAddress.COLUMN_EMAIL);
            int email_type_idx = cursor.getColumnIndex(IEmailAddress.COLUMN_TYPE);

            int phone_custom_idx = cursor.getColumnIndex(Phone.LABEL);
            int phone_data_idx = cursor.getColumnIndex(IPhoneNumber.COLUMN_NUMBER);
            int phone_type_idx = cursor.getColumnIndex(IPhoneNumber.COLUMN_TYPE);

            int photo_data_idx = cursor.getColumnIndex(IContact.COLUMN_PHOTOURI);

            int lastRawId = -1;
            IContact contact = null;
            while (cursor.moveToNext()) {
                int rawId = cursor.getInt(raw_contact_id_idx);
                if (rawId != lastRawId) {
                    lastRawId = rawId;
                    contact = ContactBuilder.getBuilder().createContact();
                    contact.setId(ContactIDEncoder.encode(rawId));
                    contacts.add(contact);
                }

                String mimetype = cursor.getString(mimetype_idx);
                if (IContact.MIMETYPE_NAME.equals(mimetype)) {
                    String value;
                    if ((value = cursor.getString(firstName_idx)) != null)
                        contact.setFirstName(value);
                    if ((value = cursor.getString(middleName_idx)) != null)
                        contact.setMiddleName(value);
                    if ((value = cursor.getString(lastName_idx)) != null)
                        contact.setLastName(value);
                    if ((value = cursor.getString(phoneticName_idx)) != null)
                        contact.setPhoneticName(value);
                }
                else if (IPhoneNumber.MIMETYPE.equals(mimetype)) {
                    int nativeType = cursor.getInt(phone_type_idx);
                    String type;
                    String[] types;
                    if (nativeType == Phone.TYPE_CUSTOM) {
                        types = new String[] {cursor.getString(phone_custom_idx)};
                    }
                    else if ((type = ContactTypeUtil.getWaikikiPhoneType(nativeType)) == null) {
                        types = null;
                    }
                    else {
                        types = new String[] {type};
                    }
                    contact.addPhoneNumber(cursor.getString(phone_data_idx), types);
                }
                else if (IEmailAddress.MIMETYPE.equals(mimetype)) {
                    int nativeType = cursor.getInt(email_type_idx);
                    String type;
                    String[] types;
                    if (nativeType == Email.TYPE_CUSTOM) {
                        types = new String[] {cursor.getString(email_custom_idx)};
                    }
                    else if ((type = ContactTypeUtil.getWaikikiEmailType(nativeType)) == null) {
                        types = null;
                    }
                    else {
                        types = new String[] {type};
                    }
                    contact.addEmailAddress(cursor.getString(email_data_idx), types);
                }
                else if (IContact.MIMETYPE_NICKNAME.equals(mimetype)) {
                    contact.addNickname(cursor.getString(nickname_idx));
                }
                else if (IContact.MIMETYPE_PHOTOURI.equals(mimetype)) {
                    byte[] blob = cursor.getBlob(photo_data_idx);

                    if (blob != null) {
                        // contact.setPhotoURI(IContact.PREFIX_PHOTO_URI + "/" +
                        // contact.getId());
                        long longRawId = Long.parseLong(contact.getId());
                        contact.setPhotoURI(fileSystemManager.toUri(photoFileSystem.getFileByRawId(
                                longRawId).getPath()));
                    }
                }
                else if (IContactAddress.MIMETYPE.equals(mimetype)) {
                    int nativeType = cursor.getInt(address_types_idx);
                    String[] types;
                    String type;
                    if (nativeType == Email.TYPE_CUSTOM) {
                        types = new String[] {cursor.getString(address_custom_idx)};
                    }
                    else if ((type = ContactTypeUtil.getWaikikiPostalType(nativeType)) == null) {
                        types = null;
                    }
                    else {
                        types = new String[] {type};
                    }

                    contact.addContactAddress(cursor.getString(address_country_idx),
                            cursor.getString(address_region_idx), null /* county */,
                            cursor.getString(address_city_idx),
                            cursor.getString(address_street_idx), null /* additionalInformation */,
                            cursor.getString(address_postalcode_idx), types);
                }
            }
        }
        finally {
            if (cursor != null) cursor.close();
        }

        return contacts;
    }

    private int[] query(ContentResolver cr, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        int[] rawIds = null;
        try {
            cursor =
                    cr.query(Data.CONTENT_URI, new String[] {Data.RAW_CONTACT_ID}, selection,
                            selectionArgs, null);
            rawIds = new int[cursor.getCount()];
            int index = 0;

            while (cursor.moveToNext()) {
                rawIds[index++] = cursor.getInt(0);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rawIds;
    }

    private String buildSeletionStatement(String mimetype, int[] rawIds) {
        StringBuffer buf = new StringBuffer("(");
        buf.append(Data.MIMETYPE).append("=?");
        buf.append(" AND ").append(mimetype).append(" LIKE ?");

        if (rawIds != null && rawIds.length > 0) {
            buf.append(" AND ").append(Data.RAW_CONTACT_ID).append(" IN (").append(rawIds[0]);
            for (int i = 1; i < rawIds.length; i++) {
                buf.append(",").append(rawIds[i]);
            }
            buf.append(")");
        }

        buf.append(")");
        return buf.toString();
    }

}

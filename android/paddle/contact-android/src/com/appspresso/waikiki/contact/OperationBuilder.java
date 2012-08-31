package com.appspresso.waikiki.contact;

import java.util.ArrayList;
import java.util.List;
import com.appspresso.waikiki.contact.data.IContact;
import com.appspresso.waikiki.contact.data.IContactAddress;
import com.appspresso.waikiki.contact.data.IEmailAddress;
import com.appspresso.waikiki.contact.data.IPhoneNumber;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

class OperationBuilder {
    private static AddOperationBuilder addOperationBuilder = null;
    private static UpdateOperationBuilder updateOperationBuilder = null;

    public static IContactOperationBuilder getAddContactOperationBuilder() {
        if (addOperationBuilder == null) {
            synchronized (AddOperationBuilder.class) {
                if (addOperationBuilder == null) {
                    addOperationBuilder = new AddOperationBuilder();
                }
            }
        }
        return addOperationBuilder;
    }

    public static IContactOperationBuilder getUpdateOperationBuilder() {
        if (updateOperationBuilder == null) {
            synchronized (UpdateOperationBuilder.class) {
                if (updateOperationBuilder == null) {
                    updateOperationBuilder = new UpdateOperationBuilder();
                }
            }
        }
        return updateOperationBuilder;
    }

    private static class AddOperationBuilder implements IContactOperationBuilder {

        @Override
        public ArrayList<ContentProviderOperation> build(IContact contact) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            int rawContactInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DISABLED)
                    // .withValue(RawContacts.ACCOUNT_NAME,
                    // "").withValue(RawContacts.ACCOUNT_TYPE, "").build());
                    .build());

            buildContact(contact, ops, rawContactInsertIndex);

            return ops;
        }

        private void buildContact(IContact contact, List<ContentProviderOperation> ops,
                int rawContactInsertIndex) {

            // XXX Contact.phoneticName, given? middle? family?
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE, IContact.MIMETYPE_NAME)
                    .withValue(IContact.COLUMN_FIRSTNAME, contact.getFirstName())
                    .withValue(IContact.COLUMN_MIDDLENAME, contact.getMiddleName())
                    .withValue(IContact.COLUMN_LASTNAME, contact.getLastName())
                    .withValue(IContact.COLUMN_PHONETICNAME, contact.getPhoneticName()).build());

            // TODO PhotoURI
            buildNicknames(contact.getNicknames(), ops, rawContactInsertIndex);
            buildAddresses(contact.getAddresses(), ops, rawContactInsertIndex);
            buildEmails(contact.getEmails(), ops, rawContactInsertIndex);
            buildPhoneNumbers(contact.getPhoneNumbers(), ops, rawContactInsertIndex);
        }

        private void buildPhoneNumbers(IPhoneNumber[] phoneNumbers,
                List<ContentProviderOperation> ops, int rawContactInsertIndex) {
            if (phoneNumbers == null || phoneNumbers.length < 1) return;

            for (IPhoneNumber phone : phoneNumbers) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                .withValue(Data.MIMETYPE, IPhoneNumber.MIMETYPE)
                                .withValue(IPhoneNumber.COLUMN_NUMBER, phone.getNumber());

                String[] types = phone.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativePhoneType(type);
                    if (nativeType == Phone.TYPE_CUSTOM) {
                        builder.withValue(Phone.LABEL, type);
                    }
                    builder.withValue(IPhoneNumber.COLUMN_TYPE, nativeType);
                }
                else {
                    builder.withValue(Phone.LABEL, "VOICE");
                    builder.withValue(IPhoneNumber.COLUMN_TYPE, Phone.TYPE_CUSTOM);
                }
                ops.add(builder.build());
            }
        }

        private void buildEmails(IEmailAddress[] emails, List<ContentProviderOperation> ops,
                int rawContactInsertIndex) {
            if (emails == null || emails.length < 1) return;

            for (IEmailAddress email : emails) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                .withValue(Data.MIMETYPE, IEmailAddress.MIMETYPE)
                                .withValue(IEmailAddress.COLUMN_EMAIL, email.getEmail());

                String[] types = email.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativeEmailType(type);
                    if (nativeType == Email.TYPE_CUSTOM) {
                        builder.withValue(Email.LABEL, type);
                    }
                    builder.withValue(IEmailAddress.COLUMN_TYPE, nativeType);
                }
                else {
                    builder.withValue(Email.LABEL, "HOME");
                    builder.withValue(IEmailAddress.COLUMN_TYPE, Email.TYPE_CUSTOM);
                }

                ops.add(builder.build());
            }

        }

        private void buildAddresses(IContactAddress[] addresses,
                List<ContentProviderOperation> ops, int rawContactInsertIndex) {
            if (addresses == null || addresses.length < 1) return;

            for (IContactAddress address : addresses) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                .withValue(Data.MIMETYPE, IContactAddress.MIMETYPE);

                String value = address.getCountry();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_COUNTRY, value);
                }

                value = address.getRegion();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_REGION, value);
                }

                // value = address.getCounty()
                // if(value != null){
                // builder.withValue(StructuredPostal.Co, value);
                // }

                value = address.getCity();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_CITY, value);
                }

                value = address.getStreetAddress();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_STREETADDRESS, value);
                }

                // XXX streetNumber, Premises, addtionalInformation

                value = address.getPostalCode();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_POSTALCODE, value);
                }

                String[] types = address.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativePostalType(type);
                    if (nativeType == StructuredPostal.TYPE_CUSTOM) {
                        builder.withValue(StructuredPostal.LABEL, type);
                    }
                    builder.withValue(IContactAddress.COLUMN_TYPE, nativeType);
                }
                else {
                    builder.withValue(StructuredPostal.LABEL, "HOME");
                    builder.withValue(IContactAddress.COLUMN_TYPE, StructuredPostal.TYPE_CUSTOM);
                }
                ops.add(builder.build());
            }
        }

        private void buildNicknames(String[] nicknames, List<ContentProviderOperation> ops,
                int rawContactInsertIndex) {
            if (nicknames == null || nicknames.length < 1) return;

            for (String nickname : nicknames) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(Data.MIMETYPE, IContact.MIMETYPE_NICKNAME)
                        .withValue(IContact.COLUMN_NICKNAME, nickname).build());
            }
        }
    }

    private static class UpdateOperationBuilder implements IContactOperationBuilder {

        @Override
        public ArrayList<ContentProviderOperation> build(IContact contact) {
            int rawid = ContactIDEncoder.decode(contact.getId());
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation
                    .newDelete(Data.CONTENT_URI)
                    .withSelection(
                            // Data.RAW_CONTACT_ID + " = ? AND (" +
                            // Data.MIMETYPE + " IN (?, ?, ?, ?) OR "
                            // + StructuredName.DISPLAY_NAME + " IS NULL)",
                            Data.RAW_CONTACT_ID + " = ? AND (" + Data.MIMETYPE
                                    + " IN (?, ?, ?, ?))",
                            new String[] {Integer.toString(rawid), IContact.MIMETYPE_NICKNAME,
                                    IEmailAddress.MIMETYPE, IContactAddress.MIMETYPE,
                                    IPhoneNumber.MIMETYPE}).build());

            buildContact(contact, ops, rawid);

            return ops;
        }

        private void buildContact(IContact contact, List<ContentProviderOperation> ops, int rawid) {
            ops.add(ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE + " == ?",
                            new String[] {Integer.toString(rawid), IContact.MIMETYPE_NAME})
                    .withValue(IContact.COLUMN_FIRSTNAME, contact.getFirstName())
                    .withValue(IContact.COLUMN_MIDDLENAME, contact.getMiddleName())
                    .withValue(IContact.COLUMN_LASTNAME, contact.getLastName())
                    .withValue(IContact.COLUMN_PHONETICNAME, contact.getPhoneticName()).build());
            // FIXME DB 상에 저장 되었으나, Edit 화면에 보이지 않음

            // TODO PhotoURI

            buildNicknames(contact.getNicknames(), ops, rawid);
            buildAddresses(contact.getAddresses(), ops, rawid);
            buildEmails(contact.getEmails(), ops, rawid);
            buildPhoneNumbers(contact.getPhoneNumbers(), ops, rawid);
        }

        private void buildPhoneNumbers(IPhoneNumber[] phoneNumbers,
                List<ContentProviderOperation> ops, int rawid) {
            if (phoneNumbers == null || phoneNumbers.length < 1) return;

            for (IPhoneNumber phone : phoneNumbers) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValue(Data.RAW_CONTACT_ID, rawid)
                                .withValue(Data.MIMETYPE, IPhoneNumber.MIMETYPE)
                                .withValue(IPhoneNumber.COLUMN_NUMBER, phone.getNumber());

                String[] types = phone.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativePhoneType(type);
                    if (nativeType == Phone.TYPE_CUSTOM) {
                        builder.withValue(Phone.LABEL, type);
                    }
                    builder.withValue(IPhoneNumber.COLUMN_TYPE, nativeType);
                }
                // else {
                // builder.withValue(IPhoneNumber.COLUMN_TYPE,
                // ContactTypeUtil.getDefaultPhoneType());
                // }
                ops.add(builder.build());
            }
        }

        private void buildEmails(IEmailAddress[] emails, List<ContentProviderOperation> ops,
                int rawid) {
            if (emails == null || emails.length < 1) return;

            for (IEmailAddress email : emails) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValue(Data.RAW_CONTACT_ID, rawid)
                                .withValue(Data.MIMETYPE, IEmailAddress.MIMETYPE)
                                .withValue(IEmailAddress.COLUMN_EMAIL, email.getEmail());

                String[] types = email.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativeEmailType(type);
                    if (nativeType == Email.TYPE_CUSTOM) {
                        builder.withValue(Email.LABEL, type);
                    }
                    builder.withValue(IEmailAddress.COLUMN_TYPE, nativeType);
                }
                else {
                    builder.withValue(Email.LABEL, "HOME");
                    builder.withValue(IEmailAddress.COLUMN_TYPE, Email.TYPE_CUSTOM);
                }
                ops.add(builder.build());
            }

        }

        private void buildAddresses(IContactAddress[] addresses,
                List<ContentProviderOperation> ops, int rawid) {
            if (addresses == null || addresses.length < 1) return;

            for (IContactAddress address : addresses) {
                Builder builder =
                        ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValue(Data.RAW_CONTACT_ID, rawid)
                                .withValue(Data.MIMETYPE, IContactAddress.MIMETYPE);

                String value = address.getCountry();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_COUNTRY, value);
                }

                value = address.getRegion();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_REGION, value);
                }

                // value = address.getCounty()
                // if(value != null){
                // builder.withValue(StructuredPostal.Co, value);
                // }

                value = address.getCity();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_CITY, value);
                }

                value = address.getStreetAddress();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_STREETADDRESS, value);
                }

                // XXX addtionalInformation

                value = address.getPostalCode();
                if (value != null) {
                    builder.withValue(IContactAddress.COLUMN_POSTALCODE, value);
                }

                String[] types = address.getTypes();
                if (types != null && types.length > 0) {
                    String type = ContactTypeUtil.resolveType(types);
                    int nativeType = ContactTypeUtil.getNativePostalType(type);
                    if (nativeType == StructuredPostal.TYPE_CUSTOM) {
                        builder.withValue(StructuredPostal.LABEL, type);
                    }
                    builder.withValue(IContactAddress.COLUMN_TYPE, nativeType);
                }
                else {
                    builder.withValue(StructuredPostal.LABEL, "HOME");
                    builder.withValue(IContactAddress.COLUMN_TYPE, StructuredPostal.TYPE_CUSTOM);
                }
                ops.add(builder.build());
            }
        }

        private void buildNicknames(String[] nicknames, List<ContentProviderOperation> ops,
                int rawid) {
            if (nicknames == null || nicknames.length < 1) return;

            for (String nickname : nicknames) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.RAW_CONTACT_ID, rawid)
                        .withValue(Data.MIMETYPE, IContact.MIMETYPE_NICKNAME)
                        .withValue(IContact.COLUMN_NICKNAME, nickname).build());
            }
        }
    }
}

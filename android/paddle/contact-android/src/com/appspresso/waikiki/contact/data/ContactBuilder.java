package com.appspresso.waikiki.contact.data;

import java.util.ArrayList;
import java.util.Map;

public class ContactBuilder {
    private static ContactBuilder builder = null;

    public static ContactBuilder getBuilder() {
        if (builder == null) {
            synchronized (ContactBuilder.class) {
                if (builder == null) {
                    builder = new ContactBuilder();
                }
            }
        }

        return builder;
    }

    public IContact createContact() {
        return new Contact();
    }

    public IContact createContactFromMap(Map<String, Object> map) {
        Contact contact = new Contact();

        // XXX contact.setId((String) map.get(Contact.ATTR_ID));
        contact.setId((String) map.get("id"));
        contact.setFirstName((String) map.get(Contact.ATTR_FIRSTNAME));
        contact.setMiddleName((String) map.get(Contact.ATTR_MIDDLENAME));
        contact.setLastName((String) map.get(Contact.ATTR_LASTNAME));
        contact.setPhoneticName((String) map.get(Contact.ATTR_PHONETICNAME));
        contact.setPhotoURI((String) map.get(Contact.ATTR_PHOTOURI));

        String[] nicknames = toStringArray((Object[]) map.get(Contact.ATTR_NICKNAMES));
        if (nicknames != null) {
            for (String nickname : nicknames) {
                contact.addNickname(nickname);
            }
        }

        setAddresses(contact, (Object[]) map.get(Contact.ATTR_ADDRESSES));
        setPhoneNumbers(contact, (Object[]) map.get(Contact.ATTR_PHONENUMBERS));
        setEmails(contact, (Object[]) map.get(Contact.ATTR_EMAILS));
        // contact.setEmails(getEmails((Object[])
        // map.get(Contact.ATTR_EMAILS)));

        return contact;
    }

    private void setEmails(Contact contact, Object[] objs) {
        if (objs == null) return;

        int length = objs.length;
        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) objs[i];
            // emails[i] = new EmailAddress();
            // emails[i].setEmail((String) map.get(EmailAddress.ATTR_EMAIL));
            // emails[i].setTypes(toStringArray((Object[])
            // map.get(EmailAddress.ATTR_TYPES)));
            contact.addEmailAddress((String) map.get(EmailAddress.ATTR_EMAIL),
                    toStringArray((Object[]) map.get(EmailAddress.ATTR_TYPES)));
        }
    }

    private void setPhoneNumbers(Contact contact, Object[] objs) {
        if (objs == null) return;

        int length = objs.length;
        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) objs[i];
            contact.addPhoneNumber((String) map.get(PhoneNumber.ATTR_NUMBER),
                    toStringArray((Object[]) map.get(PhoneNumber.ATTR_TYPES)));
        }
    }

    private void setAddresses(Contact contact, Object[] objs) {
        if (objs == null) return;

        int length = objs.length;
        ContactAddress[] addresses = new ContactAddress[length];
        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) objs[i];
            // types
            Object types = map.get(ContactAddress.ATTR_TYPES);
            if (types != null && types instanceof Object[]) {
                map.put(ContactAddress.ATTR_TYPES, toStringArray((Object[]) types));
            }
            addresses[i] = new ContactAddress(map);
            // addresses[i].setCountry((String)
            // map.get(ContactAddress.ATTR_COUNTRY));
            // addresses[i].setRegion((String)
            // map.get(ContactAddress.ATTR_REGION));
            // addresses[i].setCounty((String)
            // map.get(ContactAddress.ATTR_COUNTY));
            // addresses[i].setCity((String) map.get(ContactAddress.ATTR_CITY));
            // addresses[i].setStreet((String)
            // map.get(ContactAddress.ATTR_STREET));
            // addresses[i].setStreetNumber((String)
            // map.get(ContactAddress.ATTR_STREETNUMBER));
            // addresses[i].setPremises((String)
            // map.get(ContactAddress.ATTR_PREMISES));
            // addresses[i].setAdditionalInformation((String)
            // map.get(ContactAddress.ATTR_ADDITIONALINFORMATION));
            // addresses[i].setPostalCode((String)
            // map.get(ContactAddress.ATTR_POSTALCODE));
            // addresses[i].setTypes(toStringArray((Object[])
            // map.get(ContactAddress.ATTR_TYPES)));
        }

        contact.setAddresses(addresses);
    }

    private String[] toStringArray(Object[] objs) {
        /*
         * 데이터베이스에서 타입을 읽어올 때 타입이 존재하지 않더라도 null인 하나의 타입을 가지고 있는 것으로 처리되어 있다. 실제 입력을 위해서는 이렇게 null로
         * 넘어온 값들은 제외하고 넘겨주도록 한다.
         */

        if (objs == null) return null;

        ArrayList<String> list = new ArrayList<String>();
        for (Object obj : objs) {
            if (obj == null) continue;
            list.add(obj.toString());
        }

        // return list.toArray(new String[]{});
        return (list.size() > 0) ? list.toArray(new String[] {}) : null;
    }
}

package com.appspresso.waikiki.contact.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Contact implements IContact {

    // Contact
    private String _id;

    // ContactProperties
    private String firstName = "";
    private String middleName = "";
    private String lastName = "";
    private String phoneticName = "";
    private String photoURI;

    private List<String> nicknames = new ArrayList<String>();
    private List<ContactAddress> addresses = new ArrayList<ContactAddress>();
    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
    private List<EmailAddress> emails = new ArrayList<EmailAddress>();

    @Override
    public Object getPluginResult() {
        Map<String, Object> res = new HashMap<String, Object>(9);
        res.put(ATTR_ID, this._id);
        res.put(ATTR_FIRSTNAME, this.firstName);
        res.put(ATTR_MIDDLENAME, this.middleName);
        res.put(ATTR_LASTNAME, this.lastName);
        res.put(ATTR_NICKNAMES, this.nicknames);
        res.put(ATTR_PHONETICNAME, this.phoneticName);
        res.put(ATTR_ADDRESSES, this.addresses);
        res.put(ATTR_PHOTOURI, this.photoURI);
        res.put(ATTR_PHONENUMBERS, this.phoneNumbers);
        res.put(ATTR_EMAILS, this.emails);

        return res;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public void setId(String _id) {
        this._id = _id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String[] getNicknames() {
        if (nicknames == null) return new String[] {};

        return (String[]) nicknames.toArray(new String[nicknames.size()]);
    }

    @Override
    public String getPhoneticName() {
        return phoneticName;
    }

    public void setPhoneticName(String phoneticName) {
        this.phoneticName = phoneticName;
    }

    @Override
    public ContactAddress[] getAddresses() {
        if (addresses == null) return new ContactAddress[] {};

        return (ContactAddress[]) addresses.toArray(new ContactAddress[addresses.size()]);
    }

    public void setAddresses(ContactAddress[] addresses) {
        this.addresses = Arrays.asList(addresses);
    }

    @Override
    public String getPhotoURI() {
        return this.photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    @Override
    public IPhoneNumber[] getPhoneNumbers() {
        if (phoneNumbers == null) return new PhoneNumber[] {};

        return (PhoneNumber[]) phoneNumbers.toArray(new PhoneNumber[phoneNumbers.size()]);
    }

    @Override
    public IEmailAddress[] getEmails() {
        if (emails == null) return new EmailAddress[] {};

        return (EmailAddress[]) emails.toArray(new EmailAddress[emails.size()]);
    }

    @Override
    public void addPhoneNumber(String number, String[] types) {
        if (number == null) { return; }

        this.phoneNumbers.add(new PhoneNumber(number, types));
    }

    @Override
    public void addEmailAddress(String email, String[] types) {
        if (email == null) { return; }

        this.emails.add(new EmailAddress(email, types));
    }

    @Override
    public void addNickname(String nickname) {
        if (nickname == null) { return; }

        this.nicknames.add(nickname);
    }

    @Override
    public void addContactAddress(String country, String region, String county, String city,
            String streetAddress, String additionalInformation, String postalCode, String[] types) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(IContactAddress.ATTR_COUNTRY, country);
        map.put(IContactAddress.ATTR_REGION, region);
        map.put(IContactAddress.ATTR_COUNTY, county);
        map.put(IContactAddress.ATTR_CITY, city);
        map.put(IContactAddress.ATTR_STREETADDRESS, streetAddress);
        map.put(IContactAddress.ATTR_ADDITIONALINFORMATION, additionalInformation);
        map.put(IContactAddress.ATTR_POSTALCODE, postalCode);
        map.put(IContactAddress.ATTR_TYPES, types);

        this.addresses.add(new ContactAddress(map));
    }
}

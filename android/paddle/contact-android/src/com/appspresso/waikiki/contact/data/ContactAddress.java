package com.appspresso.waikiki.contact.data;

import java.util.Map;

class ContactAddress implements IContactAddress {

    private Map<String, Object> address;

    public ContactAddress(Map<String, Object> map) {
        this.address = map;
    }

    @Override
    public Object getPluginResult() {
        // Map<String, Object> res = new HashMap<String, Object>();
        //
        // res.put(ATTR_COUNTRY, this.country);
        // res.put(ATTR_REGION, this.region);
        // res.put(ATTR_COUNTY, this.county);
        // res.put(ATTR_CITY, this.city);
        // res.put(ATTR_STREET, this.street);
        // res.put(ATTR_STREETNUMBER, this.streetNumber);
        // res.put(ATTR_PREMISES, this.premises);
        // res.put(ATTR_ADDITIONALINFORMATION, this.additionalInformation);
        // res.put(ATTR_POSTALCODE, this.postalCode);
        // res.put("types", this.types);

        return this.address;
    }

    @Override
    public String getCountry() {
        // return country;
        return getStringValue(ATTR_COUNTRY);
    }

    @Override
    public String getRegion() {
        // return region;
        return getStringValue(ATTR_REGION);
    }

    @Override
    public String getCounty() {
        // return county;
        return getStringValue(ATTR_COUNTY);
    }

    @Override
    public String getCity() {
        // return city;
        return getStringValue(ATTR_CITY);
    }

    @Override
    public String getStreetAddress() {
        // return streetNumber;
        return getStringValue(ATTR_STREETADDRESS);
    }

    @Override
    public String getAdditionalInformation() {
        // return additionalInformation;
        return getStringValue(ATTR_ADDITIONALINFORMATION);
    }

    @Override
    public String getPostalCode() {
        return getStringValue(ATTR_POSTALCODE);
    }

    @Override
    public String[] getTypes() {
        Object value = this.address.get(ATTR_TYPES);
        return value != null ? (String[]) this.address.get(ATTR_TYPES) : null;
    }

    private String getStringValue(String attribute) {
        Object value = this.address.get(attribute);
        return value != null ? (String) value : null;
    }

}

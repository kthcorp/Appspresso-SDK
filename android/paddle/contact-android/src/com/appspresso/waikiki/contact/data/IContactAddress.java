package com.appspresso.waikiki.contact.data;

import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

import com.appspresso.api.AxPluginResult;

public interface IContactAddress extends AxPluginResult {
    String MIMETYPE = StructuredPostal.CONTENT_ITEM_TYPE;

    String COLUMN_COUNTRY = StructuredPostal.COUNTRY;
    String COLUMN_REGION = StructuredPostal.REGION;
    String COLUMN_COUNTY = null;
    String COLUMN_CITY = StructuredPostal.CITY;
    String COLUMN_STREETADDRESS = StructuredPostal.STREET;
    String COLUMN_ADDITIONALINFORMATION = null;
    String COLUMN_POSTALCODE = StructuredPostal.POSTCODE;
    String COLUMN_TYPE = StructuredPostal.TYPE;

    String ATTR_COUNTRY = "country";
    String ATTR_REGION = "region";
    String ATTR_COUNTY = "county";
    String ATTR_CITY = "city";
    String ATTR_STREETADDRESS = "streetAddress";
    String ATTR_ADDITIONALINFORMATION = "additionalInformation";
    String ATTR_POSTALCODE = "postalCode";
    String ATTR_TYPES = "types";

    String[] getTypes();

    String getPostalCode();

    String getAdditionalInformation();

    String getStreetAddress();

    String getCity();

    String getCounty();

    String getRegion();

    String getCountry();

}

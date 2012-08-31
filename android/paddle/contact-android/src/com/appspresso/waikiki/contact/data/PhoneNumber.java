package com.appspresso.waikiki.contact.data;

import java.util.HashMap;
import java.util.Map;

class PhoneNumber implements IPhoneNumber {

    private String number;
    private String[] types;

    public PhoneNumber(String number, String[] types) {
        this.number = number;
        this.types = types;
    }

    @Override
    public Object getPluginResult() {
        Map<String, Object> res = new HashMap<String, Object>(2);

        res.put(ATTR_NUMBER, this.number);
        res.put(ATTR_TYPES, this.types);
        return res;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

}

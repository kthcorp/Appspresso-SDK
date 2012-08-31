package com.appspresso.waikiki.contact.data;

import java.util.HashMap;
import java.util.Map;

class EmailAddress implements IEmailAddress {

    private String email;
    private String[] types;

    public EmailAddress(String email, String[] types) {
        this.email = email;
        this.types = types;
    }

    @Override
    public Object getPluginResult() {
        Map<String, Object> res = new HashMap<String, Object>(2);
        res.put(ATTR_EMAIL, this.email);
        res.put(ATTR_TYPES, this.types);

        return res;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

}

package com.appspresso.waikiki.contact;

class ContactIDEncoder {

    public static String encode(int rawId) {
        String id = Integer.toString(rawId);
        return id;
    }

    public static int decode(String id) {
        int rawId = Integer.parseInt(id);
        return rawId;
    }
}

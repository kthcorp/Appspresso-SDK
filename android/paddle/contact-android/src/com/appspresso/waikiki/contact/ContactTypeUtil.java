package com.appspresso.waikiki.contact;

import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

class ContactTypeUtil {
    public static int getNativePostalType(String type) {
        if ("WORK".equals(type)) {
            return StructuredPostal.TYPE_WORK;
        }
        else if ("HOME".equals(type)) { return StructuredPostal.TYPE_HOME; }

        return getDefaultPostalType();
    }

    public static int getDefaultPostalType() {
        return StructuredPostal.TYPE_CUSTOM;
    }

    public static int getNativePhoneType(String type) {
        if ("WORK".equals(type)) {
            return Phone.TYPE_WORK;
        }
        else if ("HOME".equals(type)) {
            return Phone.TYPE_HOME;
        }
        else if ("FAX".equals(type)) {
            return Phone.TYPE_FAX_HOME;
        }
        else if ("CELL".equals(type)) {
            return Phone.TYPE_MOBILE;
        }
        else if ("MSG".equals(type)) {
            return Phone.TYPE_MMS;
        }
        else if ("PAGER".equals(type)) {
            return Phone.TYPE_PAGER;
        }
        else if ("CAR".equals(type)) {
            return Phone.TYPE_CAR;
        }
        else if ("ISDN".equals(type)) {
            return Phone.TYPE_ISDN;
        }
        else if ("VOICE".equals(type)) {}
        else if ("BBS".equals(type)) {}
        else if ("MODEM".equals(type)) {}
        else if ("VIDEO".equals(type)) {}
        else if ("PCS".equals(type)) {}
        else if ("OTHER".equals(type)) { return Phone.TYPE_OTHER; }
        return getDefaultPhoneType();
    }

    public static int getDefaultPhoneType() {
        return Phone.TYPE_CUSTOM;
    }

    public static int getNativeEmailType(String type) {
        if ("WORK".equals(type)) {
            return Email.TYPE_WORK;
        }
        else if ("HOME".equals(type)) { return Email.TYPE_HOME; }
        return getDefaultEmailType();
    }

    public static int getDefaultEmailType() {
        return Email.TYPE_CUSTOM;
    }

    public static String getWaikikiPhoneType(int type) {
        switch (type) {
            case Phone.TYPE_WORK:
                return "WORK";
            case Phone.TYPE_HOME:
                return "HOME";
            case Phone.TYPE_FAX_HOME:
            case Phone.TYPE_FAX_WORK:
                return "FAX";
            case Phone.TYPE_MMS:
                return "MSG";
            case Phone.TYPE_PAGER:
                return "PAGER";
            case Phone.TYPE_CAR:
                return "CAR";
            case Phone.TYPE_ISDN:
                return "ISDN";
            case Phone.TYPE_MOBILE:
                return "CELL";
            case Phone.TYPE_OTHER:
                return "OTHER";
        }

        return null;
    }

    public static String getWaikikiEmailType(int type) {
        switch (type) {
            case Email.TYPE_WORK:
                return "WORK";
            case Email.TYPE_HOME:
                return "HOME";
        }
        return null;
    }

    public static String getWaikikiPostalType(int type) {
        switch (type) {
            case StructuredPostal.TYPE_WORK:
                return "WORK";
            case StructuredPostal.TYPE_HOME:
                return "HOME";
        }

        return null;
    }

    public static String resolveType(String[] types) {
        String type = null;
        if (types == null) return null;

        if (types.length == 1 && "PREF".equals(types[0])) { return "PREF"; }

        for (int i = 0; i < types.length; i++) {
            if (!"PREF".equals(types[i])) {
                type = types[i];
                break;
            }
        }
        return type;
    }

}

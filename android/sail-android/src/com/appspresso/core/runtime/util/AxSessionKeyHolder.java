package com.appspresso.core.runtime.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

import org.apache.commons.logging.Log;

import com.appspresso.api.AxLog;

public class AxSessionKeyHolder {

    private static final Log L = AxLog.getLog(AxSessionKeyHolder.class);
    private static AxSessionKeyHolder self;

    private String key;

    private AxSessionKeyHolder() {
        key = null;
    }

    public static AxSessionKeyHolder instance() {
        if (self == null) {
            self = new AxSessionKeyHolder();
        }
        return self;
    }

    public String generate() {
        if (key != null) {
            if (L.isWarnEnabled()) {
                L.warn("re-request auth page. maybe attempt to attack");
            }
            return key;
        }

        String base = "appspresso" + System.currentTimeMillis() + new Random().nextFloat();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(base.getBytes());

            Formatter formatter = new Formatter();
            for (byte piece : bytes) {
                formatter.format("%02x", piece);
            }
            key = formatter.toString();
        }
        catch (NoSuchAlgorithmException e) {
            if (L.isWarnEnabled()) {
                L.warn("cannot initiate SHA-1 algorithm", e);
            }
            key = base;
        }

        return key;
    }

    public String key() {
        if (key == null) return "appspresso session uninitialized";

        return key;
    }

}

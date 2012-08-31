package com.appspresso.waikiki.orientation;

import java.util.HashMap;
import com.appspresso.api.AxPluginResult;

public class Rotation implements AxPluginResult {
    public static final String FIELD_ALPHA = "alpha";
    public static final String FIELD_BETA = "beta";
    public static final String FIELD_GAMMA = "gamma";

    private long[] value;
    private HashMap<String, Object> fieldMap;

    public Rotation() {
        value = new long[3];
        fieldMap = new HashMap<String, Object>();
    }

    public synchronized void setValue(float[] value) {
        this.value[0] = 360 - (long) value[0];
        this.value[1] = (long) -value[1];
        this.value[2] = (long) -value[2];
    }

    public synchronized long[] getValue() {
        return value;
    }

    @Override
    public synchronized Object getPluginResult() {
        fieldMap.put(FIELD_ALPHA, value[0]);
        fieldMap.put(FIELD_BETA, value[1]);
        fieldMap.put(FIELD_GAMMA, value[2]);
        return fieldMap;
    }
}

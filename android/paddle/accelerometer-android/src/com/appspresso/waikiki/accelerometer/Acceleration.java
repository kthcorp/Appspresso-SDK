package com.appspresso.waikiki.accelerometer;

import java.util.HashMap;

import com.appspresso.api.AxPluginResult;

public class Acceleration implements AxPluginResult {
    public static final String FIELD_XAXIS = "xAxis";
    public static final String FIELD_YAXIS = "yAxis";
    public static final String FIELD_ZAXIS = "zAxis";

    private float[] value;
    private HashMap<String, Object> fieldMap;

    public Acceleration() {
        value = new float[3];
        fieldMap = new HashMap<String, Object>();
    }

    public void setValue(float[] value) {
        this.value[0] = -value[0];
        this.value[1] = -value[1];
        this.value[2] = -value[2];
    }

    public float[] getValue() {
        return value;
    }

    @Override
    public Object getPluginResult() {
        fieldMap.put(FIELD_XAXIS, value[0]);
        fieldMap.put(FIELD_YAXIS, value[1]);
        fieldMap.put(FIELD_ZAXIS, value[2]);
        return fieldMap;
    }
}

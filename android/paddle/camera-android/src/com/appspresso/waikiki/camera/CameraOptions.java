package com.appspresso.waikiki.camera;

import java.util.HashMap;
import java.util.Map;

import com.appspresso.api.AxPluginResult;

class CameraOptions implements AxPluginResult {
    private String destinationFilename;
    private boolean highRes;

    public CameraOptions(Map<String, Object> map) {
        if (map.containsKey("destinationFilename")) {
            destinationFilename = (String) map.get("destinationFilename");
        }
        if (map.containsKey("highRes")) {
            highRes = (Boolean) map.get("highRes");
        }
    }

    public String getDestinationFileName() {
        return destinationFilename;
    }

    public boolean isHighRes() {
        return highRes;
    }

    @Override
    public Object getPluginResult() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("destinationFilename", destinationFilename);
        result.put("highRes", highRes);
        return result;
    }
}

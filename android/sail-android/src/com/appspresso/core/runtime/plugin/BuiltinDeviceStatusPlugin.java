package com.appspresso.core.runtime.plugin;

import android.os.Build;

import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;

public class BuiltinDeviceStatusPlugin extends DefaultAxPlugin {

    static final String NAME = "ax.builtin.devicestatus";

    public void getVendor(AxPluginContext context) {
        context.sendResult(Build.BRAND);
    }

    public void getModel(AxPluginContext context) {
        context.sendResult(Build.MODEL);
    }
}

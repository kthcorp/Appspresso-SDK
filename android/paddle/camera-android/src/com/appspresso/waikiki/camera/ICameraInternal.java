package com.appspresso.waikiki.camera;

import android.app.Activity;

import com.appspresso.api.AxPluginContext;

public interface ICameraInternal extends ICamera {

    String getId();

    void startPreview(AxPluginContext context);

    void stopPreview(AxPluginContext context);

    void setPreviewLayout(AxPluginContext context);

    void onActivityResume(Activity activity);

    void onActivityPause(Activity activity);

}

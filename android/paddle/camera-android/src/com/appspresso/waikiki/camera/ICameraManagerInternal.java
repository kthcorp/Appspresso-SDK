package com.appspresso.waikiki.camera;

import android.app.Activity;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxPluginContext;

public interface ICameraManagerInternal extends ICameraManager {

    ICameraInternal getCamera(AxPluginContext context);

    ICameraInternal newCameraInstance(AxRuntimeContext runtimeContext);

    void onActivityResume(Activity activity);

    void onActivityPause(Activity activity);
}

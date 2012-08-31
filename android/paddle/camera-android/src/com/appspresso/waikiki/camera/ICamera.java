package com.appspresso.waikiki.camera;

import com.appspresso.api.AxPluginContext;

public interface ICamera {
    public void captureImage(AxPluginContext context);

    public void startVideoCapture(AxPluginContext context);

    public void stopVideoCapture(AxPluginContext context);

    public void createPreviewNode(AxPluginContext context);
}

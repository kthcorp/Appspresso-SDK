package com.appspresso.waikiki.camera;

import com.appspresso.api.AxRuntimeContext;

public class CameraManagerAPILevel8 extends CameraManagerAPILevel7 {

    public CameraManagerAPILevel8(AxRuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    public ICameraInternal newCameraInstance(AxRuntimeContext runtimeContext) {
        return new CameraAPILevel8(runtimeContext);
    }

}

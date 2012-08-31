package com.appspresso.waikiki.deviceapis;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.w3.Feature;

public class Deviceapis extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = FeatureConstants.FEATURE_DEVICEAPIS;

    private AxRuntimeContext runtimeContext;
    private DeviceapisImpl deviceapis;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        if (!runtimeContext.isActivatedFeature(FEATURE_DEFAULT)) { throw new AxError(
                AxError.SECURITY_ERR, "이 모듈을 사용하기 위해서는 최소한 하나 이상의 feature가 필요합니다."); // TODO Error
                                                                                     // message
        }

        super.activate(runtimeContext);
        this.runtimeContext = runtimeContext;
        this.deviceapis = new DeviceapisImpl();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        super.deactivate(runtimeContext);
        this.runtimeContext = null;
        this.deviceapis = null;
    }

    public void listAvailableFeatures(AxPluginContext context) {
        Feature[] configuredFeatures = runtimeContext.getActivatedFeatures();
        Feature[] availableFeatures = deviceapis.listAvailableFeatures(configuredFeatures);

        context.sendResult(availableFeatures);
    }

    public void listActivatedFeatures(AxPluginContext context) {
        Feature[] configuredFeatures = runtimeContext.getActivatedFeatures();
        Feature[] activatedFeatures = deviceapis.listActivatedFeatures(configuredFeatures);

        context.sendResult(activatedFeatures);
    }
}

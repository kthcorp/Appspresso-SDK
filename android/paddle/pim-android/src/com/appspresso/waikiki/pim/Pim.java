package com.appspresso.waikiki.pim;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;

public class Pim extends DefaultAxPlugin {
    @Override
    public void activate(AxRuntimeContext axRuntimeContext) {
        axRuntimeContext.requirePlugin("deviceapis");
        super.activate(axRuntimeContext);
    }

    @Override
    public void deactivate(AxRuntimeContext axRuntimeContext) {
        super.deactivate(axRuntimeContext);
    }
}

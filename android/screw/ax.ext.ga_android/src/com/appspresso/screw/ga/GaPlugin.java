package com.appspresso.screw.ga;

import org.apache.commons.logging.Log;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GaPlugin extends DefaultAxPlugin {

    private static final Log L = AxLog.getLog(GaPlugin.class);

    private GoogleAnalyticsTracker gaTracker;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        gaTracker = GoogleAnalyticsTracker.getInstance();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        try {
            gaTracker.dispatch();
            gaTracker.stopSession();
            gaTracker = null;
        }
        catch (Exception ignored) {}

        super.deactivate(runtimeContext);
    }

    //
    // called from native code
    //

    public GoogleAnalyticsTracker getTracker() {
        return gaTracker;
    }

    public void startTracker(String accountId, int dispatchPeriod) throws Exception {
        gaTracker.startNewSession(accountId, dispatchPeriod, runtimeContext.getActivity());
        gaTracker.dispatch();// 이전 세션에서 아직 못보낸 이벤트들 있으면 지금 보내자...
    }

    public void stopTracker() throws Exception {
        gaTracker.dispatch();// 아직 못보낸 이벤트 있으면 지금 보내자...
        gaTracker.stopSession();
    }

    public void trackEvent(String category, String action, String label, int value)
            throws Exception {
        gaTracker.trackEvent(category, action, label, value);
    }

    public void trackPageview(String page) throws Exception {
        gaTracker.trackPageView(page);
    }

    //
    // called from javascript code
    //

    public void startTracker(AxPluginContext context) {
        String accountId = context.getParamAsString(0);
        int dispatchPeriod = context.getParamAsNumber(1, 10).intValue();

        if (L.isTraceEnabled()) {
            L.trace("startTracker: accountId=" + accountId + ",dispatchPeriod=" + dispatchPeriod);
        }

        try {
            startTracker(accountId, dispatchPeriod);
            context.sendResult();
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void stopTracker(AxPluginContext context) {
        if (L.isTraceEnabled()) {
            L.trace("stopTracker");
        }

        try {
            stopTracker();
            context.sendResult();
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void trackEvent(AxPluginContext context) {
        String category = context.getParamAsString(0);
        String action = context.getParamAsString(1, "");
        String label = context.getParamAsString(2, "");
        int value = context.getParamAsNumber(3, -1).intValue();

        if (L.isTraceEnabled()) {
            L.trace("trackEvent: category=" + category + ",action=" + action + ",label=" + label
                    + ",value=" + value);
        }

        try {
            trackEvent(category, action, label, value);
            context.sendResult();
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void trackPageview(AxPluginContext context) {
        String page = context.getParamAsString(0);

        if (L.isTraceEnabled()) {
            L.trace("trackPageview: page=" + page);
        }

        try {
            trackPageview(page);
            context.sendResult();
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

}

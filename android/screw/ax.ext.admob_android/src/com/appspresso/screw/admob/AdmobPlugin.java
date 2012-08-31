package com.appspresso.screw.admob;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdmobPlugin extends DefaultAxPlugin {

    private static final Log L = AxLog.getLog(AdmobPlugin.class);

    private Map<String, AdView> adViews;

    private Activity realActivity;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        adViews = new HashMap<String, AdView>(1);
        realActivity = runtimeContext.getActivity();

    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        for (AdView adView : adViews.values()) {
            adView.destroy();
        }
        adViews.clear();
        adViews = null;

        super.deactivate(runtimeContext);
    }

    public void showAdmob(final AxPluginContext context) {
        final String pubId = context.getParamAsString(0);

        synchronized (adViews) {

            // XXX: AdView는 runOnUiThread 에서 제어 가능... althjs
            realActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    AdView adView = adViews.get(pubId);

                    if (adView == null) {
                        adView = new AdView(realActivity, AdSize.BANNER, pubId);

                        RelativeLayout adRootLayout = new RelativeLayout(realActivity);
                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);

                        adRootLayout.setLayoutParams(params);

                        adRootLayout.addView(adView);
                        ((ViewGroup) runtimeContext.getWebView().getParent()).addView(adRootLayout);
                    }

                    adView.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams adParams =
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                    int position = context.getNamedParamAsNumber(1, "position", 0).intValue();

                    if (position > 0 && position < 7) { // 포지션 파라미터 우선 선택 -
                                                        // althjs

                        switch (position) {
                            case 1:
                            case 2:
                            case 3:
                                adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                break;
                            case 4:
                            case 5:
                            case 6:
                                adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                break;
                            default:
                                adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        }

                        switch (position) {
                            case 1:
                            case 4:
                                adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                break;
                            case 2:
                            case 5:
                                adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                break;
                            case 3:
                            case 6:
                                adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                break;
                            default:
                                adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        }

                    }
                    else { // fixed position

                        /*
                         * default top, width, height 구하는 코드. 일단 주석처리... althjs if (top < 0) { // by
                         * default, bottom of screen android.view.WindowManager wm =
                         * (android.view.WindowManager) runtimeContext .getActivity()
                         * .getSystemService( android.content.Context.WINDOW_SERVICE); top =
                         * wm.getDefaultDisplay().getHeight() - AdSize.BANNER.getHeight(); } if
                         * (width <= 0) { width = AdSize.BANNER.getWidth(); } if (height <= 0) {
                         * height = AdSize.BANNER.getHeight(); }
                         */

                        // TODO: width, height 구현 필요 - althjs
                        int width = context.getNamedParamAsNumber(1, "width", -1).intValue();
                        int height = context.getNamedParamAsNumber(1, "height", -1).intValue();
                        int top = context.getNamedParamAsNumber(1, "top", 0).intValue();
                        int left = context.getNamedParamAsNumber(1, "left", 0).intValue();

                        if (L.isTraceEnabled()) {
                            L.trace("showAdmob: pubId=" + pubId + ",top=" + top + ",left=" + left
                                    + ",width=" + width + ",height=" + height);
                        }
                        // XXX: RelativeLayout의 layout이 원하는데로 동작하지 않음. AdView에
                        // 직접 마진을 주는 방식으로 처리 - althjs
                        adParams.setMargins(left, top, 0, 0);

                    }

                    try {

                        adView.setLayoutParams(adParams);
                        adView.setVisibility(View.VISIBLE);
                        adView.loadAd(new AdRequest());
                        context.sendResult();

                        adViews.put(pubId, adView);

                    }
                    catch (Exception e) {

                        L.trace("showAdmob err: " + e.toString());
                        context.sendError(AxError.UNKNOWN_ERR, e.getMessage());

                    }
                }
            });

        }

    }

    public void hideAdmob(final AxPluginContext context) {
        String pubId = context.getParamAsString(0);

        if (L.isTraceEnabled()) {
            L.trace("hideAdmob: pubId=" + pubId);
        }

        final AdView adView = adViews.get(pubId);
        if (adView == null) {
            context.sendError(AxError.INVALID_VALUES_ERR, "invalid pubId: " + pubId);
            return;
        }

        try {

            realActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        adView.stopLoading();
                        adView.setVisibility(View.GONE);
                        context.sendResult();
                    }
                    catch (Exception e) {
                        if (L.isTraceEnabled()) {
                            L.trace("hideAdmob err on runOnUiThread: " + e.getMessage());
                        }
                        context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
                    }
                }
            });
        }
        catch (Exception e) {
            if (L.isTraceEnabled()) {
                L.trace("hideAdmob err: " + e.getMessage());
            }
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void refreshAdmob(AxPluginContext context) {
        String pubId = context.getParamAsString(0);

        if (L.isTraceEnabled()) {
            L.trace("refreshAdmob: pubId=" + pubId);
        }

        AdView adView = adViews.get(pubId);
        if (adView == null) {
            context.sendError(AxError.INVALID_VALUES_ERR, "invalid pubId: " + pubId);
            return;
        }

        try {
            Looper.prepare();
            adView.loadAd(new AdRequest());
            context.sendResult();
            Looper.loop();
        }
        catch (Exception e) {
            if (L.isTraceEnabled()) {
                L.trace("refreshAdmob err: " + e.getMessage());
            }
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

}

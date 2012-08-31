package com.appspresso.waikiki.geolocation;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit.GeolocationPermissions.Callback;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.view.WebChromeClientAdapter;
import com.appspresso.internal.AxConfig;
import com.appspresso.w3.Feature;

public class Geolocation extends DefaultAxPlugin {
    private final static String GEOLOCATION_FEATURE_URI = "http://www.w3.org/TR/geolocation-API/";

    private final static String PREFEREENCE_NAME = "deviceapis.gelocation.preferences";
    private final static String KEY_ALLOW = "KEY_PERMISSION";

    private HashMap<String, String> params = new HashMap<String, String>(0);

    private WebChromeClientAdapter webChromeClientListener = new WebChromeClientAdapter() {

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final Callback callback) {
            final SharedPreferences pref = getSharedPreferences();
            boolean allow = pref.getBoolean(KEY_ALLOW, false);

            if (allow) {
                callback.invoke(origin, allow, true);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(runtimeContext.getActivity());
                builder.setCancelable(false)
                        .setMessage(R.string.ax_geoperm_msg)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        callback.invoke(origin, true, true);

                                        Editor editor = pref.edit();
                                        editor.putBoolean(KEY_ALLOW, true);
                                        editor.commit();
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        callback.invoke(origin, false, true);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    };

    @SuppressWarnings("deprecation")
    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        runtimeContext.addWebChromeClientListener(webChromeClientListener);

        Feature[] features = runtimeContext.getActivatedFeatures();
        for (Feature f : features) {
            if (GEOLOCATION_FEATURE_URI.equals(f.getUri())) {
                this.params.putAll(f.getParams());
                break;
            }
        }

        if (AxConfig.getAttributeAsBoolean("app.devel", false)) {
            clearGeolocationPermission();
        }
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        runtimeContext.removeWebChromeClientListener(webChromeClientListener);

        super.deactivate(runtimeContext);
    }

    private void clearGeolocationPermission() {
        Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }

    private SharedPreferences getSharedPreferences() {
        return runtimeContext.getActivity().getSharedPreferences(PREFEREENCE_NAME,
                Context.MODE_PRIVATE);
    }
}

package com.appspresso.screw.android;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.activity.ActivityAdapter;
import com.appspresso.api.activity.ActivityListener;

/**
 * ax.ext.android
 */
public class AndroidPlugin extends DefaultAxPlugin {

    private static final String JS_CALLBACK_ON_BACK_PRESSED = "ax.ext.android.onBackPressed";
    private static final String JS_CALLBACK_ON_OPTIONS_ITEM_SELECTED =
            "ax.ext.android.onOptionsItemSelected";

    private boolean onBackPressed = false;
    private boolean onOptionsItemSelected = false;
    private String[] optionsItems = null;

    // 만든 재진입(reentrant) 방지를 위해 대충... 락까지 동원하긴 좀 뭣하고...
    private boolean onBackPressed_running = false;
    private boolean onOptionsItemSelected_running = false;

    //
    // IActivityListener
    //

    private final ActivityListener activityListener = new ActivityAdapter() {

        @Override
        public boolean onBackPressed(final Activity activity) {
            if (!onBackPressed) { return false; }
            if (onBackPressed_running) { return true; }

            onBackPressed_running = true;
            try {
                runtimeContext.invokeJavaScriptFunction(JS_CALLBACK_ON_BACK_PRESSED);
            }
            finally {
                onBackPressed_running = false;
            }
            return true;
        }

        // @Override
        // public boolean onCreateOptionsMenu(final Activity activity, Menu
        // menu) {
        // if (optionsMenuResource != 0) {
        // activity.getMenuInflater().inflate(optionsMenuResource, menu);
        // return true;
        // }
        // return false;// not handled
        // }

        @Override
        public boolean onPrepareOptionsMenu(final Activity activity, Menu menu) {
            if (optionsItems == null) { return false; }

            menu.clear();
            if (onOptionsItemSelected) {
                for (int itemId = 0; itemId < optionsItems.length; itemId++) {
                    // TODO: 아이콘 지원...
                    menu.add(Menu.NONE, itemId, 0, optionsItems[itemId]);
                }
            }
            // activity.getMenuInflater().inflate(optionsMenuResource, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(final Activity activity, MenuItem item) {
            if (!onOptionsItemSelected) { return false; }
            if (onOptionsItemSelected_running) { return true; }

            onOptionsItemSelected_running = true;
            try {
                runtimeContext.invokeJavaScriptFunction(JS_CALLBACK_ON_OPTIONS_ITEM_SELECTED,
                        item.getItemId());
            }
            finally {
                onOptionsItemSelected_running = false;
            }
            return true;
        }

    };

    //
    //
    //

    @Override
    public void activate(AxRuntimeContext context) {
        super.activate(context);
        context.addActivityListener(activityListener);
    }

    @Override
    public void deactivate(AxRuntimeContext context) {
        context.removeActivityListener(activityListener);
        super.deactivate(context);
    }

    //
    //
    //

    public void setOnBackPressed(AxPluginContext context) {
        this.onBackPressed = context.getParamAsBoolean(0, false);
        context.sendResult();
    }

    public void setOptionsItems(AxPluginContext context) {
        this.optionsItems = context.getParamAsStringArray(0, null);
        context.sendResult();
    }

    public void setOnOptionsItemSelected(AxPluginContext context) {
        this.onOptionsItemSelected = context.getParamAsBoolean(0, false);
        context.sendResult();
    }

    public void startActivity(AxPluginContext context) {
        String action = context.getNamedParamAsString(0, "action");
        String data = context.getNamedParamAsString(0, "data", null);
        String type = context.getNamedParamAsString(0, "type", null);
        int flags = context.getNamedParamAsNumber(0, "flags", -1).intValue();
        Map<String, Object> extras = context.getParamAsMap(1);
        // context.getNamedParamAsString(0, "extras", null);

        Intent intent = new Intent();
        if (action != null) {
            intent.setAction(action);
        }
        if (data != null) {
            intent.setData(Uri.parse(data));
        }
        if (type != null) {
            intent.setType(type);
        }
        if (flags > 0) {
            intent.addFlags(flags);
        }
        // TODO: 여러가지 타입의 extra 항목들을 어떻게 전달할까나?
        if (extras != null) {
            for (Map.Entry<String, Object> entry : extras.entrySet()) {
                intent.putExtra(entry.getKey(), (String) entry.getValue());
            }
        }

        try {
            runtimeContext.getActivity().startActivity(intent);

            context.sendResult();
        }
        catch (Exception e) {
            // ActivityNotFoundException
            context.sendError(AxError.ABORT_ERR, e.getMessage());
        }
    }

    public void finish(AxPluginContext context) {
        int resultCode = context.getParamAsNumber(0, 0).intValue();

        runtimeContext.getActivity().setResult(resultCode);
        runtimeContext.getActivity().finish();

        context.sendResult();
    }

}

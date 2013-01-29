/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 모든 이벤트 처리를 여러 개의 구현체에게 위임하는 {@link ActivityListener} 구현체.
 * 
 * @version 1.0
 */
public class MulticastActivityListener implements ActivityListener {

    private final List<ActivityListener> listeners;

    public MulticastActivityListener() {
        this(new ArrayList<ActivityListener>());
    }

    public MulticastActivityListener(List<ActivityListener> listeners) {
        this.listeners = listeners;
    }

    public void addActivityListener(ActivityListener l) {
        this.listeners.add(l);
    }

    public void removeActivityListener(ActivityListener l) {
        this.listeners.remove(l);
    }

    //
    //
    //

    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {
        for (ActivityListener l : listeners) {
            l.onActivityCreate(activity, savedInstanceState);
        }
    }

    @Override
    public void onActivityRestart(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityRestart(activity);
        }
    }

    @Override
    public void onActivityStart(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityStart(activity);
        }
    }

    @Override
    public void onActivityPause(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityPause(activity);
        }
    }

    @Override
    public void onActivityResume(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityResume(activity);
        }
    }

    @Override
    public void onActivityStop(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityStop(activity);
        }
    }

    @Override
    public void onActivityDestroy(final Activity activity) {
        for (ActivityListener l : listeners) {
            l.onActivityDestroy(activity);
        }
    }

    @Override
    public void onRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        for (ActivityListener l : listeners) {
            l.onRestoreInstanceState(activity, savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle outState) {
        for (ActivityListener l : listeners) {
            l.onSaveInstanceState(activity, outState);
        }
    }

    @Override
    public boolean onActivityResult(final Activity activity, int requestCode, int resultCode,
            Intent imageReturnedIntent) {
        for (ActivityListener l : listeners) {
            if (l.onActivityResult(activity, requestCode, resultCode, imageReturnedIntent)) { return true; }
        }
        return false;
    }

    @Override
    public boolean onBackPressed(final Activity activity) {
        for (ActivityListener l : listeners) {
            if (l.onBackPressed(activity)) { return true; }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(final Activity activity, Menu menu) {
        for (ActivityListener l : listeners) {
            if (l.onCreateOptionsMenu(activity, menu)) { return true; }
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Activity activity, Menu menu) {
        for (ActivityListener l : listeners) {
            if (l.onPrepareOptionsMenu(activity, menu)) { return true; }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(final Activity activity, MenuItem item) {
        for (ActivityListener l : listeners) {
            if (l.onOptionsItemSelected(activity, item)) { return true; }
        }
        return false;
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        for (ActivityListener l : listeners) {
            l.onNewIntent(activity, intent);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        for (ActivityListener l : listeners) {
            l.onWindowFocusChanged(hasFocus);
        }
    }

}

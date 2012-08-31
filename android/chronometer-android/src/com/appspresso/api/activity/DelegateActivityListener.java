/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package com.appspresso.api.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 모든 이벤트 처리를 별도의 구현체에게 위임하는 {@link ActivityListener} 구현체.
 * 
 * @version 1.0
 */
public class DelegateActivityListener implements ActivityListener {

    private final ActivityListener delegate;

    public DelegateActivityListener(ActivityListener delegate) {
        this.delegate = delegate;
    }

    //
    //
    //

    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {
        delegate.onActivityCreate(activity, savedInstanceState);
    }

    @Override
    public void onActivityRestart(final Activity activity) {
        delegate.onActivityRestart(activity);
    }

    @Override
    public void onActivityStart(final Activity activity) {
        delegate.onActivityStart(activity);
    }

    @Override
    public void onActivityPause(final Activity activity) {
        delegate.onActivityPause(activity);
    }

    @Override
    public void onActivityResume(final Activity activity) {
        delegate.onActivityResume(activity);
    }

    @Override
    public void onActivityStop(final Activity activity) {
        delegate.onActivityStop(activity);
    }

    @Override
    public void onActivityDestroy(final Activity activity) {
        delegate.onActivityDestroy(activity);
    }

    @Override
    public void onRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        delegate.onRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle outState) {
        delegate.onSaveInstanceState(activity, outState);
    }

    @Override
    public boolean onActivityResult(final Activity activity, int requestCode, int resultCode,
            Intent imageReturnedIntent) {
        return delegate.onActivityResult(activity, requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public boolean onBackPressed(final Activity activity) {
        return delegate.onBackPressed(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(final Activity activity, Menu menu) {
        return delegate.onCreateOptionsMenu(activity, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Activity activity, Menu menu) {
        return delegate.onPrepareOptionsMenu(activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final Activity activity, MenuItem item) {
        return delegate.onOptionsItemSelected(activity, item);
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        delegate.onNewIntent(activity, intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        delegate.onWindowFocusChanged(hasFocus);
    }

}

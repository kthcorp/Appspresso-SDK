/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * {@link ActivityListener}의 기본 구현체.
 * <p>
 * {@link ActivityListener}의 일부 메소드만 필요할 때, 이 클래스를 상속하고 해당 메소드만 오버라이드.
 * 
 * @version 1.0
 */
public class ActivityAdapter implements ActivityListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityRestart(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityStart(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityPause(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResume(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityStop(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityDestroy(final Activity activity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRestoreInstanceState(Activity activity, Bundle savedInstanceState) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(Activity activity, Bundle outState) {}

    /**
     * {@inheritDoc}
     * 
     * @return false
     */
    @Override
    public boolean onActivityResult(final Activity activity, int requestCode, int resultCode,
            Intent imageReturnedIntent) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onBackPressed(final Activity activity) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onCreateOptionsMenu(final Activity activity, Menu menu) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onPrepareOptionsMenu(final Activity activity, Menu menu) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onOptionsItemSelected(final Activity activity, MenuItem item) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewIntent(Activity activity, Intent intent) {}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {}

}

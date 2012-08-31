package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebChromeClientAdapter;

import android.webkit.WebStorage.QuotaUpdater;

public class WebSQLDatabaseWebChromeClientListener extends WebChromeClientAdapter {

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
            long estimatedSize, long totalUsedQuota, QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(estimatedSize * 2);
    }

}

package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebChromeClientAdapter;

import android.net.Uri;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;

public class WidgetLogForSDKWebChromeClient extends WebChromeClientAdapter {
    private static final String DELIMITER = "||";
    private static final String TAG = "AppspressoWidget";
    final private String widgetID;

    public WidgetLogForSDKWebChromeClient(String widgetID) {
        this.widgetID = widgetID;
    }

    public String getSourceFile(String sourceID) {
        try {
            Uri uri = Uri.parse(sourceID);
            String path = uri.getPath();
            return path;
        }
        catch (Exception e) {
            return "unknown file";
        }
    }

    private String combineLogMessage(String message, int lineNumber, String sourceID) {
        return new StringBuilder(200).append(message).append(DELIMITER).append(lineNumber)
                .append(DELIMITER).append(getSourceFile(sourceID)).append(DELIMITER)
                .append(this.widgetID).toString();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (message != null) {
            String[] lines = message.split("\n");
            for (String line : lines) {
                Log.d(TAG, combineLogMessage(line, lineNumber, sourceID));
            }
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        String[] lines = consoleMessage.message().split("\n");
        int lineNumber = consoleMessage.lineNumber();
        String sourceId = consoleMessage.sourceId();
        MessageLevel level = consoleMessage.messageLevel();

        for (String line : lines) {
            String message = combineLogMessage(line, lineNumber, sourceId);

            if (MessageLevel.DEBUG.equals(level)) {
                Log.d(TAG, message);
            }
            else if (MessageLevel.ERROR.equals(level)) {
                Log.e(TAG, message);
            }
            else if (MessageLevel.LOG.equals(level)) {
                Log.d(TAG, message);
            }
            else if (MessageLevel.TIP.equals(level)) {
                Log.i(TAG, message);
            }
            else if (MessageLevel.WARNING.equals(level)) {
                Log.w(TAG, message);
            }
        }
        return true;
    }

}

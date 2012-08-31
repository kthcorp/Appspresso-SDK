package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebChromeClientAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;

/**
 * This class provides javascript alert dialogs(alert/confirm/prompt).
 * 
 */
public class AlertDialogWebChromeClientListener extends WebChromeClientAdapter {

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
            final JsPromptResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setCancelable(false);
        builder.setMessage(message);
        final EditText editText = new EditText(view.getContext());
        if (!TextUtils.isEmpty(defaultValue)) {
            editText.setText(defaultValue);
        }
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm(editText.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.create().show();
        return true;
    }

}

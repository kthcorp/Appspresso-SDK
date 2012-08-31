package com.appspresso.screw.ui;

import java.util.Stack;

import com.appspresso.api.AxError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * "ui" plugin helper methods.
 * 
 */
public class UiUtils {

    public static void alert(final Activity activity, final String title, final String message,
            final String positive, final boolean cancelable) {
        ModalAlertDialog<Void> dialog = new ModalAlertDialog<Void>() {
            @Override
            public AlertDialog createDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                if (cancelable == true) {
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setResult(null);
                        }
                    });
                }
                else {
                    builder.setCancelable(false);
                }

                if (!TextUtils.isEmpty(title)) {
                    builder.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    builder.setMessage(message);
                }
                DialogInterface.OnClickListener positiveOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(null);
                            }
                        };
                if (TextUtils.isEmpty(positive)) {
                    builder.setPositiveButton(android.R.string.ok, positiveOnClickListener);
                }
                else {
                    builder.setPositiveButton(positive, positiveOnClickListener);
                }
                return builder.create();
            }
        };
        dialog.showModal(activity);
        dialog.getResult();
    }

    public static boolean confirm(final Activity activity, final String title,
            final String message, final String positive, final String negative,
            final boolean cancelable) {
        ModalAlertDialog<Boolean> dialog = new ModalAlertDialog<Boolean>() {
            @Override
            public AlertDialog createDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                if (cancelable == true) {
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setResult(false);
                        }
                    });
                }
                else {
                    builder.setCancelable(false);
                }

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                if (!TextUtils.isEmpty(title)) {
                    builder.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    builder.setMessage(message);
                }
                DialogInterface.OnClickListener positiveOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(true);
                            }
                        };
                if (TextUtils.isEmpty(positive)) {
                    builder.setPositiveButton(android.R.string.ok, positiveOnClickListener);
                }
                else {
                    builder.setPositiveButton(positive, positiveOnClickListener);
                }
                DialogInterface.OnClickListener negativeOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(false);
                            }
                        };
                if (TextUtils.isEmpty(negative)) {
                    builder.setNegativeButton(android.R.string.cancel, negativeOnClickListener);
                }
                else {
                    builder.setNegativeButton(negative, negativeOnClickListener);
                }
                return builder.create();
            }
        };
        dialog.showModal(activity);
        return dialog.getResult();
    }

    public static String prompt(final Activity activity, final String title, final String message,
            final String positive, final String negative, final String value,
            final boolean cancelable) {
        ModalAlertDialog<String> dialog = new ModalAlertDialog<String>() {
            @Override
            public AlertDialog createDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                if (cancelable == true) {
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setResult(null);
                        }
                    });
                }
                else {
                    builder.setCancelable(false);
                }

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                if (!TextUtils.isEmpty(title)) {
                    builder.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    builder.setMessage(message);
                }
                final EditText editText = new EditText(activity);
                if (!TextUtils.isEmpty(value)) {
                    editText.setText(value);
                }
                builder.setView(editText);
                DialogInterface.OnClickListener positiveOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(editText.getText().toString());
                            }
                        };
                if (TextUtils.isEmpty(positive)) {
                    builder.setPositiveButton(android.R.string.ok, positiveOnClickListener);
                }
                else {
                    builder.setPositiveButton(positive, positiveOnClickListener);
                }
                DialogInterface.OnClickListener negativeOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(null);
                            }
                        };
                if (TextUtils.isEmpty(negative)) {
                    builder.setNegativeButton(android.R.string.cancel, negativeOnClickListener);
                }
                else {
                    builder.setNegativeButton(negative, negativeOnClickListener);
                }
                return builder.create();
            }
        };
        dialog.showModal(activity);
        return dialog.getResult();
    }

    public static int pick(final Activity activity, final String title, final String[] items,
            final boolean cancelable) {
        ModalAlertDialog<Integer> dialog = new ModalAlertDialog<Integer>() {
            public AlertDialog createDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                if (cancelable == true) {
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setResult(-1);
                        }
                    });
                }
                else {
                    builder.setCancelable(false);
                }

                if (!TextUtils.isEmpty(title)) {
                    builder.setTitle(title);
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        setResult(item);
                    }
                });
                return builder.create();
            }
        };
        dialog.showModal(activity);
        return dialog.getResult();
    }

    public static boolean open(final Activity activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            activity.startActivity(intent);
            return true;
        }
        catch (Exception e) {
            // ActivityNotFoundException
            return false;
        }
    }

    private static Stack<ProgressDialog> progressDialogs = new Stack<ProgressDialog>();

    public static void showProgress(final Activity activity, final String title,
            final String message) throws InterruptedException {
        int count = progressDialogs.size();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (progressDialogs) {
                    progressDialogs.push(ProgressDialog.show(activity, title, message, true, false));
                    progressDialogs.notifyAll();
                }
            }
        });

        synchronized (progressDialogs) {
            while (count == progressDialogs.size()) {
                progressDialogs.wait(500);
            }
        }
    }

    public static void hideProgress(final Activity activity) throws InterruptedException {
        int count = progressDialogs.size();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (progressDialogs) {
                    if (!progressDialogs.isEmpty()) {
                        progressDialogs.pop().dismiss();
                    }

                    progressDialogs.notifyAll();
                }
            }
        });

        synchronized (progressDialogs) {
            while (count == progressDialogs.size() && !progressDialogs.isEmpty()) {
                progressDialogs.wait(500);
            }
        }
    }

    public static void showStatusBar(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });
    }

    public static void hideStatusBar(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });
    }

}

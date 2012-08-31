package com.appspresso.screw.ui;

import android.app.Activity;
import android.app.AlertDialog;

/**
 *
 */
public abstract class ModalAlertDialog<T> implements Runnable {

    // private static final Log L =
    // ChronometerLog.getLog(ModalAlertDialog.class);

    private final Object modalLock = new Object();

    private T result;

    public ModalAlertDialog() {}

    /**
     * Create a dialog UI.
     * <p/>
     * *MUST* be overridden to provide a dialog UI.
     * 
     * @return an AlertDialog instance
     */
    public abstract AlertDialog createDialog();

    /**
     * Show the dialog box and wait until a result is available.
     * 
     * @param activity the containing activity, runs the dialog within its UI thread.
     */
    public final void showModal(final Activity activity) {
        activity.runOnUiThread(this);
    }

    /**
     * @param result
     */
    public final void setResult(T result) {
        this.result = result;
        synchronized (modalLock) {
            modalLock.notifyAll();
        }
    }

    /**
     * @return
     */
    public final T getResult() {
        synchronized (modalLock) {
            try {
                modalLock.wait();
            }
            catch (InterruptedException ignored) {}
        }
        return result;
    }

    //
    // implements Runnable
    //

    @Override
    public final void run() {
        AlertDialog dialog = createDialog();
        synchronized (modalLock) {
            dialog.show();
        }
    }

}

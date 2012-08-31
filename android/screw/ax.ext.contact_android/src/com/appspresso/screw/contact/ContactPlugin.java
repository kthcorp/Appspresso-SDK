package com.appspresso.screw.contact;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.activity.ActivityAdapter;
import com.appspresso.api.activity.ActivityListener;

/**
 * TODO: change package and class name.
 * 
 * Appspresso Plugin Contact Module
 * 
 * id: ax.ext.contact version: 1.0.0
 * 
 */
public class ContactPlugin extends DefaultAxPlugin {
    private static final int REQ_PICK_CONTACT = 62000;
    private AxPluginContext runningContext;

    private static final Log L = AxLog.getLog(ContactPlugin.class);

    private ActivityListener activityListener = new ActivityAdapter() {
        @Override
        public boolean onActivityResult(Activity activity, int requestCode, int resultCode,
                Intent data) {
            if (ContactPlugin.REQ_PICK_CONTACT == requestCode) {
                if (runningContext == null) {
                    if (L.isWarnEnabled()) {
                        L.warn("missing context");
                    }
                    return true;
                }
                AxPluginContext context = runningContext;
                runningContext = null;

                if (resultCode != Activity.RESULT_OK || data == null) {
                    context.sendError(AxError.UNKNOWN_ERR, "pick contact failed");
                    return true;
                }

                Object contact = ContactUtils.onPickContact(runtimeContext.getActivity(), data);
                if (contact == null) {
                    context.sendError(AxError.UNKNOWN_ERR, "cannot serialize picked contact");
                    return true;
                }
                context.sendResult(contact);
                return true;
            }
            return super.onActivityResult(activity, requestCode, resultCode, data);
        }
    };

    public ContactPlugin() {
        runningContext = null;
    }

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        this.runtimeContext.addActivityListener(this.activityListener);
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        this.runtimeContext.removeActivityListener(this.activityListener);

        super.deactivate(runtimeContext);
    }

    // Plugin method
    public void pickContact(AxPluginContext context) {
        if (runningContext != null) {
            context.sendError(AxError.INVALID_ACCESS_ERR, "pickContact is already running");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        this.runtimeContext.getActivity().startActivityForResult(intent, REQ_PICK_CONTACT);
        runningContext = context;
    }

}

package com.appspresso.screw.net;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.appspresso.api.AxLog;

/**
 * "net" plugin helper methods to support rfc822 "mail".
 * 
 */
public class MailUtils {

    private static final Log L = AxLog.getLog(MailUtils.class);

    private static final String MAIL_CONTENT_TYPE = "message/rfc822";

    public static void sendMail(final Activity activity, String[] to, String[] cc, String[] bcc,
            String subject, String message, String[] attachments) throws Exception {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE); // multiple
                                                                 // attachment
        // Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType(MAIL_CONTENT_TYPE);
        if (to != null && to.length > 0) {
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }
        if (cc != null && cc.length > 0) {
            intent.putExtra(Intent.EXTRA_CC, cc);
        }
        if (bcc != null && bcc.length > 0) {
            intent.putExtra(Intent.EXTRA_BCC, bcc);
        }
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (!TextUtils.isEmpty(message)) {
            intent.putExtra(Intent.EXTRA_TEXT, message);
        }

        if (attachments != null && attachments.length > 0) {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (String attachment : attachments) {
                uris.add(Uri.fromFile(new File(attachment)));
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        // if (attachments != null && attachments.length > 0) {
        // for (String attachment : attachments) {
        // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
        // File(attachment)));
        // }
        // }

        if (L.isTraceEnabled()) {
            L.trace("starting activity to send mail...");
        }
        // startActivityForResult(...) 해야 할 것 같은데...
        // 안드로이드 단말에 따라(앱에 따라) onActivityResult로 결과가 안 넘어옮 -_-;
        activity.startActivity(intent);
    }

}

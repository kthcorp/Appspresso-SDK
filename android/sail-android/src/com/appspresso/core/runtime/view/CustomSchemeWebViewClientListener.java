package com.appspresso.core.runtime.view;

import java.net.URISyntaxException;

import com.appspresso.api.view.WebViewClientAdapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

public class CustomSchemeWebViewClientListener extends WebViewClientAdapter {

    private Activity activity;

    public CustomSchemeWebViewClientListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // if (url.startsWith(SCHEME_WTAI)) {
        // // wtai://wp/mc;number
        // // number=string(phone-number)
        // if (url.startsWith(SCHEME_WTAI_MC)) {
        // Intent intent = new Intent(Intent.ACTION_VIEW,
        // Uri.parse(WebView.SCHEME_TEL +
        // url.substring(SCHEME_WTAI_MC.length())));
        // startActivity(intent);
        // return true;
        // }
        // // wtai://wp/sd;dtmf
        // // dtmf=string(dialstring)
        // if (url.startsWith(SCHEME_WTAI_SD)) {
        // // TODO: only send when there is active voice connection
        // return false;
        // }
        // // wtai://wp/ap;number;name
        // // number=string(phone-number)
        // // name=string
        // if (url.startsWith(SCHEME_WTAI_AP)) {
        // // TODO
        // return false;
        // }
        // }

        // XXX http, https, javascript scheme 에 대해 return false를 해야할까? 반대로 보장(?)
        // 지원(?) 하는 scheme(geo: tel: mailto:) 만 처리하도록 해야할까?
        if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("javascript:")) { return false; }

        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        }
        catch (URISyntaxException ex) {
            return false;
        }

        Activity activity = this.activity;
        // check whether the intent can be resolved. If not, we will see
        // whether we can download it from the Market.
        if (activity.getPackageManager().resolveActivity(intent, 0) == null) {
            String packagename = intent.getPackage();
            if (packagename != null) {
                intent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:"
                                + packagename));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                view.getContext().startActivity(intent);
                return true;
            }
            else {
                return false;
            }
        }

        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        try {
            if (activity.startActivityIfNeeded(intent, -1)) { return true; }
        }
        catch (ActivityNotFoundException ex) {
            // ignore the error. If no application can handle the URL,
            // eg about:blank, assume the browser can handle it.
        }
        return false;
    }

}

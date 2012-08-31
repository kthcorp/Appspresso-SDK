package com.appspresso.screw.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.ActivityAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class DefaultActivityCaller extends ActivityAdapter implements ActivityCaller {
    private AxRuntimeContext context;
    private DefaultProperties properties;
    private List<Callback> callbacks;
    private int requestCode;
    public ResultObserver resultObserver;

    public DefaultActivityCaller() {
        callbacks = new ArrayList<Callback>();
    }

    public void setResultObserver(ResultObserver resultObserver) {
        this.resultObserver = resultObserver;
    }

    @Override
    public void callActivity(AxRuntimeContext context) {
        this.context = context;
        context.addActivityListener(this);
        context.getActivity().startActivityForResult(createIntent(properties), getRequestCode());
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = new DefaultProperties(properties);
    }

    @Override
    public void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(Callback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public int getRequestCode() {
        return requestCode;
    }

    @Override
    public boolean onActivityResult(Activity activity, int requestCode, int resultCode,
            Intent intent) {
        if (L.isDebugEnabled()) {
            L.debug("onActivityResult - {requestCode:" + requestCode + ", resultCode:" + resultCode
                    + "}");
        }

        if (getRequestCode() != requestCode) return false;

        Iterator<Callback> iter = callbacks.iterator();
        if (resultCode == Activity.RESULT_OK) {
            while (iter.hasNext()) {
                iter.next().onResultSuccess(context, properties, intent, resultObserver);
            }
        }
        else {
            while (iter.hasNext()) {
                iter.next().onResultError(context, properties, intent, resultObserver);
            }
        }

        properties = null;
        context.removeActivityListener(this);
        return super.onActivityResult(activity, requestCode, resultCode, intent);
    }

    @Override
    public void onRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        properties = savedInstanceState.getParcelable("properties");

        if (L.isDebugEnabled()) {
            L.debug("onRestoreInstanceState - " + properties);
        }
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle outState) {
        if (L.isDebugEnabled()) {
            L.debug("onSaveInstanceState - " + properties);
        }

        outState.putParcelable("properties", properties);
    }

    public static abstract class DefaultCallback implements Callback {
        @Override
        public void onResultSuccess(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver) {
            resultObserver.success(getResult(context, properties, intent));
        }

        @Override
        public void onResultError(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver) {
            resultObserver.error(AxError.UNKNOWN_ERR, "activity result failed");
        }

        protected abstract Object getResult(AxRuntimeContext context,
                Map<String, Object> properties, Intent intent);
    }

    public static class DefaultProperties extends HashMap<String, Object> implements Parcelable {
        private static final long serialVersionUID = 7588261129130984769L;

        public DefaultProperties(Map<String, Object> properties) {
            putAll(properties);
        }

        public DefaultProperties(Parcel parcel) {
            Serializable serializable = parcel.readSerializable();
            putAll((HashMap<String, Object>) serializable);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeSerializable(this);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public abstract Intent createIntent(Map<String, Object> properties);
}

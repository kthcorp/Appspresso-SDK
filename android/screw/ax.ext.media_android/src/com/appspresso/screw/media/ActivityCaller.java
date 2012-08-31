package com.appspresso.screw.media;

import java.util.Map;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;
import android.content.Intent;

public interface ActivityCaller {
    public static final Log L = AxLog.getLog(ActivityCaller.class);

    public void callActivity(AxRuntimeContext runtimeContext);

    public void setProperties(Map<String, Object> properties);

    public void setRequestCode(int requestCode);

    public int getRequestCode();

    public void addCallback(Callback callback);

    public void removeCallback(Callback callback);

    public interface Callback {
        public void onResultSuccess(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver);

        public void onResultError(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver);
    }
}

package com.appspresso.waikiki.orientation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;

public class Orientation extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/orientation";
    public static final Log L = AxLog.getLog(Orientation.class);

    private AxRuntimeContext runtimeContext;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Set<Long> watchIds;

    private OrientationListener orientationListener;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        if (!runtimeContext.isActivatedFeature(FEATURE_DEFAULT)) { throw new AxError(
                AxError.SECURITY_ERR, null); // TODO Error message
        }

        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis");
        this.runtimeContext = runtimeContext;
        initSensorManager();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        runtimeContext = null;
        clearSensorManager();
        super.deactivate(runtimeContext);
    }

    private void initSensorManager() {
        sensorManager =
                (android.hardware.SensorManager) (runtimeContext.getActivity())
                        .getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensorList == null || sensorList.isEmpty()) { throw new AxError(
                AxError.NOT_SUPPORTED_ERR, "The orientation is not supported."); }

        sensor = sensorList.get(0);
        watchIds = new HashSet<Long>();
    }

    private void clearSensorManager() {
        sensorManager.unregisterListener(orientationListener);
        orientationListener = null;
    }

    public void getCurrentOrientation(AxPluginContext context) {
        try {
            long watchId = context.getParamAsNumber(0).longValue();
            watchOrientation(watchId);
        }
        catch (Exception ignore) {
            // Nothing to do
        }

        synchronized (this) {
            if (orientationListener == null) {
                orientationListener = new OrientationListener();
                sensorManager.registerListener(orientationListener, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }

        if (!orientationListener.isReady()) {
            int patience = 10000;
            while (!orientationListener.isReady() && patience > 0) {
                patience -= 100;
                try {
                    synchronized (orientationListener) {
                        orientationListener.wait(100);
                    }
                }
                catch (InterruptedException e) {
                    throw new AxError(AxError.NOT_AVAILABLE_ERR,
                            "*InterruptedException* occured while starting orientation.");
                }
            }

            if (patience <= 0) { throw new AxError(AxError.NOT_AVAILABLE_ERR,
                    "Cannot start orientation in 10 seconds."); }
        }

        context.sendResult(orientationListener.getRotation());
    }

    private void watchOrientation(long watchId) {
        if (watchIds == null) watchIds = new HashSet<Long>();
        watchIds.add(watchId);
    }

    public void clearWatch(AxPluginContext context) {
        long watchId = context.getParamAsNumber(0).longValue();
        watchIds.remove(watchId);

        context.sendResult();
    }

    public void notUsedTooLong() {
        if (!watchIds.isEmpty() || orientationListener == null) { return; }

        sensorManager.unregisterListener(orientationListener);
        orientationListener = null;
        // TODO 계속 인스턴스를 지워야 하나?
    }

    class OrientationListener implements SensorEventListener {
        // 3초 이상 accelerometer 사용하지 않으면 에너지 절약한다.
        private static final long TURNOFF_TIMER = 3000;
        private Rotation rotation;
        private boolean ready;
        private long accessTime;

        OrientationListener() {
            this.rotation = new Rotation();
            ready = false;
        }

        public boolean isReady() {
            return ready;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            rotation.setValue(event.values);
            if (ready == false) {
                ready = true;
                synchronized (this) {
                    this.notifyAll();
                }
            }

            if (accessTime != 0 && System.currentTimeMillis() - accessTime > TURNOFF_TIMER) {
                notUsedTooLong();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Nothing to do.
        }

        public Rotation getRotation() {
            accessTime = System.currentTimeMillis();
            return rotation;
        }
    }
}

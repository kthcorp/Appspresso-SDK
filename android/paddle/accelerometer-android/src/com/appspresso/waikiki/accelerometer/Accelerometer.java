package com.appspresso.waikiki.accelerometer;

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

public class Accelerometer extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/accelerometer";
    public static final Log L = AxLog.getLog(Accelerometer.class);

    private AxRuntimeContext runtimeContext;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Set<Long> watchIds;

    private AccelerometerListener accelerationListener;

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
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList == null || sensorList.isEmpty()) { throw new AxError(
                AxError.NOT_SUPPORTED_ERR, "The orientation is not supported."); }

        sensor = sensorList.get(0);
        watchIds = new HashSet<Long>();
    }

    private void clearSensorManager() {
        sensorManager.unregisterListener(accelerationListener);
        accelerationListener = null;
    }

    public void getCurrentAcceleration(AxPluginContext context) {
        try {
            long watchId = context.getParamAsNumber(0).longValue();
            watchAccelerometer(watchId);
        }
        catch (Exception ignore) {
            // Nothing to do
        }

        synchronized (this) {
            if (accelerationListener == null) {
                accelerationListener = new AccelerometerListener();
                sensorManager.registerListener(accelerationListener, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }

        if (!accelerationListener.isReady()) {
            int patience = 10000;
            while (!accelerationListener.isReady() && patience > 0) {
                patience -= 100;
                try {
                    synchronized (accelerationListener) {
                        accelerationListener.wait(100);
                    }
                }
                catch (InterruptedException e) {
                    throw new AxError(AxError.NOT_AVAILABLE_ERR,
                            "*InterruptedException* occured while starting acceleromter.");
                }
            }

            if (patience <= 0) { throw new AxError(AxError.NOT_AVAILABLE_ERR,
                    "Cannot start accelerometer in 10 seconds."); }
        }

        context.sendResult(accelerationListener.getAcceleration());
    }

    private void watchAccelerometer(long watchId) {
        if (watchIds == null) watchIds = new HashSet<Long>();
        watchIds.add(watchId);
    }

    public void clearWatch(AxPluginContext context) {
        long watchId = context.getParamAsNumber(0).longValue();
        watchIds.remove(watchId);

        context.sendResult();
    }

    public void notUsedTooLong() {
        if (!watchIds.isEmpty() || accelerationListener == null) { return; }

        sensorManager.unregisterListener(accelerationListener);
        accelerationListener = null;
        // TODO 계속 인스턴스를 지워야 하나?
    }

    class AccelerometerListener implements SensorEventListener {
        // 3초 이상 accelerometer 사용하지 않으면 에너지 절약한다.
        private static final long TURNOFF_TIMER = 3000;
        private Acceleration acceleration;
        private boolean ready;
        private long accessTime;

        AccelerometerListener() {
            this.acceleration = new Acceleration();
            ready = false;
        }

        public boolean isReady() {
            return ready;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            acceleration.setValue(event.values);
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

        public Acceleration getAcceleration() {
            accessTime = System.currentTimeMillis();
            return acceleration;
        }
    }

}

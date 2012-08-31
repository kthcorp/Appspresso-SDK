package com.appspresso.waikiki.devicestatus.components;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class BatteryComponent implements Component {
    private Log L = AxLog.getLog("DeviceStatusManager");

    private AxRuntimeContext runtimeContext;
    private LockedReceiver levelReceiver;
    private LockedReceiver beingChargedReceiver;
    private Set<Long> levelWatchIds;
    private Set<Long> beingChargedWatchIds;

    public BatteryComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        this.runtimeContext = runtimeContext;
        this.levelWatchIds = new HashSet<Long>();
        this.beingChargedWatchIds = new HashSet<Long>();
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_BATTERY_LEVEL.equals(propertyName)) {
            return getBatteryLevel();
        }
        else if (DeviceStatusVocabulary.PROPERTY_BATTERY_BEING_CHARGED.equals(propertyName)) {
            return isBatteryBeingCharged();
        }
        else {
            return null;
        }
    }

    public Object getBatteryLevel() {
        try {
            return levelReceiver.getValue();
        }
        catch (NullPointerException notWatching) {
            Semaphore lock = new Semaphore(0);
            levelReceiver = createLevelReceiver(lock);
            runtimeContext.getActivity().registerReceiver(levelReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            try {
                lock.tryAcquire(10000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                if (L.isErrorEnabled()) {
                    L.error("", e); // Error message
                }
                throw new AxError(AxError.NOT_AVAILABLE_ERR,
                        "The current value cannot be retrieved.");
            }
        }

        try {
            return levelReceiver.getValue();
        }
        catch (NullPointerException unexpected) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "The current value cannot be retrieved.");
        }
        finally {
            if (levelWatchIds.isEmpty()) {
                runtimeContext.getActivity().unregisterReceiver(levelReceiver);
                levelReceiver = null;
            }
        }
    }

    @Override
    public synchronized void watchPropertyChange(String propertyName, long watchId) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_BATTERY_LEVEL.equals(propertyName)) {
            levelWatchIds.add(watchId);
        }
        else if (DeviceStatusVocabulary.PROPERTY_BATTERY_BEING_CHARGED.equals(propertyName)) {
            beingChargedWatchIds.add(watchId);
        }
    }

    @Override
    public synchronized void clearWatch(long watchId) {
        if (levelWatchIds.contains(watchId)) {
            levelWatchIds.remove(watchId);
            if (levelWatchIds.isEmpty()) {
                unregisterLevelReceiver();
            }
        }
        else if (beingChargedWatchIds.contains(watchId)) {
            beingChargedWatchIds.remove(watchId);
            if (beingChargedWatchIds.isEmpty()) {
                unregisterBeingChargedReceiver();
            }
        }
    }

    private synchronized void unregisterLevelReceiver() {
        if (levelReceiver == null) return;

        runtimeContext.getActivity().unregisterReceiver(levelReceiver);
        levelReceiver = null;
    }

    private synchronized void unregisterBeingChargedReceiver() {
        if (beingChargedReceiver == null) return;

        runtimeContext.getActivity().unregisterReceiver(beingChargedReceiver);
        beingChargedReceiver = null;
    }

    @Override
    public void clearWatch() {
        levelWatchIds.clear();
        unregisterLevelReceiver();

        beingChargedWatchIds.clear();
        unregisterBeingChargedReceiver();
    }

    public Object isBatteryBeingCharged() {
        try {
            return beingChargedReceiver.getValue();
        }
        catch (NullPointerException notWatching) {
            Semaphore lock = new Semaphore(0);
            beingChargedReceiver = createBeingCharedReceiver(lock);
            runtimeContext.getActivity().registerReceiver(beingChargedReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            try {
                lock.tryAcquire(100000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                if (L.isErrorEnabled()) {
                    L.error("", e); // Error message
                }
                throw new AxError(AxError.NOT_AVAILABLE_ERR, null); // Error
                                                                    // message
            }
        }

        try {
            return beingChargedReceiver.getValue();
        }
        catch (NullPointerException unexpected) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, null); // Error message
        }
        finally {
            if (beingChargedWatchIds.isEmpty()) {
                runtimeContext.getActivity().unregisterReceiver(beingChargedReceiver);
                beingChargedReceiver = null;
            }
        }
    }

    private LockedReceiver createLevelReceiver(Semaphore lock) {
        return new LockedReceiver(lock) {
            @Override
            void updateValue(Intent intent) {
                int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (rawLevel >= 0 && scale > 0) setValue((rawLevel * 100) / scale);
            }
        };
    }

    private LockedReceiver createBeingCharedReceiver(Semaphore lock) {
        return new LockedReceiver(lock) {
            @Override
            void updateValue(Intent intent) {
                switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        setValue(true);
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        if (L.isInfoEnabled()) L.info("Battery is discharging.");
                        setValue(false);
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        if (L.isInfoEnabled()) L.info("Battery is full.");
                        setValue(false);
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        if (L.isInfoEnabled()) L.info("Battery status is unknown.");
                        setValue(false);
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        if (L.isInfoEnabled()) L.info("Battery is not charing.");
                        setValue(false);
                        break;
                    default:
                }
            }
        };
    }

    public abstract class LockedReceiver extends BroadcastReceiver {
        private Semaphore lock;
        private Object value;

        LockedReceiver(Semaphore lock) {
            this.lock = lock;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateValue(intent);
            if (lock != null) lock.release();
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        abstract void updateValue(Intent intent);
    }
}

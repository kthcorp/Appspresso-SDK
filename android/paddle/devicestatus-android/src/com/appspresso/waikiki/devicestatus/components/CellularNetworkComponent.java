package com.appspresso.waikiki.devicestatus.components;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class CellularNetworkComponent implements Component {
    public static final int MCC_LENGTH = 3;

    private AxRuntimeContext runtimeContext;
    private TelephonyManager telephonyManager;
    private SignalStengthListener signalStrengthListener;
    private Set<Long> signalStrenghtWatchIds;

    public CellularNetworkComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        this.runtimeContext = runtimeContext;
        this.telephonyManager =
                (TelephonyManager) runtimeContext.getActivity().getSystemService(
                        Context.TELEPHONY_SERVICE);
        this.signalStrenghtWatchIds = new HashSet<Long>();
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_IS_IN_ROAMING.equals(propertyName)) {
            return isInRoaming();
        }
        else if (DeviceStatusVocabulary.PROPERTY_MCC.equals(propertyName)) {
            return getMobileCountryCode();
        }
        else if (DeviceStatusVocabulary.PROPERTY_MNC.equals(propertyName)) {
            return getMobileNetworkCode();
        }
        else if (DeviceStatusVocabulary.PROPERTY_SIGNAL_STRENGTH.equals(propertyName)) {
            return getSignalStrength();
        }
        else if (DeviceStatusVocabulary.PROPERTY_OPERATOR_NAME.equals(propertyName)) {
            return getOperatorName();
        }
        else {
            return null;
        }
    }

    public boolean isInRoaming() {
        return telephonyManager.isNetworkRoaming();
    }

    public String getMobileCountryCode() {
        return telephonyManager.getNetworkOperator().substring(0, MCC_LENGTH);
    }

    public String getMobileNetworkCode() {
        return telephonyManager.getNetworkOperator().substring(MCC_LENGTH);
    }

    public float getSignalStrength() {
        try {
            return signalStrengthListener.getValue();
        }
        catch (NullPointerException requiredListener) {
            final Semaphore lock = new Semaphore(0);
            registerSignalStrengthListener(lock);

            try {
                lock.tryAcquire(100000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                throw new AxError(AxError.NOT_AVAILABLE_ERR,
                        "The current value cannot be retrieved.");
            }

            float value = signalStrengthListener.getValue();
            if (signalStrenghtWatchIds.isEmpty()) unregisterSignalStrengthListener();

            return value;
        }
    }

    public String getOperatorName() {
        return telephonyManager.getNetworkOperatorName();
    }

    @Override
    public synchronized void watchPropertyChange(String propertyName, long watchId) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_SIGNAL_STRENGTH.equals(propertyName)) {
            signalStrenghtWatchIds.add(watchId);
        }
    }

    @Override
    public synchronized void clearWatch(long watchId) {
        if (!signalStrenghtWatchIds.contains(watchId)) return;

        signalStrenghtWatchIds.remove(watchId);
        if (signalStrenghtWatchIds.isEmpty()) {
            unregisterSignalStrengthListener();
        }
    }

    @Override
    public void clearWatch() {
        signalStrenghtWatchIds.clear();
        unregisterSignalStrengthListener();
    }

    private void registerSignalStrengthListener(final Semaphore lock) {
        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (signalStrengthListener != null) return;
                signalStrengthListener = new SignalStengthListener(lock);
                telephonyManager.listen(signalStrengthListener,
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                | PhoneStateListener.LISTEN_SERVICE_STATE);
            }
        });
    }

    private void unregisterSignalStrengthListener() {
        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (signalStrengthListener == null) return;
                telephonyManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_NONE);
                signalStrengthListener = null;
            }
        });
    }

    private int getEvdoLevel(final int evdoDbm, final int evdoSnr) {
        int levelEvdoDbm = 0;
        int levelEvdoSnr = 0;

        if (evdoDbm >= -65)
            levelEvdoDbm = 4 * 25;
        else if (evdoDbm >= -75)
            levelEvdoDbm = 3 * 25;
        else if (evdoDbm >= -90)
            levelEvdoDbm = 2 * 25;
        else if (evdoDbm >= -105)
            levelEvdoDbm = 1 * 25;
        else
            levelEvdoDbm = 0;

        if (evdoSnr >= 7)
            levelEvdoSnr = 4 * 25;
        else if (evdoSnr >= 5)
            levelEvdoSnr = 3 * 25;
        else if (evdoSnr >= 3)
            levelEvdoSnr = 2 * 25;
        else if (evdoSnr >= 1)
            levelEvdoSnr = 1 * 25;
        else
            levelEvdoSnr = 0;

        return (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
    }

    private int getCdmaSignalStrnegthLevel(final float cdmaDbm, final float cdmaEcio) {
        int levelDbm = 0;
        int levelEcio = 0;

        if (cdmaDbm >= -75)
            levelDbm = 4 * 25;
        else if (cdmaDbm >= -85)
            levelDbm = 3 * 25;
        else if (cdmaDbm >= -95)
            levelDbm = 2 * 25;
        else if (cdmaDbm >= -100)
            levelDbm = 1 * 25;
        else
            levelDbm = 0;

        // Ec/Io are in dB*10
        if (cdmaEcio >= -90)
            levelEcio = 4 * 25;
        else if (cdmaEcio >= -110)
            levelEcio = 3 * 25;
        else if (cdmaEcio >= -130)
            levelEcio = 2 * 25;
        else if (cdmaEcio >= -150)
            levelEcio = 1 * 25;
        else
            levelEcio = 0;

        return (levelDbm < levelEcio) ? levelDbm : levelEcio;
    }

    private int getGsmSignalStrnegthLevel(float asu) {
        final int MAX = 31;
        final int MIN = 0;

        if (asu == 99) { return 0; }

        return (int) ((float) asu / (MAX - MIN) * 100);
    }

    class SignalStengthListener extends PhoneStateListener {
        private Semaphore lock;
        private float signalStrength;

        SignalStengthListener(Semaphore lock) {
            this.lock = lock;
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength strength) {
            int phoneType = telephonyManager.getPhoneType();

            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_GSM:
                    signalStrength =
                            (float) getGsmSignalStrnegthLevel(strength.getGsmSignalStrength());
                    break;
                case TelephonyManager.PHONE_TYPE_CDMA:
                    if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                        signalStrength =
                                (float) getEvdoLevel(strength.getEvdoDbm(), strength.getEvdoSnr());
                    }
                    else {
                        signalStrength =
                                (float) getCdmaSignalStrnegthLevel(strength.getCdmaDbm(),
                                        strength.getCdmaEcio());
                    }
                    break;
                case TelephonyManager.PHONE_TYPE_NONE:
                    break;
            }

            lock.release(1);
        }

        public float getValue() {
            return signalStrength;
        }
    }
}

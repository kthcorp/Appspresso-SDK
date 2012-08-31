package com.appspresso.waikiki.devicestatus.components;

import android.os.Environment;
import android.os.StatFs;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class BuiltInMemoryUnitComponent implements Component {
    public static final String NAME = "built-in"; // TODO

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_SIZE.equals(propertyName)) {
            return getSize();
        }
        else if (DeviceStatusVocabulary.PROPERTY_REMOVABLE.equals(propertyName)) {
            return false;
        }
        else if (DeviceStatusVocabulary.PROPERTY_AVAILABLE_SIZE.equals(propertyName)) {
            return getAvailableSize();
        }
        else {
            return null;
        }
    }

    @Override
    public void watchPropertyChange(String propertyName, long watchId) throws AxError {
        // Nothing to do.
    }

    @Override
    public void clearWatch(long watchId) {
        // Nothing to do.
    }

    @Override
    public void clearWatch() {
        // Nothing to do.
    }

    /**
     * 내부메모리의 전체 크기를 byte 단위로 반환
     * 
     * @return byte 단위의 내부메모리 전체 크기
     */
    public long getSize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return stat.getBlockCount() * (long) stat.getBlockSize();
    }

    /**
     * 내부메모리의 이용가능한 크기를 byte 단위로 반환
     * 
     * @return byte 단위의 내부메모리 이용가능한 크기
     */
    public long getAvailableSize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return stat.getAvailableBlocks() * (long) stat.getBlockSize();
    }
}

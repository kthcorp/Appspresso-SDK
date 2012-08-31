package com.appspresso.waikiki.devicestatus.components;

import android.os.Environment;
import android.os.StatFs;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class ExtensionMemoryUnitComponent implements Component {
    public static final String NAME = "extension"; // TODO

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_SIZE.equals(propertyName)) {
            return getSize();
        }
        else if (DeviceStatusVocabulary.PROPERTY_REMOVABLE.equals(propertyName)) {
            return true;
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
     * 확장메모리의 전체 크기를 byte 단위로 반환
     * 
     * @return byte 단위의 확장메모리 전체 크기
     */
    public static long getSize() {
        if (!externalMemoryAvailable()) return 0;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return stat.getBlockCount() * (long) stat.getBlockSize();
    }

    /**
     * 확장메모리의 이용가능한 크기를 byte 단위로 반환
     * 
     * @return byte 단위의 확장메모리 이용가능한 크기
     */
    public static long getAvailableSize() {
        if (!externalMemoryAvailable()) return 0;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return stat.getAvailableBlocks() * (long) stat.getBlockSize();
    }

    /**
     * 현재 확장메모리가 사용가능한지 여부를 반환
     * 
     * @return 확장메모리가 사용가능하면 true, 그렇지 않으면 false
     */
    private static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}

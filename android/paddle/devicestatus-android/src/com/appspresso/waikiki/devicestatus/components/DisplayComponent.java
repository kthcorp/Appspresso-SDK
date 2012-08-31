package com.appspresso.waikiki.devicestatus.components;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class DisplayComponent implements Component {
    private static WindowManager windowManager;
    private static DisplayMetrics metrics;
    private static Display display;

    public DisplayComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        metrics = new DisplayMetrics();
        windowManager =
                (WindowManager) runtimeContext.getActivity().getSystemService(
                        Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        display = windowManager.getDefaultDisplay();
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_RESOLUTION_WIDTH.equals(propertyName)) {
            return getResolutionWidth();
        }
        else if (DeviceStatusVocabulary.PROPERTY_RESOLUTION_HEIGHT.equals(propertyName)) {
            return getResolutionHeight();
        }
        else if (DeviceStatusVocabulary.PROPERTY_DPI_X.equals(propertyName)) {
            return getDpiX();
        }
        else if (DeviceStatusVocabulary.PROPERTY_DPI_Y.equals(propertyName)) {
            return getDpiY();
        }
        else if (DeviceStatusVocabulary.PROPERTY_PIXEL_ASPECT_RATIO.equals(propertyName)) {
            return getPixelAspectRatio();
        }
        else if (DeviceStatusVocabulary.PROPERTY_COLOR_DEPTH.equals(propertyName)) {
            return getColorDepth();
        }
        else {
            return null;
        }
    }

    @Override
    public void watchPropertyChange(String propertyName, long watchId) throws AxError {
        // Nothing to do
    }

    @Override
    public void clearWatch(long watchId) {
        // Nothing to do
    }

    @Override
    public void clearWatch() {
        // Nothing to do.
    }

    /**
     * 현재 디스플레이의 pixel 단위의 가로 해상도를 가져온다. 만약 디스플레이가 90도로 오른쪽이나 왼쪽으로 회전한다면 그때의 값은 현재의
     * getResolutionHeight()와 같을 것이다.
     * 
     * @return pixel 단위의 가로 해상도
     */
    public int getResolutionWidth() {
        int orientation = display.getOrientation();
        return (0 == orientation % 2 ? metrics.widthPixels : metrics.heightPixels);
    }

    /**
     * 현재 디스플레이의 pixel 단위의 세로 해상도를 가져온다. 만약 디스플레이가 90도로 오른쪽이나 왼쪽으로 회전한다면 그때의 값은 현재의
     * getResolutionWidth()와 같을 것이다.
     * 
     * @return pixel 단위의 세로 해상도
     */
    public int getResolutionHeight() {
        int orientation = display.getOrientation();
        return (0 != orientation % 2 ? metrics.widthPixels : metrics.heightPixels);
    }

    /**
     * inch 당 pixel 수
     * 
     * @return inch 당 pixel 수
     */
    public float getDpiX() {
        return metrics.xdpi;
    }

    /**
     * inch 당 pixel 수
     * 
     * @return inch 당 pixel 수
     */
    public float getDpiY() {
        return metrics.ydpi;
    }

    /**
     * 현재 디스플레이의 종횡비를 반환한다. 디스플레이가 회전하면 변경된다.
     * 
     * @return 디스플레이의 종횡비
     */
    public float getPixelAspectRatio() {
        int orientation = display.getOrientation();
        return (0 == orientation % 2)
                ? ((float) metrics.widthPixels / (float) metrics.heightPixels)
                : ((float) metrics.heightPixels / (float) metrics.widthPixels);
    }

    public long getColorDepth() {
        int colorDepth = 0;

        switch (windowManager.getDefaultDisplay().getPixelFormat()) {
            case PixelFormat.A_8:
            case PixelFormat.L_8:
            case PixelFormat.RGB_332:
                colorDepth = 8;
                break;
            case PixelFormat.LA_88:
            case PixelFormat.RGB_565:
            case PixelFormat.RGBA_4444:
            case PixelFormat.RGBA_5551:
                colorDepth = 16;
                break;
            case PixelFormat.RGB_888:
                colorDepth = 24;
                break;
            case PixelFormat.RGBA_8888:
            case PixelFormat.RGBX_8888:
                colorDepth = 32;
                break;
        }

        return colorDepth;
    }
}

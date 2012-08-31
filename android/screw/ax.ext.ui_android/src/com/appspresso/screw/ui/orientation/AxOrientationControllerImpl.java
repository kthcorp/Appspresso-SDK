package com.appspresso.screw.ui.orientation;

import android.app.Activity;

abstract public class AxOrientationControllerImpl implements AxOrientationController {
    protected Activity activity;
    protected final int initialOrientation;

    public AxOrientationControllerImpl(Activity activity) {
        this.activity = activity;
        this.initialOrientation = activity.getRequestedOrientation();
    }

    @Override
    public void resetOrientation() {
        activity.setRequestedOrientation(initialOrientation);
    }

    @Override
    public int getOrientation() {
        int orientation = activity.getRequestedOrientation();
        return convertToOrientation(orientation);
    }

    @Override
    public void setOrientation(int orientation) {
        int nativeOrientation = convertToNativeOrientation(orientation);
        activity.setRequestedOrientation(nativeOrientation);
    }

    /**
     * AxOrientationController에서 설정된 방향값을 안드로이드에서 설정된 방향값으로 변환한다.
     * 
     * @param orientation AxOrientationController에서 설정된 방향값
     * @return 안드로이드에서 설정된 방향값
     */
    abstract protected int convertToNativeOrientation(int orientation);

    /**
     * 안드로이드에서 설정된 방향값을 안드로이드에서 AxOrientationController에서 설정된 방향값으로 변환한다.
     * 
     * @param nativeOrientation 안드로이드에서 설정된 방향값
     * @return AxOrientationController에서 설정된 방향값
     */
    abstract protected int convertToOrientation(int nativeOrientation);
}

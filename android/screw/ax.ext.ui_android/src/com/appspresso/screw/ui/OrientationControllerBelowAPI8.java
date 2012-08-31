package com.appspresso.screw.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import com.appspresso.screw.ui.orientation.AxOrientationControllerImpl;

public class OrientationControllerBelowAPI8 extends AxOrientationControllerImpl {
    public OrientationControllerBelowAPI8(Activity activity) {
        super(activity);
    }

    @Override
    protected int convertToNativeOrientation(int orientation) {
        switch (orientation) {
            case DEFAULT:
                return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            case PORTRAIT:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case LANDSCAPE:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }

        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    @Override
    protected int convertToOrientation(int nativeOrientation) {
        switch (nativeOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                return DEFAULT;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                return PORTRAIT;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                return LANDSCAPE;
        }

        return UNKNOWN;
    }
}

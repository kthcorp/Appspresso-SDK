package com.appspresso.screw.ui;

import android.app.Activity;
import android.os.Build;
import com.appspresso.screw.ui.orientation.AxOrientationController;

public class OrientationControllerFactory {
    public static AxOrientationController create(Activity activity) {
        if (Build.VERSION.SDK_INT < 9) {
            return new OrientationControllerBelowAPI8(activity);
        }
        else {
            return new OrientationController(activity);
        }
    }
}

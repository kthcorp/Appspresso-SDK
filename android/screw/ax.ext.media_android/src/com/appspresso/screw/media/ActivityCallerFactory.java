package com.appspresso.screw.media;

import java.util.HashMap;
import java.util.Map;
import android.os.Build;

public class ActivityCallerFactory {
    private static final int REQUEST_CODE_PICK_IMAGE = 9100;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 9200;
    private static final int REQUEST_CODE_CROP_IMAGE = 9300;

    //
    //
    //

    public static CaptureImageActivityCaller getCaptureImageActivityCaller(String target,
            boolean crop, ResultObserver resultObserver) {

        CaptureImageActivityCaller caller = createCaptureImageCaller();
        caller.setResultObserver(resultObserver);

        ActivityCaller.Callback callback = createCaptureImageCallback(crop);
        caller.addCallback(callback);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("target", target);
        caller.setProperties(properties);

        caller.setRequestCode(REQUEST_CODE_CAPTURE_IMAGE);

        return caller;
    }

    private static CaptureImageActivityCaller createCaptureImageCaller() {
        return new CaptureImageActivityCaller();
    }

    private static ActivityCaller.Callback createCaptureImageCallback(boolean crop) {
        return (crop)
                ? new CaptureImageActivityCaller.CropCallback()
                : new CaptureImageActivityCaller.Callback();
    }

    //
    //
    //

    public static PickImageActivityCaller getPickImageActivityCaller(String target, boolean crop,
            ResultObserver resultObserver) {

        PickImageActivityCaller caller = createPickImageCaller();
        caller.setResultObserver(resultObserver);

        ActivityCaller.Callback callback = createPickImageCallback(crop);
        caller.addCallback(callback);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("target", target);
        caller.setProperties(properties);

        caller.setRequestCode(REQUEST_CODE_PICK_IMAGE);

        return caller;
    }

    private static PickImageActivityCaller createPickImageCaller() {
        return new PickImageActivityCaller();
    }

    private static ActivityCaller.Callback createPickImageCallback(boolean crop) {
        return (crop)
                ? new PickImageActivityCaller.CropCallback()
                : new PickImageActivityCaller.Callback();
    }

    //
    //
    //

    public static CropImageActivityCaller getCropImageActivityCaller(String source, String target,
            boolean delete, ResultObserver resultObserver) {

        CropImageActivityCaller caller = createCropImageCaller();
        caller.setResultObserver(resultObserver);
        caller.addCallback(createCropImageCallback());

        if (delete) {
            caller.addCallback(new CropImageActivityCaller.DeleteSourceCallback());
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(CropImageActivityCaller.PROP_SOURCE, source);
        properties.put(CropImageActivityCaller.PROP_TARGET, target);
        caller.setProperties(properties);

        caller.setRequestCode(REQUEST_CODE_CROP_IMAGE);

        return caller;
    }

    private static CropImageActivityCaller createCropImageCaller() {
        if (isHoneycomb()) {
            return new HoneycombCropImageActivityCaller();
        }
        else {
            return new CropImageActivityCaller();
        }
    }

    private static ActivityCaller.Callback createCropImageCallback() {
        if (isHoneycomb()) {
            return new HoneycombCropImageActivityCaller.Callback();
        }
        else {
            return new CropImageActivityCaller.Callback();
        }
    }

    private static boolean isHoneycomb() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return (sdkVersion < 14 && sdkVersion > 10);
    }
}

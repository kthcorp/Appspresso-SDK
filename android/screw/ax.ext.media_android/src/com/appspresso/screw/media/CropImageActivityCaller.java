package com.appspresso.screw.media;

import java.io.File;
import java.util.Map;

import com.appspresso.api.AxRuntimeContext;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class CropImageActivityCaller extends DefaultActivityCaller {
    public static final String PROP_SOURCE = "source";
    public static final String PROP_TARGET = "target";
    public static final String PROP_NO_FACE_DETECTION = "noFaceDetection";
    public static final String PROP_OUTPUT_X = "outputX";
    public static final String PROP_OUTPUT_Y = "outputY";

    public static final boolean DEFAULT_NO_FACE_DETECTION = true;

    @Override
    public Intent createIntent(Map<String, Object> properties) {
        String source = (String) properties.get(PROP_SOURCE);
        String target = (String) properties.get(PROP_TARGET);

        if (L.isDebugEnabled()) {
            L.debug("CropImageActivityCaller.createIntent - {source=" + source + ",target="
                    + target + "}");
        }

        boolean noFaceDetection = DEFAULT_NO_FACE_DETECTION;
        if (properties.containsKey(PROP_NO_FACE_DETECTION)) {
            noFaceDetection = (Boolean) properties.get(PROP_NO_FACE_DETECTION);
        }

        File sourceFile = new File(source);
        File targetFile = new File(target);

        Uri sourceUri = Uri.fromFile(sourceFile);
        Uri targetUri = Uri.fromFile(targetFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(sourceUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
        intent.putExtra("noFaceDetection", noFaceDetection);

        if (properties.containsKey(PROP_OUTPUT_X)) {
            int outputX = (Integer) properties.get(PROP_OUTPUT_X);
            intent.putExtra(PROP_OUTPUT_X, outputX);
        }

        if (properties.containsKey(PROP_OUTPUT_Y)) {
            int outputY = (Integer) properties.get(PROP_OUTPUT_Y);
            intent.putExtra(PROP_OUTPUT_Y, outputY);
        }

        return intent;
    }

    public static class Callback extends DefaultCallback {
        @Override
        protected Object getResult(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent) {
            String target = (String) properties.get(PROP_TARGET);
            return context.getFileSystemManager().toVirtualPath(target);
        }
    }

    public static class DeleteSourceCallback extends Callback {
        @Override
        public void onResultSuccess(AxRuntimeContext runtimeContext,
                Map<String, Object> properties, Intent intent, ResultObserver resultObserver) {
            String source = (String) properties.get(PROP_SOURCE);
            new File(source).delete();
        }
    }
}

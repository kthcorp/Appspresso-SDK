package com.appspresso.screw.media;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.appspresso.api.AxRuntimeContext;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

public class HoneycombCropImageActivityCaller extends CropImageActivityCaller {
    @Override
    public Intent createIntent(Map<String, Object> properties) {
        String source = (String) properties.get(PROP_SOURCE);
        String target = (String) properties.get(PROP_TARGET);

        if (L.isDebugEnabled()) {
            L.debug("HoneycombCropImageActivityCaller.createIntent - {source=" + source
                    + ",target=" + target + "}");
        }

        boolean noFaceDetection = DEFAULT_NO_FACE_DETECTION;
        if (properties.containsKey(PROP_NO_FACE_DETECTION)) {
            noFaceDetection = (Boolean) properties.get(PROP_NO_FACE_DETECTION);
        }

        File sourceFile = new File(source);

        Uri sourceUri = Uri.fromFile(sourceFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(sourceUri, "image/*");
        intent.putExtra("noFaceDetection", noFaceDetection);

        if (properties.containsKey(PROP_OUTPUT_X)) {
            int outputX = (Integer) properties.get(PROP_OUTPUT_X);
            intent.putExtra(PROP_OUTPUT_X, outputX);
        }

        if (properties.containsKey(PROP_OUTPUT_Y)) {
            int outputY = (Integer) properties.get(PROP_OUTPUT_Y);
            intent.putExtra(PROP_OUTPUT_Y, outputY);
        }

        intent.putExtra("return-data", true);

        return intent;
    }

    public static class Callback extends DefaultCallback {
        @Override
        protected Object getResult(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent) {
            if (L.isDebugEnabled()) {
                L.debug("HoneycombCropImageActivityCaller.Callback.getResult()");
            }

            String target = (String) properties.get(PROP_TARGET);

            Bitmap bitmap = null;
            try {
                bitmap = intent.getParcelableExtra("data");
                String bitmapFilePath =
                        MediaUtils.saveBitmapToFile(bitmap, new File(target),
                                Bitmap.CompressFormat.JPEG, 100);
                return context.getFileSystemManager().toVirtualPath(bitmapFilePath);
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            finally {
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
    }
}

package com.appspresso.screw.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import com.appspresso.api.AxRuntimeContext;
import android.content.Intent;
import android.net.Uri;

public class PickImageActivityCaller extends DefaultActivityCaller {
    public static final String PROP_TARGET = "target";

    @Override
    public Intent createIntent(Map<String, Object> properties) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        return intent;
    }

    public static class Callback extends DefaultCallback {
        @Override
        protected Object getResult(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent) {
            Uri imageUri = intent.getData();
            if (imageUri == null) return null;

            try {
                InputStream is =
                        context.getActivity().getContentResolver().openInputStream(imageUri);
                String target = (String) properties.get(PROP_TARGET);

                if (MediaUtils.copy(is, new File(target))) {
                    return context.getFileSystemManager().toVirtualPath(target);
                }
                else {
                    return null;
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class CropCallback extends DefaultCallback {
        @Override
        public void onResultSuccess(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver) {
            Uri imageUri = intent.getData();
            if (imageUri == null) {
                super.onResultError(context, properties, intent, resultObserver);
                return;
            }

            String source = null;

            try {
                InputStream is =
                        context.getActivity().getContentResolver().openInputStream(imageUri);
                source = (String) properties.get(PROP_TARGET);

                if (!MediaUtils.copy(is, new File(source))) {
                    super.onResultError(context, properties, intent, resultObserver);
                    return;
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File sourceFile = new File(source);
            File targetFile = MediaUtils.createFile();

            sourceFile.renameTo(targetFile);

            source = targetFile.getAbsolutePath();
            String target = sourceFile.getAbsolutePath();

            ActivityCallerFactory.getCropImageActivityCaller(source, target, true, resultObserver)
                    .callActivity(context);
        }

        @Override
        protected Object getResult(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent) {
            return null;
        }
    }
}

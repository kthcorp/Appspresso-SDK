package com.appspresso.screw.media;

import java.io.File;
import java.util.Map;
import com.appspresso.api.AxRuntimeContext;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class CaptureImageActivityCaller extends DefaultActivityCaller {
    public static final String PROP_TARGET = "target";

    @Override
    public Intent createIntent(Map<String, Object> properties) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        String target = (String) properties.get(PROP_TARGET);
        File targetFile = new File(target);
        Uri targetUri = Uri.fromFile(targetFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);

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

    public static class CropCallback extends DefaultCallback {
        @Override
        public void onResultSuccess(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent, ResultObserver resultObserver) {
            String source = (String) properties.get(PROP_TARGET);
            String target = MediaUtils.createFile().getAbsolutePath();

            File sourceFile = new File(source);
            File targetFile = new File(target);

            sourceFile.renameTo(targetFile);

            source = targetFile.getAbsolutePath();
            target = sourceFile.getAbsolutePath();

            CropImageActivityCaller caller =
                    ActivityCallerFactory.getCropImageActivityCaller(source, target, true,
                            resultObserver);
            caller.callActivity(context);
        }

        @Override
        protected Object getResult(AxRuntimeContext context, Map<String, Object> properties,
                Intent intent) {
            return null;
        }
    }
}

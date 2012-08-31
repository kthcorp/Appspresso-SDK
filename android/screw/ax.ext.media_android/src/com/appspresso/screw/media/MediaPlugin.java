package com.appspresso.screw.media;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.logging.Log;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import com.appspresso.api.*;

public class MediaPlugin extends DefaultAxPlugin {
    public static Log L = AxLog.getLog(MediaPlugin.class);

    public void captureImage(AxPluginContext context) {
        String out = context.getNamedParamAsString(0, "out", null);
        boolean crop = context.getNamedParamAsBoolean(0, "crop", false);
        final AxPluginContext captureContext = context;

        if (L.isTraceEnabled()) {
            L.trace("captureImage {out=" + out + ", crop=" + crop + "}");
        }

        try {
            String target = toNativePath(out, false, true);
            if (target == null) target = MediaUtils.createFile().getAbsolutePath();

            if (L.isDebugEnabled()) {
                L.debug("captureImage {target=" + target + "}");
            }

            ActivityCallerFactory.getCaptureImageActivityCaller(target, crop, new ResultObserver() {
                @Override
                public void success(Object result) {
                    captureContext.sendResult(result);
                }

                @Override
                public void error(int code, String message) {
                    captureContext.sendError(code, message);
                }
            }).callActivity(runtimeContext);

        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                e.printStackTrace();
                L.debug(e);
            }

            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void pickImage(AxPluginContext context) {
        String out = context.getNamedParamAsString(0, "out", null);
        boolean crop = context.getNamedParamAsBoolean(0, "crop", false);
        final AxPluginContext pickContext = context;

        if (L.isTraceEnabled()) {
            L.trace("pickImage {out=" + out + ", crop=" + crop + "}");
        }

        try {
            String target = toNativePath(out, false, true);
            if (target == null) target = MediaUtils.createFile().getAbsolutePath();

            if (L.isDebugEnabled()) {
                L.debug("pickImage {target=" + target + "}");
            }

            ActivityCallerFactory.getPickImageActivityCaller(target, crop, new ResultObserver() {
                @Override
                public void success(Object result) {
                    pickContext.sendResult(result);
                }

                @Override
                public void error(int code, String message) {
                    pickContext.sendError(code, message);
                }
            }).callActivity(runtimeContext);

        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                e.printStackTrace();
                L.debug(e);
            }

            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void cropImage(AxPluginContext context) {
        String src = context.getNamedParamAsString(0, "src", null);
        String out = context.getNamedParamAsString(0, "out", null);
        final AxPluginContext cropContext = context;

        if (L.isTraceEnabled()) {
            L.trace("cropImage {src=" + src + ", out=" + out + "}");
        }

        try {
            String source = toNativePath(src, true, false);
            String target = toNativePath(out, false, true);
            if (target == null) target = MediaUtils.createFile().getAbsolutePath();

            if (L.isDebugEnabled()) {
                L.debug("cropImage {source=" + source + ", target=" + target + "}");
            }

            ActivityCallerFactory.getCropImageActivityCaller(source, target, false,
                    new ResultObserver() {
                        @Override
                        public void success(Object result) {
                            cropContext.sendResult(result);
                        }

                        @Override
                        public void error(int code, String message) {
                            cropContext.sendError(code, message);
                        }
                    }).callActivity(runtimeContext);

        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                e.printStackTrace();
                L.debug(e);
            }

            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void captureScreen(AxPluginContext context) {
        String out = context.getNamedParamAsString(0, "out", null);

        if (L.isTraceEnabled()) {
            L.trace("captureScreen {out=" + out + "}");
        }

        String target = toNativePath(out, false, true);
        if (target == null) target = MediaUtils.createFile().getAbsolutePath();

        try {
            Bitmap bitmap =
                    MediaUtils.captureScreen(runtimeContext.getActivity(),
                            runtimeContext.getWebView());
            target =
                    MediaUtils.saveBitmapToFile(bitmap, new File(target),
                            Bitmap.CompressFormat.PNG, 100);
            if (target == null) throw new AxError(AxError.UNKNOWN_ERR, "");

            String virtualPath = runtimeContext.getFileSystemManager().toVirtualPath(target);
            context.sendResult(virtualPath);
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                e.printStackTrace();
            }

            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void playSound(AxPluginContext context) {
        String src = context.getParamAsString(0);
        if (L.isTraceEnabled()) {
            L.trace("playSound {src=" + src + "}");
        }

        String source = toNativePath(src, true, false);
        if (L.isDebugEnabled()) {
            L.trace("playSound {source=" + source + "}");
        }

        SoundPlayer player = SoundPlayer.getInstance();

        // XXX 현재 메소드에서 각각의 항목에 대한 정의를 할 수 없음으로 임의의 값을 넣어줌
        int streamId = player.play(source, 1, 1, 1, 0, 1);
        if (L.isTraceEnabled()) {
            L.trace("playSound {streamId=" + streamId + "}");
        }

        if (streamId == 0) { throw new AxError(AxError.UNKNOWN_ERR,
                "An unknown error has occurred."); }

        context.sendResult(streamId);
    }

    public void stopSound(AxPluginContext context) {
        int streamId = context.getParamAsNumber(0).intValue();
        if (L.isTraceEnabled()) {
            L.trace("stopSound {streamId=" + streamId + "}");
        }

        SoundPlayer player = SoundPlayer.getInstance();
        player.stop(streamId);

        context.sendResult();
    }

    public void addToGallery(AxPluginContext context) {
        String src = context.getParamAsString(0);
        if (L.isTraceEnabled()) {
            L.trace("addToGallery {src=" + src + "}");
        }

        String source = toNativePath(src, true, false);
        if (L.isDebugEnabled()) {
            L.debug("addToGallery {source=" + source + "}");
        }

        ContentResolver resolver = runtimeContext.getActivity().getContentResolver();
        try {
            String result = MediaStore.Images.Media.insertImage(resolver, source, null, null);

            if (result == null) {
                context.sendError(AxError.UNKNOWN_ERR, "An unknown error has occurred.");
            }
            else {
                if (L.isTraceEnabled()) {
                    L.trace("addToGallery {result=" + result + "}");
                }
                context.sendResult();
            }
        }
        catch (FileNotFoundException e) {
            if (L.isDebugEnabled()) {
                e.printStackTrace();
                L.debug(e);
            }
            throw new AxError(AxError.NOT_FOUND_ERR, "File not found : " + src);
        }
    }

    @Deprecated
    public void playAudio(AxPluginContext ctx) {
        try {
            String src = ctx.getParamAsString(0);
            if (L.isTraceEnabled()) {
                L.trace("playAudio: src=" + src);
            }
            String srcPath = runtimeContext.getFileSystemManager().toNativePath(src);
            if (srcPath == null) {
                // TODO: support asset? stream?
                ctx.sendError(AxError.INVALID_VALUES_ERR, "not a valid file: src=" + src);
                return;
            }

            File srcFile = new File(srcPath);
            MediaUtils.playAudio(runtimeContext.getActivity(), srcFile);
            ctx.sendResult();
        }
        catch (Exception e) {
            if (L.isDebugEnabled()) {
                L.debug("playAudio error", e);
            }
            ctx.sendError(AxError.NOT_SUPPORTED_ERR, e.getMessage());
        }
    }

    /**
     * 가상 경로를 실제 물리적 경로로 변환한다.
     * 
     * @param virtualPath 가상경로
     * @param requireExist 가상경로가 실제 존재하지 않을 경우 에러를 발생하게 할 플래그
     * @param nullable 가상경로에 대한 파일이 존재하지 않을 경우 에러를 발생하게 할 플래그
     * @return 변환된 물리경로
     * @throws AxError 전달한 플러그에 대해 실제 파일이 만족하지 못할 경우 발생
     */
    private String toNativePath(String virtualPath, boolean requireExist, boolean nullable)
            throws AxError {
        String nativePath = runtimeContext.getFileSystemManager().toNativePath(virtualPath);

        if (nativePath == null) {
            if (nullable) { return null; }
            throw new AxError(AxError.INVALID_VALUES_ERR, "Invalid path : " + virtualPath);
        }

        boolean exist = new File(nativePath).exists();
        if (requireExist && !exist) { throw new AxError(AxError.NOT_FOUND_ERR, "File not found : "
                + virtualPath); }

        if (!requireExist && exist) { throw new AxError(AxError.INVALID_VALUES_ERR,
                "File already exist : " + virtualPath); }

        return nativePath;
    }
}

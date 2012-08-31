package com.appspresso.screw.media;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.View;

public class MediaUtils {
    public static String TEMPORARY_DIR_NAME = "AxFileTemporary";
    private static File TEMPORARY_DIR;

    public static File createFile(String prefix, String suffix) {
        String newFileName = prefix + Long.toHexString(System.currentTimeMillis()) + suffix;
        return new File(getAxFileTemporary(), newFileName);
    }

    public static File createFile() {
        return createFile("", ".jpg");
    }
    
    public static File getAxFileTemporary() {
    	if(TEMPORARY_DIR == null || !TEMPORARY_DIR.exists()) {
    		TEMPORARY_DIR = new File(Environment.getExternalStorageDirectory(), TEMPORARY_DIR_NAME);
    		TEMPORARY_DIR.mkdirs();
    		
    		try {
				new File(TEMPORARY_DIR, ".nomedia").createNewFile();
			} catch (IOException ignore) {
			}
    	}
    	
    	return TEMPORARY_DIR;
    }

    public static final boolean copy(InputStream is, File target) {
        OutputStream os = null;

        try {
            os = new FileOutputStream(target);

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ignore) {}
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ignore) {}
            }
        }
    }

    //
    //
    //

    private static final int CAPTURE_SCREEN_MIN_TIMEOUT = 1000;
    private static final int CAPTURE_SCREEN_MAX_TIMEOUT = 3000;

    public static Bitmap captureScreen(final Activity activity, final View view) throws Exception {
        int width = view.getWidth();
        int height = view.getHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        final Object lock = new Object();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    view.invalidate();
                    view.draw(canvas);
                }
                finally {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            }
        });

        try {
            Thread.sleep(CAPTURE_SCREEN_MIN_TIMEOUT);
        }
        catch (InterruptedException ignored) {}

        synchronized (lock) {
            try {
                lock.wait(CAPTURE_SCREEN_MAX_TIMEOUT);
            }
            catch (InterruptedException ignored) {}
        }

        return bitmap;
    }

    public static String saveBitmapToFile(final Bitmap bitmap, final File outFile,
            Bitmap.CompressFormat format, int quality) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            bitmap.compress(format, quality, out);
            out.flush();
            return outFile.getAbsolutePath();
        }
        catch (IOException e) {
            return null;
        }
        finally {
            if (out != null) out.close();
        }
    }

    @Deprecated
    public static void playAudio(final Activity activity, final File file) throws IOException {
        MediaPlayer mp = new MediaPlayer();
        mp.setDataSource(file.getAbsolutePath());
        try {
            mp.prepare();
            mp.start();
        }
        catch (IllegalStateException e) {
            mp.release();
        }
    }
}

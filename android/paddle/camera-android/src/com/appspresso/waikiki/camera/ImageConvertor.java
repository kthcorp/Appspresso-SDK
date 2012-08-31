package com.appspresso.waikiki.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.OrientationEventListener;

public class ImageConvertor {
    public static boolean convertToFile(byte[] data, File file, int rotation) {
        Bitmap bitmap = null;
        Bitmap rotatedBitmap = null;
        OutputStream os = null;
        try {

            // 2011-11-11 OutOfMemoryError를 위한 임시조치.
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = 1;
            option.inPurgeable = true;
            option.inDither = true;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, option);

            os = new FileOutputStream(file);
            if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                rotation = 0;
            }

            // Mungyu 2011-03-09 Camera.Parameters.setRotation(degree) 를 쓰면 될 것
            // 같지만 N1만 원하는대로 동작한다.
            // 그런고로 회전시킨 새로운 Bitmap을 생성하여 저장.
            Matrix m = new Matrix();
            m.setRotate(rotation, (float) bitmap.getWidth(), (float) bitmap.getHeight());

            rotatedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,
                            true);
            rotatedBitmap.compress(CompressFormat.JPEG, 100, os);
            return true;
        }
        catch (Exception e) {
            return false;
        }
        finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                }
                catch (IOException e) {}
            }

            if (bitmap != null) bitmap.recycle();
            if (rotatedBitmap != null) rotatedBitmap.recycle();
        }
    }
}

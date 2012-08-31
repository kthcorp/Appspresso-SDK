package com.appspresso.waikiki.camera;

import java.util.concurrent.Semaphore;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class JpegImageCallback implements PictureCallback {
    private Semaphore lock;
    private byte[] result;

    public JpegImageCallback(Semaphore lock) {
        this.lock = lock;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            result = data;
        }
        catch (Exception e) {

        }
        finally {
            lock.release();

            // 데이터를 이미지 파일로 처리하기 전에 카메라 프리뷰를 풀어줌으로써
            // 카메라 프리뷰가 풀린 시점과 실제 SuccessCallback (AxPluginContext의)리 호출되는 시간이
            // 차이가 난다.
            camera.startPreview();
        }
    }

    public byte[] getData() {
        byte[] data = result;
        result = null;
        return data;
    }
}

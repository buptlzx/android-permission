package com.lzx.permission.checker;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Lzx on 2017/10/27.
 */

public class CameraChecker implements IChecker {

    @Override
    public boolean check(Context context, String permission) {
        return checkCameraPermission(context);
    }

    public static boolean checkCameraPermission(Context context) {
        Camera camera = getFrontCamera();
        if (camera != null) {
            try {
                camera.release();
            } catch (Exception e) {
            }
            return true;
        }
        return false;
    }

    private static Camera getCamera(int position) {
        Camera camera = null;
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx<cameraCount; camIdx++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == position || cameraCount == 1) {
                try {
                    camera = Camera.open(camIdx);
                    if (camera != null) {
                        Camera.Parameters mParameters = camera.getParameters();
                        camera.setParameters(mParameters);
                    }
                } catch (RuntimeException e) {
                    //MX5 手机没有摄像头权限， 但是open返回数据
                    Log.e("Your_TAG", "Camera failed to open: " + e.getLocalizedMessage());
                    if (camera != null) {
                        try {
                            camera.release();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    camera = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    camera = null;
                }
                break;
            }
        }
        return camera;
    }

    private static Camera getFrontCamera() {
        try {
            return getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

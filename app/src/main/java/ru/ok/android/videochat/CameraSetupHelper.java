package ru.ok.android.videochat;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import ru.ok.android.utils.Logger;

@SuppressLint({"NewApi"})
public class CameraSetupHelper implements CameraSetupInterface {
    private Camera camera;
    private int cameraOrientation;
    private boolean frontCamera;

    public void initCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        this.camera = null;
        CameraInfo cameraInfo = new CameraInfo();
        int i = 0;
        while (i < cameraCount) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 1) {
                try {
                    this.camera = Camera.open(i);
                    this.frontCamera = true;
                    this.cameraOrientation = cameraInfo.orientation;
                    break;
                } catch (RuntimeException e) {
                    Logger.m176e("Camera failed to open: " + e.getLocalizedMessage());
                }
            } else {
                i++;
            }
        }
        if (this.camera == null) {
            i = 0;
            while (i < cameraCount) {
                Camera.getCameraInfo(i, cameraInfo);
                try {
                    this.camera = Camera.open(i);
                    this.frontCamera = cameraInfo.facing == 1;
                    this.cameraOrientation = cameraInfo.orientation;
                    return;
                } catch (RuntimeException e2) {
                    Logger.m176e("Camera failed to open: " + e2.getLocalizedMessage());
                    i++;
                }
            }
        }
    }

    public Camera getCamera() {
        return this.camera;
    }

    public boolean isFrontCamera() {
        return this.frontCamera;
    }

    public int getCameraOrientation() {
        return this.cameraOrientation;
    }
}

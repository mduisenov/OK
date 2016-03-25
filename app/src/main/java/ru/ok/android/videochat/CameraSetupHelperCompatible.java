package ru.ok.android.videochat;

import android.hardware.Camera;

public class CameraSetupHelperCompatible implements CameraSetupInterface {
    private Camera camera;

    public void initCamera() {
        this.camera = Camera.open();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public boolean isFrontCamera() {
        return false;
    }

    public int getCameraOrientation() {
        return 90;
    }
}

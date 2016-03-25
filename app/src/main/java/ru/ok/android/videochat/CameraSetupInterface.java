package ru.ok.android.videochat;

import android.hardware.Camera;

public interface CameraSetupInterface {
    Camera getCamera();

    int getCameraOrientation();

    void initCamera();

    boolean isFrontCamera();
}

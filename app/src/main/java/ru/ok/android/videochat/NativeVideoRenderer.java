package ru.ok.android.videochat;

public class NativeVideoRenderer {
    public native long getVideoRendererFactory();

    static {
        try {
            System.loadLibrary("odnoklassniki-android-glv2");
        } catch (Exception e) {
        }
    }
}

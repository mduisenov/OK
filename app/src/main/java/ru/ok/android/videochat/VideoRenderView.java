package ru.ok.android.videochat;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import ru.ok.android.utils.Logger;

public class VideoRenderView extends GLSurfaceView {
    public VideoRenderView(Context context) {
        super(context);
        init();
    }

    public VideoRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode() && VideochatController.instance().isGL20Supported()) {
            try {
                getClass().getMethod("setEGLContextClientVersion", new Class[]{Integer.TYPE}).invoke(this, new Object[]{Integer.valueOf(2)});
            } catch (Exception e) {
                Logger.m172d("Failed to set OpenGL 2.0 context - falling back to version 1");
                VideochatController.instance().forceGL10();
            }
        }
    }
}

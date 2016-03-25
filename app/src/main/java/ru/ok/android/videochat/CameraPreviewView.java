package ru.ok.android.videochat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraPreviewView extends SurfaceView implements Callback {
    private SurfaceHolder holder;
    public SurfaceListener surfaceListener;

    public interface SurfaceListener {
        void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

        void surfaceCreated(SurfaceHolder surfaceHolder);

        void surfaceDestroyed(SurfaceHolder surfaceHolder);
    }

    public CameraPreviewView(Context context) {
        super(context);
    }

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSurfaceListener(SurfaceListener listener) {
        if (this.holder == null) {
            this.holder = getHolder();
            this.holder.addCallback(this);
            this.holder.setType(3);
            this.holder.setSizeFromLayout();
        }
        this.surfaceListener = listener;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.surfaceListener != null) {
            this.surfaceListener.surfaceCreated(holder);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.surfaceListener != null) {
            this.surfaceListener.surfaceChanged(holder, format, w, h);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.surfaceListener != null) {
            this.surfaceListener.surfaceDestroyed(holder);
        }
    }
}

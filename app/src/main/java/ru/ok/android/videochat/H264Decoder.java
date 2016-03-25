package ru.ok.android.videochat;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import ru.ok.android.utils.Logger;

@TargetApi(16)
public class H264Decoder {
    private int height;
    private MediaCodec mediaCodec;
    private boolean mustReconfigure;
    private Surface surface;
    private int width;

    /* renamed from: ru.ok.android.videochat.H264Decoder.1 */
    class C14751 implements SurfaceTextureListener {
        C14751() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            H264Decoder.this.releaseSurface();
            H264Decoder.this.surface = new Surface(surfaceTexture);
            H264Decoder.this.mustReconfigure = true;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            H264Decoder.this.releaseSurface();
            H264Decoder.this.surface = new Surface(surfaceTexture);
            H264Decoder.this.mustReconfigure = true;
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            H264Decoder.this.releaseSurface();
            H264Decoder.this.mustReconfigure = true;
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    public void close() {
        closeCodec();
        releaseSurface();
    }

    private void closeCodec() {
        try {
            if (this.mediaCodec != null) {
                this.mediaCodec.stop();
            }
            if (this.mediaCodec != null) {
                this.mediaCodec.release();
            }
        } catch (Throwable e) {
            Logger.m186w(e, "Failed to stop media decoder");
        }
        this.mediaCodec = null;
    }

    private void releaseSurface() {
        if (this.surface != null) {
            this.surface.release();
            this.surface = null;
        }
    }

    public void setView(TextureView view) {
        releaseSurface();
        if (view != null) {
            SurfaceTexture surfaceTexture = view.getSurfaceTexture();
            if (surfaceTexture != null) {
                this.surface = new Surface(surfaceTexture);
            }
            this.mustReconfigure = true;
            if (view != null) {
                view.setSurfaceTextureListener(new C14751());
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

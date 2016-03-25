package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.io.IOException;
import java.lang.ref.SoftReference;
import ru.ok.android.utils.Base64;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;

public class AudioPlayerWaveView extends View {
    private float altProportion;
    private byte[] alternativePeaks;
    private SoftReference<Bitmap> bitmapBuffer;
    boolean bufferInvalid;
    private SoftReference<Canvas> canvasBuffer;
    private float displayShift;
    private int gapWidth;
    private byte[] lineSizes;
    private int lineSpace;
    private int lineWidth;
    private byte minLine;
    private Paint opaquePaint;
    private String peaksEncodedData;
    private byte[] peaksRawData;
    private float position;
    private boolean right;
    private boolean rollingMode;
    private int skipPeaks;
    TouchEventCallback touchCallback;
    private Paint transparentPaint;

    public interface TouchEventCallback {
        boolean onEvent(View view, MotionEvent motionEvent);
    }

    public AudioPlayerWaveView(Context context) {
        super(context);
        this.right = false;
        this.rollingMode = false;
        this.skipPeaks = 0;
        this.displayShift = 0.0f;
        this.altProportion = 0.0f;
        this.bufferInvalid = true;
        init();
    }

    public AudioPlayerWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.right = false;
        this.rollingMode = false;
        this.skipPeaks = 0;
        this.displayShift = 0.0f;
        this.altProportion = 0.0f;
        this.bufferInvalid = true;
        init();
    }

    public AudioPlayerWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.right = false;
        this.rollingMode = false;
        this.skipPeaks = 0;
        this.displayShift = 0.0f;
        this.altProportion = 0.0f;
        this.bufferInvalid = true;
        init();
    }

    private void init() {
        this.lineWidth = getResources().getDimensionPixelSize(2131230722);
        this.gapWidth = getResources().getDimensionPixelSize(2131230721);
        this.lineSpace = this.lineWidth + this.gapWidth;
        this.minLine = (byte) this.gapWidth;
        if (isInEditMode()) {
            this.peaksEncodedData = "AAAHAwAHAAAAAAAAAAADQGVIIGVKfzICAQAOSA4JGgENAAAMDg4MDxMCFCEHDQ0CDAoRAgAAAAAAAAAABAsOEAoQDg8NDgAAAAAAAAAAAAA=";
        }
        initPaint();
    }

    private void initPaint() {
        this.transparentPaint = new Paint();
        this.transparentPaint.setStyle(Style.STROKE);
        int transparentColor = getResources().getColor(this.right ? 2131492898 : 2131492897);
        this.transparentPaint.setColor(Color.argb(77, Color.red(transparentColor), Color.green(transparentColor), Color.blue(transparentColor)));
        this.transparentPaint.setStrokeWidth((float) this.lineWidth);
        this.opaquePaint = new Paint();
        this.opaquePaint.setStyle(Style.STROKE);
        this.opaquePaint.setColor(getResources().getColor(2131492898));
        this.opaquePaint.setStrokeWidth((float) this.lineWidth);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePeaks(w, h);
        clearBackBuffer();
    }

    private void clearBackBuffer() {
        this.canvasBuffer = null;
        Bitmap bitmap = this.bitmapBuffer == null ? null : (Bitmap) this.bitmapBuffer.get();
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.bitmapBuffer = null;
    }

    private void updatePeaks(int width, int height) {
        this.bufferInvalid = true;
        decodeAudioPeaks(((int) (((double) ((float) width)) * (1.0d - ((double) this.displayShift)))) / this.lineSpace, height);
    }

    protected void onDraw(Canvas canvas) {
        Canvas backCanvas;
        super.onDraw(canvas);
        int width = Math.min(getWidth(), canvas.getWidth());
        int height = Math.min(getHeight(), canvas.getHeight());
        Bitmap bitmap = this.bitmapBuffer == null ? null : (Bitmap) this.bitmapBuffer.get();
        if (this.canvasBuffer == null) {
            backCanvas = null;
        } else {
            backCanvas = (Canvas) this.canvasBuffer.get();
        }
        if (backCanvas == null) {
            backCanvas = new Canvas();
            this.canvasBuffer = new SoftReference(backCanvas);
        }
        if (!(bitmap == null || (height == bitmap.getHeight() && width == bitmap.getWidth()))) {
            bitmap = null;
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            this.bitmapBuffer = new SoftReference(bitmap);
            backCanvas.setBitmap(bitmap);
            this.bufferInvalid = true;
        }
        backCanvas.setBitmap(bitmap);
        if (this.bufferInvalid) {
            int splitX = (int) (this.position * ((float) width));
            int middleY = height / 2;
            bitmap.eraseColor(0);
            backCanvas.clipRect(0.0f, 0.0f, (float) splitX, (float) height, Op.REPLACE);
            int splitLine = splitX / this.lineSpace;
            float[] audioLines = null;
            if (this.lineSizes != null) {
                int idx = -1;
                audioLines = new float[(this.lineSizes.length * 4)];
                for (int i = 0; i < this.lineSizes.length; i++) {
                    float v = ((float) i) * ((float) this.lineSpace);
                    idx++;
                    audioLines[idx] = v;
                    idx++;
                    audioLines[idx] = (float) (middleY - this.lineSizes[i]);
                    idx++;
                    audioLines[idx] = v;
                    idx++;
                    audioLines[idx] = (float) (this.lineSizes[i] + middleY);
                }
                backCanvas.drawLines(audioLines, 0, Math.min((splitLine + 1) * 4, audioLines.length), this.opaquePaint);
            }
            backCanvas.clipRect((float) splitX, 0.0f, (float) width, (float) height, Op.REPLACE);
            if (audioLines != null) {
                int startLineIdx = Math.min(splitLine, audioLines.length / 4);
                backCanvas.drawLines(audioLines, startLineIdx * 4, audioLines.length - (startLineIdx * 4), this.transparentPaint);
            }
        }
        this.bufferInvalid = false;
        int targetWidth = this.lineSizes.length * this.lineSpace;
        canvas.drawBitmap(bitmap, new Rect(0, 0, targetWidth, height), new Rect(width - targetWidth, 0, width, height), null);
    }

    private void decodeAudioPeaks(int targetCount, int height) {
        if (targetCount > 0) {
            byte[] peaks = null;
            if (!StringUtils.isEmpty(this.peaksEncodedData)) {
                try {
                    peaks = Base64.decode(this.peaksEncodedData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!(this.peaksRawData == null || this.peaksRawData.length == 0)) {
                peaks = this.peaksRawData;
            }
            int peaksCount = 0;
            int peaksOffset = 0;
            if (peaks != null) {
                peaksCount = Math.max(peaks.length - this.skipPeaks, 0);
                peaksOffset = peaks.length - peaksCount;
            }
            if (this.rollingMode) {
                if (peaksCount > targetCount) {
                    peaksOffset += peaksCount - targetCount;
                    peaksCount = targetCount;
                } else {
                    targetCount = peaksCount;
                }
            }
            int alternativePeaksOffset = -1;
            if (this.alternativePeaks != null && peaks != null && this.alternativePeaks.length > 1 && peaks.length > 1) {
                alternativePeaksOffset = (this.alternativePeaks.length * peaksOffset) / peaks.length;
            }
            this.lineSizes = new byte[targetCount];
            for (int i = 0; i < targetCount; i++) {
                if (peaksCount < 2) {
                    this.lineSizes[i] = this.minLine;
                } else {
                    int interpolated = interpolateValue(peaks, peaksCount, peaksOffset, (((float) i) * ((float) peaksCount)) / ((float) targetCount));
                    if (alternativePeaksOffset >= 0 && this.altProportion > 0.001f) {
                        int altPeaksCount = this.alternativePeaks.length - alternativePeaksOffset;
                        float posAlt = (((float) i) * ((float) altPeaksCount)) / ((float) targetCount);
                        interpolated = capValue((int) ((((float) interpolateValue(this.alternativePeaks, altPeaksCount, alternativePeaksOffset, posAlt)) * this.altProportion) + (((float) interpolated) * (1.0f - this.altProportion))));
                    }
                    this.lineSizes[i] = (byte) (((height / 2) * interpolated) / 127);
                    if (this.lineSizes[i] < this.minLine) {
                        this.lineSizes[i] = this.minLine;
                    }
                }
            }
        }
    }

    private int interpolateValue(byte[] peaks, int peaksCount, int peaksOffset, float pos) {
        int interpolated;
        int low = (int) Math.floor((double) pos);
        if (low >= peaksCount - 1) {
            interpolated = (byte) capValue(peaks[(peaksOffset + peaksCount) - 1]);
        } else if (this.rollingMode) {
            interpolated = peaks[peaksOffset + low];
        } else {
            interpolated = peaks[peaksOffset + low] + ((int) (((float) (peaks[(peaksOffset + low) + 1] - peaks[peaksOffset + low])) * (pos - ((float) low))));
        }
        return capValue(interpolated);
    }

    private int capValue(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 127) {
            return 127;
        }
        return value;
    }

    public void setWaveInfo(String waveInfo) {
        this.peaksEncodedData = waveInfo;
        updatePeaks(getWidth(), getHeight());
    }

    public void setWaveInfo(byte[] waveInfo) {
        this.peaksRawData = waveInfo;
        updatePeaks(getWidth(), getHeight());
    }

    public void setAlternativePeaks(byte[] alternativePeaks) {
        this.alternativePeaks = alternativePeaks;
    }

    public void setAltProportion(float altProportion) {
        this.altProportion = altProportion;
    }

    public void setProgress(float position) {
        if (Math.abs((position - this.position) * ((float) getWidth())) >= 1.0f) {
            Logger.m172d("setProgress : " + position);
            invalidate();
            this.position = position;
            this.bufferInvalid = true;
        }
    }

    public void setIsRight() {
        this.right = true;
        initPaint();
    }

    public void setIsLeft() {
        this.right = false;
        initPaint();
    }

    public void setRollingMode(boolean rollingMode) {
        this.rollingMode = rollingMode;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearBackBuffer();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.touchCallback == null ? false : this.touchCallback.onEvent(this, event);
    }

    public int getPeaksCount() {
        return this.peaksRawData != null ? this.peaksRawData.length : 0;
    }

    public void setTouchCallback(TouchEventCallback touchCallback) {
        this.touchCallback = touchCallback;
    }

    public void setSkipPeaks(int skipPeaks) {
        this.skipPeaks = skipPeaks;
        updatePeaks(getWidth(), getHeight());
    }

    public void setDisplayShift(float displayShift) {
        this.displayShift = displayShift;
        updatePeaks(getWidth(), getHeight());
    }

    public int getWaveTotalDisplayWidth() {
        if (this.peaksRawData == null || !this.rollingMode) {
            return 0;
        }
        return this.peaksRawData.length * this.lineSpace;
    }
}

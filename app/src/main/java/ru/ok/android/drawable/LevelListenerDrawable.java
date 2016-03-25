package ru.ok.android.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import ru.ok.android.fresco.FrescoOdkl.ProgressCallback;

public class LevelListenerDrawable extends Drawable {
    private final WeakReference<ProgressCallback> callbackRef;

    protected boolean onLevelChange(int level) {
        ProgressCallback callback = (ProgressCallback) this.callbackRef.get();
        if (callback != null) {
            callback.updateProgress(level);
        }
        return super.onLevelChange(level);
    }

    public LevelListenerDrawable(ProgressCallback progressCallback) {
        this.callbackRef = new WeakReference(progressCallback);
    }

    public void draw(Canvas canvas) {
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }
}

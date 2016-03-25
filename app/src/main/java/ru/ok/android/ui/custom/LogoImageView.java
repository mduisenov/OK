package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public final class LogoImageView extends ImageView {
    private final int criticalH;
    private LogoImageListener logoImageListener;

    public interface LogoImageListener {
        void onLogoHiddenChange(boolean z);
    }

    public LogoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.criticalH = (int) TypedValue.applyDimension(1, 40.0f, context.getResources().getDisplayMetrics());
    }

    public void setLogoImageListener(LogoImageListener logoImageListener) {
        this.logoImageListener = logoImageListener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h < this.criticalH) {
            onCriticalSize();
        } else {
            onNormalSize();
        }
    }

    private void onCriticalSize() {
        setVisibility(4);
        if (this.logoImageListener != null) {
            this.logoImageListener.onLogoHiddenChange(true);
        }
    }

    private void onNormalSize() {
        setVisibility(0);
        if (this.logoImageListener != null) {
            this.logoImageListener.onLogoHiddenChange(false);
        }
    }
}

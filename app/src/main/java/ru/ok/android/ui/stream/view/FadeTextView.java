package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.text.Layout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public final class FadeTextView extends TextView {
    private int currentShaderColor;
    private final int fadeGradientHeight;
    private boolean isFadeEnabled;
    private boolean isForceFadeEnabled;
    private boolean isShaderSet;

    public FadeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isFadeEnabled = true;
        this.isForceFadeEnabled = false;
        this.fadeGradientHeight = (int) TypedValue.applyDimension(1, 60.0f, context.getResources().getDisplayMetrics());
    }

    public void setFadeEnabled(boolean isFadeEnabled) {
        this.isFadeEnabled = isFadeEnabled;
        checkUpdateFadeShader();
    }

    public void setForceFadeEnabled(boolean isForceFadeEnabled) {
        this.isForceFadeEnabled = isForceFadeEnabled;
        checkUpdateFadeShader();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.isShaderSet && getCurrentTextColor() != this.currentShaderColor) {
            checkUpdateFadeShader();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        checkUpdateFadeShader();
    }

    private boolean isTextEllipsized() {
        boolean z = true;
        Layout layout = getLayout();
        if (layout == null) {
            return false;
        }
        int lineCount = layout.getLineCount();
        if (lineCount <= 0) {
            return false;
        }
        TruncateAt truncateAt = getEllipsize();
        if (truncateAt == TruncateAt.END) {
            if (layout.getEllipsisCount(lineCount - 1) <= 0) {
                z = false;
            }
            return z;
        } else if (truncateAt == TruncateAt.START) {
            if (layout.getEllipsisCount(0) <= 0) {
                z = false;
            }
            return z;
        } else {
            for (int i = 0; i < lineCount; i++) {
                if (layout.getEllipsisCount(i) > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    private void checkUpdateFadeShader() {
        if (this.isForceFadeEnabled || (this.isFadeEnabled && isTextEllipsized())) {
            setFadeShader(getHeight(), getCurrentTextColor());
        } else {
            removeFadeShader();
        }
    }

    private void setFadeShader(int viewHeight, int textColor) {
        getPaint().setShader(new LinearGradient(0.0f, (float) Math.max(0, viewHeight - this.fadeGradientHeight), 0.0f, (float) viewHeight, textColor, 0, TileMode.CLAMP));
        this.isShaderSet = true;
        this.currentShaderColor = textColor;
    }

    private void removeFadeShader() {
        getPaint().setShader(null);
        this.isShaderSet = false;
    }
}

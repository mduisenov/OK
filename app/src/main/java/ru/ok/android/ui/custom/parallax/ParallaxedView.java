package ru.ok.android.ui.custom.parallax;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.view.View;
import java.lang.ref.WeakReference;

public abstract class ParallaxedView {
    public static boolean isAPI11;
    protected int lastOffset;
    protected WeakReference<View> view;

    protected abstract void translatePreICS(View view, float f);

    static {
        isAPI11 = VERSION.SDK_INT >= 11;
    }

    public ParallaxedView(View view) {
        this.lastOffset = 0;
        this.view = new WeakReference(view);
    }

    public boolean is(View v) {
        return (v == null || this.view == null || this.view.get() == null || !((View) this.view.get()).equals(v)) ? false : true;
    }

    @SuppressLint({"NewApi"})
    public void setOffset(float offset) {
        View view = (View) this.view.get();
        if (view == null) {
            return;
        }
        if (isAPI11) {
            view.setTranslationY(offset);
        } else {
            translatePreICS(view, offset);
        }
    }

    public void setView(View view) {
        this.view = new WeakReference(view);
    }
}

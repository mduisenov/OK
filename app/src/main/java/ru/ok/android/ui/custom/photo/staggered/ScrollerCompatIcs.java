package ru.ok.android.ui.custom.photo.staggered;

import android.annotation.TargetApi;
import android.widget.Scroller;

class ScrollerCompatIcs {
    @TargetApi(14)
    public static float getCurrVelocity(Scroller scroller) {
        return scroller.getCurrVelocity();
    }
}

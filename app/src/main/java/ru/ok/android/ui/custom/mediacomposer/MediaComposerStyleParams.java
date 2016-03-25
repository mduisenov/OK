package ru.ok.android.ui.custom.mediacomposer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import ru.ok.android.C0206R;

public final class MediaComposerStyleParams {
    public final int cornerIconOffsetPx;
    public final int itemsDefaultVerticalSpacing;
    public final long removeAnimationTime;
    public final boolean scaleToInsertEnabled;
    public final boolean showItemActionIcon;
    public final boolean swipeToDismissEnabled;
    public final int thumbnailWidth;

    public MediaComposerStyleParams(Context context, AttributeSet attrs, int defThemeAttr, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.MediaComposerView, defThemeAttr, defStyle);
        int verticalSpacing = 0;
        boolean swipeToDismissEnabled = true;
        boolean scaleToInsertEnabled = true;
        boolean showItemActionIcon = true;
        if (a != null) {
            try {
                verticalSpacing = a.getDimensionPixelSize(0, 0);
                swipeToDismissEnabled = true & a.getBoolean(1, false);
                scaleToInsertEnabled = true & a.getBoolean(2, false);
                showItemActionIcon = true & a.getBoolean(3, false);
            } finally {
                a.recycle();
            }
        }
        this.itemsDefaultVerticalSpacing = verticalSpacing;
        this.swipeToDismissEnabled = swipeToDismissEnabled;
        this.scaleToInsertEnabled = scaleToInsertEnabled;
        this.showItemActionIcon = showItemActionIcon;
        Resources res = context.getResources();
        this.thumbnailWidth = res.getDisplayMetrics().widthPixels / 2;
        this.cornerIconOffsetPx = res.getDimensionPixelOffset(2131231070);
        this.removeAnimationTime = (long) res.getInteger(17694720);
    }
}

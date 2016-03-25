package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import ru.ok.android.C0206R;

public class FeedMediaTopicStyle {
    public final int maxDisplayedBlocks;
    public final int maxTextLengthInBlock;
    public final int maxTextLinesInBlock;
    public final int maxTracksInBlock;
    public final boolean photoNoCollage;
    public final boolean showMoreAtBottom;
    public final int textAppearanceDescr;
    public final int textAppearanceLink;
    public final int textAppearanceTitle;
    public final boolean textEditable;
    public final int vSpaceNormal;
    public final int vSpaceSmall;

    public FeedMediaTopicStyle(Context context, AttributeSet attrs, int themeAttrResId, int styleResId) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.FeedMediaTopic, themeAttrResId, styleResId);
        int maxDisplayedBlocks = a.getInteger(0, 3);
        if (maxDisplayedBlocks == 0) {
            maxDisplayedBlocks = Integer.MAX_VALUE;
        }
        this.maxDisplayedBlocks = maxDisplayedBlocks;
        int maxTextLengthInBlock = a.getInteger(1, 300);
        if (maxTextLengthInBlock == 0) {
            maxTextLengthInBlock = Integer.MAX_VALUE;
        }
        this.maxTextLengthInBlock = maxTextLengthInBlock;
        int maxTextLinesInBlock = a.getInteger(2, 7);
        if (maxTextLinesInBlock == 0) {
            maxTextLinesInBlock = Integer.MAX_VALUE;
        }
        this.maxTextLinesInBlock = maxTextLinesInBlock;
        int maxTracksInBlock = a.getInteger(3, 3);
        if (maxTracksInBlock == 0) {
            maxTracksInBlock = Integer.MAX_VALUE;
        }
        this.maxTracksInBlock = maxTracksInBlock;
        this.showMoreAtBottom = a.getBoolean(8, true);
        this.textAppearanceTitle = a.getResourceId(4, 0);
        this.textAppearanceDescr = a.getResourceId(5, 0);
        this.textAppearanceLink = a.getResourceId(6, 0);
        this.textEditable = a.getBoolean(7, false);
        this.photoNoCollage = a.getBoolean(9, false);
        a.recycle();
        Resources res = context.getResources();
        this.vSpaceNormal = res.getDimensionPixelOffset(2131231000);
        this.vSpaceSmall = res.getDimensionPixelOffset(2131231001);
    }
}

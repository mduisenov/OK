package ru.ok.android.ui.custom.animationlist;

public final class RowInfo {
    public final int bottom;
    public final int left;
    public final int right;
    public final int top;

    RowInfo(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}

package ru.ok.android.ui.custom.loadmore;

public enum LoadMoreMode {
    NONE(false, false),
    TOP(true, false),
    BOTTOM(false, true),
    BOTH(true, true);
    
    public final boolean hasBottomAdditionalView;
    public final boolean hasTopAdditionalView;

    private LoadMoreMode(boolean hasTopAdditionalView, boolean hasBottomAdditionalView) {
        this.hasTopAdditionalView = hasTopAdditionalView;
        this.hasBottomAdditionalView = hasBottomAdditionalView;
    }
}

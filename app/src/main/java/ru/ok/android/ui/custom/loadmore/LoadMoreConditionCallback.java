package ru.ok.android.ui.custom.loadmore;

public interface LoadMoreConditionCallback {
    boolean isTimeToLoadBottom(int i, int i2);

    boolean isTimeToLoadTop(int i, int i2);
}

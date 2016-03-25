package ru.ok.android.ui.custom.loadmore;

public class LoadMoreConditionCallbackImpl implements LoadMoreConditionCallback {
    public boolean isTimeToLoadTop(int position, int count) {
        return position == 0;
    }

    public boolean isTimeToLoadBottom(int position, int count) {
        return position == count + -1;
    }
}

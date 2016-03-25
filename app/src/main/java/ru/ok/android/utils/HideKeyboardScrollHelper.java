package ru.ok.android.utils;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class HideKeyboardScrollHelper implements OnScrollListener {
    private Context context;
    private View mMainView;

    public HideKeyboardScrollHelper(Context context, View mMainView) {
        this.mMainView = mMainView;
        this.context = context;
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        KeyBoardUtils.hideKeyBoard(this.context, this.mMainView.getWindowToken());
    }

    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
    }
}

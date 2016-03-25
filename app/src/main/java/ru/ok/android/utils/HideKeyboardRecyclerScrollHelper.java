package ru.ok.android.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;

public class HideKeyboardRecyclerScrollHelper extends OnScrollListener {
    private Context context;
    private View mMainView;

    public HideKeyboardRecyclerScrollHelper(Context context, View mMainView) {
        this.mMainView = mMainView;
        this.context = context;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        KeyBoardUtils.hideKeyBoard(this.context, this.mMainView.getWindowToken());
    }
}

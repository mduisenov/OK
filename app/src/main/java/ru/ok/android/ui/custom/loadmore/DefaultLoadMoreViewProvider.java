package ru.ok.android.ui.custom.loadmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class DefaultLoadMoreViewProvider implements LoadMoreViewProvider {
    public LoadMoreView createLoadMoreView(Context context, boolean isTopView, ViewGroup parent) {
        return (LoadMoreView) LayoutInflater.from(context).inflate(2130903277, parent, false);
    }
}

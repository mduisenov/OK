package ru.ok.android.ui.custom.loadmore;

import android.content.Context;
import android.view.ViewGroup;

public interface LoadMoreViewProvider {
    LoadMoreView createLoadMoreView(Context context, boolean z, ViewGroup viewGroup);
}

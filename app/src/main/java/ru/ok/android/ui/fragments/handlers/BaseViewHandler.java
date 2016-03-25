package ru.ok.android.ui.fragments.handlers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseViewHandler {
    protected View mMainView;

    protected abstract int getLayoutId();

    protected abstract void onViewCreated(LayoutInflater layoutInflater, View view);

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mMainView != null) {
            return this.mMainView;
        }
        this.mMainView = inflater.inflate(getLayoutId(), container, false);
        onViewCreated(inflater, this.mMainView);
        return this.mMainView;
    }
}

package ru.ok.android.ui.custom.animationlist;

import android.widget.BaseAdapter;

public abstract class DataChangeAdapter<D> extends BaseAdapter {
    public abstract D getData();

    public abstract void setData(D d);
}

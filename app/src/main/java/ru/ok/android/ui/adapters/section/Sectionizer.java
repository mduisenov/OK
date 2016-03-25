package ru.ok.android.ui.adapters.section;

import android.widget.BaseAdapter;

public interface Sectionizer<T extends BaseAdapter> {
    String getSectionTitleForItem(T t, int i);
}

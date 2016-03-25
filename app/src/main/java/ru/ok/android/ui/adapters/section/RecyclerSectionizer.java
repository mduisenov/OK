package ru.ok.android.ui.adapters.section;

import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;

public interface RecyclerSectionizer<T extends BaseCursorRecyclerAdapter> {
    String getSectionTitleForItem(T t, int i);
}

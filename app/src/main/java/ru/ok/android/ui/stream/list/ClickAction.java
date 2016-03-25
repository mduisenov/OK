package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public interface ClickAction {
    void setClickListener(View view, StreamItemViewController streamItemViewController);

    void setTags(View view);
}

package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class NavigateInternalAction implements ClickAction {
    private final String internalUrl;

    public NavigateInternalAction(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    public void setClickListener(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getNavigateInternalListener());
    }

    public void setTags(View view) {
        view.setTag(2131624327, this.internalUrl);
    }
}

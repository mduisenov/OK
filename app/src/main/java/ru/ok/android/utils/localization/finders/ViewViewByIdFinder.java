package ru.ok.android.utils.localization.finders;

import android.view.View;
import java.util.Collection;

public final class ViewViewByIdFinder extends ViewByIdFinder {
    private final View _view;

    public /* bridge */ /* synthetic */ Collection getValidTags() {
        return super.getValidTags();
    }

    public ViewViewByIdFinder(View view) {
        this._view = view;
    }

    public View findElementById(int viewId) {
        return this._view.findViewById(viewId);
    }
}

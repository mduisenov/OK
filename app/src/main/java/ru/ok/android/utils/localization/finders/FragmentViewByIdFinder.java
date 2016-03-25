package ru.ok.android.utils.localization.finders;

import android.support.v4.app.Fragment;
import android.view.View;
import java.util.Collection;

public final class FragmentViewByIdFinder extends ViewByIdFinder {
    private final Fragment _fragment;

    public /* bridge */ /* synthetic */ Collection getValidTags() {
        return super.getValidTags();
    }

    public FragmentViewByIdFinder(Fragment fragment) {
        this._fragment = fragment;
    }

    public View findElementById(int viewId) {
        return this._fragment.getView() == null ? null : this._fragment.getView().findViewById(viewId);
    }
}

package ru.ok.android.utils.localization.finders;

import android.app.Activity;
import android.view.View;
import java.util.Collection;

public final class ActivityViewByIdFinder extends ViewByIdFinder {
    private final Activity _activity;

    public /* bridge */ /* synthetic */ Collection getValidTags() {
        return super.getValidTags();
    }

    public ActivityViewByIdFinder(Activity activity) {
        this._activity = activity;
    }

    public View findElementById(int viewId) {
        return this._activity.findViewById(viewId);
    }
}

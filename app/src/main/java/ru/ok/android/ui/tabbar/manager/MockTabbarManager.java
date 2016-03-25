package ru.ok.android.ui.tabbar.manager;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.model.events.OdnkEvent;

public class MockTabbarManager implements FullTabbarManager {
    private IllegalStateException getException() {
        return new IllegalStateException("To use tabbar: Please override " + BaseCompatToolbarActivity.class.getSimpleName() + " isUseTabbar() to return true; and be aware to use activity instanceof " + BaseTabbarManager.class.getSimpleName() + "; use " + BaseCompatToolbarActivity.class + ".isUseTabbar(activity) instead;");
    }

    public OdklTabbar getTabbarView() {
        throw getException();
    }

    public void showAboveTabbar() {
        throw getException();
    }

    public int getScrollTabbar() {
        throw getException();
    }

    public void setScrollTabbar(float scroll) {
        throw getException();
    }

    public void showTabbar(boolean isAnimate) {
        throw getException();
    }

    public void setNeedShowTabbar(boolean needShowToolbar) {
        throw getException();
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> arrayList) {
    }
}

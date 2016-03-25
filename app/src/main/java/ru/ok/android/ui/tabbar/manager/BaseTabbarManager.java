package ru.ok.android.ui.tabbar.manager;

import ru.ok.android.ui.tabbar.OdklTabbar;

public interface BaseTabbarManager {
    int getScrollTabbar();

    OdklTabbar getTabbarView();

    void setNeedShowTabbar(boolean z);

    void setScrollTabbar(float f);

    void showAboveTabbar();

    void showTabbar(boolean z);
}

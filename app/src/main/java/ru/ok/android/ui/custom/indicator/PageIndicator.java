package ru.ok.android.ui.custom.indicator;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public interface PageIndicator {
    void setOnPageChangeListener(OnPageChangeListener onPageChangeListener);

    void setViewPager(ViewPager viewPager);
}

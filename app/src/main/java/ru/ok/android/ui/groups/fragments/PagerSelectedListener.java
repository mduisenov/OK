package ru.ok.android.ui.groups.fragments;

public interface PagerSelectedListener {
    void onPageNotSelected();

    void onPageScrolledOffset(float f);

    void onPageSelected();
}

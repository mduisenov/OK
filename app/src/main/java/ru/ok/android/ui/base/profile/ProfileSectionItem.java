package ru.ok.android.ui.base.profile;

public interface ProfileSectionItem<C> {
    int getCount(C c);

    int getNameResourceId();
}

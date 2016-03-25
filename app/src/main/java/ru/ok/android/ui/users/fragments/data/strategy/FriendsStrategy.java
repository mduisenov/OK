package ru.ok.android.ui.users.fragments.data.strategy;

public interface FriendsStrategy<I> {
    CharSequence buildInfoString(I i);

    I getItem(int i);

    String getItemHeader(int i);

    int getItemsCount();
}

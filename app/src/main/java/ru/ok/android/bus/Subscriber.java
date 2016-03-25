package ru.ok.android.bus;

import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

public interface Subscriber<E> {
    void consume(@AnyRes int i, @NonNull E e);
}

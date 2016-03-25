package ru.ok.android.utils.localization.processors;

import android.content.Context;

public interface ElementAttributeProcessor<T, V> {
    String getAttributeName();

    V getResourceValueById(Context context, int i);

    void setAttributeValueForElement(T t, V v);
}

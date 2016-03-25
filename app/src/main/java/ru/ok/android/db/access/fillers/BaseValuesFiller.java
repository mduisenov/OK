package ru.ok.android.db.access.fillers;

import android.content.ContentValues;

public interface BaseValuesFiller<T> {
    void fillValues(ContentValues contentValues, T t);

    String getRequestFields();
}

package ru.ok.model;

import android.os.Parcelable;

public interface GeneralUserInfo extends Parcelable {
    String getId();

    String getName();

    int getObjectType();

    String getPicUrl();
}

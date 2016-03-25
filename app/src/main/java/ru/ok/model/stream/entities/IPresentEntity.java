package ru.ok.model.stream.entities;

import android.support.annotation.Nullable;

public interface IPresentEntity {
    @Nullable
    String getLargestPicUrl();

    boolean isBig();

    boolean isLive();
}

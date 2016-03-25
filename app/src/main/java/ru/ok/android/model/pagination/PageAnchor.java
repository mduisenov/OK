package ru.ok.android.model.pagination;

import android.os.Parcelable;
import android.support.annotation.NonNull;

public interface PageAnchor extends Parcelable {
    @NonNull
    String getBackwardAnchor();

    @NonNull
    String getForwardAnchor();
}

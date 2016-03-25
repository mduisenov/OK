package ru.ok.android.ui.image.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.model.photo.PhotoAlbumInfo;

public interface AlbumFinder {
    @Nullable
    PhotoAlbumInfo findAlbumById(@NonNull String str);
}

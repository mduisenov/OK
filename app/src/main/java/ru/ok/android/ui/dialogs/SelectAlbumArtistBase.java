package ru.ok.android.ui.dialogs;

import android.content.Context;

public class SelectAlbumArtistBase {
    protected Context context;
    protected OnSelectAlbumArtistListener listener;

    public interface OnSelectAlbumArtistListener {
        void onSelectAlbum();

        void onSelectArtist();
    }

    public SelectAlbumArtistBase(Context context) {
        this.context = context;
    }

    public void setOnSelectAlbumArtistListener(OnSelectAlbumArtistListener listener) {
        this.listener = listener;
    }

    protected int[] getItems() {
        return new int[]{2131165373, 2131165407};
    }
}

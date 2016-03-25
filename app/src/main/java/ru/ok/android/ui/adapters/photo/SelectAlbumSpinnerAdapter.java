package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import java.util.List;
import ru.ok.android.ui.adapters.spinner.BaseNavigationSpinnerAdapter;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public final class SelectAlbumSpinnerAdapter extends BaseNavigationSpinnerAdapter {
    private final List<PhotoAlbumInfo> albums;
    private final Context context;

    public SelectAlbumSpinnerAdapter(Context context, List<PhotoAlbumInfo> albums) {
        super(context);
        this.context = context;
        this.albums = albums;
    }

    protected String getItemText(int position) {
        PhotoAlbumInfo album = (PhotoAlbumInfo) this.albums.get(position);
        String title = album.getTitle();
        if (album.getOwnerType() == OwnerType.GROUP) {
            return title + " " + LocalizationManager.getString(this.context, 2131165931);
        }
        return title;
    }

    protected String getCountText(int position) {
        return null;
    }

    public int getCount() {
        return this.albums.size();
    }

    public Object getItem(int position) {
        return this.albums.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }
}

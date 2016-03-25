package ru.ok.android.ui.adapters.music.artists;

import android.view.View;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.model.wmf.Artist;

public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final UrlImageView image;
    public final TextView textArtistName;

    public ViewHolder(View view) {
        super(view);
        this.textArtistName = (TextView) view.findViewById(2131624963);
        this.image = (UrlImageView) view.findViewById(C0263R.id.image);
    }

    public void setArtist(Artist artist) {
        this.textArtistName.setText(artist.name);
        if (!this.image.equalsUrl(artist.imageUrl)) {
            if (artist.imageUrl == null || artist.imageUrl.length() <= 0) {
                this.image.setImageResource(2130837650);
            } else {
                ImageViewManager.getInstance().displayImage(artist.imageUrl, this.image, null);
            }
        }
    }
}

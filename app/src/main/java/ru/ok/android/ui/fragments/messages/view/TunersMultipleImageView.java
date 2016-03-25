package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.model.wmf.Artist;

public class TunersMultipleImageView extends BaseMultipleUrlImageView {
    private final List<Artist> artists;

    public TunersMultipleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.artists = new ArrayList();
    }

    protected void processConfig() {
        if (this.multiple.booleanValue()) {
            processMultiple(this.artists);
        } else {
            processSingle(this.artists);
        }
    }

    private void processMultiple(List<Artist> artists) {
        int i = 0;
        while (i < artists.size() && i < getChildCount()) {
            Artist artist = (Artist) artists.get(i);
            UrlImageView child = (UrlImageView) getChildAt(i);
            child.setVisibility(0);
            ImageViewManager.getInstance().displayImage(artist.imageUrl, child, 2130838321, this.blocker);
            i++;
        }
        for (int childIndex = i; childIndex < getChildCount(); childIndex++) {
            getChildAt(childIndex).setVisibility(4);
        }
    }

    private void processSingle(List<Artist> artists) {
        if (artists.size() > 0) {
            UrlImageView child = (UrlImageView) getChildAt(0);
            child.setVisibility(0);
            ImageViewManager.getInstance().displayImage(((Artist) artists.get(0)).imageUrl, child, 2130838728, this.blocker);
            return;
        }
        child = (UrlImageView) getChildAt(0);
        child.setVisibility(0);
        child.setUrl("");
        child.setImageResource(2130838728);
    }

    public int getDefaultImgRes() {
        return 2130838728;
    }

    public List<Artist> getArtists() {
        return this.artists;
    }
}

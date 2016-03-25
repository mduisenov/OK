package ru.ok.android.ui.fragments.handlers;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoBackgroundRelativeLayout;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.model.wmf.Album;

public class AlbumBestMatchHandler extends BaseBestMatchHandler {
    private Album album;
    private UrlImageView image;
    private TextView textAlbumName;
    private TextView textEnsembleName;

    public /* bridge */ /* synthetic */ void blurBackground(Uri x0) {
        super.blurBackground(x0);
    }

    public /* bridge */ /* synthetic */ void onDestroyView() {
        super.onDestroyView();
    }

    public void setData(Album album) {
        this.album = album;
        updateViewsAlbumData();
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mainView = (FrescoBackgroundRelativeLayout) inflater.inflate(2130903248, null);
        this.textAlbumName = (TextView) this.mainView.findViewById(2131624961);
        this.textEnsembleName = (TextView) this.mainView.findViewById(2131624962);
        this.image = (UrlImageView) this.mainView.findViewById(C0263R.id.image);
        updateViewsAlbumData();
        return this.mainView;
    }

    private void updateViewsAlbumData() {
        if (this.album != null && this.textAlbumName != null) {
            this.textAlbumName.setText(this.album.name);
            this.textEnsembleName.setText(this.album.ensemble);
            if (this.album.imageUrl == null || this.album.imageUrl.length() <= 0) {
                this.image.setImageResource(2130838690);
                blurBackground(FrescoOdkl.uriFromResId(2130838690));
                return;
            }
            ImageViewManager.getInstance().displayImage(this.album.imageUrl, this.image, null);
            blurBackground(Uri.parse(this.album.imageUrl));
        }
    }
}

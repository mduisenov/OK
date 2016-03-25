package ru.ok.android.ui.fragments.handlers;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoBackgroundRelativeLayout;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;

public class ArtistBestMatchHandler extends BaseBestMatchHandler implements OnClickListener {
    private Button albumsButton;
    private Artist artist;
    private UrlImageView image;
    private OnSelectArtistAlbumsListener onSelectArtistAlbumsListener;
    private OnSelectArtistRadioTracksListener onSelectArtistRadioTracksListener;
    private Button radioButton;
    private TextView textArtistName;

    public interface OnSelectArtistAlbumsListener {
        void onSelectArtistAlbums(Artist artist);
    }

    public interface OnSelectArtistRadioTracksListener {
        void onSelectArtistRadio(Artist artist);
    }

    public /* bridge */ /* synthetic */ void blurBackground(Uri x0) {
        super.blurBackground(x0);
    }

    public /* bridge */ /* synthetic */ void onDestroyView() {
        super.onDestroyView();
    }

    public void setOnSelectArtistRadioTracksListener(OnSelectArtistRadioTracksListener onSelectArtistRadioTracksListener) {
        this.onSelectArtistRadioTracksListener = onSelectArtistRadioTracksListener;
    }

    public void setOnSelectArtistAlbumsListener(OnSelectArtistAlbumsListener onSelectArtistAlbumsListener) {
        this.onSelectArtistAlbumsListener = onSelectArtistAlbumsListener;
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mainView = (FrescoBackgroundRelativeLayout) LocalizationManager.inflate(inflater.getContext(), 2130903250, null, false);
        this.textArtistName = (TextView) this.mainView.findViewById(2131624963);
        this.image = (UrlImageView) this.mainView.findViewById(C0263R.id.image);
        this.radioButton = (Button) this.mainView.findViewById(2131624965);
        this.albumsButton = (Button) this.mainView.findViewById(2131624964);
        initListeners();
        return this.mainView;
    }

    private void initListeners() {
        this.radioButton.setOnClickListener(this);
        this.albumsButton.setOnClickListener(this);
    }

    public void setData(Artist bestMatchArtist) {
        this.artist = bestMatchArtist;
        this.textArtistName.setText(bestMatchArtist.name);
        if (bestMatchArtist.imageUrl == null || bestMatchArtist.imageUrl.length() <= 0) {
            this.image.setImageResource(2130837650);
            blurBackground(FrescoOdkl.uriFromResId(2130837650));
            return;
        }
        ImageViewManager.getInstance().displayImage(bestMatchArtist.imageUrl, this.image, 2130837650, null);
        blurBackground(Uri.parse(bestMatchArtist.imageUrl));
    }

    public void onClick(View view) {
        if (view == this.radioButton) {
            if (this.artist != null && this.onSelectArtistRadioTracksListener != null) {
                this.onSelectArtistRadioTracksListener.onSelectArtistRadio(this.artist);
            }
        } else if (view == this.albumsButton && this.artist != null && this.onSelectArtistAlbumsListener != null) {
            this.onSelectArtistAlbumsListener.onSelectArtistAlbums(this.artist);
        }
    }
}

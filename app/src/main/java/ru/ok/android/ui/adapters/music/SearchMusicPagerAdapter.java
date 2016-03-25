package ru.ok.android.ui.adapters.music;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public class SearchMusicPagerAdapter extends PagerAdapter {
    private View albumView;
    private ViewSwitcher albumViewSwitcher;
    private View albumsView;
    private View artistView;
    private ViewSwitcher artistViewSwitcher;
    private View artistsView;
    private Context context;
    private View musicView;
    private List<View> pages;

    public SearchMusicPagerAdapter(Context context, View musicView, View albumsView, View artistsView, View albumView, View artistView) {
        this.context = context;
        this.musicView = musicView;
        this.albumsView = albumsView;
        this.artistsView = artistsView;
        this.albumView = albumView;
        this.artistView = artistView;
        this.albumViewSwitcher = new ViewSwitcher(context);
        this.artistViewSwitcher = new ViewSwitcher(context);
        this.pages = Collections.synchronizedList(new ArrayList());
        this.pages.add(musicView);
        this.artistViewSwitcher.addView(artistView);
        this.artistViewSwitcher.addView(artistsView);
        this.pages.add(this.artistViewSwitcher);
        this.albumViewSwitcher.addView(albumView);
        this.albumViewSwitcher.addView(albumsView);
        this.pages.add(this.albumViewSwitcher);
    }

    public void setBestMatchArtistResult() {
        if (this.artistViewSwitcher.getNextView() == this.artistView) {
            this.artistViewSwitcher.showNext();
        }
        if (this.albumViewSwitcher.getNextView() == this.albumsView) {
            this.albumViewSwitcher.showNext();
        }
    }

    public void setBestMatchAlbumsResult() {
        if (this.albumViewSwitcher.getNextView() == this.albumView) {
            this.albumViewSwitcher.showNext();
        }
        if (this.artistViewSwitcher.getNextView() == this.artistsView) {
            this.artistViewSwitcher.showNext();
        }
    }

    public void setNoBestMatch() {
        if (this.artistViewSwitcher.getCurrentView() == this.artistView) {
            this.artistViewSwitcher.showNext();
        }
        if (this.albumViewSwitcher.getCurrentView() == this.albumView) {
            this.albumViewSwitcher.showNext();
        }
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        View v = (View) this.pages.get(position);
        collection.addView(v, 0);
        return v;
    }

    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public int getCount() {
        return this.pages.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void finishUpdate(View arg0) {
    }

    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    public Parcelable saveState() {
        return null;
    }

    public void startUpdate(View arg0) {
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case RECEIVED_VALUE:
                return LocalizationManager.getString(this.context, 2131166611);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return LocalizationManager.getString(this.context, 2131165408);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return LocalizationManager.getString(this.context, 2131165374);
            default:
                return LocalizationManager.getString(this.context, 2131166611);
        }
    }
}

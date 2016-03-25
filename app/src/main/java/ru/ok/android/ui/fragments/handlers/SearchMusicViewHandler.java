package ru.ok.android.ui.fragments.handlers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.adapters.music.SearchMusicPagerAdapter;
import ru.ok.android.ui.custom.indicator.PageIndicator;
import ru.ok.android.utils.controls.music.MusicPageSelectListener;

public class SearchMusicViewHandler {
    private AlbumPlayListHandler albumPlayListHandler;
    private AlbumsMusicViewHandler albumsHandler;
    private ArtistPlayListHandler artistPlayListHandler;
    private ArtistsMusicViewHandler artistsMusicViewHandler;
    private Activity context;
    private PageIndicator mIndicator;
    private ViewGroup mMainView;
    protected MusicFragmentMode mMode;
    private ViewPager mPager;
    private MusicPageSelectListener musicPageSelectListener;
    private MusicPlayListHandler musicPlayListHandler;
    private OnPageChangeHolder pageChangeHolder;
    private SearchMusicPagerAdapter pagerAdapter;

    /* renamed from: ru.ok.android.ui.fragments.handlers.SearchMusicViewHandler.1 */
    class C08191 implements Runnable {
        C08191() {
        }

        public void run() {
            SearchMusicViewHandler.this.mPager.setCurrentItem(2);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.handlers.SearchMusicViewHandler.2 */
    class C08202 implements Runnable {
        C08202() {
        }

        public void run() {
            SearchMusicViewHandler.this.mPager.setCurrentItem(0);
        }
    }

    class OnPageChangeHolder implements OnPageChangeListener {
        OnPageChangeHolder() {
        }

        public void onPageScrolled(int i, float v, int i1) {
        }

        public void onPageSelected(int pageNumber) {
            if (pageNumber == 0) {
                notifyShowMultiAddDeletePage();
            } else {
                notifyShowSimplePage();
            }
        }

        public void onPageScrollStateChanged(int i) {
        }

        public void notifyShowMultiAddDeletePage() {
            if (SearchMusicViewHandler.this.musicPageSelectListener != null) {
                SearchMusicViewHandler.this.musicPageSelectListener.onSelectMultiAddDeletePage();
            }
        }

        public void notifyShowSimplePage() {
            if (SearchMusicViewHandler.this.musicPageSelectListener != null) {
                SearchMusicViewHandler.this.musicPageSelectListener.onSelectSimplePage();
            }
        }
    }

    public SearchMusicViewHandler(MusicFragmentMode mode, Activity context) {
        this.context = context;
        this.mMode = mode;
    }

    public void setMusicPageSelectListener(MusicPageSelectListener listener) {
        this.musicPageSelectListener = listener;
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mMainView = (ViewGroup) inflater.inflate(2130903427, container, false);
        this.mPager = (ViewPager) this.mMainView.findViewById(C0263R.id.pager);
        this.mIndicator = (PageIndicator) this.mMainView.findViewById(C0263R.id.indicator);
        this.artistPlayListHandler = new ArtistPlayListHandler(this.mMode, this.context);
        this.albumPlayListHandler = new AlbumPlayListHandler(this.mMode, this.context);
        this.musicPlayListHandler = new MusicPlayListHandler(this.mMode, this.context);
        this.albumsHandler = new AlbumsMusicViewHandler(this.context, this.mMode);
        this.artistsMusicViewHandler = new ArtistsMusicViewHandler(this.context);
        this.pagerAdapter = new SearchMusicPagerAdapter(this.context, this.musicPlayListHandler.createView(inflater, null, savedInstanceState), this.albumsHandler.createView(inflater, null, savedInstanceState), this.artistsMusicViewHandler.createView(inflater, null, savedInstanceState), this.albumPlayListHandler.createView(inflater, null, savedInstanceState), this.artistPlayListHandler.createView(inflater, null, savedInstanceState));
        this.mPager.setAdapter(this.pagerAdapter);
        this.mPager.setCurrentItem(0);
        this.pageChangeHolder = new OnPageChangeHolder();
        this.mIndicator.setViewPager(this.mPager);
        this.mIndicator.setOnPageChangeListener(this.pageChangeHolder);
        return this.mMainView;
    }

    public void onDestroyView() {
        this.musicPlayListHandler.onDestroyView();
        this.albumsHandler.onDestroyView();
        this.artistsMusicViewHandler.onDestroyView();
        this.albumPlayListHandler.onDestroyView();
        this.artistPlayListHandler.onDestroyView();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.musicPlayListHandler.setOnItemLongClickListener(listener);
    }

    public MusicPlayListHandler getMusicPlayListHandler() {
        return this.musicPlayListHandler;
    }

    public AlbumsMusicViewHandler getAlbumsHandler() {
        return this.albumsHandler;
    }

    public ArtistsMusicViewHandler getArtistsMusicViewHandler() {
        return this.artistsMusicViewHandler;
    }

    public AlbumPlayListHandler getAlbumPlayListHandler() {
        return this.albumPlayListHandler;
    }

    public ArtistPlayListHandler getArtistPlayListHandler() {
        return this.artistPlayListHandler;
    }

    public void setSelectionArtistsPage() {
        this.mPager.setCurrentItem(1);
    }

    public void setSelectionAlbumsPage() {
        this.mPager.post(new C08191());
    }

    public void setSelectionTracksPage() {
        this.mPager.post(new C08202());
    }

    public void showSearchView() {
        if (this.mPager.getCurrentItem() == 0) {
            this.pageChangeHolder.notifyShowMultiAddDeletePage();
        } else {
            this.pageChangeHolder.notifyShowSimplePage();
        }
    }

    public SearchMusicPagerAdapter getPagerAdapter() {
        return this.pagerAdapter;
    }

    public void notifyData() {
        this.pagerAdapter.notifyDataSetChanged();
    }
}

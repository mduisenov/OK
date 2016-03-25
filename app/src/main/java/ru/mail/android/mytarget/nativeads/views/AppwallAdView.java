package ru.mail.android.mytarget.nativeads.views;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;

public class AppwallAdView extends FrameLayout implements OnGlobalLayoutListener, OnScrollListener, OnItemClickListener {
    private BannerClickListener bannerClickListener;
    private BannerVisibilityListener bannerVisibilityListener;
    private ListView listView;
    private HashMap<String, Boolean> viewMap;
    private ViewTreeObserver viewTreeObserver;

    public interface BannerClickListener {
        void onBannerClick(AppwallAdTeaserView appwallAdTeaserView);
    }

    public interface BannerVisibilityListener {
        void onBannersShown(List<NativeAppwallBanner> list);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        countVisibleBanners();
    }

    public AppwallAdView(Context context) {
        super(context);
        this.viewMap = new HashMap();
        initLayout(context);
        setVerticalFadingEdgeEnabled(false);
        setBackgroundColor(-1);
    }

    private void initLayout(Context context) {
        int paddingTop = (int) TypedValue.applyDimension(1, 4.0f, context.getResources().getDisplayMetrics());
        int paddingBottom = (int) TypedValue.applyDimension(1, 4.0f, context.getResources().getDisplayMetrics());
        this.listView = new ListView(context);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalFadingEdgeEnabled(false);
        this.listView.setOnItemClickListener(this);
        this.listView.setOnScrollListener(this);
        this.listView.setPadding(0, paddingTop, 0, paddingBottom);
        this.listView.setClipToPadding(false);
        addView(this.listView, -1, -1);
        this.listView.setBackgroundColor(-1118482);
    }

    public void setupView(NativeAppwallAd appwalAd) {
        this.listView.setAdapter(new AppwallAdapter(this, getContext(), appwalAd.getBanners()));
    }

    public void notifyDataSetChanged() {
        if (this.listView != null) {
            ((AppwallAdapter) this.listView.getAdapter()).notifyDataSetChanged();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        countVisibleBanners();
        this.viewTreeObserver = getViewTreeObserver();
        if (this.viewTreeObserver.isAlive()) {
            this.viewTreeObserver.addOnGlobalLayoutListener(this);
        }
    }

    public void onGlobalLayout() {
        countVisibleBanners();
    }

    private void countVisibleBanners() {
        if (this.listView != null && this.listView.getAdapter() != null) {
            int firstVisibleRow = this.listView.getFirstVisiblePosition();
            int lastVisibleRow = this.listView.getLastVisiblePosition();
            List<NativeAppwallBanner> visibleBanners = new ArrayList();
            for (int i = firstVisibleRow; i <= lastVisibleRow; i++) {
                NativeAppwallBanner banner = (NativeAppwallBanner) this.listView.getAdapter().getItem(i);
                if (this.viewMap.get(banner.getId()) == null) {
                    visibleBanners.add(banner);
                    this.viewMap.put(banner.getId(), Boolean.valueOf(true));
                }
            }
            if (visibleBanners.size() > 0 && this.bannerVisibilityListener != null) {
                this.bannerVisibilityListener.onBannersShown(visibleBanners);
            }
        }
    }

    public void removeBanners() {
        this.listView.setAdapter(null);
        if (this.viewTreeObserver != null && this.viewTreeObserver.isAlive()) {
            this.viewTreeObserver.removeGlobalOnLayoutListener(this);
        }
    }

    public void setBannerVisibilityListener(BannerVisibilityListener bannerVisibilityListener) {
        this.bannerVisibilityListener = bannerVisibilityListener;
    }

    public void setBannerClickListener(BannerClickListener bannerClickListener) {
        this.bannerClickListener = bannerClickListener;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        AppwallCardPlaceholder placeholder = (AppwallCardPlaceholder) view;
        if (this.bannerClickListener != null) {
            this.bannerClickListener.onBannerClick(placeholder.getView());
        }
    }
}

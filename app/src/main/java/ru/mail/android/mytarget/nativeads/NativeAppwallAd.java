package ru.mail.android.mytarget.nativeads;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ru.mail.android.mytarget.Tracer;
import ru.mail.android.mytarget.ads.CustomParams;
import ru.mail.android.mytarget.ads.MyTargetActivity;
import ru.mail.android.mytarget.core.AdParams;
import ru.mail.android.mytarget.core.async.Sender;
import ru.mail.android.mytarget.core.async.StoreDataRequest;
import ru.mail.android.mytarget.core.facades.AbstractAd;
import ru.mail.android.mytarget.core.models.AdData;
import ru.mail.android.mytarget.core.models.banners.AppwallBanner;
import ru.mail.android.mytarget.core.models.banners.Banner;
import ru.mail.android.mytarget.core.models.sections.AppwallSection;
import ru.mail.android.mytarget.core.net.AppwallBannerImageLoader;
import ru.mail.android.mytarget.core.net.AppwallBannerImageLoader.AppwallBannerImageLoaderListener;
import ru.mail.android.mytarget.core.ui.InterstitialAdDialog;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.mail.android.mytarget.nativeads.views.AppwallAdTeaserView;
import ru.mail.android.mytarget.nativeads.views.AppwallAdView;
import ru.mail.android.mytarget.nativeads.views.AppwallAdView.BannerClickListener;
import ru.mail.android.mytarget.nativeads.views.AppwallAdView.BannerVisibilityListener;

public class NativeAppwallAd extends AbstractAd {
    private InterstitialAdDialog adDialog;
    private final AdParams adParams;
    private List<NativeAppwallBanner> appwallBannerList;
    private AppwallSection appwallSection;
    private boolean autoloadImages;
    private BannerClickListener bannerClickListener;
    private Map<String, NativeAppwallBanner> bannerIdMap;
    private int cachePeriod;
    private OnClickListener clickListener;
    private OnDismissListener dialogDismissListener;
    private AppwallAdView externalAppwallAdView;
    private boolean hideStatusBarInDialog;
    private AppwallAdListener listener;
    private Map<String, AppwallBannerImageLoader> loaderMap;
    private String title;
    private int titleBackgorundColor;
    private int titleSupplementaryColor;
    private int titleTextColor;
    private BannerVisibilityListener visibilityListener;

    /* renamed from: ru.mail.android.mytarget.nativeads.NativeAppwallAd.1 */
    class C01711 implements AppwallBannerImageLoaderListener {
        C01711() {
        }

        public void onComplete(NativeAppwallBanner loadedBanner) {
            if (NativeAppwallAd.this.loaderMap.containsKey(loadedBanner.getId())) {
                NativeAppwallAd.this.loaderMap.remove(loadedBanner.getId());
            }
            if (NativeAppwallAd.this.loaderMap.size() == 0) {
                NativeAppwallAd.this.internalOnLoad();
            }
        }
    }

    /* renamed from: ru.mail.android.mytarget.nativeads.NativeAppwallAd.2 */
    class C01722 implements OnClickListener {
        C01722() {
        }

        public void onClick(View v) {
            if (v.getTag() == null || !(v.getTag() instanceof String)) {
                Tracer.m35d("Banner " + v + " is not registered with AppwallAd");
                return;
            }
            NativeAppwallAd.this.doBannerClick((NativeAppwallBanner) NativeAppwallAd.this.bannerIdMap.get((String) v.getTag()));
        }
    }

    /* renamed from: ru.mail.android.mytarget.nativeads.NativeAppwallAd.3 */
    class C01733 implements BannerClickListener {
        C01733() {
        }

        public void onBannerClick(AppwallAdTeaserView bannerView) {
            NativeAppwallAd.this.doBannerClick(bannerView.getBanner());
            if (NativeAppwallAd.this.externalAppwallAdView != null) {
                NativeAppwallAd.this.externalAppwallAdView.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: ru.mail.android.mytarget.nativeads.NativeAppwallAd.4 */
    class C01744 implements BannerVisibilityListener {
        C01744() {
        }

        public void onBannersShown(List<NativeAppwallBanner> banners) {
            NativeAppwallAd.this.handleBannersShow(banners);
        }
    }

    /* renamed from: ru.mail.android.mytarget.nativeads.NativeAppwallAd.5 */
    class C01755 implements OnDismissListener {
        C01755() {
        }

        public void onDismiss(DialogInterface dialogInterface) {
            InterstitialAdDialog dialog = (InterstitialAdDialog) dialogInterface;
            dialog.setOnDismissListener(null);
            if (dialog == NativeAppwallAd.this.adDialog) {
                NativeAppwallAd.this.adDialog = null;
                if (NativeAppwallAd.this.listener != null) {
                    NativeAppwallAd.this.listener.onDismissDialog(NativeAppwallAd.this);
                }
            }
        }
    }

    public interface AppwallAdListener {
        void onClick(NativeAppwallBanner nativeAppwallBanner, NativeAppwallAd nativeAppwallAd);

        void onDismissDialog(NativeAppwallAd nativeAppwallAd);

        void onLoad(NativeAppwallAd nativeAppwallAd);

        void onNoAd(String str, NativeAppwallAd nativeAppwallAd);
    }

    public NativeAppwallAd(int slotId, Context context) {
        this(slotId, context, null);
    }

    public NativeAppwallAd(int slotId, Context context, CustomParams customParams) {
        this.appwallBannerList = new ArrayList();
        this.title = "Apps";
        this.titleBackgorundColor = -12232093;
        this.titleSupplementaryColor = -13220531;
        this.titleTextColor = -1;
        this.cachePeriod = 86400000;
        this.hideStatusBarInDialog = false;
        this.autoloadImages = true;
        this.bannerIdMap = new HashMap();
        this.loaderMap = new ConcurrentHashMap();
        this.clickListener = new C01722();
        this.bannerClickListener = new C01733();
        this.visibilityListener = new C01744();
        this.dialogDismissListener = new C01755();
        this.adParams = new AdParams(slotId);
        this.adParams.addFormat("showcaseApps");
        this.adParams.addFormat("showcaseGames");
        this.adParams.addFormat("appwall");
        if (customParams != null) {
            this.adParams.setCustomParams(customParams);
        }
        Tracer.m37i("NativeAppwallAd created. Version: 4.1.7");
        init(this.adParams, context);
    }

    public void load() {
        this.adapter.update(this.cachePeriod <= 0);
    }

    protected void onLoad(AdData data) {
        if (data.hasBanners()) {
            this.appwallBannerList = new ArrayList();
            AppwallSection section = (AppwallSection) data.getSection("appwall");
            this.appwallBannerList.addAll(getBannersFromSection(section));
            if (this.appwallBannerList.size() == 0) {
                section = (AppwallSection) data.getSection("showcaseApps");
                this.appwallBannerList.addAll(getBannersFromSection(section));
                if (this.appwallBannerList.size() == 0) {
                    section = (AppwallSection) data.getSection("showcaseGames");
                    this.appwallBannerList.addAll(getBannersFromSection(section));
                }
            }
            this.appwallSection = section;
            if (this.autoloadImages) {
                doAutoLoadImages();
                return;
            } else if (this.appwallBannerList.size() != 0) {
                internalOnLoad();
                return;
            } else {
                internalOnNoAd();
                return;
            }
        }
        internalOnNoAd();
    }

    private void internalOnLoad() {
        if (this.cachePeriod > 0) {
            Sender.addRequest(new StoreDataRequest((long) this.cachePeriod, this.adParams.getSlotId(), this.adData.getRawData().toString()), this.context);
        }
        if (this.listener != null) {
            this.listener.onLoad(this);
        }
    }

    private void internalOnNoAd() {
        if (this.listener != null) {
            this.listener.onNoAd("No ad", this);
        }
    }

    private void doAutoLoadImages() {
        AppwallBannerImageLoaderListener imageListener = new C01711();
        for (NativeAppwallBanner banner : this.appwallBannerList) {
            AppwallBannerImageLoader loader = new AppwallBannerImageLoader(banner, this.context);
            loader.setListener(imageListener);
            this.loaderMap.put(banner.getId(), loader);
        }
        for (AppwallBannerImageLoader loader2 : this.loaderMap.values()) {
            loader2.load();
        }
    }

    private List<NativeAppwallBanner> getBannersFromSection(AppwallSection section) {
        List<NativeAppwallBanner> bannerList = new ArrayList();
        if (section != null && section.getBannersCount() > 0) {
            Iterator i$ = section.getBanners().iterator();
            while (i$.hasNext()) {
                AppwallBanner banner = (AppwallBanner) i$.next();
                bannerList.add(banner);
                this.bannerIdMap.put(banner.getId(), banner);
            }
        }
        return bannerList;
    }

    protected void onLoadError(String error) {
        if (this.listener != null) {
            this.listener.onNoAd(error, this);
        }
    }

    public void show() {
        if (this.appwallBannerList.size() == 0) {
            Tracer.m37i("AppwallAd.show: No ad");
            return;
        }
        MyTargetActivity.ad = this;
        Intent intent = new Intent(this.context, MyTargetActivity.class);
        intent.setAction("ru.mail.android.mytarget.actions.appwall");
        this.context.startActivity(intent);
    }

    public void showDialog() {
        if (this.adDialog != null && this.adDialog.isShowing()) {
            Tracer.m37i("AppwallAd.showDialog: dialog already showing");
        } else if (this.appwallBannerList.size() == 0) {
            Tracer.m37i("AppwallAd.showDialog: No ad");
        } else {
            this.adDialog = new InterstitialAdDialog(this, this.hideStatusBarInDialog, this.context);
            this.adDialog.setOnDismissListener(this.dialogDismissListener);
            this.adDialog.show();
        }
    }

    public void dismissDialog() {
        if (this.adDialog != null && this.adDialog.isShowing()) {
            this.adDialog.dismiss();
        }
    }

    public boolean hasNotifications() {
        for (NativeAppwallBanner banner : this.appwallBannerList) {
            if (banner.isHasNotification()) {
                return true;
            }
        }
        return false;
    }

    public void registerAppwallAdView(AppwallAdView appsListView) {
        appsListView.setBannerClickListener(this.bannerClickListener);
        appsListView.setBannerVisibilityListener(this.visibilityListener);
        this.externalAppwallAdView = appsListView;
    }

    public void unregisterAppwallAdView(AppwallAdView view) {
        if (view != this.externalAppwallAdView) {
            Tracer.m37i("No such AppwallAdView registered");
            return;
        }
        this.externalAppwallAdView.setBannerClickListener(null);
        this.externalAppwallAdView.setBannerVisibilityListener(null);
        this.externalAppwallAdView = null;
    }

    public List<NativeAppwallBanner> getBanners() {
        return this.appwallBannerList;
    }

    public void setListener(AppwallAdListener listener) {
        this.listener = listener;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void handleBannerClick(NativeAppwallBanner banner) {
        doBannerClick(banner);
    }

    public String prepareBannerClickLink(NativeAppwallBanner banner) {
        String link = this.adData.prepareClickLink((Banner) banner, this.context);
        this.adData.handleNotification(this.adParams, this.appwallSection, banner.getId(), this.context);
        return link;
    }

    public void handleBannersShow(List<NativeAppwallBanner> banners) {
        this.adData.adShowsHandler((List) banners, this.appwallSection, this.context);
    }

    public void handleBannerShow(NativeAppwallBanner banner) {
        this.adData.adShowsHandler(banner.getId(), this.appwallSection, this.context);
    }

    private void doBannerClick(NativeAppwallBanner banner) {
        if (banner == null) {
            Tracer.m35d("Something horrible happened");
        } else if (this.adData == null) {
            Tracer.m35d("AdData is null, click will not be processed.");
        } else {
            try {
                this.adData.handleClick((Banner) banner, this.context);
                this.adData.handleNotification(this.adParams, this.appwallSection, banner.getId(), this.context);
            } catch (Throwable throwable) {
                Tracer.m35d(throwable.toString());
            }
            if (this.listener != null) {
                this.listener.onClick(banner, this);
            }
        }
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setTitleBackgorundColor(int titleBackgorundColor) {
        this.titleBackgorundColor = titleBackgorundColor;
    }

    public void setTitleSupplementaryColor(int titleSupplementaryColor) {
        this.titleSupplementaryColor = titleSupplementaryColor;
    }

    public int getTitleBackgorundColor() {
        return this.titleBackgorundColor;
    }

    public int getTitleSupplementaryColor() {
        return this.titleSupplementaryColor;
    }

    public int getTitleTextColor() {
        return this.titleTextColor;
    }

    public long getCachePeriod() {
        if (this.adParams == null) {
            return 0;
        }
        return this.adParams.getCachePeriod();
    }

    public void setCachePeriod(long cachePeriod) {
        if (this.adParams != null) {
            this.adParams.setCachePeriod(cachePeriod);
        }
    }

    public boolean isHideStatusBarInDialog() {
        return this.hideStatusBarInDialog;
    }

    public void setHideStatusBarInDialog(boolean hideStatusBarInDialog) {
        this.hideStatusBarInDialog = hideStatusBarInDialog;
    }
}

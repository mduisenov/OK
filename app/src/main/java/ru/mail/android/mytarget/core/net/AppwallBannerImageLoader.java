package ru.mail.android.mytarget.core.net;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.mail.android.mytarget.core.net.ImageLoader.LoaderListener;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.mail.android.mytarget.nativeads.models.ImageData;

public class AppwallBannerImageLoader {
    private NativeAppwallBanner banner;
    private Context context;
    private AppwallBannerImageLoaderListener listener;
    private ImageLoader loader;
    private LoaderListener loaderListener;
    private Map<String, ImageData> urlsImageDataMap;

    public interface AppwallBannerImageLoaderListener {
        void onComplete(NativeAppwallBanner nativeAppwallBanner);
    }

    public AppwallBannerImageLoader(NativeAppwallBanner banner, Context context) {
        this.urlsImageDataMap = new HashMap();
        this.loaderListener = new 1(this);
        this.banner = banner;
        this.context = context;
    }

    public void load() {
        ImageData statusIcon = this.banner.getStatusIcon();
        ImageData coinsIcon = this.banner.getCoinsIcon();
        ImageData gotoAppIcon = this.banner.getGotoAppIcon();
        ImageData icon = this.banner.getIcon();
        ImageData labelIcon = this.banner.getLabelIcon();
        ImageData bubbleIcon = this.banner.getBubbleIcon();
        ImageData highlightIcon = this.banner.getItemHighlightIcon();
        if (statusIcon != null) {
            this.urlsImageDataMap.put(statusIcon.getUrl(), statusIcon);
        }
        if (coinsIcon != null) {
            this.urlsImageDataMap.put(coinsIcon.getUrl(), coinsIcon);
        }
        if (gotoAppIcon != null) {
            this.urlsImageDataMap.put(gotoAppIcon.getUrl(), gotoAppIcon);
        }
        if (icon != null) {
            this.urlsImageDataMap.put(icon.getUrl(), icon);
        }
        if (labelIcon != null) {
            this.urlsImageDataMap.put(labelIcon.getUrl(), labelIcon);
        }
        if (bubbleIcon != null) {
            this.urlsImageDataMap.put(bubbleIcon.getUrl(), bubbleIcon);
        }
        if (highlightIcon != null) {
            this.urlsImageDataMap.put(highlightIcon.getUrl(), highlightIcon);
        }
        List<String> urlsList = new ArrayList();
        urlsList.addAll(this.urlsImageDataMap.keySet());
        this.loader = new ImageLoader();
        this.loader.setListener(this.loaderListener);
        this.loader.loadImages(urlsList, this.context);
    }

    public void setListener(AppwallBannerImageLoaderListener listener) {
        this.listener = listener;
    }
}

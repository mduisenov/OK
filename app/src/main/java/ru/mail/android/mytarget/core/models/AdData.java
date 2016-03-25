package ru.mail.android.mytarget.core.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.URLUtil;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.android.mytarget.Tracer;
import ru.mail.android.mytarget.core.AdParams;
import ru.mail.android.mytarget.core.async.Sender;
import ru.mail.android.mytarget.core.async.StoreDataRequest;
import ru.mail.android.mytarget.core.models.banners.Banner;
import ru.mail.android.mytarget.core.models.banners.NativeAdBanner;
import ru.mail.android.mytarget.core.models.sections.AppwallSection;
import ru.mail.android.mytarget.core.models.sections.NativeAdSection;
import ru.mail.android.mytarget.core.models.sections.Section;
import ru.mail.android.mytarget.core.utils.LruCache;
import ru.mail.android.mytarget.core.utils.UrlUtils;

public class AdData extends AbstractModel {
    public static final int EXCLUDED_ITEMS_SIZE = 10;
    private static LruCache<String, String> excluded;
    private long cachePeriod;
    private long expiredTime;
    private String html;
    private JSONObject rawData;
    private ArrayList<Section> sections;
    private String url;

    static {
        excluded = new LruCache(EXCLUDED_ITEMS_SIZE);
    }

    public static LruCache<String, String> getExcluded() {
        return excluded;
    }

    public AdditionalData getFirstAdditional() {
        AdditionalData additionalData = null;
        Iterator i$ = getSections().iterator();
        while (i$.hasNext()) {
            additionalData = ((Section) i$.next()).getFirstAdditional();
            if (additionalData != null) {
                break;
            }
        }
        return additionalData;
    }

    public void removeAdditionalData(AdditionalData additionalData) {
        Iterator i$ = getSections().iterator();
        while (i$.hasNext()) {
            if (((Section) i$.next()).removeAdditionalData(additionalData)) {
                return;
            }
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getRawData() {
        return this.rawData;
    }

    public void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }

    public boolean isExpired() {
        if (System.currentTimeMillis() > this.expiredTime) {
            return true;
        }
        return false;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public AdData(long cachePeriod) {
        this.sections = new ArrayList();
        this.cachePeriod = cachePeriod;
        this.expiredTime = System.currentTimeMillis() + cachePeriod;
    }

    public boolean addSection(Section section) {
        if (this.sections.contains(section)) {
            return false;
        }
        if (this.sections.size() == 0 || section.getIndex() == -1) {
            this.sections.add(section);
        } else {
            int count = 0;
            boolean added = false;
            Iterator i$ = this.sections.iterator();
            while (i$.hasNext()) {
                Section s = (Section) i$.next();
                if (s.getIndex() > section.getIndex() || s.getIndex() == -1) {
                    added = true;
                    this.sections.add(count, section);
                    break;
                }
                count++;
            }
            if (!added) {
                this.sections.add(section);
            }
        }
        return true;
    }

    public Section removeSection(String name) {
        Section section = getSection(name);
        if (section == null) {
            return null;
        }
        removeSection(section);
        return section;
    }

    public boolean removeSection(Section section) {
        return this.sections.remove(section);
    }

    public Section getSection(String name) {
        Iterator i$ = this.sections.iterator();
        while (i$.hasNext()) {
            Section section = (Section) i$.next();
            if (section.getName().equals(name)) {
                return section;
            }
        }
        return null;
    }

    public ArrayList<Section> getSections() {
        return new ArrayList(this.sections);
    }

    public boolean hasBanners() {
        Iterator i$ = this.sections.iterator();
        while (i$.hasNext()) {
            if (((Section) i$.next()).getBannersCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAdditioonals() {
        Iterator i$ = this.sections.iterator();
        while (i$.hasNext()) {
            if (!((Section) i$.next()).getAdditionalDatas().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void handleClick(Banner banner, Context context) {
        Intent intent = null;
        String bundleId = banner.getBundleId();
        if (!(bundleId == null || bundleId.equals(""))) {
            intent = context.getPackageManager().getLaunchIntentForPackage(bundleId);
        }
        if (intent != null) {
            Sender.addStat(banner.getStats(), ProductAction.ACTION_CLICK, context);
            String trackingUrl = banner.getTrackingLink();
            if (!(trackingUrl == null || !URLUtil.isNetworkUrl(trackingUrl) || UrlUtils.isKnownLocation(trackingUrl))) {
                Sender.addStat(trackingUrl, context);
            }
            if (!startAppByUrlscheme(banner, context)) {
                intent.addFlags(268435456);
                context.startActivity(intent);
                return;
            }
            return;
        }
        forwardUser(banner, context);
    }

    public String prepareClickLink(Banner banner, Context context) {
        Sender.addStat(banner.getStats(), ProductAction.ACTION_CLICK, context);
        return banner.getTrackingLink();
    }

    public boolean handleNotification(AdParams params, Section section, String bannerId, Context context) {
        boolean result = false;
        if (section instanceof AppwallSection) {
            AppwallSection appwallSection = (AppwallSection) section;
            if (appwallSection.getBannerHasNotification(bannerId)) {
                result = appwallSection.setBannerHasNotification(bannerId, false);
                updateJsonDataForBanner(section, bannerId);
                try {
                    this.rawData.put("html_wrapper", this.html);
                    String jsonString = this.rawData.toString();
                    this.rawData.remove("html_wrapper");
                    Sender.addRequest(new StoreDataRequest(this.cachePeriod, params.getSlotId(), jsonString), context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void updateJsonDataForBanner(Section section, String bannerId) {
        try {
            JSONArray bannerArray = this.rawData.getJSONObject(section.getName()).getJSONArray("banners");
            int count = bannerArray.length();
            for (int i = 0; i < count; i++) {
                JSONObject bannerObject = (JSONObject) bannerArray.get(i);
                if (bannerObject.getString("bannerID").equals(bannerId)) {
                    bannerObject.put("hasNotification", false);
                    Tracer.m35d("Changed notification in raw data for banner " + bannerId);
                }
            }
        } catch (JSONException e) {
            Tracer.m35d("Error updating cache notification for section=" + section.getName() + " and bannerId=" + bannerId + ", " + e);
        }
    }

    public void adShowsHandler(List<Banner> bannerList, Section section, Context context) {
        for (Banner banner : bannerList) {
            adShowsHandler(banner.getId(), section, context);
        }
    }

    public void adShowsHandler(String bannerId, Section section, Context context) {
        if (section.getName() != null) {
            Tracer.m35d("Ad shows. adId: " + bannerId + " in section " + section.getName());
        } else {
            Tracer.m35d("Ad shows. adId: " + bannerId);
        }
        sendStatForBanner(bannerId, "playbackStarted", section.getName(), context);
    }

    public void progressStatHandler(Banner banner, Set<ProgressStat> stats, float time, Context context) {
        Iterator<ProgressStat> iterator = stats.iterator();
        while (iterator.hasNext()) {
            Stat stat = (ProgressStat) iterator.next();
            if (stat.getValue() <= time) {
                Sender.addStat(stat, context);
                iterator.remove();
            }
        }
    }

    public void statHandler(Banner banner, String stat, Context context) {
        Sender.addStat(banner.getStats(), stat, context);
    }

    public void statHandler(Section section, String stat, Context context) {
        Sender.addStat(section.getStats(), stat, context);
    }

    @Deprecated
    public void adShowsHandler(String[] ids, String sectionName, Context context) {
        for (String id : ids) {
            if (sectionName != null) {
                Tracer.m35d("Ad shows. adId: " + id + " in section " + sectionName);
            } else {
                Tracer.m35d("Ad shows. adId: " + id);
            }
            sendStatForBanner(id, "playbackStarted", sectionName, context);
        }
    }

    public void adShowsHandler(Banner banner, Context context) {
        if (banner != null) {
            Tracer.m35d("Ad shows. adId: " + banner.getId());
            Sender.addStat(banner.getStats(), "playbackStarted", context);
        }
    }

    public void eventHandler(String event, String[] params, Context context) {
        Tracer.m35d("Event from app: " + event);
        int ln = params.length;
        if (ln > 0) {
            sendStatForBanner(params[0], event, ln > 1 ? params[1] : null, context);
        }
    }

    public void adClickHandler(String id, String sectionName, Context context) {
        if (sectionName != null) {
            Tracer.m35d("Ad clicked. adId: " + id + " in section " + sectionName);
        } else {
            Tracer.m35d("Ad clicked. adId: " + id);
        }
        sendStatForBanner(id, ProductAction.ACTION_CLICK, sectionName, context);
    }

    public void adClickHandler(Banner banner, Context context) {
        Sender.addStat(banner.getStats(), ProductAction.ACTION_CLICK, context);
    }

    private boolean startAppByUrlscheme(Banner banner, Context context) {
        String urlscheme = banner.getUrlScheme();
        if (!(urlscheme == null || urlscheme.equals(""))) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(urlscheme));
                intent.addFlags(268435456);
                context.startActivity(intent);
                return true;
            } catch (Throwable th) {
            }
        }
        return false;
    }

    private void forwardUser(Banner banner, Context context) {
        String finalUrl = banner.getFinalLink();
        String trackingUrl = banner.getTrackingLink();
        boolean isFinalUrlHandled = false;
        if (!(finalUrl == null || finalUrl.equals("") || !UrlUtils.isKnownLocation(finalUrl))) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(finalUrl));
                intent.addFlags(268435456);
                context.startActivity(intent);
                isFinalUrlHandled = true;
            } catch (Throwable e) {
                Tracer.m35d(e.getMessage());
            }
        }
        if (isFinalUrlHandled) {
            Sender.addStat(banner.getStats(), ProductAction.ACTION_CLICK, context);
            if (trackingUrl != null && URLUtil.isNetworkUrl(trackingUrl) && !UrlUtils.isKnownLocation(trackingUrl)) {
                Sender.addStat(trackingUrl, context);
            }
        } else if (trackingUrl != null && !trackingUrl.equals("")) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse(trackingUrl));
                intent.addFlags(268435456);
                context.startActivity(intent);
                Sender.addStat(banner.getStats(), ProductAction.ACTION_CLICK, context);
            } catch (Throwable e2) {
                Tracer.m35d(e2.getMessage());
            }
        }
    }

    private void sendStatForBanner(String bannerId, String statType, String sectionName, Context context) {
        Banner banner;
        if (sectionName != null) {
            Section section = getSection(sectionName);
            if (section != null) {
                banner = section.getBanner(bannerId);
                if (banner != null) {
                    Sender.addStat(banner.getStats(), statType, context);
                    return;
                }
                return;
            }
            return;
        }
        Iterator i$ = this.sections.iterator();
        while (i$.hasNext()) {
            banner = ((Section) i$.next()).getBanner(bannerId);
            if (banner != null) {
                Sender.addStat(banner.getStats(), statType, context);
            }
        }
    }

    public void checkExclude() {
        Section s = getSection("nativeads");
        if (s instanceof NativeAdSection) {
            NativeAdSection section = (NativeAdSection) s;
            if (section.getBannersCount() > 0) {
                Iterator i$ = section.getBanners().iterator();
                while (i$.hasNext()) {
                    NativeAdBanner nextBanner = (NativeAdBanner) i$.next();
                    excluded.put(nextBanner.getId(), nextBanner.getId());
                }
            }
        }
    }
}

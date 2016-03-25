package ru.mail.android.mytarget.core.models.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.mail.android.mytarget.core.models.AbstractModel;
import ru.mail.android.mytarget.core.models.AdditionalData;
import ru.mail.android.mytarget.core.models.Stat;
import ru.mail.android.mytarget.core.models.banners.Banner;

public abstract class AbstractSection<T extends Banner> extends AbstractModel implements Section<T> {
    private ArrayList<AdditionalData> additionalDatas;
    protected String advertisingLabel;
    protected ArrayList<T> banners;
    protected int bannersCount;
    protected int index;
    protected String infoUrl;
    protected String name;
    protected ArrayList<Stat> stats;
    protected String type;

    public String getType() {
        return this.type;
    }

    public int getBannersCount() {
        return this.bannersCount;
    }

    public String getInfoUrl() {
        return this.infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public void setAdvertisingLabel(String advertisingLabel) {
        this.advertisingLabel = advertisingLabel;
    }

    public String getAdvertisingLabel() {
        return this.advertisingLabel;
    }

    public AbstractSection(String type, String name, int index) {
        this.additionalDatas = new ArrayList();
        this.bannersCount = 0;
        this.banners = new ArrayList();
        this.stats = new ArrayList();
        this.type = type;
        this.name = name;
        this.index = index;
    }

    public T getBanner(String id) {
        Iterator i$ = this.banners.iterator();
        while (i$.hasNext()) {
            Banner banner = (Banner) i$.next();
            if (banner.getId().equals(id)) {
                return banner;
            }
        }
        return null;
    }

    public ArrayList<T> getBanners() {
        return new ArrayList(this.banners);
    }

    public boolean removeBanner(T banner) {
        return this.banners.remove(banner);
    }

    public T removeBanner(String id) {
        Banner banner = getBanner(id);
        if (banner == null) {
            return null;
        }
        removeBanner(banner);
        return banner;
    }

    public synchronized boolean addStat(Stat stat) {
        boolean z;
        if (this.stats.contains(stat)) {
            z = false;
        } else {
            this.stats.add(stat);
            z = true;
        }
        return z;
    }

    public ArrayList<Stat> getStats() {
        return new ArrayList(this.stats);
    }

    public synchronized void addAdditionalData(AdditionalData additionalData) {
        this.additionalDatas.add(additionalData);
    }

    public ArrayList<AdditionalData> getAdditionalDatas() {
        return new ArrayList(this.additionalDatas);
    }

    public AdditionalData getAdditionalData(String url) {
        Iterator i$ = this.additionalDatas.iterator();
        while (i$.hasNext()) {
            AdditionalData additionalData = (AdditionalData) i$.next();
            if (additionalData.getUrl().equals(url)) {
                return additionalData;
            }
        }
        return null;
    }

    public void removeAdditionalData(String url) {
        Iterator<AdditionalData> iterator = this.additionalDatas.iterator();
        while (iterator.hasNext()) {
            if (((AdditionalData) iterator.next()).getUrl().equals(url)) {
                iterator.remove();
                return;
            }
        }
    }

    public void addStats(List<Stat> statList) {
        for (Stat stat : statList) {
            addStat(stat);
        }
    }

    public AdditionalData getFirstAdditional() {
        if (this.additionalDatas.isEmpty()) {
            return null;
        }
        return (AdditionalData) this.additionalDatas.get(0);
    }

    public boolean removeAdditionalData(AdditionalData additionalData) {
        if (!this.additionalDatas.contains(additionalData)) {
            return false;
        }
        this.additionalDatas.remove(additionalData);
        return true;
    }
}

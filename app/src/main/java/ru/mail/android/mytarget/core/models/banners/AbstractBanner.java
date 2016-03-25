package ru.mail.android.mytarget.core.models.banners;

import java.util.ArrayList;
import java.util.Iterator;
import ru.mail.android.mytarget.core.models.AbstractModel;
import ru.mail.android.mytarget.core.models.Stat;

public abstract class AbstractBanner extends AbstractModel implements Banner {
    protected String advertisingLabel;
    protected String ageRestrictions;
    protected boolean appInstalled;
    protected String bundleId;
    protected String ctaText;
    protected String finalLink;
    protected int height;
    protected String id;
    protected String navigationType;
    protected ArrayList<Stat> stats;
    protected int timeout;
    protected String trackingLink;
    protected String type;
    protected String urlScheme;
    protected int width;

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getUrlScheme() {
        return this.urlScheme;
    }

    public void setUrlScheme(String urlScheme) {
        this.urlScheme = urlScheme;
    }

    public String getBundleId() {
        return this.bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public void setAppInstalled(boolean appInstalled) {
        this.appInstalled = appInstalled;
    }

    public boolean isAppInstalled() {
        return this.appInstalled;
    }

    public String getTrackingLink() {
        return this.trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getFinalLink() {
        return this.finalLink;
    }

    public void setFinalLink(String finalLink) {
        this.finalLink = finalLink;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAgeRestrictions() {
        return this.ageRestrictions;
    }

    public void setAgeRestrictions(String ageRestrictions) {
        this.ageRestrictions = ageRestrictions;
    }

    public void setNavigationType(String navigationType) {
        this.navigationType = navigationType;
    }

    public String getNavigationType() {
        return this.navigationType;
    }

    public void setCtaText(String ctaText) {
        this.ctaText = ctaText;
    }

    public String getCtaText() {
        return this.ctaText;
    }

    public String getAdvertisingLabel() {
        return this.advertisingLabel;
    }

    public void setAdvertisingLabel(String advertisingLabel) {
        this.advertisingLabel = advertisingLabel;
    }

    public AbstractBanner(String id, String type) {
        this.stats = new ArrayList();
        this.id = id;
        this.type = type;
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

    public void addStats(ArrayList<Stat> stats) {
        Iterator i$ = stats.iterator();
        while (i$.hasNext()) {
            addStat((Stat) i$.next());
        }
    }
}

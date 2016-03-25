package ru.mail.android.mytarget.core.models.sections;

import java.util.ArrayList;
import java.util.Iterator;
import ru.mail.android.mytarget.core.enums.Sections;
import ru.mail.android.mytarget.core.models.IconStatus;
import ru.mail.android.mytarget.core.models.banners.AppwallBanner;
import ru.mail.android.mytarget.core.models.banners.Banner;

public class AppwallSection extends AbstractSection<AppwallBanner> {
    private String bubbleIconHDUrl;
    private String bubbleIconUrl;
    private String gotoAppIconHDUrl;
    private String gotoAppIconUrl;
    private boolean hasNotification;
    private String iconHDUrl;
    private ArrayList<IconStatus> iconStatuses;
    private String iconUrl;
    private String itemHighlightIconUrl;
    private String labelIconHDUrl;
    private String labelIconUrl;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconHDUrl() {
        return this.iconHDUrl;
    }

    public void setIconHDUrl(String iconHDUrl) {
        this.iconHDUrl = iconHDUrl;
    }

    public String getBubbleIconUrl() {
        return this.bubbleIconUrl;
    }

    public void setBubbleIconUrl(String bubbleIconUrl) {
        this.bubbleIconUrl = bubbleIconUrl;
    }

    public String getBubbleIconHDUrl() {
        return this.bubbleIconHDUrl;
    }

    public void setBubbleIconHDUrl(String bubbleIconHDUrl) {
        this.bubbleIconHDUrl = bubbleIconHDUrl;
    }

    public String getLabelIconUrl() {
        return this.labelIconUrl;
    }

    public void setLabelIconUrl(String labelIconUrl) {
        this.labelIconUrl = labelIconUrl;
    }

    public String getLabelIconHDUrl() {
        return this.labelIconHDUrl;
    }

    public void setLabelIconHDUrl(String labelIconHDUrl) {
        this.labelIconHDUrl = labelIconHDUrl;
    }

    public String getGotoAppIconUrl() {
        return this.gotoAppIconUrl;
    }

    public void setGotoAppIconUrl(String gotoAppIconUrl) {
        this.gotoAppIconUrl = gotoAppIconUrl;
    }

    public String getGotoAppIconHDUrl() {
        return this.gotoAppIconHDUrl;
    }

    public void setGotoAppIconHDUrl(String gotoAppIconHDUrl) {
        this.gotoAppIconHDUrl = gotoAppIconHDUrl;
    }

    public boolean isHasNotification() {
        return this.hasNotification;
    }

    public String getItemHighlightIconUrl() {
        return this.itemHighlightIconUrl;
    }

    public void setItemHighlightIconUrl(String itemHighlightIconUrl) {
        this.itemHighlightIconUrl = itemHighlightIconUrl;
    }

    public AppwallSection(String name, int index) {
        super(Sections.APPWALL, name, index);
        this.hasNotification = false;
        this.iconStatuses = new ArrayList();
    }

    public boolean addBanner(Banner banner) {
        if (!(banner instanceof AppwallBanner) || getBanner(banner.getId()) != null) {
            return false;
        }
        AppwallBanner b = (AppwallBanner) banner;
        this.banners.add(b);
        this.bannersCount++;
        if (!b.isHasNotification() || this.hasNotification) {
            return true;
        }
        this.hasNotification = true;
        return true;
    }

    public boolean getBannerHasNotification(String bannerId) {
        Iterator i$ = this.banners.iterator();
        while (i$.hasNext()) {
            AppwallBanner banner = (AppwallBanner) i$.next();
            if (banner.getId().equals(bannerId)) {
                return banner.isHasNotification();
            }
        }
        return false;
    }

    public boolean setBannerHasNotification(String bannerId, boolean hasNotification) {
        boolean hasN = false;
        Iterator i$ = this.banners.iterator();
        while (i$.hasNext()) {
            AppwallBanner banner = (AppwallBanner) i$.next();
            if (banner.getId().equals(bannerId)) {
                banner.setHasNotification(hasNotification);
            }
            if (!hasN) {
                hasN = banner.isHasNotification();
            }
        }
        if (hasN == this.hasNotification) {
            return false;
        }
        this.hasNotification = hasN;
        return true;
    }

    public boolean addIconStatus(IconStatus status) {
        if (getIconStatus(status.getValue()) != null) {
            return false;
        }
        this.iconStatuses.add(status);
        return true;
    }

    public IconStatus getIconStatus(String value) {
        Iterator i$ = this.iconStatuses.iterator();
        while (i$.hasNext()) {
            IconStatus status = (IconStatus) i$.next();
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    public ArrayList<IconStatus> getIconStatuses() {
        return new ArrayList(this.iconStatuses);
    }
}

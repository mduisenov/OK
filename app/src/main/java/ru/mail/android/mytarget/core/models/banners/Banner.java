package ru.mail.android.mytarget.core.models.banners;

import java.util.ArrayList;
import ru.mail.android.mytarget.core.models.Stat;

public interface Banner {
    boolean addStat(Stat stat);

    void addStats(ArrayList<Stat> arrayList);

    String getAdvertisingLabel();

    String getAgeRestrictions();

    String getBundleId();

    String getCtaText();

    String getFinalLink();

    int getHeight();

    String getId();

    String getNavigationType();

    ArrayList<Stat> getStats();

    int getTimeout();

    String getTrackingLink();

    String getType();

    String getUrlScheme();

    int getWidth();

    boolean isAppInstalled();

    void setAdvertisingLabel(String str);

    void setAgeRestrictions(String str);

    void setAppInstalled(boolean z);

    void setBundleId(String str);

    void setCtaText(String str);

    void setFinalLink(String str);

    void setHeight(int i);

    void setNavigationType(String str);

    void setTimeout(int i);

    void setTrackingLink(String str);

    void setUrlScheme(String str);

    void setWidth(int i);
}

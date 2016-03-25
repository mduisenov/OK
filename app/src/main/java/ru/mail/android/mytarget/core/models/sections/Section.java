package ru.mail.android.mytarget.core.models.sections;

import java.util.ArrayList;
import java.util.List;
import ru.mail.android.mytarget.core.models.AdditionalData;
import ru.mail.android.mytarget.core.models.Stat;
import ru.mail.android.mytarget.core.models.banners.Banner;

public interface Section<T extends Banner> {
    void addAdditionalData(AdditionalData additionalData);

    boolean addBanner(Banner banner);

    boolean addStat(Stat stat);

    void addStats(List<Stat> list);

    AdditionalData getAdditionalData(String str);

    ArrayList<AdditionalData> getAdditionalDatas();

    String getAdvertisingLabel();

    T getBanner(String str);

    ArrayList<T> getBanners();

    int getBannersCount();

    AdditionalData getFirstAdditional();

    int getIndex();

    String getInfoUrl();

    String getName();

    ArrayList<Stat> getStats();

    String getType();

    void removeAdditionalData(String str);

    boolean removeAdditionalData(AdditionalData additionalData);

    T removeBanner(String str);

    boolean removeBanner(T t);

    void setAdvertisingLabel(String str);

    void setInfoUrl(String str);
}

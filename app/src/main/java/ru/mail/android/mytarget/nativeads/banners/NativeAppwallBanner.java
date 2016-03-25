package ru.mail.android.mytarget.nativeads.banners;

import ru.mail.android.mytarget.nativeads.models.ImageData;

public interface NativeAppwallBanner {
    ImageData getBubbleIcon();

    String getBubbleId();

    int getCoins();

    ImageData getCoinsIcon();

    int getCoinsIconBgColor();

    int getCoinsIconTextColor();

    String getDescription();

    ImageData getGotoAppIcon();

    ImageData getIcon();

    String getId();

    ImageData getItemHighlightIcon();

    ImageData getLabelIcon();

    String getLabelType();

    int getMrgsId();

    String getPaidType();

    float getRating();

    String getStatus();

    ImageData getStatusIcon();

    String getTitle();

    int getVotes();

    boolean isAppInstalled();

    boolean isBanner();

    boolean isHasNotification();

    boolean isItemHighlight();

    boolean isMain();

    boolean isRequireCategoryHighlight();

    boolean isRequireWifi();

    boolean isSubItem();
}

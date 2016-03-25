package ru.ok.android.ui.stream.data;

import ru.ok.model.stream.banner.Banner;

public enum BannerType {
    GO_TO_SITE(1, 2131165358, 2131165325),
    INSTALL_APP(2, 2131165359, 2131165327),
    INSTALL_GAME(6, 2131165361, 2131165328),
    GO_TO_GROUP(3, 2131165362, 2131165324),
    GO_TO(4, 2131165358, 2131165321),
    GO_TO_GROUP_THEME(5, 2131165363, 2131165326),
    GO_TO_EVENT(7, 2131165360, 2131165322),
    JOIN_GROUP(3, 2131165358, 2131165329),
    JOIN_USER(8, 2131165358, 2131166011);
    
    public final int actionType;
    public final int footerMessageResourceId;
    public final int headerMessageResourceId;

    private BannerType(int actionType, int headerMessageResourceId, int footerMessageResourceId) {
        this.actionType = actionType;
        this.headerMessageResourceId = headerMessageResourceId;
        this.footerMessageResourceId = footerMessageResourceId;
    }

    public static BannerType byBanner(Banner banner) {
        for (BannerType bannerType : values()) {
            if (bannerType.actionType == banner.actionType) {
                return bannerType;
            }
        }
        return GO_TO;
    }
}

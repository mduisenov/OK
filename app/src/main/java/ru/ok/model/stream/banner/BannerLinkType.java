package ru.ok.model.stream.banner;

public enum BannerLinkType {
    HEAD_LINK(1),
    SIDE_LINK(3),
    SIDE_LINK_2(4),
    FEED_BANNER(5);
    
    public final int code;

    private BannerLinkType(int code) {
        this.code = code;
    }

    public static BannerLinkType safeValueOf(String name) {
        for (BannerLinkType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public boolean belongsTo(BannerLinkType[] types) {
        for (BannerLinkType type : types) {
            if (type == this) {
                return true;
            }
        }
        return false;
    }

    public static BannerLinkType findByCode(BannerLinkType[] types, int code) {
        for (BannerLinkType type : types) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}

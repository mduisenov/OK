package ru.ok.model.stream.banner;

import android.support.annotation.NonNull;
import ru.ok.android.proto.MessagesProto.Message;

public class PromoLink {
    public final Banner banner;
    public final long fetchedTime;
    public final String fid;
    @NonNull
    public final StatPixelHolder statPixels;
    public final int type;

    PromoLink(int type, String fid, long fetchedTime, Banner banner, @NonNull StatPixelHolder statPixels) {
        this.type = type;
        this.fid = fid;
        this.fetchedTime = fetchedTime;
        this.banner = banner;
        this.statPixels = statPixels;
    }

    public String toString() {
        return "PromoLink{banner=" + this.banner + ", type=" + promoLinkTypeToString(this.type) + ", fetchedTime=" + this.fetchedTime + ", fid=" + this.fid + ", pixels=" + this.statPixels + '}';
    }

    static String promoLinkTypeToString(int type) {
        switch (type) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "HEAD_LINK";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "SIDE_LINK";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "SIDE_LINK_2";
            case Message.UUID_FIELD_NUMBER /*5*/:
                return "FEED_BANNER";
            default:
                return "UNKNOWN(" + type + ")";
        }
    }
}

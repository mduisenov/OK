package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;

public class PromoLinkBuilder implements Parcelable, StatPixelHolder {
    public static final Creator<PromoLinkBuilder> CREATOR;
    private BannerBuilder banner;
    private long fetchedTime;
    private String fid;
    @NonNull
    private final StatPixelHolderImpl pixels;
    private int type;

    /* renamed from: ru.ok.model.stream.banner.PromoLinkBuilder.1 */
    static class C16061 implements Creator<PromoLinkBuilder> {
        C16061() {
        }

        public PromoLinkBuilder createFromParcel(Parcel source) {
            return new PromoLinkBuilder(source);
        }

        public PromoLinkBuilder[] newArray(int size) {
            return new PromoLinkBuilder[size];
        }
    }

    public PromoLinkBuilder() {
        this.type = -1;
        this.pixels = new StatPixelHolderImpl();
    }

    public PromoLink build() throws FeedObjectException {
        if (this.type < 0) {
            throw new FeedObjectException("type not set: " + this.type);
        } else if (this.banner != null) {
            return new PromoLink(this.type, this.fid, this.fetchedTime, this.banner.build(), this.pixels);
        } else {
            throw new FeedObjectException("banner not set");
        }
    }

    public PromoLinkBuilder setType(BannerLinkType type) {
        this.type = convertType(type);
        return this;
    }

    public PromoLinkBuilder setType(int type) {
        this.type = type;
        return this;
    }

    public PromoLinkBuilder setFriendId(String fid) {
        this.fid = fid;
        return this;
    }

    public PromoLinkBuilder setBanner(BannerBuilder banner) {
        this.banner = banner;
        return this;
    }

    public PromoLinkBuilder setFetchedTime(long fetchedTime) {
        this.fetchedTime = fetchedTime;
        return this;
    }

    public static int convertType(BannerLinkType type) {
        if (type == null) {
            return 1;
        }
        return type.code;
    }

    public int getType() {
        return this.type;
    }

    public String getFriendId() {
        return this.fid;
    }

    public BannerBuilder getBanner() {
        return this.banner;
    }

    public static ArrayList<PromoLink> build(ArrayList<PromoLinkBuilder> builders) {
        if (builders == null) {
            return null;
        }
        ArrayList<PromoLink> promoLinks = new ArrayList(builders.size());
        Iterator i$ = builders.iterator();
        while (i$.hasNext()) {
            try {
                promoLinks.add(((PromoLinkBuilder) i$.next()).build());
            } catch (Throwable e) {
                Logger.m186w(e, "Failed to build promo link");
            }
        }
        return promoLinks;
    }

    public String toString() {
        return "PromoLinkBuilder[type=" + PromoLink.promoLinkTypeToString(this.type) + " fid=" + this.fid + " fetchedTime=" + this.fetchedTime + " banner=" + this.banner + " pixels=" + this.pixels + "]";
    }

    public void addStatPixel(int type, String pixelUrl) {
        this.pixels.addStatPixel(type, pixelUrl);
    }

    public void addStatPixels(int type, Collection<String> pixelUrls) {
        this.pixels.addStatPixels(type, pixelUrls);
    }

    @Nullable
    public ArrayList<String> getStatPixels(int type) {
        return this.pixels.getStatPixels(type);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.fid);
        dest.writeParcelable(this.banner, flags);
        dest.writeLong(this.fetchedTime);
        dest.writeParcelable(this.pixels, flags);
    }

    protected PromoLinkBuilder(Parcel src) {
        this.type = -1;
        ClassLoader cl = PromoLink.class.getClassLoader();
        this.type = src.readInt();
        this.fid = src.readString();
        this.banner = (BannerBuilder) src.readParcelable(cl);
        this.fetchedTime = src.readLong();
        this.pixels = (StatPixelHolderImpl) src.readParcelable(cl);
    }

    static {
        CREATOR = new C16061();
    }
}

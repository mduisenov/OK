package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;
import ru.ok.model.stream.banner.BannerBuilder;

public class FeedBannerEntityBuilder extends BaseEntityBuilder<FeedBannerEntityBuilder, FeedBannerEntity> {
    public static final Creator<FeedBannerEntityBuilder> CREATOR;
    BannerBuilder banner;

    /* renamed from: ru.ok.model.stream.entities.FeedBannerEntityBuilder.1 */
    static class C16161 implements Creator<FeedBannerEntityBuilder> {
        C16161() {
        }

        public FeedBannerEntityBuilder createFromParcel(Parcel source) {
            FeedBannerEntityBuilder builder = new FeedBannerEntityBuilder();
            try {
                builder.readFromParcel(source);
                return builder;
            } catch (Throwable e) {
                Logger.m186w(e, "Failed to un-parcel banner");
                return null;
            }
        }

        public FeedBannerEntityBuilder[] newArray(int size) {
            return new FeedBannerEntityBuilder[size];
        }
    }

    public FeedBannerEntityBuilder() {
        super(14);
    }

    public FeedBannerEntityBuilder withBanner(BannerBuilder banner) {
        this.banner = banner;
        withId(banner == null ? null : banner.getId());
        return this;
    }

    protected FeedBannerEntity doPreBuild() throws FeedObjectException {
        if (this.banner != null) {
            return new FeedBannerEntity(this.banner.build());
        }
        throw new FeedObjectException("Banner not set");
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.banner, flags);
    }

    protected FeedBannerEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.banner = (BannerBuilder) src.readParcelable(AbsFeedPhotoEntityBuilder.class.getClassLoader());
        }
    }

    static {
        CREATOR = new C16161();
    }
}

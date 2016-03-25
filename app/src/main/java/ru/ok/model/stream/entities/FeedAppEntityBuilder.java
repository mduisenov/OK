package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedAppEntityBuilder extends BaseEntityBuilder<FeedAppEntityBuilder, FeedAppEntity> {
    public static final Creator<FeedAppEntityBuilder> CREATOR;
    int height;
    String iconUrl;
    String name;
    String storeId;
    String tabStoreId;
    String url;
    int width;

    /* renamed from: ru.ok.model.stream.entities.FeedAppEntityBuilder.1 */
    static class C16151 implements Creator<FeedAppEntityBuilder> {
        C16151() {
        }

        public FeedAppEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedAppEntityBuilder().readFromParcel(source);
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to un-parcel app entity.");
                return null;
            }
        }

        public FeedAppEntityBuilder[] newArray(int size) {
            return new FeedAppEntityBuilder[size];
        }
    }

    public FeedAppEntityBuilder() {
        super(1);
    }

    protected FeedAppEntity doPreBuild() throws FeedObjectException {
        if (getId() == null) {
            throw new FeedObjectException("App ID is null");
        } else if (this.iconUrl != null) {
            return new FeedAppEntity(getLikeInfo(), getId(), this.iconUrl, this.width, this.height, this.url, this.name, this.storeId, this.tabStoreId);
        } else {
            throw new FeedObjectException("App icon url is null");
        }
    }

    public void getRefs(List<String> list) {
    }

    public FeedAppEntityBuilder withIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public FeedAppEntityBuilder withWidth(int width) {
        this.width = width;
        return this;
    }

    public FeedAppEntityBuilder withHeight(int height) {
        this.height = height;
        return this;
    }

    public FeedAppEntityBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public FeedAppEntityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FeedAppEntityBuilder withStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public FeedAppEntityBuilder withTabStoreId(String tabStoreId) {
        this.tabStoreId = tabStoreId;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.iconUrl);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.storeId);
        dest.writeString(this.tabStoreId);
    }

    protected FeedAppEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        super.readFromParcel(src);
        this.iconUrl = src.readString();
        this.width = src.readInt();
        this.height = src.readInt();
        this.name = src.readString();
        this.url = src.readString();
        if (this.iconUrl == null) {
            throw new RecoverableUnParcelException("Un-parcelled icon url is null");
        }
        this.storeId = src.readString();
        this.tabStoreId = src.readString();
        return this;
    }

    static {
        CREATOR = new C16151();
    }
}

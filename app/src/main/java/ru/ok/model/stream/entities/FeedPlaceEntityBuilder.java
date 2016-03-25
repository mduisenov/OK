package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedPlaceEntityBuilder extends BaseEntityBuilder<FeedPlaceEntityBuilder, FeedPlaceEntity> {
    public static final Creator<FeedPlaceEntityBuilder> CREATOR;
    double latitude;
    double longitude;
    String name;

    /* renamed from: ru.ok.model.stream.entities.FeedPlaceEntityBuilder.1 */
    static class C16231 implements Creator<FeedPlaceEntityBuilder> {
        C16231() {
        }

        public FeedPlaceEntityBuilder createFromParcel(Parcel source) {
            FeedPlaceEntityBuilder builder = new FeedPlaceEntityBuilder();
            try {
                builder.readFromParcel(source);
                return builder;
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to read music artist from parcel");
                return null;
            }
        }

        public FeedPlaceEntityBuilder[] newArray(int size) {
            return new FeedPlaceEntityBuilder[size];
        }
    }

    public FeedPlaceEntityBuilder() {
        super(17);
    }

    public FeedPlaceEntityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FeedPlaceEntityBuilder withLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public FeedPlaceEntityBuilder withLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    protected FeedPlaceEntity doPreBuild() throws FeedObjectException {
        return new FeedPlaceEntity(getId(), this.name, this.latitude, this.longitude);
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected FeedPlaceEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            FeedPlaceEntityBuilder feedPlaceEntityBuilder = (FeedPlaceEntityBuilder) super.readFromParcel(src);
            return feedPlaceEntityBuilder;
        } finally {
            this.name = src.readString();
            this.latitude = src.readDouble();
            this.longitude = src.readDouble();
        }
    }

    static {
        CREATOR = new C16231();
    }
}

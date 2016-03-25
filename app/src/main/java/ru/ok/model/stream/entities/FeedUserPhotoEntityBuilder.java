package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedUserPhotoEntityBuilder extends AbsFeedPhotoEntityBuilder {
    public static final Creator<FeedUserPhotoEntityBuilder> CREATOR;

    /* renamed from: ru.ok.model.stream.entities.FeedUserPhotoEntityBuilder.1 */
    static class C16301 implements Creator<FeedUserPhotoEntityBuilder> {
        C16301() {
        }

        public FeedUserPhotoEntityBuilder createFromParcel(Parcel source) {
            try {
                return (FeedUserPhotoEntityBuilder) new FeedUserPhotoEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedUserPhotoEntityBuilder[] newArray(int size) {
            return new FeedUserPhotoEntityBuilder[size];
        }
    }

    public FeedUserPhotoEntityBuilder() {
        super(5);
    }

    static {
        CREATOR = new C16301();
    }
}

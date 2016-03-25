package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedGroupPhotoEntityBuilder extends AbsFeedPhotoEntityBuilder {
    public static final Creator<FeedGroupPhotoEntityBuilder> CREATOR;

    /* renamed from: ru.ok.model.stream.entities.FeedGroupPhotoEntityBuilder.1 */
    static class C16181 implements Creator<FeedGroupPhotoEntityBuilder> {
        C16181() {
        }

        public FeedGroupPhotoEntityBuilder createFromParcel(Parcel source) {
            try {
                return (FeedGroupPhotoEntityBuilder) new FeedGroupPhotoEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedGroupPhotoEntityBuilder[] newArray(int size) {
            return new FeedGroupPhotoEntityBuilder[size];
        }
    }

    public FeedGroupPhotoEntityBuilder() {
        super(12);
    }

    static {
        CREATOR = new C16181();
    }
}

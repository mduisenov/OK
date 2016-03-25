package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.entities.BaseEntity;

public class MediaItemStubBuilder extends MediaItemBuilder<MediaItemStubBuilder, MediaItemStub> {
    public static final Creator<MediaItemStubBuilder> CREATOR;
    String text;

    /* renamed from: ru.ok.model.mediatopics.MediaItemStubBuilder.1 */
    static class C15351 implements Creator<MediaItemStubBuilder> {
        C15351() {
        }

        public MediaItemStubBuilder createFromParcel(Parcel source) {
            return new MediaItemStubBuilder(source);
        }

        public MediaItemStubBuilder[] newArray(int size) {
            return new MediaItemStubBuilder[size];
        }
    }

    public MediaItemStubBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.text);
    }

    public MediaItemStubBuilder(Parcel source) {
        this.text = source.readString();
    }

    public MediaItemStub resolveRefs(Map<String, BaseEntity> map) throws EntityRefNotResolvedException {
        return new MediaItemStub(this.text);
    }

    static {
        CREATOR = new C15351();
    }
}

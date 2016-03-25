package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedAppEntity;

public class MediaItemAppBuilder extends MediaItemBuilder<MediaItemAppBuilder, MediaItemApp> implements Parcelable {
    public static final Creator<MediaItemAppBuilder> CREATOR;
    String actionMark;
    String actionText;
    String appRef;
    String image;
    String imageMark;
    String imageTitle;
    String text;

    /* renamed from: ru.ok.model.mediatopics.MediaItemAppBuilder.1 */
    static class C15301 implements Creator<MediaItemAppBuilder> {
        C15301() {
        }

        public MediaItemAppBuilder createFromParcel(Parcel in) {
            return new MediaItemAppBuilder(in);
        }

        public MediaItemAppBuilder[] newArray(int size) {
            return new MediaItemAppBuilder[size];
        }
    }

    protected MediaItemAppBuilder(Parcel in) {
        super(in);
        this.appRef = in.readString();
        this.text = in.readString();
        this.actionText = in.readString();
        this.actionMark = in.readString();
        this.image = in.readString();
        this.imageTitle = in.readString();
        this.imageMark = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.appRef);
        dest.writeString(this.text);
        dest.writeString(this.actionText);
        dest.writeString(this.actionMark);
        dest.writeString(this.image);
        dest.writeString(this.imageTitle);
        dest.writeString(this.imageMark);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15301();
    }

    public MediaItemApp resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        List<FeedAppEntity> apps = new ArrayList();
        Utils.resolveRefs(resolvedEntities, Collections.singletonList(this.appRef), apps, FeedAppEntity.class);
        if (apps.isEmpty()) {
            return null;
        }
        return new MediaItemApp((FeedAppEntity) apps.get(0), this.text, this.image, this.imageTitle, this.imageMark, this.actionText, this.actionMark);
    }

    public MediaItemAppBuilder withImage(String image, String title, String mark) {
        this.image = image;
        this.imageTitle = title;
        this.imageMark = mark;
        return this;
    }

    public MediaItemAppBuilder withAction(String text, String mark) {
        this.actionText = text;
        this.actionMark = mark;
        return this;
    }

    public MediaItemAppBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public MediaItemAppBuilder withAppRef(String appRef) {
        this.appRef = appRef;
        return this;
    }
}

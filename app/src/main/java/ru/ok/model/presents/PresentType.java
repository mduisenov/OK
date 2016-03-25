package ru.ok.model.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.Serializable;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;

public class PresentType implements Parcelable, Serializable, IPresentType {
    public static final Creator<PresentType> CREATOR;
    private static final long serialVersionUID = 1;
    @Nullable
    public final AnimationProperties animationProperties;
    @NonNull
    public final String id;
    public final boolean isAnimated;
    public final boolean isBadge;
    public final boolean isLive;
    public final boolean isOverlay;
    @NonNull
    public final PhotoSize photoSize;
    @NonNull
    public final TreeSet<PhotoSize> sprites;

    /* renamed from: ru.ok.model.presents.PresentType.1 */
    static class C15781 implements Creator<PresentType> {
        C15781() {
        }

        public PresentType createFromParcel(Parcel parcel) {
            return new PresentType(parcel);
        }

        public PresentType[] newArray(int count) {
            return new PresentType[count];
        }
    }

    public PresentType(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.sprites = new TreeSet();
        this.id = parcel.readString();
        this.photoSize = (PhotoSize) parcel.readParcelable(getClass().getClassLoader());
        this.animationProperties = (AnimationProperties) parcel.readParcelable(getClass().getClassLoader());
        for (Parcelable parcelable : parcel.readParcelableArray(getClass().getClassLoader())) {
            this.sprites.add((PhotoSize) parcelable);
        }
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isBadge = z;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isLive = z;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isOverlay = z;
        if (parcel.readInt() != 1) {
            z2 = false;
        }
        this.isAnimated = z2;
    }

    public PresentType(@NonNull String id, @NonNull PhotoSize photoSize, @Nullable TreeSet<PhotoSize> sprites, @Nullable AnimationProperties animationProperties, boolean isBadge, boolean isLive, boolean isOverlay, boolean isAnimated) {
        this.sprites = new TreeSet();
        this.id = id;
        this.photoSize = photoSize;
        this.animationProperties = animationProperties;
        this.isBadge = isBadge;
        this.isLive = isLive;
        this.isOverlay = isOverlay;
        this.isAnimated = isAnimated;
        this.sprites.addAll(sprites);
    }

    @NonNull
    public String getStaticImage() {
        return this.photoSize.getUrl();
    }

    @Nullable
    public AnimationProperties getAnimationProperties() {
        return this.animationProperties;
    }

    @Nullable
    public TreeSet<PhotoSize> getSprites() {
        return this.sprites;
    }

    public boolean isLive() {
        return this.isLive;
    }

    public boolean isAnimated() {
        return this.isAnimated;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.id);
        dest.writeParcelable(this.photoSize, flags);
        dest.writeParcelable(this.animationProperties, flags);
        dest.writeParcelableArray((Parcelable[]) this.sprites.toArray(new PhotoSize[0]), flags);
        if (this.isBadge) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isLive) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isOverlay) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.isAnimated) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    static {
        CREATOR = new C15781();
    }
}

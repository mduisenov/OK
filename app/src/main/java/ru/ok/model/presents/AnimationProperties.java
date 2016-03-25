package ru.ok.model.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

public class AnimationProperties implements Parcelable {
    public static final Creator<AnimationProperties> CREATOR;
    public final int duration;
    public final int framesCount;
    public final int replayDelay;

    /* renamed from: ru.ok.model.presents.AnimationProperties.1 */
    static class C15761 implements Creator<AnimationProperties> {
        C15761() {
        }

        public AnimationProperties createFromParcel(Parcel parcel) {
            return new AnimationProperties(parcel);
        }

        public AnimationProperties[] newArray(int count) {
            return new AnimationProperties[count];
        }
    }

    public AnimationProperties(@NonNull Parcel parcel) {
        this.framesCount = parcel.readInt();
        this.duration = parcel.readInt();
        this.replayDelay = parcel.readInt();
    }

    public AnimationProperties(int framesCount, int duration, int replayDelay) {
        this.framesCount = framesCount;
        this.duration = duration;
        this.replayDelay = replayDelay;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.framesCount);
        parcel.writeInt(this.duration);
        parcel.writeInt(this.replayDelay);
    }

    static {
        CREATOR = new C15761();
    }
}

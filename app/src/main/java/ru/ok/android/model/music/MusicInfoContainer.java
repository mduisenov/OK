package ru.ok.android.model.music;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.model.wmf.PlayTrackInfo;
import ru.ok.model.wmf.Track;

public class MusicInfoContainer implements Parcelable {
    public static final Creator<MusicInfoContainer> CREATOR;
    public final Track next;
    public final PlayTrackInfo playTrackInfo;
    public final Track prev;
    public final boolean shuffle;
    public final Track track;

    /* renamed from: ru.ok.android.model.music.MusicInfoContainer.1 */
    static class C03711 implements Creator<MusicInfoContainer> {
        C03711() {
        }

        public MusicInfoContainer createFromParcel(Parcel parcel) {
            return new MusicInfoContainer(parcel);
        }

        public MusicInfoContainer[] newArray(int count) {
            return new MusicInfoContainer[count];
        }
    }

    public MusicInfoContainer(Track trackParcelable, PlayTrackInfo playTrackInfoParcelable, Track prev, Track next, boolean shuffle) {
        this.track = trackParcelable;
        this.playTrackInfo = playTrackInfoParcelable;
        this.prev = prev;
        this.next = next;
        this.shuffle = shuffle;
    }

    public MusicInfoContainer(Parcel parcel) {
        this.track = (Track) parcel.readParcelable(Track.class.getClassLoader());
        this.playTrackInfo = (PlayTrackInfo) parcel.readParcelable(PlayTrackInfo.class.getClassLoader());
        this.shuffle = parcel.readInt() != 0;
        this.prev = (Track) parcel.readParcelable(Track.class.getClassLoader());
        this.next = (Track) parcel.readParcelable(Track.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        int i = 1;
        if (!(o instanceof MusicInfoContainer)) {
            return false;
        }
        MusicInfoContainer musicInfoContainer = (MusicInfoContainer) o;
        boolean equals = this.playTrackInfo == null ? musicInfoContainer.playTrackInfo == null : this.playTrackInfo.equals(musicInfoContainer.playTrackInfo);
        if (this.shuffle != musicInfoContainer.shuffle) {
            i = 0;
        }
        return equals & i;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.track, flags);
        parcel.writeParcelable(this.playTrackInfo, flags);
        parcel.writeInt(this.shuffle ? 1 : 0);
        parcel.writeParcelable(this.prev, flags);
        parcel.writeParcelable(this.next, flags);
    }

    static {
        CREATOR = new C03711();
    }

    public String toString() {
        return "MusicInfoContainer{track=" + this.track + "; " + "prev=" + this.prev + "; " + "next=" + this.next + "}";
    }
}

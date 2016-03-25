package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public class MusicItem extends MediaItem {
    public static final Creator<MusicItem> CREATOR;
    private static final long serialVersionUID = 2;
    List<Track> mTracks;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.MusicItem.1 */
    static class C06771 implements Creator<MusicItem> {
        C06771() {
        }

        public MusicItem createFromParcel(Parcel source) {
            return new MusicItem(source);
        }

        public MusicItem[] newArray(int size) {
            return new MusicItem[size];
        }
    }

    public List<Track> getTracks() {
        return this.mTracks;
    }

    public void setTracks(List<Track> tracks) {
        this.mTracks = tracks;
    }

    public boolean isEmpty() {
        return this.mTracks == null || this.mTracks.size() == 0;
    }

    public String getSampleText() {
        if (this.mTracks == null) {
            return "";
        }
        String sampleText = null;
        String foundArtistName = null;
        String foundTrackName = null;
        for (Track track : this.mTracks) {
            Artist artist = track.artist;
            String artistName = artist == null ? null : artist.name;
            String trackName = track.name;
            if (!TextUtils.isEmpty(trackName)) {
                if (!TextUtils.isEmpty(artistName)) {
                    sampleText = artistName + " - " + trackName;
                    break;
                } else if (foundTrackName == null) {
                    foundTrackName = trackName;
                }
            }
            if (!TextUtils.isEmpty(artistName) && foundArtistName == null) {
                foundArtistName = artistName;
            }
        }
        if (sampleText != null) {
            return sampleText;
        }
        if (foundTrackName != null) {
            sampleText = foundTrackName;
        }
        if (foundArtistName != null) {
            return foundArtistName;
        }
        return sampleText;
    }

    public String toString() {
        return "MusicItem[" + (this.mTracks == null ? 0 : this.mTracks.size()) + " tracks: " + this.mTracks + "]";
    }

    public MusicItem() {
        super(MediaItemType.MUSIC);
        this.mTracks = new ArrayList();
    }

    public MusicItem(Parcel source) {
        super(MediaItemType.MUSIC, source);
        this.mTracks = new ArrayList();
        source.readList(this.mTracks, Track.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.mTracks);
    }

    static {
        CREATOR = new C06771();
    }

    public static boolean equal(MusicItem m1, MusicItem m2) {
        if (m1 == null) {
            if (m2 == null) {
                return true;
            }
            return false;
        } else if (m2 != null) {
            return ObjectUtils.listsEqual(m1.mTracks, m2.mTracks);
        } else {
            return false;
        }
    }
}

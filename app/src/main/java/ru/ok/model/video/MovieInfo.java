package ru.ok.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Iterator;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;

public class MovieInfo implements Parcelable {
    public static final Creator<MovieInfo> CREATOR;
    public final String collage;
    public final int commentsCount;
    public final String contentType;
    public final int dailyViews;
    public final int duration;
    public final String groupId;
    public final String id;
    public final LikeSummary likeSummary;
    public final int likesCount;
    public final String ownerId;
    public final TreeSet<PhotoSize> thumbnails;
    public final String title;
    public final int totalViews;

    /* renamed from: ru.ok.model.video.MovieInfo.1 */
    static class C16371 implements Creator<MovieInfo> {
        C16371() {
        }

        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    }

    public MovieInfo(String id, String contentType, String title, int duration, String groupId, String ownerId, int commentsCount, int likesCount, String collage, int totalViews, int dailyViews, LikeSummary likeSummary) {
        this.thumbnails = new TreeSet();
        this.id = id;
        this.contentType = contentType;
        this.title = title;
        this.duration = duration;
        this.groupId = groupId;
        this.ownerId = ownerId;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
        this.collage = collage;
        this.totalViews = totalViews;
        this.dailyViews = dailyViews;
        this.likeSummary = likeSummary;
    }

    public MovieInfo(Parcel source) {
        this.thumbnails = new TreeSet();
        this.id = source.readString();
        this.contentType = source.readString();
        this.title = source.readString();
        this.duration = source.readInt();
        this.groupId = source.readString();
        this.ownerId = source.readString();
        this.commentsCount = source.readInt();
        this.likesCount = source.readInt();
        this.collage = source.readString();
        this.totalViews = source.readInt();
        this.dailyViews = source.readInt();
        this.likeSummary = (LikeSummary) source.readParcelable(LikeSummary.class.getClassLoader());
        int countThumbnails = source.readInt();
        ClassLoader cl = PhotoSize.class.getClassLoader();
        for (int i = 0; i < countThumbnails; i++) {
            this.thumbnails.add(source.readParcelable(cl));
        }
    }

    public String getId() {
        return this.id;
    }

    public int getTotalViews() {
        return this.totalViews;
    }

    public TreeSet<PhotoSize> getThumbnails() {
        return this.thumbnails;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.contentType);
        dest.writeString(this.title);
        dest.writeInt(this.duration);
        dest.writeString(this.groupId);
        dest.writeString(this.ownerId);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.likesCount);
        dest.writeString(this.collage);
        dest.writeInt(this.totalViews);
        dest.writeInt(this.dailyViews);
        dest.writeParcelable(this.likeSummary, flags);
        dest.writeInt(this.thumbnails.size());
        Iterator i$ = this.thumbnails.iterator();
        while (i$.hasNext()) {
            dest.writeParcelable((PhotoSize) i$.next(), flags);
        }
    }

    static {
        CREATOR = new C16371();
    }
}

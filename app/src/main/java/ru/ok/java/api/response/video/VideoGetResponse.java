package ru.ok.java.api.response.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.video.Advertisement;
import ru.ok.model.video.LikeSummary;
import ru.ok.model.video.LiveStream;

public final class VideoGetResponse implements Parcelable, Serializable {
    public static final Creator<VideoGetResponse> CREATOR;
    private static final long serialVersionUID = 1;
    public final Advertisement advertisement;
    public final Set<String> contentPresentations;
    public final String contentType;
    public final int duration;
    public final int fromTime;
    public final String id;
    public final LikeSummary likeSummary;
    public final LiveStream liveStream;
    public final String permalink;
    public final VideoStatus status;
    public final TreeSet<PhotoSize> thumbnails;
    public final String title;
    public final int totalViews;
    public final String url1080p;
    public final String url1440p;
    public final String url144p;
    public final String url2160p;
    public final String url240p;
    public final String url360p;
    public final String url480p;
    public final String url720p;
    public final String urlDash;
    public final String urlExternal;
    public final String urlHls;
    public final String urlLiveHls;

    public enum VideoStatus {
        UNKNOWN,
        OK,
        ERROR,
        UPLOADING,
        PROCESSING,
        ON_MODERATION,
        BLOCKED,
        CENSORED,
        COPYRIGHTS_RESTRICTED,
        UNAVAILABLE,
        LIMITED_ACCESS;

        public static VideoStatus safeValueOf(String statusString) {
            for (VideoStatus status : values()) {
                if (status.toString().equals(statusString)) {
                    return status;
                }
            }
            return UNKNOWN;
        }
    }

    public VideoGetResponse(String id, String contentType, String url144p, String url240p, String url360p, String url480p, String url720p, String url1080p, String url1440p, String url2160p, String urlDash, String urlHls, String urlLiveHls, String urlExternal, VideoStatus status, LikeSummary likeSummary, LiveStream liveStream, String permalink, String title, int totalViews, int duration, List<String> contentPresentationsList, Advertisement advertisement, int fromTime) {
        this.thumbnails = new TreeSet();
        this.id = id;
        this.contentType = contentType;
        this.url144p = url144p;
        this.url240p = url240p;
        this.url360p = url360p;
        this.url480p = url480p;
        this.url720p = url720p;
        this.url1080p = url1080p;
        this.url1440p = url1440p;
        this.url2160p = url2160p;
        this.urlDash = urlDash;
        this.urlHls = urlHls;
        this.urlLiveHls = urlLiveHls;
        this.urlExternal = urlExternal;
        this.status = status;
        this.likeSummary = likeSummary;
        this.permalink = permalink;
        this.title = title;
        this.totalViews = totalViews;
        this.duration = duration;
        this.liveStream = liveStream;
        this.contentPresentations = new HashSet(contentPresentationsList);
        this.advertisement = advertisement;
        this.fromTime = fromTime;
    }

    public boolean isLiveStreamResponse() {
        if (this.contentPresentations == null || !this.contentPresentations.contains("live_hls")) {
            return false;
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.contentType);
        dest.writeString(this.url144p);
        dest.writeString(this.url240p);
        dest.writeString(this.url360p);
        dest.writeString(this.url480p);
        dest.writeString(this.url720p);
        dest.writeString(this.url1080p);
        dest.writeString(this.url1440p);
        dest.writeString(this.url2160p);
        dest.writeString(this.urlDash);
        dest.writeString(this.urlHls);
        dest.writeString(this.urlLiveHls);
        dest.writeString(this.urlExternal);
        dest.writeInt(this.status.ordinal());
        dest.writeParcelable(this.likeSummary, flags);
        dest.writeParcelable(this.liveStream, flags);
        dest.writeString(this.permalink);
        dest.writeString(this.title);
        dest.writeInt(this.totalViews);
        dest.writeInt(this.duration);
        dest.writeStringList(new ArrayList(this.contentPresentations));
        dest.writeParcelable(this.advertisement, flags);
        dest.writeInt(this.fromTime);
        dest.writeInt(this.thumbnails.size());
        Iterator i$ = this.thumbnails.iterator();
        while (i$.hasNext()) {
            dest.writeParcelable((PhotoSize) i$.next(), flags);
        }
    }

    public boolean isContentTypeVideo() {
        return this.contentType != null && this.contentType.contains("video");
    }

    static {
        CREATOR = new 1();
    }

    private static List<String> createStringList(Parcel source) {
        List<String> stringsList = new ArrayList();
        source.readStringList(stringsList);
        return stringsList;
    }
}

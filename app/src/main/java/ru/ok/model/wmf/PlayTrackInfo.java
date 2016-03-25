package ru.ok.model.wmf;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.UnsupportedEncodingException;
import java.util.List;
import ru.ok.java.api.wmf.utils.MD5HashBuilder;
import ru.ok.java.api.wmf.utils.UrlUtils;

public class PlayTrackInfo implements Parcelable {
    public static final Creator<PlayTrackInfo> CREATOR;
    public final String contentUrl;
    public final long duration;
    public final String imageUrl;
    public final long size;
    public final long trackId;
    public final String userId;
    public final String userName;

    public PlayTrackInfo(long trackId, String imageUrl, String contentUrl, long size, long duration, String userName, String userId) {
        this.trackId = trackId;
        this.imageUrl = imageUrl;
        this.contentUrl = contentUrl;
        this.size = size;
        this.duration = duration;
        this.userName = userName;
        this.userId = userId;
    }

    public static String getBigImageUrl(String bigImageUrl) {
        return bigImageUrl.replace("type=2", "type=1");
    }

    public String getMp3ContentUrl() throws UnsupportedEncodingException {
        return this.contentUrl + "&clientHash=" + MD5HashBuilder.buildHash((String) ((List) UrlUtils.getUrlParameters(this.contentUrl).get("md5")).get(0)) + "&" + "client" + "=" + "android";
    }

    public PlayTrackInfo(Parcel parcel) {
        this.trackId = parcel.readLong();
        this.imageUrl = parcel.readString();
        this.contentUrl = parcel.readString();
        this.size = parcel.readLong();
        this.duration = parcel.readLong();
        this.userName = parcel.readString();
        this.userId = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.trackId);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.contentUrl);
        parcel.writeLong(this.size);
        parcel.writeLong(this.duration);
        parcel.writeString(this.userName);
        parcel.writeString(this.userId);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new 1();
    }
}

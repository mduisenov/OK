package ru.ok.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class ContentUrl implements Parcelable, Serializable {
    public static final Creator<ContentUrl> CREATOR;
    private static final long serialVersionUID = 1;
    private String contentType;
    private Uri uri;

    /* renamed from: ru.ok.model.ContentUrl.1 */
    static class C15071 implements Creator<ContentUrl> {
        C15071() {
        }

        public ContentUrl createFromParcel(Parcel source) {
            ContentUrl contentUrl = new ContentUrl();
            contentUrl.readFromParcel(source);
            return contentUrl;
        }

        public ContentUrl[] newArray(int size) {
            return new ContentUrl[size];
        }
    }

    public ContentUrl(Uri uri, String contentType) {
        this.uri = uri;
        this.contentType = contentType;
    }

    private ContentUrl() {
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getContentType() {
        return this.contentType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.contentType);
    }

    public final void readFromParcel(Parcel parcel) {
        this.uri = (Uri) parcel.readParcelable(ContentUrl.class.getClassLoader());
        this.contentType = parcel.readString();
    }

    static {
        CREATOR = new C15071();
    }
}

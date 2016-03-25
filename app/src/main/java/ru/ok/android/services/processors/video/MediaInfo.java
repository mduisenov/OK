package ru.ok.android.services.processors.video;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;

public abstract class MediaInfo implements Parcelable, Serializable {
    private static final long serialVersionUID = 1;
    private final String displayName;
    private transient int hashCode;
    private final String mimeType;
    private final long sizeBytes;
    private transient Uri uri;
    private final String uriStr;

    public abstract void cleanUp();

    public abstract Bitmap getThumbnail(ContentResolver contentResolver, int i, int i2);

    public abstract InputStreamHolder getThumbnailStreamHolder(ContentResolver contentResolver, int i, int i2);

    public abstract InputStream open(ContentResolver contentResolver) throws FileNotFoundException;

    protected MediaInfo(Uri uri, String displayName, long sizeBytes, String mimeType) {
        this.uri = uri;
        this.uriStr = uri == null ? null : uri.toString();
        this.displayName = displayName;
        this.sizeBytes = sizeBytes;
        this.mimeType = mimeType;
    }

    protected MediaInfo(Parcel src) {
        this.uri = (Uri) src.readParcelable(MediaInfo.class.getClassLoader());
        this.uriStr = this.uri == null ? null : this.uri.toString();
        this.displayName = src.readString();
        this.sizeBytes = src.readLong();
        this.mimeType = src.readString();
    }

    @Nullable
    public static MediaInfo fromUri(@NonNull Context context, @Nullable Uri uri, @NonNull String defaultDisplayName) {
        if (uri == null) {
            return null;
        }
        try {
            String scheme = uri.getScheme();
            if ("content".equals(scheme)) {
                if ("media".equals(uri.getAuthority())) {
                    return MediaInfoMediaStore.fromMediaUri(context, uri, defaultDisplayName);
                }
                if (isDocumentUri(context, uri)) {
                    return MediaInfoDocument.fromDocumentUri(context, uri, defaultDisplayName);
                }
                return new MediaInfoGenericContent(uri, null, -1);
            } else if ("file".equals(scheme)) {
                return MediaInfoFile.fromFileUri(context, uri, defaultDisplayName);
            } else {
                return null;
            }
        } catch (Throwable e) {
            Logger.m178e(e);
            return null;
        }
    }

    @SuppressLint({"NewApi"})
    private static boolean isDocumentUri(Context context, Uri mediaUri) {
        return VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, mediaUri);
    }

    public final Uri getUri() {
        return this.uri;
    }

    public final String getDisplayName() {
        return this.displayName;
    }

    public final long getSizeBytes() {
        return this.sizeBytes;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public boolean isPersistent() {
        return true;
    }

    public String toString() {
        return "MediaInfo[uri=" + this.uriStr + " displayName=" + this.displayName + " sizeBytes=" + this.sizeBytes + "]";
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaInfo other = (MediaInfo) o;
        if (TextUtils.equals(this.displayName, other.displayName) && TextUtils.equals(this.uriStr, other.uriStr) && this.sizeBytes == other.sizeBytes) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = 190022183 * ((int) this.sizeBytes);
            if (this.displayName != null) {
                hashCode += 239419083 * this.displayName.hashCode();
            }
            if (this.uriStr != null) {
                hashCode += 923864143 * this.uriStr.hashCode();
            }
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.displayName);
        dest.writeLong(this.sizeBytes);
        dest.writeString(this.mimeType);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            this.uri = TextUtils.isEmpty(this.uriStr) ? null : Uri.parse(this.uriStr);
        } catch (Throwable th) {
        }
    }
}

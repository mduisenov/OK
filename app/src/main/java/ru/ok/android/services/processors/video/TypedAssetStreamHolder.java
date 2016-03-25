package ru.ok.android.services.processors.video;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor.AutoCloseInputStream;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.InputStreamHolder;

@TargetApi(11)
public class TypedAssetStreamHolder implements InputStreamHolder {
    public static final Creator<TypedAssetStreamHolder> CREATOR;
    private final Uri contentUri;
    private transient InputStream inputStream;
    private String mimeType;
    private Bundle opts;

    /* renamed from: ru.ok.android.services.processors.video.TypedAssetStreamHolder.1 */
    static class C05081 implements Creator<TypedAssetStreamHolder> {
        C05081() {
        }

        public TypedAssetStreamHolder createFromParcel(Parcel src) {
            ClassLoader cl = TypedAssetStreamHolder.class.getClassLoader();
            return new TypedAssetStreamHolder((Uri) src.readParcelable(cl), src.readString(), (Bundle) src.readParcelable(cl));
        }

        public TypedAssetStreamHolder[] newArray(int size) {
            return new TypedAssetStreamHolder[size];
        }
    }

    public InputStream open(ContentResolver cr) throws IOException {
        IOUtils.closeSilently(this.inputStream);
        InputStream autoCloseInputStream = new AutoCloseInputStream(cr.openTypedAssetFileDescriptor(this.contentUri, this.mimeType, this.opts));
        this.inputStream = autoCloseInputStream;
        return autoCloseInputStream;
    }

    public void close() throws IOException {
        IOUtils.closeSilently(this.inputStream);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.contentUri, flags);
        dest.writeString(this.mimeType);
    }

    public String toString() {
        return "TypedAssetStreamHolder[uri=" + this.contentUri + " mimeType=" + this.mimeType + " opts=" + this.opts + "]";
    }

    public TypedAssetStreamHolder(Uri contentUri, String mimeType, Bundle opts) {
        this.contentUri = contentUri;
        this.mimeType = mimeType;
        this.opts = opts;
    }

    static {
        CREATOR = new C05081();
    }
}

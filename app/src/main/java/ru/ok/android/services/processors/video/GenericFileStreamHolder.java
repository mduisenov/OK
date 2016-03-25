package ru.ok.android.services.processors.video;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.utils.InputStreamHolder;

public class GenericFileStreamHolder implements InputStreamHolder {
    public static final Creator<GenericFileStreamHolder> CREATOR;
    private FileInputStream is;
    private final String path;

    /* renamed from: ru.ok.android.services.processors.video.GenericFileStreamHolder.1 */
    static class C05011 implements Creator<GenericFileStreamHolder> {
        C05011() {
        }

        public GenericFileStreamHolder createFromParcel(Parcel source) {
            return new GenericFileStreamHolder(source.readString());
        }

        public GenericFileStreamHolder[] newArray(int size) {
            return new GenericFileStreamHolder[size];
        }
    }

    public GenericFileStreamHolder(String path) {
        this.path = path;
    }

    public InputStream open(ContentResolver cr) throws IOException {
        close();
        InputStream fileInputStream = new FileInputStream(this.path);
        this.is = fileInputStream;
        return fileInputStream;
    }

    public void close() throws IOException {
        if (this.is != null) {
            this.is.close();
            this.is = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
    }

    static {
        CREATOR = new C05011();
    }
}

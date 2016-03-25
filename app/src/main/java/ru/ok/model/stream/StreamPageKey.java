package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class StreamPageKey implements Parcelable {
    public static final Creator<StreamPageKey> CREATOR;
    @Nullable
    String anchor;
    int count;
    private transient String key;
    int pageNumber;

    /* renamed from: ru.ok.model.stream.StreamPageKey.1 */
    static class C16021 implements Creator<StreamPageKey> {
        C16021() {
        }

        public StreamPageKey createFromParcel(Parcel source) {
            return new StreamPageKey(source);
        }

        public StreamPageKey[] newArray(int size) {
            return new StreamPageKey[size];
        }
    }

    StreamPageKey() {
    }

    public StreamPageKey(String anchor, int count, int pageNumber) {
        this.anchor = anchor;
        this.count = count;
        this.pageNumber = pageNumber;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public int getCount() {
        return this.count;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public static StreamPageKey firstPageKey(int count) {
        return new StreamPageKey(null, count, 0);
    }

    public boolean isFirstPage() {
        return this.anchor == null;
    }

    public String getKey() {
        if (this.key == null) {
            this.key = this.anchor + "_" + this.count;
        }
        return this.key;
    }

    public static StreamPageKey fromKeyAndPageNumber(@NonNull String key, int pageNumber) throws IllegalArgumentException {
        int delimiterPos = key.lastIndexOf(95);
        if (delimiterPos < 0 || delimiterPos + 1 >= key.length()) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        try {
            int count = Integer.parseInt(key.substring(delimiterPos + 1));
            String anchor = key.substring(0, delimiterPos);
            if ("null".equals(anchor)) {
                anchor = null;
            }
            return new StreamPageKey(anchor, count, pageNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
    }

    public String toString() {
        return "StreamPageKey[anchor=" + this.anchor + " count=" + this.count + " pageNumber=" + this.pageNumber + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.anchor);
        dest.writeInt(this.count);
        dest.writeInt(this.pageNumber);
    }

    StreamPageKey(Parcel src) {
        this.anchor = src.readString();
        this.count = src.readInt();
        this.pageNumber = src.readInt();
    }

    static {
        CREATOR = new C16021();
    }
}

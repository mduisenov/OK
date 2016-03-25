package ru.ok.model.stream.message;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import ru.ok.java.api.utils.DigestUtils;

public final class FeedMessage implements Parcelable, Serializable {
    public static final Creator<FeedMessage> CREATOR;
    private static final long serialVersionUID = 1;
    ArrayList<FeedMessageSpan> spans;
    String text;

    /* renamed from: ru.ok.model.stream.message.FeedMessage.1 */
    static class C16321 implements Creator<FeedMessage> {
        C16321() {
        }

        public FeedMessage createFromParcel(Parcel source) {
            return new FeedMessage(source.readString(), source.readArrayList(FeedMessage.class.getClassLoader()));
        }

        public FeedMessage[] newArray(int size) {
            return new FeedMessage[size];
        }
    }

    FeedMessage() {
    }

    public FeedMessage(String text, ArrayList<FeedMessageSpan> spans) {
        this.text = text;
        this.spans = spans;
    }

    @NonNull
    public String getText() {
        return this.text == null ? "" : this.text;
    }

    @Nullable
    public ArrayList<FeedMessageSpan> getSpans() {
        return this.spans;
    }

    public void digest(MessageDigest digest, byte[] buffer) {
        DigestUtils.addString(digest, this.text);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeList(this.spans);
    }

    static {
        CREATOR = new C16321();
    }

    public String toString() {
        return "FeedMessage[\"" + this.text + "\" with " + this.spans.size() + " spans]";
    }
}

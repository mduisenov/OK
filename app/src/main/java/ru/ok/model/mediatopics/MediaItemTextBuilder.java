package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.message.FeedEntitySpan;
import ru.ok.model.stream.message.FeedMessageBuilder;
import ru.ok.model.stream.message.FeedMessageSpan;

public class MediaItemTextBuilder extends MediaItemBuilder<MediaItemTextBuilder, MediaItemText> {
    public static final Creator<MediaItemTextBuilder> CREATOR;
    String textTokens;

    /* renamed from: ru.ok.model.mediatopics.MediaItemTextBuilder.1 */
    static class C15361 implements Creator<MediaItemTextBuilder> {
        C15361() {
        }

        public MediaItemTextBuilder createFromParcel(Parcel source) {
            return new MediaItemTextBuilder(source);
        }

        public MediaItemTextBuilder[] newArray(int size) {
            return new MediaItemTextBuilder[size];
        }
    }

    public MediaItemTextBuilder withTextTokens(String textTokens) {
        this.textTokens = textTokens;
        return this;
    }

    public MediaItemText resolveRefs(Map<String, BaseEntity> map) {
        return new MediaItemText(FeedMessageBuilder.buildMessage(this.textTokens));
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        ArrayList<FeedMessageSpan> spans = FeedMessageBuilder.buildMessage(this.textTokens).getSpans();
        if (spans != null) {
            int size = spans.size();
            for (int i = 0; i < size; i++) {
                FeedMessageSpan span = (FeedMessageSpan) spans.get(i);
                if (span instanceof FeedEntitySpan) {
                    outRefs.add(((FeedEntitySpan) span).getRef());
                }
            }
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.textTokens);
    }

    MediaItemTextBuilder(Parcel src) {
        super(src);
        this.textTokens = src.readString();
    }

    static {
        CREATOR = new C15361();
    }
}

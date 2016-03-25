package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ListPollQuestion extends PollQuestion implements Parcelable {
    public static final Creator<ListPollQuestion> CREATOR;
    private final List<ListPollItem> items;

    /* renamed from: ru.ok.model.poll.ListPollQuestion.1 */
    static class C15681 implements Creator<ListPollQuestion> {
        C15681() {
        }

        public ListPollQuestion createFromParcel(Parcel in) {
            return new ListPollQuestion(in);
        }

        public ListPollQuestion[] newArray(int size) {
            return new ListPollQuestion[size];
        }
    }

    public static class ListPollItem implements Parcelable {
        public static final Creator<ListPollItem> CREATOR;
        private final String id;
        private final String otherText;
        private final String title;

        /* renamed from: ru.ok.model.poll.ListPollQuestion.ListPollItem.1 */
        static class C15691 implements Creator<ListPollItem> {
            C15691() {
            }

            public ListPollItem createFromParcel(Parcel in) {
                return new ListPollItem(in);
            }

            public ListPollItem[] newArray(int size) {
                return new ListPollItem[size];
            }
        }

        public boolean isOther() {
            return (this.otherText == null || this.otherText.isEmpty()) ? false : true;
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getOtherText() {
            return this.otherText;
        }

        protected ListPollItem(Parcel in) {
            this.id = in.readString();
            this.title = in.readString();
            this.otherText = in.readString();
        }

        static {
            CREATOR = new C15691();
        }

        public ListPollItem(String id, String title, @Nullable String otherText) {
            this.id = id;
            this.title = title;
            this.otherText = otherText;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.title);
            dest.writeString(this.otherText);
        }
    }

    protected ListPollQuestion(Parcel in) {
        super(in);
        this.items = in.createTypedArrayList(ListPollItem.CREATOR);
    }

    public ListPollQuestion(List<ListPollItem> items, String title, int step) {
        super(title, step);
        this.items = items;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.items);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15681();
    }

    public List<ListPollItem> getItems() {
        return this.items;
    }

    public void shuffle() {
        ListPollItem other = null;
        for (ListPollItem item : this.items) {
            if (item.isOther()) {
                other = item;
            }
        }
        if (other != null) {
            this.items.remove(other);
            Collections.shuffle(this.items);
            this.items.add(other);
            return;
        }
        Collections.shuffle(this.items);
    }
}

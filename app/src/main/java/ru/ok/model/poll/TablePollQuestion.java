package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Collections;
import java.util.List;

public class TablePollQuestion extends PollQuestion implements Parcelable {
    public static final Creator<TablePollQuestion> CREATOR;
    private final List<TablePollItem> items;

    /* renamed from: ru.ok.model.poll.TablePollQuestion.1 */
    static class C15731 implements Creator<TablePollQuestion> {
        C15731() {
        }

        public TablePollQuestion createFromParcel(Parcel in) {
            return new TablePollQuestion(in);
        }

        public TablePollQuestion[] newArray(int size) {
            return new TablePollQuestion[size];
        }
    }

    public static class TablePollItem implements Parcelable {
        public static final Creator<TablePollItem> CREATOR;
        private final String id;
        private final String otherText;
        private final int resId;
        private final int selectedResId;
        private final String title;

        /* renamed from: ru.ok.model.poll.TablePollQuestion.TablePollItem.1 */
        static class C15741 implements Creator<TablePollItem> {
            C15741() {
            }

            public TablePollItem createFromParcel(Parcel in) {
                return new TablePollItem(in);
            }

            public TablePollItem[] newArray(int size) {
                return new TablePollItem[size];
            }
        }

        public TablePollItem(String id, String title, int resId, int selectedResId) {
            this.title = title;
            this.resId = resId;
            this.selectedResId = selectedResId;
            this.id = id;
            this.otherText = null;
        }

        public TablePollItem(String id, String title, int resId, int selectedResId, String otherText) {
            this.title = title;
            this.resId = resId;
            this.selectedResId = selectedResId;
            this.id = id;
            this.otherText = otherText;
        }

        protected TablePollItem(Parcel in) {
            this.title = in.readString();
            this.resId = in.readInt();
            this.selectedResId = in.readInt();
            this.id = in.readString();
            this.otherText = in.readString();
        }

        static {
            CREATOR = new C15741();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeInt(this.resId);
            dest.writeInt(this.selectedResId);
            dest.writeString(this.id);
            dest.writeString(this.otherText);
        }

        public String getTitle() {
            return this.title;
        }

        public int getResId() {
            return this.resId;
        }

        public int getSelectedResId() {
            return this.selectedResId;
        }

        public String getId() {
            return this.id;
        }

        public boolean isOther() {
            return (this.otherText == null || this.otherText.isEmpty()) ? false : true;
        }

        public String getOtherText() {
            return this.otherText;
        }
    }

    public TablePollQuestion(List<TablePollItem> items, String title, int step) {
        super(title, step);
        this.items = items;
    }

    protected TablePollQuestion(Parcel in) {
        super(in);
        this.items = in.createTypedArrayList(TablePollItem.CREATOR);
    }

    static {
        CREATOR = new C15731();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.items);
    }

    public List<TablePollItem> getItems() {
        return this.items;
    }

    public void shuffle() {
        TablePollItem other = null;
        for (TablePollItem item : this.items) {
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

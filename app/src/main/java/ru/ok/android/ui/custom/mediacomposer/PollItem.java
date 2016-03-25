package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.model.mediatopics.MediaItemType;

public class PollItem extends MediaItem {
    public static final Creator<PollItem> CREATOR;
    private static final long serialVersionUID = 1;
    protected final List<String> answers;
    protected boolean multiAnswersAllowed;
    protected String title;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.PollItem.1 */
    static class C06801 implements Creator<PollItem> {
        C06801() {
        }

        public PollItem createFromParcel(Parcel source) {
            return new PollItem(source);
        }

        public PollItem[] newArray(int size) {
            return new PollItem[size];
        }
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isMultiAnswersAllowed() {
        return this.multiAnswersAllowed;
    }

    public List<String> getAnswers() {
        return this.answers;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addAnswer(String answer) {
        this.answers.add(answer);
    }

    public void setMultiAnswersAllowed(boolean multiAnswersAllowed) {
        this.multiAnswersAllowed = multiAnswersAllowed;
    }

    public PollItem() {
        super(MediaItemType.POLL);
        this.title = "";
        this.answers = new ArrayList();
        this.multiAnswersAllowed = false;
    }

    PollItem(Parcel source) {
        boolean z = false;
        super(MediaItemType.POLL, source);
        this.title = "";
        this.answers = new ArrayList();
        this.multiAnswersAllowed = false;
        if (source.readInt() != 0) {
            z = true;
        }
        this.multiAnswersAllowed = z;
        source.readStringList(this.answers);
        this.title = source.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.multiAnswersAllowed ? 1 : 0);
        dest.writeStringList(this.answers);
        dest.writeString(this.title);
    }

    public boolean isEmpty() {
        if (!TextUtils.isEmpty(this.title)) {
            return false;
        }
        for (String answer : this.answers) {
            if (!TextUtils.isEmpty(answer)) {
                return false;
            }
        }
        return true;
    }

    public String getSampleText() {
        if (!TextUtils.isEmpty(this.title)) {
            return this.title;
        }
        for (String answer : this.answers) {
            if (!TextUtils.isEmpty(answer)) {
                return answer;
            }
        }
        return "";
    }

    public String toString() {
        return "PollItem[\"" + this.title + "\" : " + this.answers + " canMultiply=" + isMultiAnswersAllowed() + "]";
    }

    static {
        CREATOR = new C06801();
    }

    public static boolean equal(PollItem p1, PollItem p2) {
        if (p1 == null) {
            if (p2 == null) {
                return true;
            }
            return false;
        } else if (p2 == null) {
            return false;
        } else {
            if (TextUtils.equals(p1.title, p2.title) && p1.multiAnswersAllowed == p2.multiAnswersAllowed && ObjectUtils.listsEqual(p1.answers, p2.answers)) {
                return true;
            }
            return false;
        }
    }
}

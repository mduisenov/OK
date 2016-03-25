package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SkipPollQuestion extends PollQuestion implements Parcelable {
    public static final Creator<SkipPollQuestion> CREATOR;

    /* renamed from: ru.ok.model.poll.SkipPollQuestion.1 */
    static class C15721 implements Creator<SkipPollQuestion> {
        C15721() {
        }

        public SkipPollQuestion createFromParcel(Parcel in) {
            return new SkipPollQuestion(in);
        }

        public SkipPollQuestion[] newArray(int size) {
            return new SkipPollQuestion[size];
        }
    }

    public SkipPollQuestion(int step) {
        super("", step);
    }

    protected SkipPollQuestion(Parcel in) {
        super(in);
    }

    static {
        CREATOR = new C15721();
    }

    public boolean isSkip() {
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}

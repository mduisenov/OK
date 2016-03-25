package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TextPollQuestion extends PollQuestion implements Parcelable {
    public static final Creator<TextPollQuestion> CREATOR;

    /* renamed from: ru.ok.model.poll.TextPollQuestion.1 */
    static class C15751 implements Creator<TextPollQuestion> {
        C15751() {
        }

        public TextPollQuestion createFromParcel(Parcel in) {
            return new TextPollQuestion(in);
        }

        public TextPollQuestion[] newArray(int size) {
            return new TextPollQuestion[size];
        }
    }

    public TextPollQuestion(String title, int step) {
        super(title, step);
    }

    protected TextPollQuestion(Parcel in) {
        super(in);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    static {
        CREATOR = new C15751();
    }

    public int describeContents() {
        return 0;
    }
}

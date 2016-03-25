package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AppPollAnswer implements Parcelable {
    public static final Creator<AppPollAnswer> CREATOR;
    private final String answer;
    private final String answerIndex;
    private final String answerText;
    private final boolean skip;
    private final int step;

    /* renamed from: ru.ok.model.poll.AppPollAnswer.1 */
    static class C15661 implements Creator<AppPollAnswer> {
        C15661() {
        }

        public AppPollAnswer createFromParcel(Parcel in) {
            return new AppPollAnswer(in);
        }

        public AppPollAnswer[] newArray(int size) {
            return new AppPollAnswer[size];
        }
    }

    public AppPollAnswer(boolean skip, int step) {
        this.skip = skip;
        this.step = step;
        this.answer = null;
        this.answerText = null;
        this.answerIndex = null;
    }

    public AppPollAnswer(String answer, String answerIndex, int step) {
        this.answer = answer;
        this.answerIndex = answerIndex;
        this.step = step;
        this.answerText = null;
        this.skip = false;
    }

    public AppPollAnswer(String answer, String text, String answerIndex, int step) {
        this.answer = answer;
        this.answerText = text;
        this.answerIndex = answerIndex;
        this.step = step;
        this.skip = false;
    }

    protected AppPollAnswer(Parcel in) {
        boolean z = true;
        this.answer = in.readString();
        this.answerText = in.readString();
        this.answerIndex = in.readString();
        this.step = in.readInt();
        if (in.readByte() != (byte) 1) {
            z = false;
        }
        this.skip = z;
    }

    public boolean isSkip() {
        return this.skip;
    }

    public int getStep() {
        return this.step;
    }

    public String getAnswerIndex() {
        return this.answerIndex;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public String getAnswer() {
        return this.answer;
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15661();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.answer);
        dest.writeString(this.answerText);
        dest.writeString(this.answerIndex);
        dest.writeInt(this.step);
        dest.writeByte((byte) (this.skip ? 1 : 0));
    }
}

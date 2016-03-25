package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RatingPollQuestion extends PollQuestion implements Parcelable {
    public static final Creator<RatingPollQuestion> CREATOR;

    /* renamed from: ru.ok.model.poll.RatingPollQuestion.1 */
    static class C15701 implements Creator<RatingPollQuestion> {
        C15701() {
        }

        public RatingPollQuestion createFromParcel(Parcel in) {
            return new RatingPollQuestion(in);
        }

        public RatingPollQuestion[] newArray(int size) {
            return new RatingPollQuestion[size];
        }
    }

    public RatingPollQuestion(String title, int step) {
        super(title, step);
    }

    protected RatingPollQuestion(Parcel in) {
        super(in);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15701();
    }
}

package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;

public class SimplePollStep implements PollStep {
    public static final Creator<SimplePollStep> CREATOR;
    PollQuestion pollQuestion;

    /* renamed from: ru.ok.model.poll.SimplePollStep.1 */
    static class C15711 implements Creator<SimplePollStep> {
        C15711() {
        }

        public SimplePollStep createFromParcel(Parcel in) {
            return new SimplePollStep(in);
        }

        public SimplePollStep[] newArray(int size) {
            return new SimplePollStep[size];
        }
    }

    public SimplePollStep(PollQuestion pollQuestion) {
        this.pollQuestion = pollQuestion;
    }

    protected SimplePollStep(Parcel in) {
        this.pollQuestion = (PollQuestion) in.readParcelable(getClass().getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pollQuestion, flags);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15711();
    }

    public PollQuestion getQuestion(List<AppPollAnswer> list) {
        return this.pollQuestion;
    }
}

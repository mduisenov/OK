package ru.ok.model.poll;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GraphPollStep implements PollStep {
    public static final Creator<GraphPollStep> CREATOR;
    Map<String, PollStep> questionHashMap;
    int step;

    /* renamed from: ru.ok.model.poll.GraphPollStep.1 */
    static class C15671 implements Creator<GraphPollStep> {
        C15671() {
        }

        public GraphPollStep createFromParcel(Parcel source) {
            return new GraphPollStep(source);
        }

        public GraphPollStep[] newArray(int size) {
            return new GraphPollStep[size];
        }
    }

    public GraphPollStep(Map<String, PollStep> questionHashMap, int step) {
        this.questionHashMap = questionHashMap;
        this.step = step;
    }

    public GraphPollStep(Parcel source) {
        this.step = source.readInt();
        int size = source.readInt();
        this.questionHashMap = new HashMap(size * 2);
        for (int i = 0; i < size; i++) {
            this.questionHashMap.put(source.readString(), (PollStep) source.readParcelable(getClass().getClassLoader()));
        }
    }

    public PollQuestion getQuestion(List<AppPollAnswer> answers) {
        return ((PollStep) this.questionHashMap.get(((AppPollAnswer) answers.get(this.step - 1)).getAnswer())).getQuestion(answers);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.step);
        dest.writeInt(this.questionHashMap.size());
        for (Entry<String, PollStep> entry : this.questionHashMap.entrySet()) {
            dest.writeString((String) entry.getKey());
            dest.writeParcelable((Parcelable) entry.getValue(), flags);
        }
    }

    static {
        CREATOR = new C15671();
    }
}

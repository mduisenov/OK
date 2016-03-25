package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import java.util.List;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.RecoverableUnParcelException;

public final class FeedPollEntity extends BaseEntity {
    public final List<Answer> answers;
    public final int count;
    public final String id;
    public final List<String> options;
    public final String question;

    public static class Answer implements Parcelable, Serializable {
        public static final Creator<Answer> CREATOR;
        private static final long serialVersionUID = 1;
        public final String id;
        public final String text;
        public final ActionCountInfo voteInfo;

        /* renamed from: ru.ok.model.stream.entities.FeedPollEntity.Answer.1 */
        static class C16251 implements Creator<Answer> {
            C16251() {
            }

            public Answer createFromParcel(Parcel source) {
                try {
                    return new Answer(source);
                } catch (RecoverableUnParcelException e) {
                    return null;
                }
            }

            public Answer[] newArray(int size) {
                return new Answer[size];
            }
        }

        public Answer(String id, String text, ActionCountInfo voteInfo) {
            this.id = id;
            this.text = text;
            this.voteInfo = voteInfo;
        }

        public boolean isSelf() {
            return this.voteInfo != null && this.voteInfo.self;
        }

        public int getVotesCount() {
            return this.voteInfo == null ? 0 : this.voteInfo.count;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.text);
            dest.writeParcelable(this.voteInfo, flags);
        }

        protected Answer(Parcel src) throws RecoverableUnParcelException {
            ClassLoader cl = Answer.class.getClassLoader();
            String id = src.readString();
            String text = src.readString();
            this.voteInfo = (ActionCountInfo) src.readParcelable(cl);
            if (id == null) {
                throw new RecoverableUnParcelException("Unparcelled id is null");
            } else if (text == null) {
                throw new RecoverableUnParcelException("Unparcelled text is null");
            } else {
                this.id = id;
                this.text = text;
            }
        }

        static {
            CREATOR = new C16251();
        }
    }

    public FeedPollEntity(String id, String question, List<Answer> answers, LikeInfoContext likeSummary, List<String> options, int count) {
        super(11, likeSummary, null);
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.options = options;
        this.count = count;
    }

    public String getId() {
        return this.id;
    }

    public int getTotalParticipants() {
        return this.count;
    }
}

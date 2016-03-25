package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;

public class FeedPollEntityBuilder extends BaseEntityBuilder<FeedPollEntityBuilder, FeedPollEntity> {
    public static final Creator<FeedPollEntityBuilder> CREATOR;
    final ArrayList<Answer> answers;
    int count;
    final ArrayList<String> options;
    String question;

    /* renamed from: ru.ok.model.stream.entities.FeedPollEntityBuilder.1 */
    static class C16261 implements Creator<FeedPollEntityBuilder> {
        C16261() {
        }

        public FeedPollEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedPollEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedPollEntityBuilder[] newArray(int size) {
            return new FeedPollEntityBuilder[size];
        }
    }

    public FeedPollEntityBuilder withQuestion(String question) {
        this.question = question;
        return this;
    }

    public FeedPollEntityBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public FeedPollEntityBuilder addAnswer(Answer answer) {
        this.answers.add(answer);
        return this;
    }

    public FeedPollEntityBuilder addOption(String option) {
        this.options.add(option);
        return this;
    }

    protected FeedPollEntity doPreBuild() throws FeedObjectException {
        String id = getId();
        if (id != null) {
            return new FeedPollEntity(id, this.question, this.answers, getLikeInfo(), this.options, this.count);
        }
        throw new FeedObjectException("Feed poll ID is null");
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.question);
        dest.writeList(this.answers);
        dest.writeStringList(this.options);
        dest.writeInt(this.count);
    }

    public FeedPollEntityBuilder() {
        super(11);
        this.count = 0;
        this.answers = new ArrayList();
        this.options = new ArrayList();
    }

    protected FeedPollEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        String question;
        ClassLoader cl;
        try {
            super.readFromParcel(src);
            cl = FeedPollEntityBuilder.class.getClassLoader();
            question = src.readString();
            src.readList(this.answers, cl);
            src.readStringList(this.options);
            withCount(src.readInt());
            if (question == null) {
                throw new RecoverableUnParcelException("Feed poll question is null");
            }
            this.question = question;
            return this;
        } catch (Throwable th) {
            cl = FeedPollEntityBuilder.class.getClassLoader();
            question = src.readString();
            src.readList(this.answers, cl);
            src.readStringList(this.options);
            withCount(src.readInt());
            if (question == null) {
                RecoverableUnParcelException recoverableUnParcelException = new RecoverableUnParcelException("Feed poll question is null");
            } else {
                this.question = question;
            }
        }
    }

    static {
        CREATOR = new C16261();
    }
}

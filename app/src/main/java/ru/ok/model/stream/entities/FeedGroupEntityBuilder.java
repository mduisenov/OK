package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.model.GroupInfo;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedGroupEntityBuilder extends BaseEntityBuilder<FeedGroupEntityBuilder, FeedGroupEntity> {
    public static final Creator<FeedGroupEntityBuilder> CREATOR;
    GroupInfo groupInfo;

    /* renamed from: ru.ok.model.stream.entities.FeedGroupEntityBuilder.1 */
    static class C16171 implements Creator<FeedGroupEntityBuilder> {
        C16171() {
        }

        public FeedGroupEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedGroupEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedGroupEntityBuilder[] newArray(int size) {
            return new FeedGroupEntityBuilder[size];
        }
    }

    public FeedGroupEntityBuilder() {
        super(2);
    }

    public FeedGroupEntityBuilder withGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
        withId(groupInfo.getId());
        return this;
    }

    protected FeedGroupEntity doPreBuild() throws FeedObjectException {
        if (this.groupInfo == null) {
            throw new FeedObjectException("GroupInfo not set");
        } else if (this.groupInfo.getId() != null) {
            return new FeedGroupEntity(this.groupInfo, getLikeInfo());
        } else {
            throw new FeedObjectException("Group ID is null");
        }
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.groupInfo, flags);
    }

    protected FeedGroupEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.groupInfo = (GroupInfo) src.readParcelable(FeedGroupEntityBuilder.class.getClassLoader());
        }
    }

    static {
        CREATOR = new C16171();
    }
}

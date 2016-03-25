package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedUserEntityBuilder extends BaseEntityBuilder<FeedUserEntityBuilder, FeedUserEntity> {
    public static final Creator<FeedUserEntityBuilder> CREATOR;
    UserInfo userInfo;

    /* renamed from: ru.ok.model.stream.entities.FeedUserEntityBuilder.1 */
    static class C16291 implements Creator<FeedUserEntityBuilder> {
        C16291() {
        }

        public FeedUserEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedUserEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedUserEntityBuilder[] newArray(int size) {
            return new FeedUserEntityBuilder[size];
        }
    }

    public FeedUserEntityBuilder withUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo != null) {
            withId(userInfo.uid);
        }
        return this;
    }

    protected FeedUserEntity doPreBuild() {
        return new FeedUserEntity(this.userInfo, getLikeInfo());
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.userInfo, flags);
    }

    public FeedUserEntityBuilder() {
        super(7);
    }

    protected FeedUserEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.userInfo = (UserInfo) src.readParcelable(FeedUserEntityBuilder.class.getClassLoader());
        }
    }

    static {
        CREATOR = new C16291();
    }
}

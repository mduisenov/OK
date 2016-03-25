package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedAchievementEntityBuilder extends BaseEntityBuilder<FeedAchievementEntityBuilder, FeedAchievementEntity> {
    public static final Creator<FeedAchievementEntityBuilder> CREATOR;
    String achievementTypeRef;
    String receiverRef;

    /* renamed from: ru.ok.model.stream.entities.FeedAchievementEntityBuilder.1 */
    static class C16121 implements Creator<FeedAchievementEntityBuilder> {
        C16121() {
        }

        public FeedAchievementEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedAchievementEntityBuilder().readFromParcel(source);
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to un-parcel achievement");
                return null;
            }
        }

        public FeedAchievementEntityBuilder[] newArray(int size) {
            return new FeedAchievementEntityBuilder[size];
        }
    }

    public FeedAchievementEntityBuilder() {
        super(22);
    }

    public FeedAchievementEntityBuilder withReceiverRef(String receiverRef) {
        this.receiverRef = receiverRef;
        return this;
    }

    public FeedAchievementEntityBuilder withAchievementTypeRef(String achievementTypeRef) {
        this.achievementTypeRef = achievementTypeRef;
        return this;
    }

    protected FeedAchievementEntity doPreBuild() throws FeedObjectException {
        return new FeedAchievementEntity(getId(), null, null);
    }

    public void getRefs(List<String> outRefs) {
        if (this.receiverRef != null) {
            outRefs.add(this.receiverRef);
        }
        if (this.achievementTypeRef != null) {
            outRefs.add(this.achievementTypeRef);
        }
    }

    protected void resolveRefs(Map<String, BaseEntity> resolvedEntities, FeedAchievementEntity entity) throws EntityRefNotResolvedException {
        if (this.receiverRef != null) {
            BaseEntity receiver = (BaseEntity) resolvedEntities.get(this.receiverRef);
            if (receiver == null) {
                throw new EntityRefNotResolvedException(this.receiverRef, "receiver ref of an achievement not resolver");
            }
            entity.setReceiver(receiver);
        }
        if (this.achievementTypeRef != null) {
            BaseEntity achievementTypeEntity = (BaseEntity) resolvedEntities.get(this.achievementTypeRef);
            if (achievementTypeEntity == null) {
                throw new EntityRefNotResolvedException(this.achievementTypeRef, "Achievement type ref of an achievement not resolved");
            } else if (achievementTypeEntity instanceof FeedAchievementTypeEntity) {
                entity.setAchievementType((FeedAchievementTypeEntity) achievementTypeEntity);
            } else {
                throw new EntityRefNotResolvedException(this.achievementTypeRef, "Achievement type ref resolved to wrong type: " + achievementTypeEntity);
            }
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.receiverRef);
        dest.writeString(this.achievementTypeRef);
    }

    protected FeedAchievementEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        RecoverableUnParcelException exception = null;
        try {
            super.readFromParcel(src);
        } catch (RecoverableUnParcelException e) {
            exception = e;
        }
        this.receiverRef = src.readString();
        this.achievementTypeRef = src.readString();
        if (exception == null) {
            return this;
        }
        throw exception;
    }

    static {
        CREATOR = new C16121();
    }
}

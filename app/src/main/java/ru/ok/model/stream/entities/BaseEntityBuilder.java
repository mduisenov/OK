package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.Map;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObject;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.FeedObjectVisitor;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.RecoverableUnParcelException;

public abstract class BaseEntityBuilder<TBuilder extends BaseEntityBuilder, TEntity extends BaseEntity> implements Parcelable, FeedObject {
    DiscussionSummary discussionSummary;
    String id;
    LikeInfoContext likeInfo;
    private transient boolean prebuiltCalled;
    private transient TEntity prebuiltEntity;
    int type;

    protected abstract TEntity doPreBuild() throws FeedObjectException;

    public TBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TBuilder withLikeInfo(LikeInfoContext likeInfo) {
        this.likeInfo = likeInfo;
        return this;
    }

    public TBuilder withDiscussionSummary(DiscussionSummary discussionSummary) {
        this.discussionSummary = discussionSummary;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    public DiscussionSummary getDiscussionSummary() {
        return this.discussionSummary;
    }

    public int getType() {
        return this.type;
    }

    public final TEntity preBuild() throws FeedObjectException {
        if (TextUtils.isEmpty(this.id)) {
            throw new FeedObjectException("ID is empty");
        }
        this.prebuiltCalled = true;
        this.prebuiltEntity = doPreBuild();
        return this.prebuiltEntity;
    }

    public final TEntity build(Map<String, BaseEntity> resolvedEntities) throws FeedObjectException {
        if (this.prebuiltEntity != null) {
            resolveRefs(resolvedEntities, this.prebuiltEntity);
            return this.prebuiltEntity;
        } else if (this.prebuiltCalled) {
            throw new FeedObjectException("preBuild() has failed");
        } else {
            throw new IllegalStateException("preBuild() method was not called.");
        }
    }

    protected void resolveRefs(Map<String, BaseEntity> map, TEntity tEntity) throws EntityRefNotResolvedException {
    }

    public void accept(String ref, FeedObjectVisitor visitor) {
        visitor.visit(ref, this);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.likeInfo, flags);
        dest.writeParcelable(this.discussionSummary, flags);
    }

    protected BaseEntityBuilder(int type) {
        this.type = type;
    }

    protected TBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        ClassLoader cl = BaseEntityBuilder.class.getClassLoader();
        String id = src.readString();
        LikeInfoContext likeInfo = (LikeInfoContext) src.readParcelable(cl);
        DiscussionSummary discussionSummary = (DiscussionSummary) src.readParcelable(cl);
        if (id == null) {
            throw new RecoverableUnParcelException("Un-parceled ID is null");
        }
        this.id = id;
        if (likeInfo != null) {
            this.likeInfo = likeInfo;
        }
        this.discussionSummary = discussionSummary;
        return this;
    }
}

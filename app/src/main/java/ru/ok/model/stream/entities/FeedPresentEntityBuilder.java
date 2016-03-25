package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;
import ru.ok.model.stream.message.FeedMessage;

public class FeedPresentEntityBuilder extends BaseEntityBuilder<FeedPresentEntityBuilder, FeedPresentEntity> {
    public static final Creator<FeedPresentEntityBuilder> CREATOR;
    List<String> musicTrackRefs;
    String presentTypeRef;
    FeedMessage receiverLabel;
    String receiverRef;
    FeedMessage senderLabel;
    String senderRef;

    /* renamed from: ru.ok.model.stream.entities.FeedPresentEntityBuilder.1 */
    static class C16271 implements Creator<FeedPresentEntityBuilder> {
        C16271() {
        }

        public FeedPresentEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedPresentEntityBuilder().readFromParcel(source);
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to un-parcel present");
                return null;
            }
        }

        public FeedPresentEntityBuilder[] newArray(int size) {
            return new FeedPresentEntityBuilder[size];
        }
    }

    public FeedPresentEntityBuilder() {
        super(6);
    }

    public FeedPresentEntityBuilder withSenderRef(String senderRef) {
        this.senderRef = senderRef;
        return this;
    }

    public FeedPresentEntityBuilder withReceiverRef(String receiverRef) {
        this.receiverRef = receiverRef;
        return this;
    }

    public FeedPresentEntityBuilder withPresentTypeRef(String presentTypeRef) {
        this.presentTypeRef = presentTypeRef;
        return this;
    }

    public FeedPresentEntityBuilder withMusicTracksRefs(List<String> musicTrackRefs) {
        this.musicTrackRefs = musicTrackRefs;
        return this;
    }

    public FeedPresentEntityBuilder withSenderLabel(FeedMessage text) {
        this.senderLabel = text;
        return this;
    }

    public FeedPresentEntityBuilder withReceiverLabel(FeedMessage text) {
        this.receiverLabel = text;
        return this;
    }

    protected FeedPresentEntity doPreBuild() throws FeedObjectException {
        return new FeedPresentEntity(getId(), null, null, null, null, null);
    }

    public void getRefs(List<String> outRefs) {
        if (this.senderRef != null) {
            outRefs.add(this.senderRef);
        }
        if (this.receiverRef != null) {
            outRefs.add(this.receiverRef);
        }
        if (this.presentTypeRef != null) {
            outRefs.add(this.presentTypeRef);
        }
        if (this.musicTrackRefs != null) {
            outRefs.addAll(this.musicTrackRefs);
        }
    }

    protected void resolveRefs(Map<String, BaseEntity> resolvedEntities, FeedPresentEntity entity) throws EntityRefNotResolvedException {
        if (this.senderRef != null) {
            BaseEntity sender = (BaseEntity) resolvedEntities.get(this.senderRef);
            if (sender != null) {
                entity.setSender(sender);
            }
        }
        if (this.receiverRef != null) {
            BaseEntity receiver = (BaseEntity) resolvedEntities.get(this.receiverRef);
            if (receiver == null) {
                throw new EntityRefNotResolvedException(this.receiverRef, "receiver ref of a present not resolver");
            }
            entity.setReceiver(receiver);
        }
        if (this.presentTypeRef != null) {
            BaseEntity presentTypeEntity = (BaseEntity) resolvedEntities.get(this.presentTypeRef);
            if (presentTypeEntity == null) {
                throw new EntityRefNotResolvedException(this.presentTypeRef, "present type ref of a present not resolved");
            } else if (presentTypeEntity instanceof FeedPresentTypeEntity) {
                entity.setPresentType((FeedPresentTypeEntity) presentTypeEntity);
            } else {
                throw new EntityRefNotResolvedException(this.presentTypeRef, "present type ref resolved to wrong type: " + presentTypeEntity);
            }
        }
        if (this.musicTrackRefs != null) {
            List<FeedMusicTrackEntity> tracks = null;
            for (String trackRef : this.musicTrackRefs) {
                BaseEntity trackEntity = (BaseEntity) resolvedEntities.get(trackRef);
                if (trackEntity == null) {
                    throw new EntityRefNotResolvedException(trackRef, "track ref of present not resolved");
                } else if (trackEntity instanceof FeedMusicTrackEntity) {
                    FeedMusicTrackEntity track = (FeedMusicTrackEntity) trackEntity;
                    if (tracks == null) {
                        tracks = new ArrayList();
                    }
                    tracks.add(track);
                } else {
                    Logger.m185w("Track refs to strange class: %s", trackEntity.getClass());
                }
            }
            entity.setTracks(tracks);
        }
        entity.setSenderLabel(this.senderLabel);
        entity.setReceiverLabel(this.receiverLabel);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.senderRef);
        dest.writeString(this.receiverRef);
        dest.writeString(this.presentTypeRef);
        dest.writeStringList(this.musicTrackRefs);
        dest.writeParcelable(this.senderLabel, 0);
        dest.writeParcelable(this.receiverLabel, 0);
    }

    protected FeedPresentEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        RecoverableUnParcelException exception = null;
        try {
            super.readFromParcel(src);
        } catch (RecoverableUnParcelException e) {
            exception = e;
        }
        this.senderRef = src.readString();
        this.receiverRef = src.readString();
        this.presentTypeRef = src.readString();
        this.musicTrackRefs = src.readArrayList(FeedPresentEntityBuilder.class.getClassLoader());
        this.senderLabel = (FeedMessage) src.readParcelable(FeedMessage.class.getClassLoader());
        this.receiverLabel = (FeedMessage) src.readParcelable(FeedMessage.class.getClassLoader());
        if (exception == null) {
            return this;
        }
        throw exception;
    }

    static {
        CREATOR = new C16271();
    }
}

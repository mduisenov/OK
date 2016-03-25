package ru.ok.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import com.google.android.gms.ads.AdRequest;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ru.ok.android.C0206R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.utils.DigestUtils;
import ru.ok.model.stream.banner.StatPixelHolder;
import ru.ok.model.stream.banner.StatPixelHolderImpl;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.stream.message.FeedMessage;

public class Feed implements FeedObject, StatPixelHolder {
    static final ArrayList EMPTY_ARRAY_LIST;
    int actionType;
    int dataFlags;
    long date;
    @Nullable
    String deleteId;
    @Nullable
    public String digest;
    @Nullable
    DiscussionSummary discussionSummary;
    @NonNull
    final transient FeedRefs<BaseEntity> entities;
    @Nullable
    transient HashMap<String, BaseEntity> entitiesByRefId;
    private final transient int[] entitiesTypeMask;
    @NonNull
    final FeedStringRefs entityRefs;
    String feedStatInfo;
    int feedType;
    long id;
    @Nullable
    LikeInfoContext likeInfo;
    @Nullable
    FeedMessage message;
    @NonNull
    StreamPageKey pageKey;
    int pattern;
    boolean pinned;
    @NonNull
    final StatPixelHolderImpl pixels;
    @Nullable
    String spamId;
    @Nullable
    FeedMessage title;
    String uuid;

    public Feed() {
        this.pattern = 0;
        this.entityRefs = new FeedStringRefs();
        this.entities = new FeedRefs();
        this.entitiesTypeMask = new int[10];
        this.pixels = new StatPixelHolderImpl();
    }

    public long getId() {
        return this.id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public int getPattern() {
        return this.pattern;
    }

    public int getFeedType() {
        return this.feedType;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public String getFeedStatInfo() {
        return this.feedStatInfo;
    }

    public long getDate() {
        return this.date;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getFeedOwners() {
        return getEntities(0);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getOwners() {
        return getEntities(2);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getActors() {
        return getEntities(1);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getTargets() {
        return getEntities(4);
    }

    public int getTargetTypesMask() {
        return getEntityTypesMask(4);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getPlaces() {
        return getEntities(7);
    }

    public int getPlaceTypesMask() {
        return getEntityTypesMask(7);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getPresents() {
        if (this.pattern == 4 || this.pattern == 9) {
            return getEntities(4);
        }
        return EMPTY_ARRAY_LIST;
    }

    public int getPresentTypesMask() {
        if (this.pattern == 4) {
            return getEntityTypesMask(4);
        }
        return 0;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getPins() {
        return getEntities(8);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getSenders() {
        if (this.pattern == 4) {
            return getEntities(1);
        }
        return EMPTY_ARRAY_LIST;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getReceivers() {
        if (this.pattern == 4) {
            return getEntities(2);
        }
        return EMPTY_ARRAY_LIST;
    }

    public int getReceiverTypesMask() {
        if (this.pattern == 4) {
            return getEntityTypesMask(2);
        }
        return 0;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getFriends() {
        if (this.pattern == 2) {
            return getEntities(4);
        }
        return EMPTY_ARRAY_LIST;
    }

    public int getFriendTypesMask() {
        if (this.pattern == 2) {
            return getEntityTypesMask(4);
        }
        return 0;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getAuthors() {
        return getEntities(3);
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getHolidays() {
        if (this.pattern == 4) {
            return getEntities(7);
        }
        return EMPTY_ARRAY_LIST;
    }

    @NonNull
    public ArrayList<? extends BaseEntity> getBanners() {
        return getEntities(9);
    }

    @Nullable
    public FeedMessage getMessage() {
        return this.message;
    }

    @Nullable
    public FeedMessage getTitle() {
        return this.title;
    }

    @Nullable
    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    @Nullable
    public DiscussionSummary getDiscussionSummary() {
        return this.discussionSummary;
    }

    @Nullable
    public String getSpamId() {
        return this.spamId;
    }

    @Nullable
    public String getDeleteId() {
        return this.deleteId;
    }

    public boolean hasDataFlag(int flag) {
        return (this.dataFlags & flag) == flag;
    }

    public boolean hasStatPixels(int statisticsType) {
        ArrayList<String> urls = this.pixels.getStatPixels(statisticsType);
        return (urls == null || urls.isEmpty()) ? false : true;
    }

    @Nullable
    public BaseEntity getEntity(@NonNull String refId) {
        if (this.entitiesByRefId == null) {
            return null;
        }
        return (BaseEntity) this.entitiesByRefId.get(refId);
    }

    @NonNull
    public Collection<BaseEntity> getEntities() {
        if (this.entitiesByRefId == null) {
            return Collections.emptyList();
        }
        return this.entitiesByRefId.values();
    }

    @NonNull
    public StreamPageKey getPageKey() {
        return this.pageKey;
    }

    public int getActionType() {
        return this.actionType;
    }

    public final String toString() {
        StringBuilder out = new StringBuilder(300);
        out.append(getClass().getSimpleName()).append("[");
        fieldsToString(out);
        out.append("]");
        return out.toString();
    }

    private void fieldsToString(StringBuilder out) {
        out.append(" id=").append(this.id);
        out.append(" pattern=").append(this.pattern);
        out.append(" date=").append(this.date);
        out.append(" message=");
        if (this.message == null) {
            out.append("null");
        } else {
            out.append('\"').append(this.message).append('\"');
        }
        out.append(" title=");
        if (this.title == null) {
            out.append("null");
        } else {
            out.append('\"').append(this.title).append('\"');
        }
        out.append(" likeInfo=").append(this.likeInfo);
        out.append(" spamId=").append(this.spamId);
        out.append(" pinned=").append(this.pinned);
        entitiesToString(out, 0, "feedOwner");
        entitiesToString(out, 2, "owner");
        entitiesToString(out, 1, "actor");
        entitiesToString(out, 4, "target");
        entitiesToString(out, 5, "original_author");
        entitiesToString(out, 6, "original_owner");
        entitiesToString(out, 7, "place");
        entitiesToString(out, 8, "pin");
        entitiesToString(out, 9, "banner");
    }

    private void entitiesToString(StringBuilder out, int index, String roleName) {
        int i = 0;
        Iterator i$ = getEntities(index).iterator();
        while (i$.hasNext()) {
            int i2 = i + 1;
            out.append(' ').append(roleName).append('[').append(i).append("]=").append((BaseEntity) i$.next());
            i = i2;
        }
    }

    @NonNull
    private ArrayList<BaseEntity> getEntities(int roleIndex) {
        ArrayList<BaseEntity> entities = this.entities.getRefs(roleIndex);
        if (entities == null) {
            return EMPTY_ARRAY_LIST;
        }
        return entities;
    }

    static {
        EMPTY_ARRAY_LIST = new ArrayList();
    }

    int getEntityTypesMask(int roleIndex) {
        return this.entitiesTypeMask[roleIndex];
    }

    public void validate() throws FeedObjectException {
        if (this.pattern == 7) {
            ArrayList<String> bannerRefs = getBannerRefs();
            if (bannerRefs == null || bannerRefs.isEmpty()) {
                throw new FeedObjectException("Banner feed w/o banner object");
            }
        }
        if (this.pageKey == null) {
            throw new FeedObjectException("Feed has no page key");
        }
    }

    static int calculateEntityFlags(List<? extends BaseEntity> entities) {
        int flags = 0;
        if (entities != null) {
            for (BaseEntity entity : entities) {
                switch (entity.getType()) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        flags |= NotificationCompat.FLAG_LOCAL_ONLY;
                        break;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        flags |= 16;
                        break;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                    case Message.TASKID_FIELD_NUMBER /*8*/:
                        flags |= 32;
                        break;
                    case Message.UUID_FIELD_NUMBER /*5*/:
                    case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                        flags |= 1;
                        break;
                    case Message.REPLYTO_FIELD_NUMBER /*6*/:
                        flags |= NotificationCompat.FLAG_HIGH_PRIORITY;
                        break;
                    case Message.ATTACHES_FIELD_NUMBER /*7*/:
                        flags |= 8;
                        break;
                    case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                        flags |= 2;
                        break;
                    case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                        flags |= 64;
                        if ((entity instanceof FeedMusicTrackEntity) && ((FeedMusicTrackEntity) entity).hasImage()) {
                            flags |= 2048;
                            break;
                        }
                    case Message.EDITINFO_FIELD_NUMBER /*11*/:
                        flags |= AdRequest.MAX_CONTENT_URL_LENGTH;
                        break;
                    case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                        flags |= 4;
                        break;
                    case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                        flags |= 1024;
                        break;
                    case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                        flags |= FragmentTransaction.TRANSIT_ENTER_MASK;
                        break;
                    case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                        flags |= FragmentTransaction.TRANSIT_EXIT_MASK;
                        break;
                    case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                        flags |= 16384;
                        break;
                    case C0206R.styleable.Toolbar_logoDescription /*22*/:
                        flags |= 32768;
                        break;
                    case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                        flags |= 65536;
                        break;
                    default:
                        flags |= LinearLayoutManager.INVALID_OFFSET;
                        break;
                }
            }
        }
        return flags;
    }

    public void addStatPixel(int type, String url) {
        this.pixels.addStatPixel(type, url);
    }

    public void addStatPixels(int type, Collection<String> statUrls) {
        this.pixels.addStatPixels(type, statUrls);
    }

    @Nullable
    public ArrayList<String> getStatPixels(int type) {
        return this.pixels.getStatPixels(type);
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setMessage(@Nullable FeedMessage message) {
        this.message = message;
    }

    public void setTitle(@Nullable FeedMessage title) {
        this.title = title;
    }

    public void setLikeInfo(@Nullable LikeInfoContext likeInfo) {
        this.likeInfo = likeInfo;
    }

    public void setDiscussionSummary(@Nullable DiscussionSummary discussionSummary) {
        this.discussionSummary = discussionSummary;
    }

    public void setSpamId(@Nullable String spamId) {
        this.spamId = spamId;
    }

    public void setDeleteId(@Nullable String deleteId) {
        this.deleteId = deleteId;
    }

    public void addDataFlag(int flag) {
        this.dataFlags |= flag;
    }

    public void setFeedType(int feedType) {
        this.feedType = feedType;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setFeedStatInfo(String feedStatInfo) {
        this.feedStatInfo = feedStatInfo;
    }

    public void setPageKey(@NonNull StreamPageKey pageKey) {
        this.pageKey = pageKey;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public void setFeedOwnerRefs(ArrayList<String> refs) {
        this.entityRefs.set(0, refs);
    }

    public void setOwnerRefs(ArrayList<String> refs) {
        this.entityRefs.set(2, refs);
    }

    public void setActorRefs(ArrayList<String> refs) {
        this.entityRefs.set(1, refs);
    }

    public void setPinRefs(ArrayList<String> refs) {
        this.entityRefs.set(8, refs);
    }

    public void setTargetRefs(ArrayList<String> refs) {
        this.entityRefs.set(4, refs);
    }

    public void addTargetRef(String ref) {
        this.entityRefs.add(4, ref);
    }

    public void setHolidayRefs(ArrayList<String> refs) {
        this.entityRefs.set(7, refs);
    }

    public void setPlaceRefs(ArrayList<String> refs) {
        this.entityRefs.set(7, refs);
    }

    public void setAuthorRefs(ArrayList<String> refs) {
        this.entityRefs.set(3, refs);
    }

    public void addBannerRef(String ref) {
        this.entityRefs.add(9, ref);
    }

    @Nullable
    public ArrayList<String> getBannerRefs() {
        return this.entityRefs.getRefs(9);
    }

    public void getRefs(List<String> outRefs) {
        if (outRefs != null) {
            for (int i = 0; i < 10; i++) {
                ArrayList<String> refs = this.entityRefs.getRefs(i);
                if (refs != null) {
                    outRefs.addAll(refs);
                }
            }
        }
    }

    public void accept(String ref, FeedObjectVisitor visitor) {
        visitor.visit(ref, this);
    }

    public void resolveRefs(Map<String, BaseEntity> resolvedEntities) {
        this.entitiesByRefId = new HashMap();
        this.entityRefs.resolve(resolvedEntities, this.entities, this.entitiesByRefId);
        for (int i = 0; i < 10; i++) {
            this.entitiesTypeMask[i] = calculateEntityFlags(getEntities(i));
        }
    }

    public void digest(MessageDigest digest, byte[] buffer) {
        byte b = (byte) 0;
        DigestUtils.addInt(digest, this.pattern, buffer);
        DigestUtils.addLong(digest, this.date, buffer);
        digest.update(this.message == null ? (byte) 0 : (byte) 1);
        if (this.message != null) {
            this.message.digest(digest, buffer);
        }
        if (this.title != null) {
            b = (byte) 1;
        }
        digest.update(b);
        if (this.title != null) {
            this.title.digest(digest, buffer);
        }
        this.entityRefs.digest(digest, buffer);
        DigestUtils.addInt(digest, this.dataFlags, buffer);
    }
}

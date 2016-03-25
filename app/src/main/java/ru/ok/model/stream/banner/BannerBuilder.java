package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.FeedObjectException;

public class BannerBuilder implements Parcelable {
    public static final Creator<BannerBuilder> CREATOR;
    int actionType;
    String ageRestriction;
    String clickUrl;
    int color;
    long db_id;
    String deepLink;
    String disclaimer;
    String header;
    int iconType;
    String iconUrl;
    String iconUrlHd;
    String id;
    List<PhotoSize> images;
    String info;
    float rating;
    int template;
    String text;
    String topicId;
    int users;
    VideoData videoData;
    int votes;

    /* renamed from: ru.ok.model.stream.banner.BannerBuilder.1 */
    static class C16051 implements Creator<BannerBuilder> {
        C16051() {
        }

        public BannerBuilder createFromParcel(Parcel source) {
            return new BannerBuilder(source);
        }

        public BannerBuilder[] newArray(int size) {
            return new BannerBuilder[size];
        }
    }

    public BannerBuilder() {
        this.template = -1;
        this.actionType = 0;
        this.iconType = 0;
    }

    public BannerBuilder withDbId(long db_id) {
        this.db_id = db_id;
        return this;
    }

    public BannerBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public BannerBuilder withTemplate(int template) {
        this.template = template;
        return this;
    }

    public BannerBuilder withHeader(String header) {
        this.header = header;
        return this;
    }

    public BannerBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public BannerBuilder withColor(int color) {
        this.color = color;
        return this;
    }

    public BannerBuilder withActionType(int actionType) {
        this.actionType = actionType;
        return this;
    }

    public BannerBuilder withIconType(int iconType) {
        this.iconType = iconType;
        return this;
    }

    public BannerBuilder withIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public BannerBuilder withIconUrlHd(String iconUrlHd) {
        this.iconUrlHd = iconUrlHd;
        return this;
    }

    public BannerBuilder addImage(PhotoSize image) {
        if (this.images == null) {
            this.images = new ArrayList();
        }
        this.images.add(image);
        return this;
    }

    public BannerBuilder withClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
        return this;
    }

    public BannerBuilder withDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
        return this;
    }

    public BannerBuilder withInfo(String info) {
        this.info = info;
        return this;
    }

    public BannerBuilder withAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
        return this;
    }

    public BannerBuilder withVideoData(VideoData data) {
        this.videoData = data;
        return this;
    }

    public BannerBuilder withUsers(int users) {
        this.users = users;
        return this;
    }

    public BannerBuilder withRating(float rating) {
        this.rating = rating;
        return this;
    }

    public BannerBuilder withVotes(int votes) {
        this.votes = votes;
        return this;
    }

    public BannerBuilder withTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public BannerBuilder withDeepLink(String deepLink) {
        this.deepLink = deepLink;
        return this;
    }

    public Banner build() throws FeedObjectException {
        if (this.template == -1) {
            throw new FeedObjectException("banner template id not set");
        } else if (TextUtils.isEmpty(this.header) && TextUtils.isEmpty(this.text) && TextUtils.isEmpty(this.iconUrl) && TextUtils.isEmpty(this.iconUrlHd) && this.images == null && (this.template != 6 || TextUtils.isEmpty(this.topicId))) {
            throw new FeedObjectException("banner has no content");
        } else {
            List<PhotoSize> images;
            if (this.images == null) {
                images = Collections.emptyList();
            } else {
                images = this.images;
            }
            if (this.template == 5) {
                if (this.videoData == null) {
                    throw new FeedObjectException("video banner has no video data");
                }
                return new VideoBanner(this.db_id, this.id, this.header, this.text, this.actionType, this.iconType, this.iconUrl, this.iconUrlHd, images, this.clickUrl, this.color, this.disclaimer, this.info, this.ageRestriction, this.deepLink, this.videoData);
            } else if (this.votes == 0 || this.rating == 0.0f) {
                return new Banner(this.db_id, this.id, this.template, this.header, this.text, this.actionType, this.iconType, this.iconUrl, this.iconUrlHd, images, this.clickUrl, this.color, this.disclaimer, this.info, this.ageRestriction, this.topicId, this.deepLink);
            } else {
                return new BannerWithRating(this.db_id, this.id, this.template, this.header, this.text, this.actionType, this.iconType, this.iconUrl, this.iconUrlHd, images, this.clickUrl, this.color, this.disclaimer, this.info, this.ageRestriction, this.topicId, this.deepLink, this.votes, this.users, this.rating);
            }
        }
    }

    public long getDbId() {
        return this.db_id;
    }

    public String getId() {
        return this.id;
    }

    public VideoData getVideoData() {
        return this.videoData;
    }

    public int getActionType() {
        return this.actionType;
    }

    public int getColor() {
        return this.color;
    }

    public int getIconType() {
        return this.iconType;
    }

    public int getTemplate() {
        return this.template;
    }

    public List<PhotoSize> getImages() {
        return this.images;
    }

    public String getClickUrl() {
        return this.clickUrl;
    }

    public String getDisclaimer() {
        return this.disclaimer;
    }

    public String getHeader() {
        return this.header;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getIconUrlHd() {
        return this.iconUrlHd;
    }

    public String getInfo() {
        return this.info;
    }

    public String getText() {
        return this.text;
    }

    public int getVotes() {
        return this.votes;
    }

    public int getUsers() {
        return this.users;
    }

    public float getRating() {
        return this.rating;
    }

    public String getAgeRestriction() {
        return this.ageRestriction;
    }

    public String getDeepLink() {
        return this.deepLink;
    }

    public String toString() {
        return "BannerBuilder[" + getHeader() + "]";
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.db_id);
        dest.writeString(this.id);
        dest.writeInt(this.template);
        dest.writeString(this.header);
        dest.writeString(this.text);
        dest.writeInt(this.actionType);
        dest.writeInt(this.iconType);
        dest.writeString(this.iconUrl);
        dest.writeString(this.iconUrlHd);
        dest.writeList(this.images);
        dest.writeString(this.clickUrl);
        dest.writeInt(this.color);
        dest.writeString(this.disclaimer);
        dest.writeString(this.info);
        dest.writeParcelable(this.videoData, flags);
        dest.writeInt(this.votes);
        dest.writeInt(this.users);
        dest.writeFloat(this.rating);
        dest.writeString(this.ageRestriction);
        dest.writeString(this.topicId);
        dest.writeString(this.deepLink);
    }

    protected BannerBuilder(Parcel src) {
        this.template = -1;
        this.actionType = 0;
        this.iconType = 0;
        ClassLoader cl = BannerBuilder.class.getClassLoader();
        this.db_id = src.readLong();
        this.id = src.readString();
        this.template = src.readInt();
        this.header = src.readString();
        this.text = src.readString();
        this.actionType = src.readInt();
        this.iconType = src.readInt();
        this.iconUrl = src.readString();
        this.iconUrlHd = src.readString();
        this.images = src.readArrayList(cl);
        this.clickUrl = src.readString();
        this.color = src.readInt();
        this.disclaimer = src.readString();
        this.info = src.readString();
        this.videoData = (VideoData) src.readParcelable(cl);
        this.votes = src.readInt();
        this.users = src.readInt();
        this.rating = src.readFloat();
        this.ageRestriction = src.readString();
        this.topicId = src.readString();
        this.deepLink = src.readString();
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C16051();
    }
}

package ru.ok.android.storage.serializer.stream;

import java.io.IOException;
import java.io.OutputStream;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.Address;
import ru.ok.model.AddressSerializer;
import ru.ok.model.Discussion;
import ru.ok.model.DiscussionSerializer;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupInfoSerializer;
import ru.ok.model.GroupSubCategory;
import ru.ok.model.GroupSubCategorySerializer;
import ru.ok.model.ImageUrl;
import ru.ok.model.ImageUrlSerializer;
import ru.ok.model.Location;
import ru.ok.model.LocationSerializer;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfoLocationSerializer;
import ru.ok.model.UserInfoSerializer;
import ru.ok.model.UserStatus;
import ru.ok.model.UserStatusSerializer;
import ru.ok.model.mediatopics.MediaItemAppBuilder;
import ru.ok.model.mediatopics.MediaItemAppBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemLinkBuilder;
import ru.ok.model.mediatopics.MediaItemLinkBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemMusicBuilder;
import ru.ok.model.mediatopics.MediaItemMusicBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemPhotoBuilder;
import ru.ok.model.mediatopics.MediaItemPhotoBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemPollBuilder;
import ru.ok.model.mediatopics.MediaItemPollBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemStubBuilder;
import ru.ok.model.mediatopics.MediaItemStubBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemTextBuilder;
import ru.ok.model.mediatopics.MediaItemTextBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemTopicBuilder;
import ru.ok.model.mediatopics.MediaItemTopicBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemVideoBuilder;
import ru.ok.model.mediatopics.MediaItemVideoBuilderSerializer;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfoSerializer;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoInfoSerializer;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.photo.PhotoSizeSerializer;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.ActionCountInfoSerializer;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.DiscussionSummarySerializer;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedSerializer;
import ru.ok.model.stream.FeedStringRefs;
import ru.ok.model.stream.FeedStringRefsSerializer;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.LikeInfoContextSerializer;
import ru.ok.model.stream.LikeInfoSerializer;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;
import ru.ok.model.stream.StreamPageKeySerializer;
import ru.ok.model.stream.StreamPageSerializer;
import ru.ok.model.stream.UnreadStreamPage;
import ru.ok.model.stream.UnreadStreamPageSerializer;
import ru.ok.model.stream.banner.BannerBuilder;
import ru.ok.model.stream.banner.BannerBuilderSerializer;
import ru.ok.model.stream.banner.StatPixelHolderImpl;
import ru.ok.model.stream.banner.StatPixelHolderImplSerializer;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.stream.banner.VideoDataSerializer;
import ru.ok.model.stream.banner.VideoProgressStat;
import ru.ok.model.stream.banner.VideoProgressStatSerializer;
import ru.ok.model.stream.banner.VideoStat;
import ru.ok.model.stream.banner.VideoStatSerializer;
import ru.ok.model.stream.entities.AnswerSerializer;
import ru.ok.model.stream.entities.FeedAchievementEntityBuilder;
import ru.ok.model.stream.entities.FeedAchievementEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder;
import ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAlbumEntityBuilder;
import ru.ok.model.stream.entities.FeedAlbumEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAppEntityBuilder;
import ru.ok.model.stream.entities.FeedAppEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedBannerEntityBuilder;
import ru.ok.model.stream.entities.FeedBannerEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedGroupEntityBuilder;
import ru.ok.model.stream.entities.FeedGroupEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedGroupPhotoEntityBuilder;
import ru.ok.model.stream.entities.FeedGroupPhotoEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedHolidayEntityBuilder;
import ru.ok.model.stream.entities.FeedHolidayEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilder;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicAlbumEntityBuilder;
import ru.ok.model.stream.entities.FeedMusicAlbumEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicArtistEntityBuilder;
import ru.ok.model.stream.entities.FeedMusicArtistEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicTrackEntityBuilder;
import ru.ok.model.stream.entities.FeedMusicTrackEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPlaceEntityBuilder;
import ru.ok.model.stream.entities.FeedPlaceEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPlayListEntityBuilder;
import ru.ok.model.stream.entities.FeedPlayListEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;
import ru.ok.model.stream.entities.FeedPollEntityBuilder;
import ru.ok.model.stream.entities.FeedPollEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPresentEntityBuilder;
import ru.ok.model.stream.entities.FeedPresentEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder;
import ru.ok.model.stream.entities.FeedPresentTypeEntityBulderSerializer;
import ru.ok.model.stream.entities.FeedUserEntityBuilder;
import ru.ok.model.stream.entities.FeedUserEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedUserPhotoEntityBuilder;
import ru.ok.model.stream.entities.FeedUserPhotoEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedVideoEntityBuilder;
import ru.ok.model.stream.entities.FeedVideoEntityBuilderSerializer;
import ru.ok.model.stream.message.FeedActorSpan;
import ru.ok.model.stream.message.FeedActorSpanSerializer;
import ru.ok.model.stream.message.FeedEntitySpan;
import ru.ok.model.stream.message.FeedEntitySpanSerializer;
import ru.ok.model.stream.message.FeedMessage;
import ru.ok.model.stream.message.FeedMessageSerializer;
import ru.ok.model.stream.message.FeedTargetAppSpan;
import ru.ok.model.stream.message.FeedTargetAppSpanSerializer;
import ru.ok.model.stream.message.FeedTargetGroupSpan;
import ru.ok.model.stream.message.FeedTargetGroupSpanSerializer;
import ru.ok.model.stream.message.FeedTargetSpan;
import ru.ok.model.stream.message.FeedTargetSpanSerializer;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.AlbumSerializer;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.ArtistSerializer;

public class StreamSerialOutputStream extends SimpleSerialOutputStream {
    public StreamSerialOutputStream(OutputStream out) {
        super(out);
    }

    public void writeObject(Object o) throws IOException {
        writeBoolean(o != null);
        if (o != null) {
            Class klass = o.getClass();
            if (klass == Feed.class) {
                writeInt(1);
                FeedSerializer.write(this, (Feed) o);
            } else if (klass == FeedStringRefs.class) {
                writeInt(2);
                FeedStringRefsSerializer.write(this, (FeedStringRefs) o);
            } else if (klass == FeedActorSpan.class) {
                writeInt(3);
                FeedActorSpanSerializer.write(this, (FeedActorSpan) o);
            } else if (klass == FeedEntitySpan.class) {
                writeInt(4);
                FeedEntitySpanSerializer.write(this, (FeedEntitySpan) o);
            } else if (klass == FeedTargetSpan.class) {
                writeInt(7);
                FeedTargetSpanSerializer.write(this, (FeedTargetSpan) o);
            } else if (klass == FeedTargetAppSpan.class) {
                writeInt(5);
                FeedTargetAppSpanSerializer.write(this, (FeedTargetAppSpan) o);
            } else if (klass == FeedTargetGroupSpan.class) {
                writeInt(6);
                FeedTargetGroupSpanSerializer.write(this, (FeedTargetGroupSpan) o);
            } else if (klass == FeedMessage.class) {
                writeInt(8);
                FeedMessageSerializer.write(this, (FeedMessage) o);
            } else if (klass == ActionCountInfo.class) {
                writeInt(9);
                ActionCountInfoSerializer.write(this, (ActionCountInfo) o);
            } else if (klass == LikeInfo.class) {
                writeInt(10);
                LikeInfoSerializer.write(this, (LikeInfo) o);
            } else if (klass == LikeInfoContext.class) {
                writeInt(11);
                LikeInfoContextSerializer.write(this, (LikeInfoContext) o);
            } else if (klass == Discussion.class) {
                writeInt(12);
                DiscussionSerializer.write(this, (Discussion) o);
            } else if (klass == DiscussionSummary.class) {
                writeInt(13);
                DiscussionSummarySerializer.write(this, (DiscussionSummary) o);
            } else if (klass == StreamPageKey.class) {
                writeInt(14);
                StreamPageKeySerializer.write(this, (StreamPageKey) o);
            } else if (klass == StreamPage.class) {
                writeInt(15);
                StreamPageSerializer.write(this, (StreamPage) o);
            } else if (klass == PhotoSize.class) {
                writeInt(16);
                PhotoSizeSerializer.write(this, (PhotoSize) o);
            } else if (klass == PhotoInfo.class) {
                writeInt(17);
                PhotoInfoSerializer.write(this, (PhotoInfo) o);
            } else if (klass == FeedUserPhotoEntityBuilder.class) {
                writeInt(18);
                FeedUserPhotoEntityBuilderSerializer.write(this, (FeedUserPhotoEntityBuilder) o);
            } else if (klass == FeedGroupPhotoEntityBuilder.class) {
                writeInt(19);
                FeedGroupPhotoEntityBuilderSerializer.write(this, (FeedGroupPhotoEntityBuilder) o);
            } else if (klass == FeedAchievementEntityBuilder.class) {
                writeInt(20);
                FeedAchievementEntityBuilderSerializer.write(this, (FeedAchievementEntityBuilder) o);
            } else if (klass == FeedAchievementTypeEntityBuilder.class) {
                writeInt(21);
                FeedAchievementTypeEntityBuilderSerializer.write(this, (FeedAchievementTypeEntityBuilder) o);
            } else if (klass == PhotoAlbumInfo.class) {
                writeInt(22);
                PhotoAlbumInfoSerializer.write(this, (PhotoAlbumInfo) o);
            } else if (klass == FeedAlbumEntityBuilder.class) {
                writeInt(23);
                FeedAlbumEntityBuilderSerializer.write(this, (FeedAlbumEntityBuilder) o);
            } else if (klass == FeedAppEntityBuilder.class) {
                writeInt(24);
                FeedAppEntityBuilderSerializer.write(this, (FeedAppEntityBuilder) o);
            } else if (klass == BannerBuilder.class) {
                writeInt(25);
                BannerBuilderSerializer.write(this, (BannerBuilder) o);
            } else if (klass == VideoData.class) {
                writeInt(26);
                VideoDataSerializer.write(this, (VideoData) o);
            } else if (klass == VideoStat.class) {
                writeInt(27);
                VideoStatSerializer.write(this, (VideoStat) o);
            } else if (klass == FeedBannerEntityBuilder.class) {
                writeInt(28);
                FeedBannerEntityBuilderSerializer.write(this, (FeedBannerEntityBuilder) o);
            } else if (klass == GroupInfo.class) {
                writeInt(29);
                GroupInfoSerializer.write(this, (GroupInfo) o);
            } else if (klass == Address.class) {
                writeInt(30);
                AddressSerializer.write(this, (Address) o);
            } else if (klass == Location.class) {
                writeInt(31);
                LocationSerializer.write(this, (Location) o);
            } else if (klass == GroupSubCategory.class) {
                writeInt(32);
                GroupSubCategorySerializer.write(this, (GroupSubCategory) o);
            } else if (klass == FeedGroupEntityBuilder.class) {
                writeInt(33);
                FeedGroupEntityBuilderSerializer.write(this, (FeedGroupEntityBuilder) o);
            } else if (klass == FeedHolidayEntityBuilder.class) {
                writeInt(34);
                FeedHolidayEntityBuilderSerializer.write(this, (FeedHolidayEntityBuilder) o);
            } else if (klass == MediaItemLinkBuilder.class) {
                writeInt(35);
                MediaItemLinkBuilderSerializer.write(this, (MediaItemLinkBuilder) o);
            } else if (klass == ImageUrl.class) {
                writeInt(36);
                ImageUrlSerializer.write(this, (ImageUrl) o);
            } else if (klass == MediaItemMusicBuilder.class) {
                writeInt(37);
                MediaItemMusicBuilderSerializer.write(this, (MediaItemMusicBuilder) o);
            } else if (klass == MediaItemPhotoBuilder.class) {
                writeInt(38);
                MediaItemPhotoBuilderSerializer.write(this, (MediaItemPhotoBuilder) o);
            } else if (klass == MediaItemPollBuilder.class) {
                writeInt(39);
                MediaItemPollBuilderSerializer.write(this, (MediaItemPollBuilder) o);
            } else if (klass == MediaItemTextBuilder.class) {
                writeInt(40);
                MediaItemTextBuilderSerializer.write(this, (MediaItemTextBuilder) o);
            } else if (klass == MediaItemTopicBuilder.class) {
                writeInt(41);
                MediaItemTopicBuilderSerializer.write(this, (MediaItemTopicBuilder) o);
            } else if (klass == MediaItemVideoBuilder.class) {
                writeInt(42);
                MediaItemVideoBuilderSerializer.write(this, (MediaItemVideoBuilder) o);
            } else if (klass == FeedMediaTopicEntityBuilder.class) {
                writeInt(43);
                FeedMediaTopicEntityBuilderSerializer.write(this, (FeedMediaTopicEntityBuilder) o);
            } else if (klass == Album.class) {
                writeInt(44);
                AlbumSerializer.write(this, (Album) o);
            } else if (klass == FeedMusicAlbumEntityBuilder.class) {
                writeInt(45);
                FeedMusicAlbumEntityBuilderSerializer.write(this, (FeedMusicAlbumEntityBuilder) o);
            } else if (klass == Artist.class) {
                writeInt(46);
                ArtistSerializer.write(this, (Artist) o);
            } else if (klass == FeedMusicArtistEntityBuilder.class) {
                writeInt(47);
                FeedMusicArtistEntityBuilderSerializer.write(this, (FeedMusicArtistEntityBuilder) o);
            } else if (klass == FeedMusicTrackEntityBuilder.class) {
                writeInt(48);
                FeedMusicTrackEntityBuilderSerializer.write(this, (FeedMusicTrackEntityBuilder) o);
            } else if (klass == FeedPlaceEntityBuilder.class) {
                writeInt(49);
                FeedPlaceEntityBuilderSerializer.write(this, (FeedPlaceEntityBuilder) o);
            } else if (klass == FeedPlayListEntityBuilder.class) {
                writeInt(50);
                FeedPlayListEntityBuilderSerializer.write(this, (FeedPlayListEntityBuilder) o);
            } else if (klass == Answer.class) {
                writeInt(51);
                AnswerSerializer.write(this, (Answer) o);
            } else if (klass == FeedPollEntityBuilder.class) {
                writeInt(52);
                FeedPollEntityBuilderSerializer.write(this, (FeedPollEntityBuilder) o);
            } else if (klass == FeedPresentEntityBuilder.class) {
                writeInt(53);
                FeedPresentEntityBuilderSerializer.write(this, (FeedPresentEntityBuilder) o);
            } else if (klass == FeedPresentTypeEntityBuilder.class) {
                writeInt(54);
                FeedPresentTypeEntityBulderSerializer.write(this, (FeedPresentTypeEntityBuilder) o);
            } else if (klass == UserInfo.class) {
                writeInt(55);
                UserInfoSerializer.write(this, (UserInfo) o);
            } else if (klass == UserInfo.Location.class) {
                writeInt(56);
                UserInfoLocationSerializer.write(this, (UserInfo.Location) o);
            } else if (klass == UserStatus.class) {
                writeInt(57);
                UserStatusSerializer.write(this, (UserStatus) o);
            } else if (klass == FeedUserEntityBuilder.class) {
                writeInt(58);
                FeedUserEntityBuilderSerializer.write(this, (FeedUserEntityBuilder) o);
            } else if (klass == FeedVideoEntityBuilder.class) {
                writeInt(59);
                FeedVideoEntityBuilderSerializer.write(this, (FeedVideoEntityBuilder) o);
            } else if (klass == UnreadStreamPage.class) {
                writeInt(60);
                UnreadStreamPageSerializer.write(this, (UnreadStreamPage) o);
            } else if (klass == VideoProgressStat.class) {
                writeInt(61);
                VideoProgressStatSerializer.write(this, (VideoProgressStat) o);
            } else if (klass == StatPixelHolderImpl.class) {
                writeInt(62);
                StatPixelHolderImplSerializer.write(this, (StatPixelHolderImpl) o);
            } else if (klass == MediaItemAppBuilder.class) {
                writeInt(63);
                MediaItemAppBuilderSerializer.write(this, (MediaItemAppBuilder) o);
            } else if (klass == MediaItemStubBuilder.class) {
                writeInt(64);
                MediaItemStubBuilderSerializer.write(this, (MediaItemStubBuilder) o);
            } else {
                throw new SimpleSerialException("Not simple serializable class: " + klass.getName());
            }
        }
    }
}

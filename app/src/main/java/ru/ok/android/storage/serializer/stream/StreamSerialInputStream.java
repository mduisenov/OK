package ru.ok.android.storage.serializer.stream;

import java.io.IOException;
import java.io.InputStream;
import ru.ok.android.C0206R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.model.AddressSerializer;
import ru.ok.model.DiscussionSerializer;
import ru.ok.model.GroupInfoSerializer;
import ru.ok.model.GroupSubCategorySerializer;
import ru.ok.model.ImageUrlSerializer;
import ru.ok.model.LocationSerializer;
import ru.ok.model.UserInfoLocationSerializer;
import ru.ok.model.UserInfoSerializer;
import ru.ok.model.UserStatusSerializer;
import ru.ok.model.mediatopics.MediaItemAppBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemLinkBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemMusicBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemPhotoBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemPollBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemStubBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemTextBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemTopicBuilderSerializer;
import ru.ok.model.mediatopics.MediaItemVideoBuilderSerializer;
import ru.ok.model.photo.PhotoAlbumInfoSerializer;
import ru.ok.model.photo.PhotoInfoSerializer;
import ru.ok.model.photo.PhotoSizeSerializer;
import ru.ok.model.stream.ActionCountInfoSerializer;
import ru.ok.model.stream.DiscussionSummarySerializer;
import ru.ok.model.stream.FeedSerializer;
import ru.ok.model.stream.FeedStringRefsSerializer;
import ru.ok.model.stream.LikeInfoContextSerializer;
import ru.ok.model.stream.LikeInfoSerializer;
import ru.ok.model.stream.StreamPageKeySerializer;
import ru.ok.model.stream.StreamPageSerializer;
import ru.ok.model.stream.UnreadStreamPageSerializer;
import ru.ok.model.stream.banner.BannerBuilderSerializer;
import ru.ok.model.stream.banner.StatPixelHolderImplSerializer;
import ru.ok.model.stream.banner.VideoDataSerializer;
import ru.ok.model.stream.banner.VideoProgressStatSerializer;
import ru.ok.model.stream.banner.VideoStatSerializer;
import ru.ok.model.stream.entities.AnswerSerializer;
import ru.ok.model.stream.entities.FeedAchievementEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAlbumEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedAppEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedBannerEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedGroupEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedGroupPhotoEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedHolidayEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicAlbumEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicArtistEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedMusicTrackEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPlaceEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPlayListEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPollEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPresentEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedPresentTypeEntityBulderSerializer;
import ru.ok.model.stream.entities.FeedUserEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedUserPhotoEntityBuilderSerializer;
import ru.ok.model.stream.entities.FeedVideoEntityBuilderSerializer;
import ru.ok.model.stream.message.FeedActorSpanSerializer;
import ru.ok.model.stream.message.FeedEntitySpanSerializer;
import ru.ok.model.stream.message.FeedMessageSerializer;
import ru.ok.model.stream.message.FeedTargetAppSpanSerializer;
import ru.ok.model.stream.message.FeedTargetGroupSpanSerializer;
import ru.ok.model.stream.message.FeedTargetSpanSerializer;
import ru.ok.model.wmf.AlbumSerializer;
import ru.ok.model.wmf.ArtistSerializer;

public class StreamSerialInputStream extends SimpleSerialInputStream {
    public StreamSerialInputStream(InputStream in) {
        super(in);
    }

    public <T> T readObject() throws IOException {
        if (!readBoolean()) {
            return null;
        }
        int type = readInt();
        switch (type) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return FeedSerializer.read(this);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return FeedStringRefsSerializer.read(this);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return FeedActorSpanSerializer.read(this);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return FeedEntitySpanSerializer.read(this);
            case Message.UUID_FIELD_NUMBER /*5*/:
                return FeedTargetAppSpanSerializer.read(this);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return FeedTargetGroupSpanSerializer.read(this);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return FeedTargetSpanSerializer.read(this);
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return FeedMessageSerializer.read(this);
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return ActionCountInfoSerializer.read(this);
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return LikeInfoSerializer.read(this);
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                return LikeInfoContextSerializer.read(this);
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                return DiscussionSerializer.read(this);
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                return DiscussionSummarySerializer.read(this);
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                return StreamPageKeySerializer.read(this);
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                return StreamPageSerializer.read(this);
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                return PhotoSizeSerializer.read(this);
            case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                return PhotoInfoSerializer.read(this);
            case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                return FeedUserPhotoEntityBuilderSerializer.read(this);
            case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                return FeedGroupPhotoEntityBuilderSerializer.read(this);
            case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                return FeedAchievementEntityBuilderSerializer.read(this);
            case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                return FeedAchievementTypeEntityBuilderSerializer.read(this);
            case C0206R.styleable.Toolbar_logoDescription /*22*/:
                return PhotoAlbumInfoSerializer.read(this);
            case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                return FeedAlbumEntityBuilderSerializer.read(this);
            case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                return FeedAppEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionMenuTextAppearance /*25*/:
                return BannerBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                return VideoDataSerializer.read(this);
            case C0206R.styleable.Theme_actionModeStyle /*27*/:
                return VideoStatSerializer.read(this);
            case C0206R.styleable.Theme_actionModeCloseButtonStyle /*28*/:
                return FeedBannerEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModeBackground /*29*/:
                return GroupInfoSerializer.read(this);
            case C0206R.styleable.Theme_actionModeSplitBackground /*30*/:
                return AddressSerializer.read(this);
            case C0206R.styleable.Theme_actionModeCloseDrawable /*31*/:
                return LocationSerializer.read(this);
            case C0206R.styleable.Theme_actionModeCutDrawable /*32*/:
                return GroupSubCategorySerializer.read(this);
            case C0206R.styleable.Theme_actionModeCopyDrawable /*33*/:
                return FeedGroupEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModePasteDrawable /*34*/:
                return FeedHolidayEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModeSelectAllDrawable /*35*/:
                return MediaItemLinkBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModeShareDrawable /*36*/:
                return ImageUrlSerializer.read(this);
            case C0206R.styleable.Theme_actionModeFindDrawable /*37*/:
                return MediaItemMusicBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModeWebSearchDrawable /*38*/:
                return MediaItemPhotoBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionModePopupWindowStyle /*39*/:
                return MediaItemPollBuilderSerializer.read(this);
            case C0206R.styleable.Theme_textAppearanceLargePopupMenu /*40*/:
                return MediaItemTextBuilderSerializer.read(this);
            case C0206R.styleable.Theme_textAppearanceSmallPopupMenu /*41*/:
                return MediaItemTopicBuilderSerializer.read(this);
            case C0206R.styleable.Theme_dialogTheme /*42*/:
                return MediaItemVideoBuilderSerializer.read(this);
            case C0206R.styleable.Theme_dialogPreferredPadding /*43*/:
                return FeedMediaTopicEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_listDividerAlertDialog /*44*/:
                return AlbumSerializer.read(this);
            case C0206R.styleable.Theme_actionDropDownStyle /*45*/:
                return FeedMusicAlbumEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_dropdownListPreferredItemHeight /*46*/:
                return ArtistSerializer.read(this);
            case C0206R.styleable.Theme_spinnerDropDownItemStyle /*47*/:
                return FeedMusicArtistEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_homeAsUpIndicator /*48*/:
                return FeedMusicTrackEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_actionButtonStyle /*49*/:
                return FeedPlaceEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_buttonBarStyle /*50*/:
                return FeedPlayListEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_buttonBarButtonStyle /*51*/:
                return AnswerSerializer.read(this);
            case C0206R.styleable.Theme_selectableItemBackground /*52*/:
                return FeedPollEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_selectableItemBackgroundBorderless /*53*/:
                return FeedPresentEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_borderlessButtonStyle /*54*/:
                return FeedPresentTypeEntityBulderSerializer.read(this);
            case C0206R.styleable.Theme_dividerVertical /*55*/:
                return UserInfoSerializer.read(this);
            case C0206R.styleable.Theme_dividerHorizontal /*56*/:
                return UserInfoLocationSerializer.read(this);
            case C0206R.styleable.Theme_activityChooserViewStyle /*57*/:
                return UserStatusSerializer.read(this);
            case C0206R.styleable.Theme_toolbarStyle /*58*/:
                return FeedUserEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_toolbarNavigationButtonStyle /*59*/:
                return FeedVideoEntityBuilderSerializer.read(this);
            case C0206R.styleable.Theme_popupMenuStyle /*60*/:
                return UnreadStreamPageSerializer.read(this);
            case C0206R.styleable.Theme_popupWindowStyle /*61*/:
                return VideoProgressStatSerializer.read(this);
            case C0206R.styleable.Theme_editTextColor /*62*/:
                return StatPixelHolderImplSerializer.read(this);
            case C0206R.styleable.Theme_editTextBackground /*63*/:
                return MediaItemAppBuilderSerializer.read(this);
            case C0206R.styleable.Theme_textAppearanceSearchResultTitle /*64*/:
                return MediaItemStubBuilderSerializer.read(this);
            default:
                try {
                    throw new SimpleSerialException("Unexpected type: " + type);
                } catch (ClassCastException e) {
                    throw new SimpleSerialException("Type mismatch: " + e, e);
                }
        }
        throw new SimpleSerialException("Type mismatch: " + e, e);
    }
}

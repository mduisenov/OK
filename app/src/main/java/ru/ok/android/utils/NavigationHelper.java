package ru.ok.android.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.fragments.PaymentWebFragment;
import ru.ok.android.fragments.PymkFragment;
import ru.ok.android.fragments.adman.AdmanBannersFragment;
import ru.ok.android.fragments.groups.GroupWebFragment;
import ru.ok.android.fragments.image.PhotoPaymentWebFragment;
import ru.ok.android.fragments.marks.MarksWebFragment;
import ru.ok.android.fragments.music.AlbumFragment;
import ru.ok.android.fragments.music.AlbumsFragment;
import ru.ok.android.fragments.music.ArtistFragment;
import ru.ok.android.fragments.music.CustomPlayListFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.MusicPlayListFragment;
import ru.ok.android.fragments.music.SimilarPlayListFragment;
import ru.ok.android.fragments.music.collections.MusicCollectionFragment;
import ru.ok.android.fragments.music.pop.PopMusicFragment;
import ru.ok.android.fragments.music.tuners.MusicTunersFragment;
import ru.ok.android.fragments.music.users.MyMusicFragment;
import ru.ok.android.fragments.music.users.UserMusicFragment;
import ru.ok.android.fragments.notification.NotificationWebFragment;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.ui.NotLoggedInWebActivity;
import ru.ok.android.ui.activity.PlayListActivity;
import ru.ok.android.ui.activity.PlayerActivity;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.fragments.PlayerFragment;
import ru.ok.android.ui.fragments.SearchMusicFragment;
import ru.ok.android.ui.fragments.StubFragment;
import ru.ok.android.ui.fragments.messages.ConversationParticipantsFragment;
import ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.fragments.messages.MessagesFragment;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.users.UserTopicsFragment;
import ru.ok.android.ui.fragments.users.UsersLikedDiscussionCommentFragment;
import ru.ok.android.ui.fragments.users.UsersLikedDiscussionFragment;
import ru.ok.android.ui.groups.activity.ProfileGroupsActivity;
import ru.ok.android.ui.groups.fragments.CommunityUsersFragment;
import ru.ok.android.ui.groups.fragments.GroupTopicsFragment;
import ru.ok.android.ui.groups.fragments.GroupsComboFragment;
import ru.ok.android.ui.groups.fragments.GroupsFragment;
import ru.ok.android.ui.groups.fragments.SingletonGroupTopicsListFragment;
import ru.ok.android.ui.mediatopics.MediaTopicTextEditActivity;
import ru.ok.android.ui.messaging.activity.MessageEditActivity;
import ru.ok.android.ui.messaging.activity.MessagesActivity;
import ru.ok.android.ui.messaging.activity.SelectFriendsForChatActivity;
import ru.ok.android.ui.nativeRegistration.FirstEnterActivity;
import ru.ok.android.ui.nativeRegistration.NativeLoginActivity;
import ru.ok.android.ui.places.fragments.PlaceLocationFragment;
import ru.ok.android.ui.polls.AppPollsActivity;
import ru.ok.android.ui.presents.activity.PreloadSendPresentActivity;
import ru.ok.android.ui.presents.fragment.UserPresentsFragment;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.search.activity.SearchActivity;
import ru.ok.android.ui.search.fragment.SearchByCommunityFragment;
import ru.ok.android.ui.search.fragment.SearchClassmatesFragment;
import ru.ok.android.ui.settings.SettingsActivity;
import ru.ok.android.ui.settings.SettingsProfileActivity;
import ru.ok.android.ui.stream.StreamUserFragment;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.ui.users.activity.ProfileUserActivity;
import ru.ok.android.ui.users.activity.RecommendedUsersActivity;
import ru.ok.android.ui.users.activity.SearchFriendsActivity;
import ru.ok.android.ui.users.activity.SelectFriendsActivity;
import ru.ok.android.ui.users.fragments.FragmentFriends;
import ru.ok.android.ui.users.fragments.FragmentGuest;
import ru.ok.android.ui.users.fragments.FriendsTabFragment;
import ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment;
import ru.ok.android.ui.video.VideoWebFragment;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.animation.PlayerAnimationHelper;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.videochat.MakeCallManager;
import ru.ok.android.videochat.MakeCallManager.OnCallErrorListener;
import ru.ok.android.widget.menuitems.SlidingMenuHelper;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.java.api.request.presents.PresentsRequest.Direction;
import ru.ok.model.Address;
import ru.ok.model.Discussion;
import ru.ok.model.Location;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.search.SearchType;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.UserTrackCollection;

public class NavigationHelper {
    private static String EXTRA_INTENT_FROM_LAST_ACTIVITY;

    public interface FragmentsPresenter {
        boolean canShowFragmentOnLocation(FragmentLocation fragmentLocation);

        Fragment showFragment(ActivityExecutor activityExecutor);
    }

    /* renamed from: ru.ok.android.utils.NavigationHelper.1 */
    static class C14251 implements OnCallErrorListener {
        final /* synthetic */ Context val$context;

        C14251(Context context) {
            this.val$context = context;
        }

        public void onCallError() {
            Toast.makeText(this.val$context, LocalizationManager.from(this.val$context).getString(2131165462), 0).show();
        }
    }

    public enum FragmentLocation {
        top,
        left,
        right,
        right_small,
        center
    }

    public enum Source {
        sliding_menu,
        tab_bar,
        back,
        short_link,
        app_hook,
        st_cmd,
        fake_back_stack,
        other_user,
        other_custom
    }

    public enum Tag {
        feed,
        discussion,
        conversation,
        music
    }

    public static void showArtistSimilarPage(Activity context, Artist artist, MusicFragmentMode mode) {
        OdklSubActivity.startActivityShowFragment(context, SimilarPlayListFragment.class, SimilarPlayListFragment.newArguments(artist, mode), FragmentLocation.right);
    }

    public static void showDiscussionCommentsFragment(Activity activity, Discussion discussion, Page page, String interseptedUrl) {
        showDiscussionCommentsFragment(activity, discussion, page, interseptedUrl, ActivityOptionsCompat.makeCustomAnimation(activity, 2130968586, 2130968587).toBundle());
    }

    public static void showPymk(Activity activity) {
        String url = SlidingMenuHelper.getUrl(Type.pymk);
        if (!TextUtils.isEmpty(url)) {
            showExternalUrlPage(activity, url, false, Type.pymk);
        }
    }

    public static void showDiscussionCommentsFragment(Activity activity, Discussion discussion, Page page, String interseptedUrl, Bundle options) {
        ActivityExecutor builder = new ActivityExecutor(activity, DiscussionCommentsFragment.class);
        builder.setArguments(DiscussionCommentsFragment.newArguments(discussion, page, interseptedUrl));
        builder.setFragmentLocation(FragmentLocation.right);
        builder.setActivityCompatOption(options);
        builder.setNeedToolbar(false);
        builder.setAddToBackStack(false);
        builder.setTag("tag_discussion_comments");
        builder.execute();
    }

    public static void showDiscussionLikes(Activity context, Discussion discussion) {
        showDiscussionLikes(context, discussion, false);
    }

    public static void showDiscussionLikes(Activity context, Discussion discussion, boolean selfLike) {
        OdklSubActivity.startActivityShowFragment(context, UsersLikedDiscussionFragment.class, UsersLikedDiscussionFragment.newArguments(discussion, selfLike));
    }

    public static void showDiscussionLikes(Activity context, Discussion discussion, Bundle options) {
        OdklSubActivity.startActivityShowFragment(context, UsersLikedDiscussionFragment.class, UsersLikedDiscussionFragment.newArguments(discussion), options);
    }

    public static void showAlbumsPage(Activity context, Artist artist, MusicFragmentMode mode) {
        showAlbumsPage(context, artist, mode, false);
    }

    public static void showAlbumsPage(Activity context, Artist artist, MusicFragmentMode mode, boolean showPlayerButton) {
        OdklSubActivity.startActivityShowFragment(context, AlbumsFragment.class, AlbumsFragment.newArguments(artist, mode, showPlayerButton), FragmentLocation.right);
    }

    public static void showArtistPage(Activity context, Artist artist, MusicFragmentMode mode) {
        OdklSubActivity.startActivityShowFragment(context, ArtistFragment.class, ArtistFragment.newArguments(artist, mode), FragmentLocation.right);
    }

    public static void showAlbumPage(Activity context, Album album, MusicFragmentMode mode) {
        OdklSubActivity.startActivityShowFragment(context, AlbumFragment.class, AlbumFragment.newArguments(album, mode), FragmentLocation.right);
    }

    public static void showUserInfo(Activity activity, String userId) {
        showUserInfo(activity, userId, null);
    }

    public static void showUserInfo(Activity activity, String userId, String extraAction) {
        if (TextUtils.equals(OdnoklassnikiApplication.getCurrentUser().uid, userId)) {
            showCurrentUser(activity, false);
            return;
        }
        Intent profileIntent = new Intent(activity, ProfileUserActivity.class);
        profileIntent.putExtra("user_input", userId);
        profileIntent.setData(Uri.parse("http://www.odnoklassniki.ru/profile/" + userId));
        profileIntent.putExtra("pending_action", extraAction);
        activity.startActivity(profileIntent);
    }

    public static void showGroupTopics(Activity context, String groupId) {
        OdklSubActivity.startActivityShowFragment(context, GroupTopicsFragment.class, GroupTopicsFragment.newArgumentsGroupTag(groupId, null));
    }

    public static void showGroupTopicRejected(Activity context, String groupId, String topicId) {
        OdklSubActivity.startActivityShowFragment(context, SingletonGroupTopicsListFragment.class, SingletonGroupTopicsListFragment.newArguments(groupId, topicId));
    }

    public static void showUserTopics(Activity context, String userId) {
        showUserTopics(context, userId, null);
    }

    public static void showUserTopics(Activity context, String userId, String filter) {
        OdklSubActivity.startActivityShowFragment(context, UserTopicsFragment.class, UserTopicsFragment.newArguments(userId, filter));
    }

    public static void showGroupTopics(Activity context, String groupId, Long tagId) {
        OdklSubActivity.startActivityShowFragment(context, GroupTopicsFragment.class, GroupTopicsFragment.newArgumentsGroupTag(groupId, tagId));
    }

    public static void showGroupTopicsFilter(Activity context, String groupId, String filter) {
        OdklSubActivity.startActivityShowFragment(context, GroupTopicsFragment.class, GroupTopicsFragment.newArgumentsGroupFilter(groupId, filter));
    }

    public static void showVideos(Activity context, boolean fromLeftMenu) {
        OdklSubActivity.startActivityShowFragment(context, VideoWebFragment.class, null, fromLeftMenu);
    }

    public static void showAddressLocation(Activity activity, Location location, Address address, String placeName) {
        ActivityExecutor builder = new ActivityExecutor(activity, PlaceLocationFragment.class);
        builder.setArguments(PlaceLocationFragment.newArguments(location, address, placeName));
        builder.setFragmentLocation(FragmentLocation.center);
        builder.setNeedToolbar(false);
        builder.setAddToBackStack(false);
        builder.setSlidingMenuEnable(false);
        builder.execute();
    }

    public static void startPhotoUploadSequence(Activity activity, PhotoAlbumInfo albumInfo, int choiceMode, int uploadTarget) {
        activity.startActivity(IntentUtils.createIntentToAddImages(activity, albumInfo, choiceMode, uploadTarget, true, true, "imgupldr"));
    }

    public static void showGroupPhotoAlbums(Context context, String gid) {
        startActivityWithoutDuplicate(context, IntentUtils.createIntentForGroupAlbums(context, gid));
    }

    public static void showGroupPhotoAlbum(Context context, String gid, String aid) {
        startActivityWithoutDuplicate(context, IntentUtils.createIntentForGroupAlbum(context, gid, aid));
    }

    public static void showUserPhotoAlbums(Context context, String uid, boolean fromLeftMenuActivity) {
        startActivityWithoutDuplicate(context, IntentUtils.createIntentForUserAlbums(context, uid, fromLeftMenuActivity));
    }

    public static void showUserPhotoAlbum(Context context, String uid, String aid) {
        startActivityWithoutDuplicate(context, IntentUtils.createIntentForUserAlbum(context, uid, aid));
    }

    public static void showPhoto(Context context, PhotoOwner photoOwner, String aid, String pid, int sourceId) {
        showPhoto(context, photoOwner, aid, pid, null, sourceId);
    }

    public static void showPhoto(Context context, PhotoOwner photoOwner, String aid, String pid, String[] spids, int sourceId) {
        startActivityWithoutDuplicate(context, IntentUtils.createIntentForPhotoView(context, photoOwner, aid, pid, spids, sourceId));
    }

    public static void showPhoto(Activity activity, Intent intent, Bundle animationBundle) {
        intent.putExtra("pla_animation_bundle", animationBundle);
        startActivityWithoutDuplicate(activity, intent);
        if (animationBundle != null) {
            activity.overridePendingTransition(0, 0);
        }
    }

    public static void showAttachImage(Activity activity, Bundle animationBundle, ArrayList<Attachment> attachments, Attachment selected, int sourceId) {
        Intent intent = IntentUtils.createIntentForAttachView(activity, attachments, selected, sourceId);
        intent.putExtra("pla_animation_bundle", animationBundle);
        startActivityWithoutDuplicate(activity, intent);
        if (animationBundle != null) {
            activity.overridePendingTransition(0, 0);
        }
    }

    public static void onCallUser(Context context, String userId) {
        if (MakeCallManager.isCallSupports()) {
            MakeCallManager makeCallManager = MakeCallManager.createCallManager(context, userId);
            makeCallManager.setListenerCallError(new C14251(context));
            makeCallManager.call();
            return;
        }
        Toast.makeText(context, LocalizationManager.from(context).getString(2131165467), 0).show();
    }

    public static void showFeedbackPage(Activity context, boolean fromLeftMenu) {
        showPage(context, NotLoggedInWebFragment.Page.FeedBack, fromLeftMenu);
    }

    public static void showFaqPage(Activity context, boolean fromLeftMenu) {
        showPage(context, NotLoggedInWebFragment.Page.Faq, fromLeftMenu);
    }

    private static void showPage(Activity context, NotLoggedInWebFragment.Page page, boolean fromLeftMenu) {
        OdklSubActivity.startActivityShowFragment(context, NotLoggedInWebFragment.class, NotLoggedInWebFragment.newArguments(page, true), fromLeftMenu);
    }

    public static void showExternalUrlPage(Activity context, String url, boolean fromLeftMenu) {
        OdklSubActivity.startActivityShowFragment(context, ExternalUrlWebFragment.class, ExternalUrlWebFragment.newArguments(url), fromLeftMenu);
    }

    public static void showExternalUrlPage(Activity context, String url, boolean fromLeftMenu, boolean isNeedToolbar) {
        OdklSubActivity.startActivityShowFragment(context, ExternalUrlWebFragment.class, ExternalUrlWebFragment.newArguments(url), fromLeftMenu, isNeedToolbar);
    }

    public static void showExternalUrlPage(Activity context, String url, SoftInputType inputType) {
        OdklSubActivity.startActivityShowFragment(context, ExternalUrlWebFragment.class, ExternalUrlWebFragment.newArguments(url), inputType);
    }

    public static void showExternalUrlPage(Activity context, String url, boolean fromLeftMenu, Type type) {
        OdklSubActivity.startActivityShowFragment(context, ExternalUrlWebFragment.class, ExternalUrlWebFragment.newArguments(url, type), fromLeftMenu, type.isNeedTabBar());
    }

    public static void showSettings(Activity activity, boolean allowNonLoginState) {
        Intent intentSettings = new Intent(activity, SettingsActivity.class);
        intentSettings.putExtra("allow_non_login_state", allowNonLoginState);
        activity.startActivityForResult(intentSettings, 790);
    }

    public static void showVideo(Context context, String videoId, String videoUrl) {
        showVideo(context, videoId, videoUrl, null);
    }

    public static void showVideo(Context context, String videoId, String videoUrl, VideoData videoData) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("VIDEO_URL", videoUrl);
        intent.putExtra("VIDEO_STAT_DATA", videoData);
        startActivityWithoutDuplicate(context, intent);
    }

    public static void showCommentLikes(Activity context, Discussion discussion, String commentId) {
        OdklSubActivity.startActivityShowFragment(context, UsersLikedDiscussionCommentFragment.class, UsersLikedDiscussionCommentFragment.newArguments(discussion, commentId));
    }

    public static void showNotificationsPage(Activity context, boolean fromMenu) {
        ActivityExecutor executor = new ActivityExecutor(context, NotificationWebFragment.class);
        executor.setActivityFromMenu(fromMenu);
        executor.setSlidingMenuEnable(true);
        executor.execute();
    }

    public static Intent createIntentForNotificationPage(Context context) {
        Intent intent = OdklSubActivity.createIntent(context, NotificationWebFragment.class, null, FragmentLocation.center);
        intent.putExtra(OdklSubActivity.FLAG_LAUNCH_NEW_ACTIVITY_ON_NEW_INTENT, true);
        intent.putExtra("key_toolbar_visible", true);
        return intent;
    }

    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }

    public static void showMessagesForConversation(Activity activity, String conversationId, String userId, ArrayList<MediaInfo> mediaInfosToSend) {
        showMessages(activity, MessagesFragment.newArgumentsConversation(conversationId, userId, !(activity instanceof OdklActivity), mediaInfosToSend), conversationId);
    }

    public static void showMessagesForConversation(Activity activity, String conversationId, String userId) {
        showMessagesForConversation(activity, conversationId, userId, null);
    }

    public static void showMessagesForUser(Activity activity, String userId) {
        showMessagesForUser(activity, userId, null);
    }

    public static void showMessagesForUser(Activity activity, String userId, ArrayList<MediaInfo> mediaInfosToSend) {
        showMessages(activity, MessagesFragment.newArgumentsUser(userId, !(activity instanceof OdklActivity), mediaInfosToSend), userId);
    }

    private static void showMessages(Activity activity, Bundle arguments, String userId) {
        if (activity != null) {
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(activity, 2130968586, 2130968587).toBundle();
            Class clazz = MessagesFragment.class;
            if (TextUtils.isEmpty(userId)) {
                if (DeviceUtils.getType(activity) == DeviceLayoutType.LARGE) {
                    clazz = StubFragment.class;
                    arguments = StubFragment.newArguments(LocalizationManager.getString((Context) activity, 2131166499), LocalizationManager.getString((Context) activity, 2131165638));
                } else {
                    return;
                }
            }
            ActivityExecutor builder = new ActivityExecutor(activity, clazz);
            builder.setArguments(arguments);
            builder.setFragmentLocation(FragmentLocation.right);
            builder.setActivityCompatOption(bundle);
            builder.setNeedToolbar(false);
            builder.setAddToBackStack(false);
            builder.setTag("tag_messages");
            builder.execute();
        }
    }

    public static void showConversationParticipants(Activity activity, String conversationId) {
        ActivityExecutor builder = new ActivityExecutor(activity, ConversationParticipantsFragment.class);
        builder.setArguments(ConversationParticipantsFragment.newArguments(conversationId));
        builder.setFragmentLocation(FragmentLocation.center);
        builder.setNeedToolbar(false);
        builder.setAddToBackStack(false);
        builder.setSlidingMenuEnable(false);
        builder.execute();
    }

    public static void showMyMusicPage(Activity activity, int page, MusicFragmentMode mode) {
        if (OdnoklassnikiApplication.getCurrentUser() != null && !TextUtils.isEmpty(OdnoklassnikiApplication.getCurrentUser().uid)) {
            ActivityExecutor builder = new ActivityExecutor(activity, MyMusicFragment.class);
            builder.setArguments(MyMusicFragment.newArguments(mode, page));
            builder.setFragmentLocation(FragmentLocation.right);
            builder.setActivityCompatOption(null);
            builder.setNeedToolbar(true);
            builder.setSlidingMenuEnable(true);
            builder.execute();
        } else if (activity == null) {
        }
    }

    public static void showUserMusicPage(Activity activity, String userId, MusicFragmentMode mode) {
        if (activity != null) {
            String text = LocalizationManager.getString((Context) activity, 2131166500);
            String title = LocalizationManager.getString((Context) activity, 2131166223);
            Class clazz = UserMusicFragment.class;
            Bundle arguments = UserMusicFragment.newArguments(userId, mode);
            if (TextUtils.isEmpty(userId)) {
                if (DeviceUtils.getType(activity) == DeviceLayoutType.LARGE) {
                    clazz = StubFragment.class;
                    arguments = StubFragment.newArguments(text, title);
                } else {
                    return;
                }
            }
            ActivityExecutor builder = new ActivityExecutor(activity, clazz);
            builder.setArguments(arguments);
            builder.setFragmentLocation(FragmentLocation.right);
            builder.setAddToBackStack(false);
            builder.setSlidingMenuEnable(true);
            builder.execute();
        }
    }

    public static void showNewMusicPlaylist(Activity activity, MusicFragmentMode mode) {
        ActivityExecutor builder = new ActivityExecutor(activity, PopMusicFragment.class);
        builder.setArguments(PopMusicFragment.newArguments(mode));
        builder.setFragmentLocation(FragmentLocation.right);
        builder.setSlidingMenuEnable(true);
        builder.setTag("tag_new_music");
        builder.execute();
    }

    public static void showMusicTuners(Activity activity) {
        ActivityExecutor builder = new ActivityExecutor(activity, MusicTunersFragment.class);
        builder.setFragmentLocation(FragmentLocation.right);
        builder.setSlidingMenuEnable(true);
        builder.setAddToBackStack(false);
        builder.execute();
    }

    public static void showCurrentUser(Activity activity, boolean fromMenu) {
        Intent profileIntent = new Intent(activity, ProfileUserActivity.class);
        profileIntent.putExtra("is_back_to_feed", fromMenu);
        activity.startActivity(profileIntent);
    }

    public static void showPayment(Activity activity, boolean fromLeftMenu) {
        OdklSubActivity.startActivityShowFragment(activity, PaymentWebFragment.class, null, fromLeftMenu);
    }

    public static void showSearchMusic(Activity activity, String startText, MusicFragmentMode mode) {
        ActivityExecutor builder = new ActivityExecutor(activity, SearchMusicFragment.class);
        builder.setArguments(SearchMusicFragment.newArguments(startText, mode));
        builder.setSoftInputType(SoftInputType.PAN);
        builder.setFragmentLocation(FragmentLocation.right);
        builder.setNeedToolbar(true);
        builder.execute();
    }

    public static void showMusicTrack(Activity activity, long trackId) {
        ActivityExecutor builder = new ActivityExecutor(activity, CustomPlayListFragment.class);
        builder.setArguments(CustomPlayListFragment.newArguments(trackId));
        builder.setSoftInputType(SoftInputType.PAN);
        builder.setFragmentLocation(FragmentLocation.right);
        builder.setNeedToolbar(true);
        builder.execute();
    }

    public static void showWebSettings(Activity activity) {
        startActivityWithoutDuplicate(activity, new Intent(activity, SettingsProfileActivity.class));
    }

    public static void showMusicPlayer(Activity activity) {
        showMusicPlayer(activity, false);
    }

    public static void showMusicPlayer(Activity activity, boolean animate) {
        boolean z = true;
        if (DeviceUtils.getType(activity) == DeviceLayoutType.SMALL) {
            Intent intent = new Intent(activity, PlayerActivity.class);
            String str = "extra_animate";
            if (!(animate && PlayerAnimationHelper.isAnimationEnabled())) {
                z = false;
            }
            intent.putExtra(str, z);
            startActivityWithoutDuplicate(activity, intent);
            if (animate) {
                activity.overridePendingTransition(2130968611, 2130968612);
            }
        } else if (!(activity instanceof ProfileUserActivity)) {
            ActivityExecutor activityExecutor = new ActivityExecutor(activity, PlayerFragment.class);
            activityExecutor.setFragmentLocation(FragmentLocation.top);
            activityExecutor.setAddToBackStack(false);
            activityExecutor.setNeedToolbar(true);
            activityExecutor.setTag("tag_music_player");
            activityExecutor.execute();
        }
    }

    public static void showPlayerPlayListPage(Activity activity) {
        if (DeviceUtils.getType(activity) == DeviceLayoutType.SMALL) {
            startActivityWithoutDuplicate(activity, new Intent(activity, PlayListActivity.class));
            return;
        }
        ActivityExecutor builder = new ActivityExecutor(activity, MusicPlayListFragment.class);
        builder.setFragmentLocation(FragmentLocation.right);
        builder.execute();
    }

    public static void showMusicCollectionFragment(Activity activity, UserTrackCollection collection, MusicListType type, MusicFragmentMode mode) {
        ActivityExecutor builder = new ActivityExecutor(activity, MusicCollectionFragment.class);
        builder.setArguments(MusicCollectionFragment.newArguments(collection, type, mode));
        builder.setFragmentLocation(FragmentLocation.right);
        builder.execute();
    }

    public static void showAdmanBannersFragment(Activity activity, String sectionName) {
        ActivityExecutor builder = new ActivityExecutor(activity, AdmanBannersFragment.class);
        builder.setArguments(AdmanBannersFragment.newArguments(sectionName));
        builder.setFragmentLocation(FragmentLocation.center);
        builder.execute();
    }

    public static void showGroupInfo(Activity activity, String groupId, String extraAction) {
        Intent profileIntent = new Intent(activity, ProfileGroupsActivity.class);
        profileIntent.putExtra("group_input", groupId);
        profileIntent.putExtra("pending_action", extraAction);
        activity.startActivity(profileIntent);
    }

    public static void showGroups(Activity activity) {
        showGroups(activity, null, false);
    }

    public static void showGroupsFromMenu(Activity activity) {
        showGroups(activity, null, true);
    }

    public static void showGroups(Activity activity, String categoryId) {
        showGroups(activity, categoryId, false);
    }

    private static void showGroups(Activity activity, String categoryId, boolean fromLeftMenu) {
        new ActivityExecutor(activity, GroupsComboFragment.class).setArguments(categoryId == null ? null : GroupsComboFragment.newArguments(categoryId)).setActivityFromMenu(fromLeftMenu).setNeedToolbar(true).setSoftInputType(SoftInputType.PAN).execute();
    }

    public static void showCurrentUserGroups(Activity activity) {
        showUserGroups(activity, null);
    }

    public static void showUserGroups(Activity activity, String userId) {
        new ActivityExecutor(activity, GroupsFragment.class).setArguments(userId == null ? null : GroupsFragment.newArguments(userId)).setActivityFromMenu(false).setNeedToolbar(true).setAddToBackStack(true).setSoftInputType(SoftInputType.PAN).execute();
    }

    public static void showGroupInfo(Activity activity, String groupId) {
        showGroupInfo(activity, groupId, null);
    }

    public static void showGroupInfoWeb(Activity activity, String groupId) {
        OdklSubActivity.startActivityShowFragment(activity, GroupWebFragment.class, GroupWebFragment.newArguments(groupId));
    }

    public static void showPhotoAlbum(Activity activity, PhotoAlbumInfo albumInfo) {
        if (albumInfo.getOwnerType() == OwnerType.GROUP) {
            showGroupPhotoAlbum(activity, albumInfo.getGroupId(), albumInfo.getId());
        } else {
            showUserPhotoAlbum(activity, albumInfo.getUserId(), albumInfo.getId());
        }
    }

    public static void showFeedPage(Activity activity, Source source) {
        startActivityWithoutDuplicate(activity, createIntent(activity, Tag.feed, source));
    }

    public static void showFeedPage(Activity activity, Source source, Tag currentTag) {
        Intent intent = createIntent(activity, Tag.feed, source);
        intent.putExtra("current_tag", currentTag);
        startActivityWithoutDuplicate(activity, intent);
    }

    public static void showDiscussionPage(Activity activity) {
        startActivityWithoutDuplicate(activity, createIntentForTag(activity, Tag.discussion));
    }

    public static void showConversationsPage(Activity activity) {
        startActivityWithoutDuplicate(activity, createIntentForTag(activity, Tag.conversation));
    }

    public static void showMusicPage(Activity activity) {
        startActivityWithoutDuplicate(activity, createIntentForTag(activity, Tag.music));
    }

    public static void showMarksPage(Activity activity) {
        new ActivityExecutor(activity, MarksWebFragment.class).setActivityFromMenu(true).setFragmentLocation(FragmentLocation.center).setSoftInputType(SoftInputType.PAN).setAddToBackStack(false).execute();
    }

    public static void showGuestPage(Activity activity) {
        ActivityExecutor executor = new ActivityExecutor(activity, FragmentGuest.class);
        executor.setActivityFromMenu(true);
        executor.execute();
    }

    public static void showUserStreamPage(Activity activity, String uid) {
        ActivityExecutor executor = new ActivityExecutor(activity, StreamUserFragment.class);
        executor.setArguments(StreamUserFragment.newArguments(uid));
        executor.setActivityFromMenu(false);
        executor.execute();
    }

    public static void showGroupStreamPage(Activity activity, String gid) {
        showGroupInfo(activity, gid);
    }

    public static void showSearchPage(Activity activity, View view) {
        showSearchPage(activity, view, null, null);
    }

    public static void showSearchPage(Activity activity, View view, String query, SearchType searchType) {
        if (!(activity instanceof SearchActivity)) {
            Bundle opts = null;
            if (view != null) {
                opts = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle();
            }
            Intent intent = new Intent(activity, SearchActivity.class);
            intent.putExtra("saquery", query);
            intent.putExtra("satype", searchType);
            ActivityCompat.startActivity(activity, intent, opts);
        }
    }

    public static Intent createIntentForTag(Context context, Tag tag) {
        return createIntent(context, tag, null);
    }

    public static Intent createIntent(Context context, Tag tag, Source source) {
        Intent intent = createIntentForOdklActivity(context);
        if (tag != null) {
            intent.putExtra("extra_need_screen", tag.toString());
        }
        if (source != null) {
            intent.putExtra("extra_navigation_source", source.toString());
        }
        return intent;
    }

    public static void showFilterableUsers(Fragment fragment, int requestCode, boolean returnUserId) {
        Intent intent = new Intent(fragment.getActivity(), SearchFriendsActivity.class);
        intent.putExtra("return_user_id", returnUserId);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntentForShowUser(Context context, String userId) {
        Intent profileIntent = new Intent(context, ProfileUserActivity.class);
        if (!OdnoklassnikiApplication.getCurrentUser().uid.equals(userId)) {
            profileIntent.putExtra("user_input", userId);
        }
        return profileIntent;
    }

    public static Intent createIntentForUrl(Context context, String url, Class<? extends OdklSubActivity> activityClass) {
        Bundle args = ExternalUrlWebFragment.newArguments(url);
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("key_class_name", ExternalUrlWebFragment.class);
        intent.putExtra("key_argument_name", args);
        return intent;
    }

    public static Intent createIntentForShowMessagesForUser(Context context, String userId) {
        Intent intent = createIntentForOdklActivity(context);
        intent.setAction("ru.ok.android.ui.OdklActivity.SHOW_MESSAGES");
        intent.putExtra("key_toolbar_visible", false);
        intent.putExtra("uid", userId);
        intent.putExtra("FORCE_PROCESS_INTENT", true);
        return intent;
    }

    public static Intent createIntentForShowMessagesForConversation(Context context, String conversationId) {
        Intent intent = createIntentForOdklActivity(context);
        intent.setAction("ru.ok.android.ui.OdklActivity.SHOW_MESSAGES");
        intent.putExtra("key_toolbar_visible", false);
        intent.putExtra("CONVERSATION_ID", conversationId);
        intent.putExtra("FORCE_PROCESS_INTENT", true);
        return intent;
    }

    public static Intent smartLaunchMessagesIntent(Context context, String conversationId) {
        if (DeviceUtils.getType(context) == DeviceLayoutType.LARGE) {
            return createIntentForShowMessagesForConversation(context, conversationId);
        }
        Intent result = new Intent(context, MessagesActivity.class);
        result.putExtra("CONVERSATION_ID", conversationId);
        return result;
    }

    public static Intent createIntentForShowDiscussion(Context context, Discussion discussion, Page page) {
        Intent intent = createIntentForOdklActivity(context);
        intent.setAction("ru.ok.android.ui.OdklActivity.SHOW_DISCUSSIONS");
        intent.putExtra("extra_discussion", discussion);
        intent.putExtra("fragment_is_dialog", true);
        intent.putExtra("key_toolbar_visible", false);
        intent.putExtra("FORCE_PROCESS_INTENT", true);
        intent.putExtra("extra_discussion_page", page.index);
        return intent;
    }

    public static void showPhotoMarkPayment(Activity activity, String aid, String pid, String fid) {
        ActivityExecutor builder = new ActivityExecutor(activity, PhotoPaymentWebFragment.class);
        builder.setArguments(PhotoPaymentWebFragment.newArguments(aid, pid, fid));
        builder.execute();
    }

    public static void startActivityWithoutDuplicate(Context context, Intent intent) {
        if (intent.getComponent() != null && context.getClass().getName().equals(intent.getComponent().getClassName())) {
            Activity activity = (Activity) context;
            if (Utils.equalBundles(activity.getIntent() == null ? null : activity.getIntent().getExtras(), intent.getExtras())) {
                new Instrumentation().callActivityOnNewIntent(activity, intent);
                return;
            }
        }
        context.startActivity(intent);
    }

    public static Intent createIntentForOdklActivity(Context context) {
        Intent intent = new Intent(context, OdklActivity.class);
        intent.setFlags(67239936);
        return intent;
    }

    public static Intent createIntentForBackFromSlidingMenuOpenActivity(Context context) {
        Intent intent = createIntentForTag(context, Tag.feed);
        intent.setFlags(67239936);
        return intent;
    }

    public static void clickHomeButton(Context context) {
        context.startActivity(createIntentForOdklActivity(context));
    }

    public static void launchApplication(@NonNull Context context, @NonNull String appPackage) {
        Logger.m172d("requested launching app " + appPackage);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackage);
        if (intent != null) {
            try {
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException e) {
            }
        }
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackage)));
        } catch (ActivityNotFoundException e2) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage)));
        }
    }

    public static void putIntentToIntent(Intent where, Intent what) {
        where.putExtra(EXTRA_INTENT_FROM_LAST_ACTIVITY, what);
    }

    public static Intent getIntentFromIntent(Intent from) {
        return (Intent) from.getParcelableExtra(EXTRA_INTENT_FROM_LAST_ACTIVITY);
    }

    static {
        EXTRA_INTENT_FROM_LAST_ACTIVITY = "EXTRA_INTENT_FROM_PREW_ACTIVITY";
    }

    public static void login(Activity activity) {
        login(activity, true);
    }

    public static void firstEnter(Activity activity) {
        Intent oldIntent = activity.getIntent();
        Intent intent = new Intent(activity, FirstEnterActivity.class);
        putIntentToIntent(intent, oldIntent);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void login(Activity activity, boolean noHistory) {
        login(activity, activity instanceof NotLoggedInWebActivity ? null : activity.getIntent(), noHistory);
        activity.finish();
    }

    public static void goToRegistration(Activity activity) {
        Intent intent = new Intent(activity, NativeLoginActivity.class);
        intent.putExtra("registration", true);
        activity.startActivity(intent);
    }

    public static void goToOldRegistration(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, NotLoggedInWebActivity.class);
        intent.putExtra("page", NotLoggedInWebFragment.Page.Registration);
        WebBaseFragment.clearCookie();
        activity.startActivityForResult(intent, requestCode);
    }

    public static void nativeLogin(Context context, Intent intentInput, boolean newTask) {
        Intent intent = new Intent(context, NativeLoginActivity.class);
        putIntentToIntent(intent, intentInput);
        if (newTask) {
            intent.addFlags(268468224);
        }
        context.startActivity(intent);
    }

    public static void login(Context context, Intent intentInput, boolean noHistory) {
        nativeLogin(context, intentInput, true);
    }

    public static void loginAfterWebRegistration(Activity activity, String user, String token) {
        Intent intent = new Intent(activity, NativeLoginActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("token", token);
        intent.putExtra("login_from_web_registration", true);
        putIntentToIntent(intent, activity.getIntent());
        intent.setFlags((intent.getFlags() | 32768) | 268435456);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void selectFriendsFiltered(Fragment fragment, UsersSelectionParams selectionParams, int target, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), SelectFriendsActivity.class);
        intent.putExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131166502);
        intent.putExtra("select_target", target);
        intent.putExtra("selection_params", selectionParams);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void selectFriendsFilteredForChat(Fragment fragment, UsersSelectionParams selectionParams, int target, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), SelectFriendsForChatActivity.class);
        intent.putExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131166502);
        intent.putExtra("select_target", target);
        intent.putExtra("selection_params", selectionParams);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showFriends(Activity activity, boolean fromLeftMenu) {
        ActivityExecutor executor = new ActivityExecutor(activity, FriendsTabFragment.class);
        executor.setActivityFromMenu(fromLeftMenu);
        executor.setSoftInputType(SoftInputType.PAN);
        executor.execute();
    }

    public static void makePresent(@NonNull Activity activity, @NonNull SendPresentShortLinkBuilder builder) {
        if (!PresentSettingsHelper.getSettings().nativeSendEnabled || builder.getPresentId() == null || builder.getUserId() == null) {
            showExternalUrlPage(activity, builder.build(), false);
        } else {
            activity.startActivity(PreloadSendPresentActivity.createIntent(activity, builder));
        }
    }

    public static void showUserSentPresents(@NonNull Activity activity, @Nullable String userId, @Nullable String token) {
        ActivityExecutor executor = new ActivityExecutor(activity, UserPresentsFragment.class);
        executor.setArguments(UserPresentsFragment.newArguments(userId, Direction.SENT, token));
        executor.execute();
    }

    public static void showUserReceivedPresents(@NonNull Activity activity, @Nullable String userId, @Nullable String token) {
        ActivityExecutor executor = new ActivityExecutor(activity, UserPresentsFragment.class);
        executor.setArguments(UserPresentsFragment.newArguments(userId, Direction.ACCEPTED, token));
        executor.execute();
    }

    public static void showUserFriends(Activity activity, String fid, String relation) {
        ActivityExecutor executor = new ActivityExecutor(activity, FragmentFriends.class);
        executor.setArguments(FragmentFriends.newArguments(fid, relation));
        executor.execute();
    }

    public static void showStreamFriends(ShowFragmentActivity activity) {
        ActivityExecutor executor = new ActivityExecutor(activity, OnlineFriendsStreamFragment.class);
        executor.setFragmentLocation(FragmentLocation.right_small);
        executor.setArguments(OnlineFriendsStreamFragment.createArguments());
        executor.setAddToBackStack(false);
        executor.setTag("online_friends_stream");
        activity.showFragment(executor);
    }

    public static void showRecommendedUsersPage(Activity activity, Bundle resultBundle) {
        Bundle arguments = new Bundle();
        arguments.putBoolean("fragment_is_dialog", true);
        arguments.putBoolean("KEY_BACK_TO_PREVIOUS_ACTIVITY", resultBundle.getBoolean("KEY_BACK_TO_PREVIOUS_ACTIVITY", false));
        Intent intent = new Intent(activity, RecommendedUsersActivity.class);
        intent.putExtra("key_argument_name", arguments);
        intent.setFlags(131072);
        activity.startActivity(intent);
    }

    public static void showRecommendedUsersPage(Activity activity) {
        showRecommendedUsersPage(activity, new Bundle());
    }

    public static void showDetailedPymk(Activity activity) {
        ActivityExecutor executor = new ActivityExecutor(activity, PymkFragment.class);
        executor.setNeedToolbar(false);
        executor.execute();
    }

    public static void openInExternalApp(Context context, Uri data) {
        if (data != null) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", data);
                intent.addFlags(268435456);
                context.startActivity(intent);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public static <M extends MessageBase> void showEditMessageActivity(Fragment fragment, OfflineMessage<M> message, int messageEditTitleResourceId, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), MessageEditActivity.class);
        intent.putExtra(Message.ELEMENT, message);
        intent.putExtra("title_id", messageEditTitleResourceId);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showAppPoll(Activity activity) {
        activity.startActivity(new Intent(activity, AppPollsActivity.class));
    }

    public static void showEditMediaTopicTextActivity(Fragment fragment, String text, String topicId, int blockIndex, int requestCode, int titleResourceId, int completedResourceId, int errorResourceId) {
        Intent intent = new Intent(fragment.getActivity(), MediaTopicTextEditActivity.class);
        intent.putExtra(Stanza.TEXT, text);
        intent.putExtra("topic_id", topicId);
        intent.putExtra("block_index", blockIndex);
        intent.putExtra("title_resource_id", titleResourceId);
        intent.putExtra("completed_resource_id", completedResourceId);
        intent.putExtra("error_resource_id", errorResourceId);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showSearchClassmatesFragment(Activity activity) {
        ActivityExecutor executor = new ActivityExecutor(activity, SearchClassmatesFragment.class);
        executor.setNeedToolbar(false);
        executor.execute();
    }

    public static void showSearchColleaguesFragment(Activity activity) {
        ActivityExecutor executor = new ActivityExecutor(activity, SearchByCommunityFragment.class);
        Bundle arguments = new Bundle();
        arguments.putInt("type", 3);
        arguments.putBoolean("indicator_enabled", false);
        arguments.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, LocalizationManager.getString(activity.getApplicationContext(), 2131166492));
        executor.setNeedToolbar(false);
        executor.setArguments(arguments);
        executor.execute();
    }

    public static void showCommunityUsersFragment(Activity activity, String groupId, int startYear, int endYear, String title) {
        ActivityExecutor executor = new ActivityExecutor(activity, CommunityUsersFragment.class);
        Bundle args = new Bundle();
        args.putString("group_id", groupId);
        args.putInt("start_year", startYear);
        args.putInt("end_year", endYear);
        if (title != null) {
            args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, title);
        }
        executor.setArguments(args);
        executor.setNeedToolbar(false);
        executor.execute();
    }
}

package ru.ok.android.fragments.web.client.interceptor.hooks;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.web.WebExternalUrlManager;
import ru.ok.android.fragments.web.hooks.ExternalAppOpenProcessor.ExternalAppOpenListener;
import ru.ok.android.fragments.web.hooks.HookCreateTopicProcessor.OnCreateTopicListener;
import ru.ok.android.fragments.web.hooks.HookErrorObserver.OnErrorUrlListener;
import ru.ok.android.fragments.web.hooks.HookErrorObserver.OnUserBlockedListener;
import ru.ok.android.fragments.web.hooks.HookFeedProcessor.HookFeedListener;
import ru.ok.android.fragments.web.hooks.HookFinishActivityProcessor.OnFinishActivityListener;
import ru.ok.android.fragments.web.hooks.HookFriends.HookFriendsListener;
import ru.ok.android.fragments.web.hooks.HookLogoutAllProcessor.OnLogoutAllListener;
import ru.ok.android.fragments.web.hooks.HookLogoutProcessor.OnLogoutUrlLoadingListener;
import ru.ok.android.fragments.web.hooks.HookMakeCallProcessor.OnMakeCallUrlLoadingListener;
import ru.ok.android.fragments.web.hooks.HookMessagesProcessor.OnShowMessagesUrlLoadingListener;
import ru.ok.android.fragments.web.hooks.HookNativePayment.HookNativePaymentListener;
import ru.ok.android.fragments.web.hooks.HookNotificationProcessor.OnNotificationCountUpdateListener;
import ru.ok.android.fragments.web.hooks.HookOpenUserMusicProcessor.HookOpenMusicListener;
import ru.ok.android.fragments.web.hooks.HookOutLinkProcessor.OnOutLinkOpenListener;
import ru.ok.android.fragments.web.hooks.HookPauseTrackProcessor.OnPauseMusicListener;
import ru.ok.android.fragments.web.hooks.HookPlayMusicProcessor.OnPlayMusicListener;
import ru.ok.android.fragments.web.hooks.HookPlayTrackProcessor.OnPlayTrackListener;
import ru.ok.android.fragments.web.hooks.HookRedirectProcessor.OnRedirectUrlLoadingListener;
import ru.ok.android.fragments.web.hooks.HookSessionFailedProcessor.OnSessionFailedListener;
import ru.ok.android.fragments.web.hooks.HookSyslinkProcessor.OnSyslinkListener;
import ru.ok.android.fragments.web.hooks.HookUploadPhotoProcessor.HookUploadPhotoListener;
import ru.ok.android.fragments.web.hooks.HookVideoProcessor.HookVideoListener;
import ru.ok.android.fragments.web.hooks.HookVideoUploadProcesor.HookVideoUploadListener;
import ru.ok.android.fragments.web.hooks.HookVideoV2Processor.HookVideoV2Listener;
import ru.ok.android.fragments.web.hooks.HookVideoV3Processor.Listener;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionCommentsProcessor.HookDiscussionCommentsListener;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoBaseProcessor.HookDiscussionInfoListener;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionLikesProcessor.HookDiscussionLikesListener;
import ru.ok.android.fragments.web.hooks.group.HookGroupThemeDeletedProcessor.HookGroupThemeDeletedListener;
import ru.ok.android.fragments.web.hooks.photo.HookGroupAvatarObserver.OnGroupAvatarListener;
import ru.ok.android.fragments.web.hooks.photo.HookOpenGroupPhotoAlbumObserver.OnOpenGroupPhotoAlbumListener;
import ru.ok.android.fragments.web.hooks.photo.HookOpenGroupPhotosObserver.OnOpenGroupPhotosListener;
import ru.ok.android.fragments.web.hooks.photo.HookOpenUserPhotoAlbumObserver.OnOpenUserPhotoAlbumListener;
import ru.ok.android.fragments.web.hooks.photo.HookOpenUserPhotosObserver.OnOpenUserPhotosListener;
import ru.ok.android.fragments.web.hooks.photo.HookShowGroupPhotoObserver.OnShowGroupPhotoListener;
import ru.ok.android.fragments.web.hooks.photo.HookShowUserPhotoObserver.OnShowUserPhotoListener;
import ru.ok.android.fragments.web.hooks.profiles.HookGroupProfileProcessor.HookGroupProfileListener;
import ru.ok.android.fragments.web.hooks.profiles.HookUserProfileProcessor.HookUserProfileListener;
import ru.ok.android.fragments.web.hooks.search.HookSearchBaseObserver.OnSearchListener;
import ru.ok.android.fragments.web.hooks.search.HookSearchFriendsObserver.OnSearchFriendsListener;
import ru.ok.android.fragments.web.hooks.search.HookSearchHappeningsObserver.OnSearchHappeningsListener;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.processors.music.StatusPlayMusicProcessor;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.dialogs.ErrorDialog;
import ru.ok.android.ui.dialogs.PasswordDialog;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.videochat.MakeCallManager;
import ru.ok.android.videochat.VideochatController;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.Discussion;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.search.SearchType;

public class AppHooksBridge implements ExternalAppOpenListener, OnCreateTopicListener, OnErrorUrlListener, OnUserBlockedListener, HookFeedListener, OnFinishActivityListener, HookFriendsListener, OnLogoutAllListener, OnLogoutUrlLoadingListener, OnMakeCallUrlLoadingListener, OnShowMessagesUrlLoadingListener, HookNativePaymentListener, OnNotificationCountUpdateListener, HookOpenMusicListener, OnOutLinkOpenListener, OnPauseMusicListener, OnPlayMusicListener, OnPlayTrackListener, OnRedirectUrlLoadingListener, OnSessionFailedListener, OnSyslinkListener, HookUploadPhotoListener, HookVideoListener, HookVideoUploadListener, HookVideoV2Listener, Listener, HookDiscussionCommentsListener, HookDiscussionInfoListener, HookDiscussionLikesListener, HookGroupThemeDeletedListener, OnGroupAvatarListener, OnOpenGroupPhotoAlbumListener, OnOpenGroupPhotosListener, OnOpenUserPhotoAlbumListener, OnOpenUserPhotosListener, OnShowGroupPhotoListener, OnShowUserPhotoListener, HookGroupProfileListener, HookUserProfileListener, OnSearchListener, OnSearchFriendsListener, OnSearchHappeningsListener {
    protected final Activity activity;
    private WebExternalUrlManager externalUrlManager;

    /* renamed from: ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksBridge.1 */
    class C03351 implements OnClickListener {
        final /* synthetic */ PhotoAlbumInfo val$albumInfo;
        final /* synthetic */ String val$gid;
        final /* synthetic */ String val$pid;

        C03351(PhotoAlbumInfo photoAlbumInfo, String str, String str2) {
            this.val$albumInfo = photoAlbumInfo;
            this.val$gid = str;
            this.val$pid = str2;
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case RECEIVED_VALUE:
                    NavigationHelper.startPhotoUploadSequence(AppHooksBridge.this.activity, this.val$albumInfo, 1, 1);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    NavigationHelper.showPhoto(AppHooksBridge.this.activity, new PhotoOwner(this.val$gid, 1), null, this.val$pid, 0);
                default:
            }
        }
    }

    public AppHooksBridge(Activity activity) {
        this.activity = activity;
        this.externalUrlManager = new WebExternalUrlManager(activity);
    }

    public void onExternalOpen(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(uri);
        try {
            this.activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Logger.m173d("No found app for this url %s", uri + "");
        }
    }

    public void onMakeCallUrlLoading(String from, String to, String sid, String disp, String userName, String userPic) {
        if (MakeCallManager.isCallSupports()) {
            try {
                VideochatController.instance().processOutgoingCall(from, to, sid, disp, userName, userPic, "");
                return;
            } catch (Exception e) {
                onMakeCallUrlError();
                return;
            }
        }
        Toast.makeText(this.activity, LocalizationManager.from(this.activity).getString(2131165467), 0).show();
    }

    public void onMakeCallUrlError() {
        new ErrorDialog(this.activity, 2131165800, 2131165595).show();
    }

    public void onShowMessages(String userId) {
        if (TextUtils.isEmpty(userId)) {
            NavigationHelper.showConversationsPage(this.activity);
        } else {
            NavigationHelper.showMessagesForUser(this.activity, userId);
        }
    }

    public void onOutLinkOpenInBrowser(String url) {
        WebExternalUrlManager.onOutLinkOpenInBrowser(this.activity, url);
    }

    public void onPlayTrack(Long trackId) {
        Utils.getServiceHelper().getStatusMusic(trackId.longValue());
    }

    public void onPauseMusic() {
        this.activity.startService(MusicService.getTogglePlayIntent(this.activity));
    }

    public void onDiscussionCommentsSelected(Discussion discussion, Uri uri) {
        NavigationHelper.showDiscussionCommentsFragment(this.activity, new Discussion(uri.getQueryParameter("id"), uri.getQueryParameter("type")), Page.MESSAGES, uri + "");
    }

    public void onShowVideo(String videoUrl) {
        NavigationHelper.showVideo(this.activity, null, videoUrl);
    }

    public void onShowVideoV2(String videoId) {
        NavigationHelper.showVideo(this.activity, videoId, null);
    }

    public void onShowVideoV3(String videoId, String url, boolean isBlocked) {
        NavigationHelper.showVideo(this.activity, videoId, url);
    }

    public void onPlayMusic(long trackId, String playListIds, String userId) {
        GlobalBus.send(2131624087, new BusEvent(StatusPlayMusicProcessor.fillBundle(trackId, playListIds, userId)));
    }

    public void onNotificationCountUpdate(int count) {
        EventsManager.getInstance().updateNotificationCounter(count);
    }

    public void onNotificationTotalUpdate(int total) {
        EventsManager.getInstance().updateNotificationTotal(total);
    }

    public void onOpenUserPhotoAlbum(String uid, String aid) {
        NavigationHelper.showUserPhotoAlbum(this.activity, uid, aid);
    }

    public void onOpenUserPhotos(String uid) {
        NavigationHelper.showUserPhotoAlbums(this.activity, uid, false);
    }

    public void onShowGroupPhoto(String aid, String pid, String gid, String[] spids) {
        NavigationHelper.showPhoto(this.activity, new PhotoOwner(gid, 1), aid, pid, spids, 0);
    }

    public void onShowUserPhoto(String aid, String pid, String uid, String[] spids) {
        NavigationHelper.showPhoto(this.activity, new PhotoOwner(uid, 0), aid, pid, spids, 0);
    }

    public void onOpenGroupPhotoAlbum(String gid, String aid) {
        NavigationHelper.showGroupPhotoAlbum(this.activity, gid, aid);
    }

    public void onOpenGroupPhotos(String gid) {
        NavigationHelper.showGroupPhotoAlbums(this.activity, gid);
    }

    public void onShowFriends(String uid, String relation) {
        if (TextUtils.isEmpty(uid)) {
            uid = OdnoklassnikiApplication.getCurrentUser().uid;
        }
        NavigationHelper.showUserFriends(this.activity, uid, relation);
    }

    public void onChooserUploadPhoto(String aid, String gid, String albumName) {
        showChooserUploadPhoto(aid, gid, albumName);
    }

    public void onDiscussionLikesSelected(Discussion discussion) {
        NavigationHelper.showDiscussionLikes(this.activity, discussion);
    }

    public void onDiscussionInfoSelected(Discussion discussion) {
        NavigationHelper.showDiscussionCommentsFragment(this.activity, discussion, Page.INFO, null);
    }

    public void onErrorUrlLoad(String url) {
    }

    public void onGroupAvatarClicked(String gid, String pid) {
        PhotoAlbumInfo albumInfo = new PhotoAlbumInfo();
        albumInfo.setOwnerType(OwnerType.GROUP);
        albumInfo.setGroupId(gid);
        albumInfo.setId("group_main");
        albumInfo.setTitle(LocalizationManager.getString(this.activity, 2131165933));
        Builder builder = new Builder(this.activity);
        builder.setTitle(LocalizationManager.getString(this.activity, 2131165932));
        builder.setItems(LocalizationManager.getStringArray(this.activity, 2131558417), new C03351(albumInfo, gid, pid));
        builder.show();
    }

    public void onRedirectUrlLoading(String redirectUrl) {
        Logger.m173d("redirectUrl=%s", redirectUrl);
        onSessionFailed(redirectUrl);
    }

    public void onShowNativePayment() {
    }

    public void onSessionFailed(String goToUrl) {
        Logger.m173d("goToUrl=%s", goToUrl);
    }

    public void onLoadSysLink(String url) {
        preprocessUrl(url);
    }

    public void onLogoutUrlLoading() {
        AuthorizationControl.getInstance().showLogoutDialog(this.activity);
    }

    public void onErrorUserBlocked() {
    }

    public void onCreateGroupTopic(String groupId) {
        Logger.m173d("groupId=%s", groupId);
        Intent createTopic = new Intent();
        createTopic.setClassName(this.activity, "ru.ok.android.ui.activity.MediaComposerGroupActivity");
        createTopic.putExtra("media_topic_gid", groupId);
        this.activity.startActivity(createTopic);
        MediaComposerStats.open("apphook", MediaTopicType.GROUP_THEME);
    }

    public void onCreateUserTopic() {
        Logger.m172d("");
        Intent createTopic = new Intent();
        createTopic.setClassName(this.activity, "ru.ok.android.ui.activity.MediaComposerUserActivity");
        this.activity.startActivity(createTopic);
        MediaComposerStats.open("apphook", MediaTopicType.USER);
    }

    public void onUploadVideo(String groupId) {
        Logger.m173d("groupId=%s", groupId);
    }

    public void onSearchFriends(String query) {
    }

    public void onSearchHappenings(String query) {
    }

    public void onSearch(String query, SearchType searchType) {
        NavigationHelper.showSearchPage(this.activity, null, query, searchType);
    }

    public void onShowFeed() {
        NavigationHelper.showFeedPage(this.activity, Source.app_hook);
    }

    public void onOpenMusic(String uid) {
        if (TextUtils.isEmpty(uid)) {
            NavigationHelper.showMyMusicPage(this.activity, 0, MusicFragmentMode.STANDARD);
        } else {
            NavigationHelper.showUserMusicPage(this.activity, uid, MusicFragmentMode.STANDARD);
        }
    }

    public void onLogoutAll() {
        if (this.activity != null && !this.activity.isFinishing()) {
            PasswordDialog.newInstance().show(this.activity.getFragmentManager(), "pwd_dialog");
        }
    }

    public void showChooserUploadPhoto(String aid, String gid, String albumName) {
        PhotoAlbumInfo album = new PhotoAlbumInfo();
        album.setId(aid);
        album.setGroupId(gid);
        album.setTitle(albumName);
        uploadPhoto(album);
    }

    public void uploadPhoto(PhotoAlbumInfo album) {
        NavigationHelper.startPhotoUploadSequence(this.activity, album, 0, 0);
    }

    public void onUserProfileSelected(String userId) {
        NavigationHelper.showUserInfo(this.activity, userId);
    }

    public void onGroupProfileSelected(String groupId) {
        NavigationHelper.showGroupInfo(this.activity, groupId);
    }

    public void onFinishActivity(int result) {
        this.activity.setResult(result);
        this.activity.finish();
    }

    private void preprocessUrl(String url) {
        this.externalUrlManager.preProcessUrl(url);
    }

    public void onGroupThemeDeletedClick(String groupId, String topicId) {
        NavigationHelper.showGroupTopicRejected(this.activity, groupId, topicId);
    }
}

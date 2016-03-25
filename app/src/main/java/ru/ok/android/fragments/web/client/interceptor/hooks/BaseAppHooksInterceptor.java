package ru.ok.android.fragments.web.client.interceptor.hooks;

import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.hooks.ExternalAppOpenProcessor;
import ru.ok.android.fragments.web.hooks.HookCdkStCmdMainProcessor;
import ru.ok.android.fragments.web.hooks.HookCreateTopicProcessor;
import ru.ok.android.fragments.web.hooks.HookErrorObserver;
import ru.ok.android.fragments.web.hooks.HookFeedProcessor;
import ru.ok.android.fragments.web.hooks.HookFinishActivityProcessor;
import ru.ok.android.fragments.web.hooks.HookFriends;
import ru.ok.android.fragments.web.hooks.HookLogoutAllProcessor;
import ru.ok.android.fragments.web.hooks.HookLogoutProcessor;
import ru.ok.android.fragments.web.hooks.HookMakeCallProcessor;
import ru.ok.android.fragments.web.hooks.HookMessagesProcessor;
import ru.ok.android.fragments.web.hooks.HookNativePayment;
import ru.ok.android.fragments.web.hooks.HookNotificationProcessor;
import ru.ok.android.fragments.web.hooks.HookOpenUserMusicProcessor;
import ru.ok.android.fragments.web.hooks.HookOutLinkProcessor;
import ru.ok.android.fragments.web.hooks.HookPauseMusicProcessor;
import ru.ok.android.fragments.web.hooks.HookPauseTrackProcessor;
import ru.ok.android.fragments.web.hooks.HookPlayMusicProcessor;
import ru.ok.android.fragments.web.hooks.HookPlayTrackProcessor;
import ru.ok.android.fragments.web.hooks.HookSyslinkProcessor;
import ru.ok.android.fragments.web.hooks.HookUploadPhotoProcessor;
import ru.ok.android.fragments.web.hooks.HookVideoProcessor;
import ru.ok.android.fragments.web.hooks.HookVideoUploadProcesor;
import ru.ok.android.fragments.web.hooks.HookVideoV2Processor;
import ru.ok.android.fragments.web.hooks.HookVideoV3Processor;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionCommentsProcessor;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoMediaNewsProcessor;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoMediaThemeProcessor;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoUserMediaProcessor;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionLikesProcessor;
import ru.ok.android.fragments.web.hooks.group.HookGroupThemeDeletedProcessor;
import ru.ok.android.fragments.web.hooks.photo.HookGroupAvatarObserver;
import ru.ok.android.fragments.web.hooks.photo.HookOpenGroupPhotoAlbumObserver;
import ru.ok.android.fragments.web.hooks.photo.HookOpenGroupPhotosObserver;
import ru.ok.android.fragments.web.hooks.photo.HookOpenUserPhotoAlbumObserver;
import ru.ok.android.fragments.web.hooks.photo.HookOpenUserPhotosObserver;
import ru.ok.android.fragments.web.hooks.photo.HookShowGroupPhotoObserver;
import ru.ok.android.fragments.web.hooks.photo.HookShowUserPhotoObserver;
import ru.ok.android.fragments.web.hooks.profiles.HookGroupProfileProcessor;
import ru.ok.android.fragments.web.hooks.profiles.HookUserProfileProcessor;
import ru.ok.android.fragments.web.hooks.search.HookSearchAllObserver;
import ru.ok.android.fragments.web.hooks.search.HookSearchCommunitiesObserver;
import ru.ok.android.fragments.web.hooks.search.HookSearchFriendsObserver;
import ru.ok.android.fragments.web.hooks.search.HookSearchGroupsObserver;
import ru.ok.android.fragments.web.hooks.search.HookSearchHappeningsObserver;
import ru.ok.android.fragments.web.hooks.search.HookSearchUsersObserver;

public class BaseAppHooksInterceptor implements UrlInterceptor {
    private final AppHooksInterceptor appHooksInterceptor;
    private final AppHooksBridge bridge;

    public BaseAppHooksInterceptor(AppHooksBridge appHooksBridge) {
        this.appHooksInterceptor = new AppHooksInterceptor();
        this.bridge = appHooksBridge;
        addAppHooks(this.appHooksInterceptor, appHooksBridge);
    }

    protected void addAppHooks(AppHooksInterceptor appHooks, AppHooksBridge bridge) {
        appHooks.addHookProcessor(new HookErrorObserver(bridge, bridge), new HookLogoutProcessor(bridge), new HookMakeCallProcessor(bridge), new HookMessagesProcessor(bridge), new HookOutLinkProcessor(bridge), new HookCdkStCmdMainProcessor(bridge, bridge), new HookPlayTrackProcessor(bridge), new HookPauseTrackProcessor(bridge), new HookDiscussionCommentsProcessor(bridge), new HookVideoProcessor(bridge), new HookVideoV2Processor(bridge), new HookVideoV3Processor(bridge), new HookVideoUploadProcesor(bridge), new HookPlayMusicProcessor(bridge), new HookPauseMusicProcessor(bridge), new HookDiscussionInfoMediaNewsProcessor(bridge), new HookDiscussionInfoMediaThemeProcessor(bridge), new HookDiscussionInfoUserMediaProcessor(bridge), new HookDiscussionLikesProcessor(bridge), new HookGroupThemeDeletedProcessor(bridge), new HookNotificationProcessor(bridge), new HookUploadPhotoProcessor(bridge), new HookOpenUserPhotoAlbumObserver(bridge), new HookOpenUserPhotosObserver(bridge), new HookOpenGroupPhotosObserver(bridge), new HookOpenGroupPhotoAlbumObserver(bridge), new HookShowUserPhotoObserver(bridge), new HookShowGroupPhotoObserver(bridge), new HookGroupAvatarObserver(bridge), new ExternalAppOpenProcessor(bridge), new HookSyslinkProcessor(bridge), new HookUserProfileProcessor(bridge), new HookGroupProfileProcessor(bridge), new HookCreateTopicProcessor(bridge), new HookFriends(bridge), new HookNativePayment(bridge), new HookSearchAllObserver(bridge), new HookSearchCommunitiesObserver(bridge), new HookSearchFriendsObserver(bridge), new HookSearchGroupsObserver(bridge), new HookSearchUsersObserver(bridge), new HookSearchHappeningsObserver(bridge), new HookOpenUserMusicProcessor(bridge), new HookLogoutAllProcessor(bridge), new HookFeedProcessor(bridge), new HookFinishActivityProcessor(bridge));
    }

    public boolean handleUrl(String url) {
        return this.appHooksInterceptor.handleUrl(url);
    }
}

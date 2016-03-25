package ru.ok.android.fragments.web.client.interceptor.shortlinks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.web.hooks.ShortLinkGuestsProcessor.HookGuestsListener;
import ru.ok.android.fragments.web.hooks.ShortLinkNotificationProcessor.NotificationsListener;
import ru.ok.android.fragments.web.hooks.discussion.ShortLinkDiscussionProcessor.DiscussionListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupProcessor.ShortLinkGroupListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupThemesProcessor.ShortLinkGroupThemesListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupsProcessor.ShortLinkGroupsListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkMyReceivedPresents.ShortLinkMyReceivedPresentsListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkMySentPresents.ShortLinkMySentPresentsListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkSendPresentProcessor.OnSendPresentListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkUserGroupsProcessor.ShortLinkUserGroupsListener;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkUserStatusesProcessor.ShortLinkUserTopicsListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkArtistRadioMusicProcessor.ArtistRadioMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkCollectionMusicProcessor.CollectionMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkCollectionsMusicProcessor.CollectionsMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkHistoryMusicProcessor.HistoryMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicAlbumProcessor.AlbumMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicArtistProcessor.ArtistMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicProcessor.MusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicSearchProcessor.SearchMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicTrackProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMyMusicProcessor.MyMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkPlayListMusicProcessor.PlayListMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkProfileMusicProcessor.ProfileMusicListener;
import ru.ok.android.fragments.web.hooks.music.ShortLinkRadioMusicProcessor.HookRadioMusicListener;
import ru.ok.android.fragments.web.hooks.users.ShortLinkProfileCurrentUserProcessor.ProfileCurrentUserListener;
import ru.ok.android.fragments.web.hooks.users.ShortLinkProfileUserProcessor.ProfileUserListener;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.music.GetAlbumInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetArtistInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetCollectionInfoCommandProcessor;
import ru.ok.android.services.processors.music.GetPlayListInfoCommandProcessor;
import ru.ok.android.ui.activity.UserMusicActivity;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.model.Discussion;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.AlbumInfo;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.ArtistInfo;
import ru.ok.model.wmf.UserTrackCollection;
import ru.ok.onelog.groups.GroupsPageOpenFactory;
import ru.ok.onelog.groups.GroupsPageOpenSource;

public final class ShortLinksBridge implements CommandListener, HookGuestsListener, NotificationsListener, DiscussionListener, ShortLinkGroupListener, ShortLinkGroupThemesListener, ShortLinkGroupsListener, ShortLinkMyReceivedPresentsListener, ShortLinkMySentPresentsListener, OnSendPresentListener, ShortLinkUserGroupsListener, ShortLinkUserTopicsListener, ArtistRadioMusicListener, CollectionMusicListener, CollectionsMusicListener, HistoryMusicListener, AlbumMusicListener, ArtistMusicListener, MusicListener, SearchMusicListener, ShortLinkMusicTrackProcessor.MusicListener, MyMusicListener, PlayListMusicListener, ProfileMusicListener, HookRadioMusicListener, ProfileCurrentUserListener, ProfileUserListener {
    private final Activity contextActivity;
    private CommandListener radioCommandListener;

    /* renamed from: ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge.1 */
    class C03361 implements Runnable {
        final /* synthetic */ Album val$album;

        C03361(Album album) {
            this.val$album = album;
        }

        public void run() {
            NavigationHelper.showAlbumPage(ShortLinksBridge.this.contextActivity, this.val$album, MusicFragmentMode.STANDARD);
        }
    }

    /* renamed from: ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge.2 */
    class C03372 implements CommandListener {
        C03372() {
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (resultCode != ResultCode.SUCCESS) {
                ShortLinksBridge.this.onError();
            } else if (GetArtistInfoCommandProcessor.isIt(commandName)) {
                Artist artist = ((ArtistInfo) data.getParcelable("command_album_out_extra")).artist;
                if (artist != null) {
                    NavigationHelper.showArtistSimilarPage(ShortLinksBridge.this.contextActivity, artist, MusicFragmentMode.STANDARD);
                } else {
                    ShortLinksBridge.this.onError();
                }
            }
        }
    }

    public ShortLinksBridge(Activity context) {
        this.radioCommandListener = new C03372();
        this.contextActivity = context;
    }

    protected ServiceHelper getServiceHelper() {
        return Utils.getServiceHelper();
    }

    public void onShowDiscussion() {
        NavigationHelper.showDiscussionPage(this.contextActivity);
    }

    public void onShowNotifications() {
        NavigationHelper.showNotificationsPage(this.contextActivity, false);
    }

    public void onShowMusic() {
        this.contextActivity.startActivity(new Intent(this.contextActivity, UserMusicActivity.class));
    }

    public void onShowMyMusic() {
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_user_id", OdnoklassnikiApplication.getCurrentUser().uid);
        this.contextActivity.startActivity(intent);
    }

    public void onShowHistoryMusic() {
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_user_id", OdnoklassnikiApplication.getCurrentUser().uid);
        intent.putExtra("extra_type", 2);
        this.contextActivity.startActivity(intent);
    }

    public void onShowCollectionsMusic() {
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_user_id", OdnoklassnikiApplication.getCurrentUser().uid);
        intent.putExtra("extra_type", 1);
        this.contextActivity.startActivity(intent);
    }

    public void onShowProfileMusic(String uid) {
        Long userId = Long.valueOf(Long.parseLong(uid));
        userId = Long.valueOf(userId.longValue() == 265224201205L ? userId.longValue() : userId.longValue() ^ 265224201205L);
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_user_id", String.valueOf(userId));
        intent.putExtra("extra_type", 1);
        this.contextActivity.startActivity(intent);
    }

    public void onShowRadioMusic() {
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_show_radio", true);
        this.contextActivity.startActivity(intent);
    }

    public void onShowSearchMusic(String text) {
        NavigationHelper.showSearchMusic(this.contextActivity, text, MusicFragmentMode.STANDARD);
    }

    public void onShowAlbumMusic(String id) {
        getServiceHelper().getAlbumInfo(Long.parseLong(id), this);
    }

    public void onShowArtistMusic(String id) {
        getServiceHelper().getArtistInfo(Long.parseLong(id), this);
    }

    public void onShowArtistRadioMusic(String uid) {
        getServiceHelper().getArtistInfo(Long.parseLong(uid), this.radioCommandListener);
    }

    public void onShowCollectionMusic(String id) {
        getServiceHelper().getCollectionInfo(Long.parseLong(id), this);
    }

    public void onShowPlayListMusic(String id) {
        getServiceHelper().getPlayListInfo(Long.parseLong(id), this);
    }

    public void onShowMyProfile() {
        NavigationHelper.showCurrentUser(this.contextActivity, false);
    }

    public void onShowUserProfile(String userId) {
        NavigationHelper.showUserInfo(this.contextActivity, userId);
    }

    public void onShowMainPage() {
        NavigationHelper.showFeedPage(this.contextActivity, Source.short_link);
    }

    public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
        if (resultCode == ResultCode.FAILURE) {
            onError();
            return;
        }
        UserTrackCollection collection;
        if (GetArtistInfoCommandProcessor.isIt(commandName)) {
            Artist artist = ((ArtistInfo) data.getParcelable("command_album_out_extra")).artist;
            if (artist != null) {
                NavigationHelper.showArtistPage(this.contextActivity, artist, MusicFragmentMode.STANDARD);
            } else {
                onError();
            }
        }
        if (GetAlbumInfoCommandProcessor.isIt(commandName)) {
            Album album = ((AlbumInfo) data.getParcelable("command_album_out_extra")).album;
            if (album != null) {
                ThreadUtil.executeOnMain(new C03361(album));
            } else {
                onError();
            }
        }
        if (GetCollectionInfoCommandProcessor.isIt(commandName)) {
            collection = (UserTrackCollection) data.getParcelable("collection_out_extra");
            if (collection != null) {
                NavigationHelper.showMusicCollectionFragment(this.contextActivity, collection, MusicListType.POP_COLLECTION, MusicFragmentMode.STANDARD);
            } else {
                onError();
            }
        }
        if (GetPlayListInfoCommandProcessor.isIt(commandName)) {
            collection = (UserTrackCollection) data.getParcelable("playlist_out_extra");
            if (collection != null) {
                NavigationHelper.showMusicCollectionFragment(this.contextActivity, collection, MusicListType.USER_COLLECTION, MusicFragmentMode.STANDARD);
            } else {
                onError();
            }
        }
    }

    private void onError() {
        Toast.makeText(this.contextActivity, 2131166230, 0).show();
        NavigationHelper.showMyMusicPage(this.contextActivity, 0, MusicFragmentMode.STANDARD);
    }

    public void onShowGroupThemes(String groupId, Long tagId, String urlPathFilter) {
        String filter = null;
        if (urlPathFilter != null) {
            if ("suggested".equals(urlPathFilter)) {
                filter = "GROUP_SUGGESTED";
            } else if ("actualtopics".equals(urlPathFilter)) {
                filter = "GROUP_ACTUAL";
            }
        }
        if (filter != null) {
            NavigationHelper.showGroupTopicsFilter(this.contextActivity, groupId, filter);
        } else {
            NavigationHelper.showGroupTopics(this.contextActivity, groupId, tagId);
        }
    }

    public void onShowUserTopics(@NonNull String userId, String urlPathFilter) {
        String filter = null;
        if (urlPathFilter != null) {
            if ("all".equals(urlPathFilter)) {
                filter = "USER_ALL";
            } else if ("links".equals(urlPathFilter)) {
                filter = "USER_SHARES";
            } else if ("marks".equals(urlPathFilter)) {
                filter = "USER_WITH";
            } else if ("apps_notes".equals(urlPathFilter)) {
                filter = "USER_GAMES";
            }
        }
        NavigationHelper.showUserTopics(this.contextActivity, userId, filter);
    }

    public void onShowUserTopic(String userId, String statusId) {
        NavigationHelper.showDiscussionCommentsFragment(this.contextActivity, new Discussion(statusId, Type.USER_STATUS.name()), Page.INFO, null);
    }

    public void onShowGroup(String groupId) {
        NavigationHelper.showGroupStreamPage(this.contextActivity, groupId);
    }

    public void onShowGuests() {
        NavigationHelper.showGuestPage(this.contextActivity);
    }

    public void onSendPresent(@NonNull String presentId, @Nullable String userId, @Nullable String holidayId, @Nullable String token) {
        NavigationHelper.makePresent(this.contextActivity, SendPresentShortLinkBuilder.sendPresent(userId, presentId).setHoliday(holidayId).setToken(token));
    }

    public void onShowMusicTrack(long trackId) {
        Intent intent = new Intent(this.contextActivity, UserMusicActivity.class);
        intent.putExtra("extra_track_id", trackId);
        this.contextActivity.startActivity(intent);
    }

    public void onShowUserGroups(String userId) {
        NavigationHelper.showUserGroups(this.contextActivity, userId);
        OneLog.log(GroupsPageOpenFactory.get(GroupsPageOpenSource.link));
    }

    public void onShowGroups(@Nullable String categoryId) {
        NavigationHelper.showGroups(this.contextActivity, categoryId);
        OneLog.log(GroupsPageOpenFactory.get(GroupsPageOpenSource.link));
    }

    public void onShowMyReceivedPresents(@Nullable String token) {
        NavigationHelper.showUserReceivedPresents(this.contextActivity, null, token);
    }

    public void onShowMySentPresents(@Nullable String token) {
        NavigationHelper.showUserSentPresents(this.contextActivity, null, token);
    }
}

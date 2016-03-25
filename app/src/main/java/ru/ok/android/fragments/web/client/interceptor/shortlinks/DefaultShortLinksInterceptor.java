package ru.ok.android.fragments.web.client.interceptor.shortlinks;

import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.hooks.ShortLinkGuestsProcessor;
import ru.ok.android.fragments.web.hooks.ShortLinkMainPageProcessor;
import ru.ok.android.fragments.web.hooks.ShortLinkNotificationProcessor;
import ru.ok.android.fragments.web.hooks.discussion.ShortLinkDiscussionProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupThemesProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupThemesTagProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupsProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkMyReceivedPresents;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkMySentPresents;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkSendPresentProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkUserGroupsProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkUserStatusesProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkArtistRadioMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkCollectionMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkCollectionsMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkHistoryMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicAlbumProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicArtistProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicSearchProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMusicTrackProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkMyMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkPlayListMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkProfileMusicProcessor;
import ru.ok.android.fragments.web.hooks.music.ShortLinkRadioMusicProcessor;
import ru.ok.android.fragments.web.hooks.users.ShortLinkProfileCurrentUserProcessor;
import ru.ok.android.fragments.web.hooks.users.ShortLinkProfileUserProcessor;

public final class DefaultShortLinksInterceptor implements UrlInterceptor {
    private final ShortLinkInterceptor shortLinkInterceptor;
    private final ShortLinksBridge shortLinksBridge;

    public DefaultShortLinksInterceptor(ShortLinksBridge bridge) {
        this.shortLinksBridge = bridge;
        this.shortLinkInterceptor = new ShortLinkInterceptor();
        this.shortLinkInterceptor.addShortLinkProcessor(new ShortLinkProfileUserProcessor(this.shortLinksBridge), new ShortLinkUserStatusesProcessor(this.shortLinksBridge), new ShortLinkUserGroupsProcessor(this.shortLinksBridge), new ShortLinkGroupsProcessor(this.shortLinksBridge), new ShortLinkGroupThemesProcessor(this.shortLinksBridge), new ShortLinkGroupThemesTagProcessor(this.shortLinksBridge), new ShortLinkProfileCurrentUserProcessor(this.shortLinksBridge), new ShortLinkArtistRadioMusicProcessor(this.shortLinksBridge), new ShortLinkProfileMusicProcessor(this.shortLinksBridge), new ShortLinkMusicSearchProcessor(this.shortLinksBridge), new ShortLinkMusicArtistProcessor(this.shortLinksBridge), new ShortLinkMusicAlbumProcessor(this.shortLinksBridge), new ShortLinkPlayListMusicProcessor(this.shortLinksBridge), new ShortLinkCollectionMusicProcessor(this.shortLinksBridge), new ShortLinkCollectionsMusicProcessor(this.shortLinksBridge), new ShortLinkHistoryMusicProcessor(this.shortLinksBridge), new ShortLinkRadioMusicProcessor(this.shortLinksBridge), new ShortLinkMyMusicProcessor(this.shortLinksBridge), new ShortLinkMusicTrackProcessor(this.shortLinksBridge), new ShortLinkMusicProcessor(this.shortLinksBridge), new ShortLinkDiscussionProcessor(this.shortLinksBridge), new ShortLinkNotificationProcessor(this.shortLinksBridge), new ShortLinkMainPageProcessor(this.shortLinksBridge), new ShortLinkGuestsProcessor(this.shortLinksBridge), new ShortLinkSendPresentProcessor(this.shortLinksBridge), new ShortLinkMySentPresents(this.shortLinksBridge), new ShortLinkMyReceivedPresents(this.shortLinksBridge));
    }

    public boolean handleUrl(String url) {
        return this.shortLinkInterceptor.handleUrl(url);
    }
}

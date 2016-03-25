package ru.ok.android.ui.video.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.java.api.response.video.VideoGetResponse.VideoStatus;
import ru.ok.model.video.LikeSummary;

public final class VideoCompatUtils {

    /* renamed from: ru.ok.android.ui.video.fragments.VideoCompatUtils.1 */
    static /* synthetic */ class C13641 {
        static final /* synthetic */ int[] f122x1ee153ec;

        static {
            f122x1ee153ec = new int[VideoStatus.values().length];
            try {
                f122x1ee153ec[VideoStatus.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f122x1ee153ec[VideoStatus.UPLOADING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f122x1ee153ec[VideoStatus.PROCESSING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f122x1ee153ec[VideoStatus.ON_MODERATION.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f122x1ee153ec[VideoStatus.BLOCKED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f122x1ee153ec[VideoStatus.CENSORED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f122x1ee153ec[VideoStatus.COPYRIGHTS_RESTRICTED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f122x1ee153ec[VideoStatus.UNAVAILABLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f122x1ee153ec[VideoStatus.LIMITED_ACCESS.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public static void likeVideo(VideoGetResponse videoInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("like_id", videoInfo.likeSummary.getLikeId());
        GlobalBus.send(2131624010, new BusEvent(bundle));
    }

    public static void unLikeVideo(VideoGetResponse videoInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("like_id", videoInfo.likeSummary.getLikeId());
        GlobalBus.send(2131624118, new BusEvent(bundle));
    }

    public static Intent createShareIntentForLink(Activity activity, String url, String title) {
        return Intent.createChooser(createShareIntentLink(activity, url), activity.getString(2131165699));
    }

    public static Intent createShareIntentLink(Activity activity, String url) {
        return IntentBuilder.from(activity).setType("text/plain").setText(url).getIntent();
    }

    public static int getErrorStatusTextResValue(VideoStatus status) {
        switch (C13641.f122x1ee153ec[status.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2131165843;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2131166781;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2131166403;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return 2131166313;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return 2131165448;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return 2131165560;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return 2131165642;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return 2131166742;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return 2131166041;
            default:
                return 2131166747;
        }
    }

    public static void notifyLikeSummary(MenuItem likeMenuItem, LikeSummary likeSummary) {
        if (likeMenuItem == null) {
            return;
        }
        if (likeSummary.isSelf()) {
            likeMenuItem.setIcon(2130837990);
        } else {
            likeMenuItem.setIcon(2130837989);
        }
    }

    public static List<Pair<FORMAT, String>> getNotEmptyFormats(VideoGetResponse response) {
        List<Pair<FORMAT, String>> returnList = new ArrayList();
        if (!TextUtils.isEmpty(response.url144p)) {
            returnList.add(new Pair(FORMAT.FORMAT_144, response.url144p));
        }
        if (!TextUtils.isEmpty(response.url240p)) {
            returnList.add(new Pair(FORMAT.FORMAT_240, response.url240p));
        }
        if (!TextUtils.isEmpty(response.url360p)) {
            returnList.add(new Pair(FORMAT.FORMAT_360, response.url360p));
        }
        if (!TextUtils.isEmpty(response.url480p)) {
            returnList.add(new Pair(FORMAT.FORMAT_480, response.url480p));
        }
        if (!TextUtils.isEmpty(response.url720p)) {
            returnList.add(new Pair(FORMAT.FORMAT_720, response.url720p));
        }
        if (!TextUtils.isEmpty(response.url1080p)) {
            returnList.add(new Pair(FORMAT.FORMAT_1080, response.url1080p));
        }
        if (!TextUtils.isEmpty(response.url1440p)) {
            returnList.add(new Pair(FORMAT.FORMAT_1440, response.url1440p));
        }
        if (!TextUtils.isEmpty(response.url2160p)) {
            returnList.add(new Pair(FORMAT.FORMAT_2160, response.url2160p));
        }
        return returnList;
    }

    public static void validResponseForMenu(VideoGetResponse response, MenuItem likeMenuItem, MenuItem shareMenuItem) {
        if (response.likeSummary != null && response.likeSummary.isLikePossible()) {
            notifyLikeSummary(likeMenuItem, response.likeSummary);
            if (likeMenuItem != null) {
                likeMenuItem.setVisible(true);
            }
        } else if (likeMenuItem != null) {
            likeMenuItem.setVisible(false);
        }
        if (TextUtils.isEmpty(response.permalink)) {
            if (shareMenuItem != null) {
                shareMenuItem.setVisible(false);
            }
        } else if (shareMenuItem != null) {
            shareMenuItem.setVisible(true);
        }
    }
}

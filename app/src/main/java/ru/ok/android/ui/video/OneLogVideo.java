package ru.ok.android.ui.video;

import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.video.player.Quality;
import ru.ok.android.utils.Logger;
import ru.ok.onelog.video.player.AdvErrorEventFactory;
import ru.ok.onelog.video.player.AdvEventFactory;
import ru.ok.onelog.video.player.AdvParam;
import ru.ok.onelog.video.player.AdvRequestDurationEventFactory;
import ru.ok.onelog.video.player.ContentType;
import ru.ok.onelog.video.player.FirstBytesEventFactory;
import ru.ok.onelog.video.player.NoAdvRequestDurationEventFactory;
import ru.ok.onelog.video.player.PausePlayerEventFactory;
import ru.ok.onelog.video.player.Place;
import ru.ok.onelog.video.player.PlayerCrashEventFactory;
import ru.ok.onelog.video.player.PlayerErrorEventFactory;
import ru.ok.onelog.video.player.RelatedSelectEventFactory;
import ru.ok.onelog.video.player.SeekEventFactory;
import ru.ok.onelog.video.player.SelectQualityEventFactory;
import ru.ok.onelog.video.player.SimplePlayerEventFactory;
import ru.ok.onelog.video.player.SimplePlayerOperation;
import ru.ok.onelog.video.player.WatchTimeEventFactory;

public final class OneLogVideo {
    private static final Place DEFAULT_PLACE;

    /* renamed from: ru.ok.android.ui.video.OneLogVideo.1 */
    static /* synthetic */ class C13531 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$video$player$Quality;

        static {
            $SwitchMap$ru$ok$android$ui$video$player$Quality = new int[Quality.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._144p.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._240p.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._360p.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._480p.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._720p.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._1080p.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._1440p.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality._2160p.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    static {
        DEFAULT_PLACE = Place.top;
    }

    public static void log(long videoId, SimplePlayerOperation operation) {
        log(videoId, operation, null);
    }

    public static void log(long videoId, SimplePlayerOperation operation, Quality quality) {
        OneLog.log(SimplePlayerEventFactory.get(operation, videoId, ContentType.dash, DEFAULT_PLACE, "", getQuality(quality), "no"));
    }

    public static void logQualityChange(long videoId, Quality selectQuality, Quality currentQuality) {
        OneLog.log(SelectQualityEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", getQuality(currentQuality), "no", getQuality(selectQuality)));
    }

    public static void logSelectRelated(long videoId, int position) {
        OneLog.log(RelatedSelectEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", position));
    }

    public static void logWatchTime(long videoId, Quality currentQuality, long timeInSec) {
        OneLog.priorityLog(WatchTimeEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", getQuality(currentQuality), "no", timeInSec));
    }

    public static void logSeek(long videoId, Quality currentQuality, long timeInSec) {
        OneLog.priorityLog(SeekEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", getQuality(currentQuality), "no", timeInSec));
    }

    public static void logPause(long videoId, Quality currentQuality, long timeInSec) {
        OneLog.priorityLog(PausePlayerEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", getQuality(currentQuality), "no", timeInSec));
    }

    public static void logError(long videoId, String message) {
        OneLog.priorityLog(PlayerErrorEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", message));
    }

    public static void logCrash(long videoId, String message) {
        OneLog.priorityLog(PlayerCrashEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", message));
    }

    public static void logFirstBytesTime(long videoId, long timeInMsSec) {
        OneLog.log(FirstBytesEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", timeInMsSec));
    }

    public static ru.ok.onelog.video.player.Quality getQuality(Quality quality) {
        if (quality != null) {
            switch (C13531.$SwitchMap$ru$ok$android$ui$video$player$Quality[quality.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return ru.ok.onelog.video.player.Quality.mobile;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return ru.ok.onelog.video.player.Quality.lowest;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return ru.ok.onelog.video.player.Quality.low;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    return ru.ok.onelog.video.player.Quality.medium;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    return ru.ok.onelog.video.player.Quality.high;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    return ru.ok.onelog.video.player.Quality.fullhd;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    return ru.ok.onelog.video.player.Quality.quadhd;
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    return ru.ok.onelog.video.player.Quality.ultrahd;
                default:
                    return ru.ok.onelog.video.player.Quality.auto;
            }
        }
        Logger.m172d("no select Quality, Quality == null");
        return ru.ok.onelog.video.player.Quality.auto;
    }

    public static void logAdvertisement(long videoId, AdvParam param) {
        OneLog.priorityLog(AdvEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", param));
    }

    public static void logAdvertisementError(long videoId, String message) {
        OneLog.log(AdvErrorEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", message));
    }

    public static void logAdvertisementStartTime(long videoId, long time) {
        OneLog.priorityLog(AdvRequestDurationEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", time));
    }

    public static void logNoAdvertisementStartTime(long videoId, long time) {
        OneLog.priorityLog(NoAdvRequestDurationEventFactory.get(videoId, ContentType.dash, DEFAULT_PLACE, "", ru.ok.onelog.video.player.Quality.auto, "no", time));
    }
}

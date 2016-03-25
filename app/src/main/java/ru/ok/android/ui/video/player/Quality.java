package ru.ok.android.ui.video.player;

import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.response.video.VideoGetResponse;

public enum Quality {
    _144p(144, 2131166822),
    _240p(240, 2131166825),
    _360p(360, 2131166826),
    _480p(480, 2131166827),
    _720p(720, 2131166828),
    _1080p(1080, 2131166821),
    _1440p(1440, 2131166823),
    _2160p(2160, 2131166824),
    Hls(0, 2131166830),
    Live_Hls(0, 2131166830),
    Auto(0, 2131166829);
    
    static final List<Quality> prioritiesForMobile;
    public final int height;
    public final int resId;

    /* renamed from: ru.ok.android.ui.video.player.Quality.1 */
    static class C13791 implements Comparator<Quality> {
        final /* synthetic */ int val$size;

        C13791(int i) {
            this.val$size = i;
        }

        public int compare(Quality q1, Quality q2) {
            return Integer.signum(Math.abs(this.val$size - q2.height) - Math.abs(this.val$size - q1.height));
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.Quality.2 */
    static class C13802 implements Comparator<Quality> {
        C13802() {
        }

        public int compare(Quality q1, Quality q2) {
            return Integer.signum(Quality.prioritiesForMobile.indexOf(q1) - Quality.prioritiesForMobile.indexOf(q2));
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.Quality.3 */
    static class C13813 implements Comparator<T> {
        final /* synthetic */ Comparator val$comparator;

        C13813(Comparator comparator) {
            this.val$comparator = comparator;
        }

        public int compare(T a, T b) {
            if (a == null && b == null) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return this.val$comparator.compare(a, b);
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.Quality.4 */
    static /* synthetic */ class C13824 {
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
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Hls.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Live_Hls.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Auto.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    static {
        prioritiesForMobile = Arrays.asList(new Quality[]{_360p, _480p, _240p, _720p, _1080p, _1440p, _144p, _2160p});
    }

    private Quality(int height, int resId) {
        this.height = height;
        this.resId = resId;
    }

    public boolean isPresentIn(VideoGetResponse r) {
        return !TextUtils.isEmpty(getUrlFrom(r));
    }

    public String getUrlFrom(VideoGetResponse r) {
        switch (C13824.$SwitchMap$ru$ok$android$ui$video$player$Quality[ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return r.url144p;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return r.url240p;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return r.url360p;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return r.url480p;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return r.url720p;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return r.url1080p;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return r.url1440p;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return r.url2160p;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return r.urlHls;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return r.urlLiveHls;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                return r.urlDash;
            default:
                Log.e("Quality", "Unsupported format " + this);
                return null;
        }
    }

    public static Comparator<Quality> bestFits(int size) {
        return nullIsAlwaysLess(new C13791(size));
    }

    public static Comparator<Quality> higherPriorityForMobileData() {
        return nullIsAlwaysLess(new C13802());
    }

    private static <T> Comparator<T> nullIsAlwaysLess(Comparator<T> comparator) {
        return new C13813(comparator);
    }
}

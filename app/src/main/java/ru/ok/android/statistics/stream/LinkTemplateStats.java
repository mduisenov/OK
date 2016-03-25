package ru.ok.android.statistics.stream;

import android.content.Context;
import android.util.DisplayMetrics;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.stream.list.StreamLinkItem.SimpleTemplateChooser;
import ru.ok.android.ui.stream.list.StreamLinkItem.TemplateType;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.onelog.feed.FeedLinkTemplate;
import ru.ok.onelog.feed.FeedLinkTemplate.DeviceDensityType;
import ru.ok.onelog.feed.FeedLinkTemplate.LinkTemplateType;
import ru.ok.onelog.feed.FeedLinkTemplateFactory;

public final class LinkTemplateStats {

    /* renamed from: ru.ok.android.statistics.stream.LinkTemplateStats.1 */
    static /* synthetic */ class C05281 {
        static final /* synthetic */ int[] f88xcb3b1035;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType;

        static {
            $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType = new int[DeviceLayoutType.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceLayoutType.BIG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceLayoutType.LARGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f88xcb3b1035 = new int[TemplateType.values().length];
            try {
                f88xcb3b1035[TemplateType.IMAGE_DOWN_BIG.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f88xcb3b1035[TemplateType.IMAGE_LEFT_SMALL.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private static LinkTemplateType getLinkTemplateType(SimpleTemplateChooser templateChooser) {
        switch (C05281.f88xcb3b1035[templateChooser.getTemplateOptions().templateType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return LinkTemplateType.big_image;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return LinkTemplateType.small_image;
            default:
                return LinkTemplateType.no_image;
        }
    }

    private static DeviceDensityType getDeviceDensityType(DisplayMetrics displayMetrics) {
        if (displayMetrics.densityDpi < 240) {
            return DeviceDensityType.mdpi;
        }
        if (displayMetrics.densityDpi < 320) {
            return DeviceDensityType.hdpi;
        }
        if (displayMetrics.densityDpi < 480) {
            return DeviceDensityType.xhdpi;
        }
        return DeviceDensityType.xxhdpi;
    }

    private static FeedLinkTemplate.DeviceLayoutType getDeviceLayoutType(Context context) {
        switch (C05281.$SwitchMap$ru$ok$android$utils$DeviceUtils$DeviceLayoutType[DeviceUtils.getType(context).ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return FeedLinkTemplate.DeviceLayoutType.big;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return FeedLinkTemplate.DeviceLayoutType.large;
            default:
                return FeedLinkTemplate.DeviceLayoutType.small;
        }
    }

    public static void logLinkTemplate(Context context, SimpleTemplateChooser templateChooser) {
        OneLog.log(FeedLinkTemplateFactory.get(getLinkTemplateType(templateChooser), getDeviceDensityType(context.getResources().getDisplayMetrics()), getDeviceLayoutType(context)));
    }
}

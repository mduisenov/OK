package ru.ok.android.target;

import android.content.Context;
import android.text.TextUtils;
import ru.mail.android.mytarget.ads.CustomParams;
import ru.mail.android.mytarget.ads.MyTargetVideoView;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.video.Advertisement;

public final class TargetUtils {
    private static boolean clear;
    private static String lang;
    private static String uid;

    public static void initTarget(String userId, String langUser, boolean clearCache) {
        if (!TextUtils.isEmpty(userId)) {
            uid = Utils.getXoredIdSafe(userId);
        }
        lang = langUser;
        clear = clearCache;
    }

    public static void setLang(String langUser) {
        lang = langUser;
    }

    public static NativeAppwallAd createTargetAdapter(Context context) {
        CustomParams params = new CustomParams();
        if (!TextUtils.isEmpty(uid)) {
            params.setOkId(uid);
        }
        params.setLang(lang);
        NativeAppwallAd ad = new NativeAppwallAd(6006, context, params);
        if (clear) {
            ad.setCachePeriod(0);
        }
        return ad;
    }

    public static void initMyTargetVideo(MyTargetVideoView videoView, Advertisement advertisement) {
        CustomParams customParams = new CustomParams();
        customParams.setCustomParam("_SITEID", "163");
        customParams.setCustomParam("_SITEZONE", String.valueOf(advertisement.getSiteZone()));
        customParams.setCustomParam("content_id", advertisement.getContentId());
        customParams.setCustomParam("duration", String.valueOf(advertisement.getDuration()));
        customParams.setCustomParam("videoQuality", String.valueOf(360));
        UserInfo userInfo = OdnoklassnikiApplication.getCurrentUser();
        if (userInfo != null) {
            customParams.setAge(userInfo.age);
            customParams.setOkId(userInfo.getId());
            if (userInfo.genderType != null) {
                customParams.setGender(userInfo.genderType.toInteger());
            }
            customParams.setLang(Settings.getCurrentLocale(OdnoklassnikiApplication.getContext()));
        }
        videoView.setVideoQuality(360);
        videoView.init(advertisement.getSlot(), customParams);
    }
}

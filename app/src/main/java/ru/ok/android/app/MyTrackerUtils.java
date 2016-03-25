package ru.ok.android.app;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import java.util.Map;
import ru.mail.android.mytracker.MRMyTracker;
import ru.mail.android.mytracker.MRMyTrackerParams;
import ru.mail.android.mytracker.providers.CustomParamsDataProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class MyTrackerUtils {
    private static CustomParamsDataProvider myTrackerCustomParams;

    public static void initMyTracker(Context context, UserInfo currentUser, String lang) {
        MRMyTracker.createTracker("06172822163857099593", context);
        MRMyTrackerParams params = MRMyTracker.getTrackerParams();
        CustomParamsDataProvider trackerCustomParams = params.getCustomParams();
        trackerCustomParams.setLang(lang);
        setMyTrackerCustomParams(trackerCustomParams, currentUser);
        params.setTrackingLaunchEnabled(true);
        params.setTrackingAppsEnabled(true);
        myTrackerCustomParams = trackerCustomParams;
        MRMyTracker.initTracker();
    }

    public static void onRegistration(String login, String uid) {
        uid = Utils.getXoredIdSafe(uid);
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(uid)) {
            Logger.m185w("invalid params: login=%s uid=%s", login, uid);
            return;
        }
        Logger.m173d("Send registration event: login=%s uid=%s", login, uid);
        MRMyTracker.trackRegistrationEvent(createEventParams(login, uid));
    }

    public static void onLoginByPassword(String login, String uid) {
        uid = Utils.getXoredIdSafe(uid);
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(uid)) {
            Logger.m185w("invalid params: login=%s uid=%s", login, uid);
            return;
        }
        Logger.m173d("Send login event: login=%s uid=%s", login, uid);
        MRMyTracker.trackLoginEvent();
    }

    public static void onCurrentUserChanged(UserInfo currentUser) {
        if (myTrackerCustomParams != null) {
            setMyTrackerCustomParams(myTrackerCustomParams, currentUser);
        }
    }

    private static void setMyTrackerCustomParams(@NonNull CustomParamsDataProvider params, @Nullable UserInfo currentUser) {
        String uid;
        int gender;
        int age;
        if (currentUser == null || TextUtils.isEmpty(currentUser.uid)) {
            uid = null;
            gender = -1;
            age = -1;
        } else {
            uid = Utils.getXoredIdSafe(currentUser.uid);
            UserGenderType genderType = currentUser.genderType;
            gender = genderType == UserGenderType.MALE ? 1 : genderType == UserGenderType.FEMALE ? 2 : 0;
            if (currentUser.age >= 0) {
                age = currentUser.age;
            } else {
                age = 0;
            }
        }
        Logger.m173d("myTracker: set user data: uid=%s, gender=%d, age=%d", uid, Integer.valueOf(gender), Integer.valueOf(age));
        if (uid != null) {
            params.setOkId(uid);
        } else {
            params.setOkIds(null);
        }
        params.setGender(gender);
        params.setAge(age);
    }

    public static void onActivityStarted(Activity activity) {
        MRMyTracker.onStartActivity(activity);
    }

    public static void onActivityStopper(Activity activity) {
        MRMyTracker.onStopActivity(activity);
    }

    private static Map<String, String> createEventParams(String login, String uid) {
        ArrayMap<String, String> params = new ArrayMap(2);
        params.put("login", login);
        params.put("ok_id", uid);
        return params;
    }
}

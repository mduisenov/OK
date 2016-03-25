package ru.ok.android.fragments.web.client.interceptor.stcmd;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.utils.Logger;

public final class StCmdUrlInterceptor implements UrlInterceptor {
    private final StCmdUrlInterceptorCallBack listener;

    public interface StCmdUrlInterceptorCallBack {
        boolean onGoDscPage();

        boolean onGoGroupStream(String str);

        boolean onGoGuestsPage();

        boolean onGoHomePage(String str);

        boolean onGoMarksPage();

        boolean onGoProfilePage();

        boolean onGoUserStream(String str);
    }

    public StCmdUrlInterceptor(StCmdUrlInterceptorCallBack listener) {
        this.listener = listener;
    }

    public static boolean isDscPageFinishUrl(Uri uri) {
        if (uri == null || !uri.isHierarchical()) {
            return false;
        }
        return TextUtils.equals(uri.getQueryParameter("st.cmd"), "userDscs");
    }

    public static boolean isMarksPageFinishUrl(Uri uri) {
        if (uri == null || !uri.isHierarchical()) {
            return false;
        }
        return TextUtils.equals(uri.getQueryParameter("st.cmd"), "userMarks");
    }

    public static boolean isEventsPageFinishUrl(Uri uri) {
        if (uri == null || !uri.isHierarchical()) {
            return false;
        }
        return TextUtils.equals(uri.getQueryParameter("st.cmd"), "userEvents");
    }

    public boolean handleUrl(String url) {
        Uri uri = Uri.parse(url);
        if (!uri.isHierarchical()) {
            return false;
        }
        String stCmd = uri.getQueryParameter("st.cmd");
        if (TextUtils.equals(stCmd, "clientRedirect")) {
            return false;
        }
        if (TextUtils.equals(stCmd, "api/goto")) {
            return false;
        }
        if (TextUtils.equals(stCmd, "userMain")) {
            return this.listener.onGoHomePage(url);
        }
        if (TextUtils.equals(stCmd, "userProfile")) {
            return this.listener.onGoProfilePage();
        }
        if (TextUtils.equals(stCmd, "userGuests")) {
            return this.listener.onGoGuestsPage();
        }
        if (TextUtils.equals(stCmd, "userMarks")) {
            return this.listener.onGoMarksPage();
        }
        if (TextUtils.equals(stCmd, "userDscs")) {
            return this.listener.onGoDscPage();
        }
        if ("friendFeeds".equals(stCmd)) {
            String uid = uri.getQueryParameter("st.friendId");
            if (!TextUtils.isEmpty(uid)) {
                try {
                    return this.listener.onGoUserStream(Long.toString(Long.parseLong(uid) ^ 265224201205L));
                } catch (Exception e) {
                    Logger.m177e("Failed to parse uid: %s", uid);
                }
            }
        }
        if ("altGroupFeeds".equals(stCmd)) {
            String gid = uri.getQueryParameter("st.groupId");
            if (!TextUtils.isEmpty(gid)) {
                try {
                    return this.listener.onGoGroupStream(Long.toString(Long.parseLong(gid) ^ 265224201205L));
                } catch (Exception e2) {
                    Logger.m177e("Failed to parse gid: %s", gid);
                }
            }
        }
        return false;
    }
}

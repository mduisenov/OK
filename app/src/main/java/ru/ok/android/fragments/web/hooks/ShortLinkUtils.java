package ru.ok.android.fragments.web.hooks;

import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.GetUrlsRequester;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.model.UserInfo;

public final class ShortLinkUtils {
    public static String getUrl(String methodName, String uid, String replaceMask) {
        return getUrl(ConfigurationPreferences.getInstance().getWebServer(), methodName, uid, replaceMask);
    }

    public static String getUrl(String server, String methodName, String uid, String replaceMask) {
        Long userId = Long.valueOf(Long.parseLong(uid));
        userId = Long.valueOf(userId.longValue() == 265224201205L ? userId.longValue() : userId.longValue() ^ 265224201205L);
        if (!TextUtils.isEmpty(replaceMask)) {
            methodName = methodName.replace(replaceMask, "" + userId);
        }
        return getUrlByPath(server, methodName);
    }

    public static String getUrlByPath(String path) {
        return getUrlByPath(ConfigurationPreferences.getInstance().getWebServer(), path);
    }

    public static String getUrlByPath(String server, String path) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetUrlsRequester(server, path)).getURI().toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String createGroupTopicShortLink(String groupIdStr, String topicIdStr) throws ShortLinkException {
        if (TextUtils.isEmpty(topicIdStr)) {
            throw new ShortLinkException("Empty topicId");
        }
        try {
            return createGroupShortLink(groupIdStr) + "/topic/" + (Long.parseLong(topicIdStr) ^ 265224201205L);
        } catch (Exception e) {
            throw new ShortLinkException("Failed to parse topicId", e);
        }
    }

    public static String createGroupShortLink(String groupIdStr) throws ShortLinkException {
        return "http://m.odnoklassniki.ru/group/" + xorId(groupIdStr);
    }

    public static String createGroupTopicsShortLink(String groupIdStr, String urlFilter) throws ShortLinkException {
        StringBuilder append = new StringBuilder().append("http://m.odnoklassniki.ru/group/").append(xorId(groupIdStr)).append("/");
        if (urlFilter == null) {
            urlFilter = "topics";
        }
        return append.append(urlFilter).toString();
    }

    public static boolean isCurrentUserNotesShortLink(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        UserInfo currentUserInfo = OdnoklassnikiApplication.getCurrentUser();
        if (currentUserInfo == null) {
            return false;
        }
        String uidStr = currentUserInfo.uid;
        if (TextUtils.isEmpty(uidStr)) {
            return false;
        }
        try {
            long uid = Long.parseLong(uidStr);
            int startOffset = 0;
            int urlLength = url.length();
            Long urlUid = null;
            while (startOffset < urlLength) {
                startOffset = url.indexOf("/profile/", startOffset);
                if (startOffset == -1) {
                    break;
                }
                startOffset += "/profile/".length();
                int tailOffset = url.indexOf("/statuses", startOffset);
                if (tailOffset != -1) {
                    String urlUidStr = url.substring(startOffset, tailOffset);
                    if (TextUtils.isEmpty(urlUidStr)) {
                        continue;
                    } else {
                        try {
                            urlUid = Long.valueOf(Long.parseLong(urlUidStr));
                            break;
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if (urlUid == null) {
                return false;
            }
            return uid == (urlUid.longValue() ^ 265224201205L);
        } catch (Exception e2) {
            Logger.m185w("Failed to parse uid: %s", uidStr);
            return false;
        }
    }

    public static String createUserVideoShortLink(String uid) throws ShortLinkException {
        return "http://m.odnoklassniki.ru/profile/" + xorId(uid) + "/video";
    }

    public static String createGroupVideoShortLink(String uid) throws ShortLinkException {
        return "http://m.odnoklassniki.ru/group/" + xorId(uid) + "/video";
    }

    public static String createUserTopicsShortLink(String uid) throws ShortLinkException {
        return "http://m.odnoklassniki.ru/profile/" + xorId(uid) + "/statuses";
    }

    public static Long parseLongNullOnFail(String idStr) {
        Long l = null;
        if (idStr != null) {
            try {
                l = Long.valueOf(Long.parseLong(idStr));
            } catch (Exception e) {
            }
        }
        return l;
    }

    public static String extractId(String idStr, boolean doXor) {
        String str = null;
        if (idStr != null) {
            try {
                Long id = Long.valueOf(idStr);
                if (id != null) {
                    if (doXor) {
                        id = Long.valueOf(id.longValue() ^ 265224201205L);
                    }
                    str = id.toString();
                }
            } catch (Exception e) {
            }
        }
        return str;
    }

    private static long xorId(String idStr) throws ShortLinkException {
        if (TextUtils.isEmpty(idStr)) {
            throw new ShortLinkException("Empty id");
        }
        try {
            return Long.parseLong(idStr) ^ 265224201205L;
        } catch (Exception e) {
            throw new ShortLinkException("Failed to parse id: " + idStr, e);
        }
    }
}

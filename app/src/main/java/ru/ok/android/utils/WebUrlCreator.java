package ru.ok.android.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.Map;
import ru.ok.android.fragments.web.AppParamsManagerImpl;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.request.GetRedirectRequest;
import ru.ok.java.api.request.GetUrlsRequester;
import ru.ok.java.api.request.MakePresentRequest;
import ru.ok.java.api.request.PaymentRequest;
import ru.ok.java.api.request.PhotoMarkPaymentRequest;
import ru.ok.java.api.request.discussions.GetDiscussionRequest;
import ru.ok.java.api.request.groups.GetGroupLinksPageRequest;
import ru.ok.java.api.request.groups.GetGroupPageRequest;
import ru.ok.java.api.request.groups.GroupSettingsPageRequest;
import ru.ok.java.api.request.groups.HappeningUrlRequest;
import ru.ok.java.api.request.groups.InviteGroupsPageRequest;
import ru.ok.java.api.request.guests.GetGuestsMarksRequest;
import ru.ok.java.api.request.notifications.GetNotificationsPageRequest;
import ru.ok.java.api.request.presents.PresentPaymentRequest;
import ru.ok.java.api.request.relatives.SetRelationsPageRequest;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.java.api.request.stickers.StickersPaymentRequest;

public class WebUrlCreator {
    public static String getDiscussionsUrl(boolean addUpParams) throws NotSessionKeyException {
        try {
            String url = new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetDiscussionRequest(ConfigurationPreferences.getInstance().getWebServer())).getURI().toString();
            if (AppParamsManagerImpl.getInstance().isEmpty() || !addUpParams) {
                return url;
            }
            url = url + "&app.params=" + AppParamsManagerImpl.getInstance().popAppParams();
            AppParamsManagerImpl.getInstance().clear();
            return url;
        } catch (Throwable e) {
            Logger.m179e(e, "error create getActivitiesUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getUserSettingsUrl() {
        return "http://m.odnoklassniki.ru/settings";
    }

    public static String getGuestsMarksUrl() throws NotSessionKeyException {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetGuestsMarksRequest(ConfigurationPreferences.getInstance().getWebServer())).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getActivitiesUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getGroupLinksPageUrl(String gid, boolean addUpParams) throws NotSessionKeyException {
        try {
            String url = new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetGroupLinksPageRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), gid)).getURI().toString();
            if (AppParamsManagerImpl.getInstance().isEmpty() || !addUpParams) {
                return url;
            }
            url = url + "&app.params=" + AppParamsManagerImpl.getInstance().popAppParams();
            AppParamsManagerImpl.getInstance().clear();
            return url;
        } catch (Throwable e) {
            Logger.m179e(e, "error black list Url");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getGroupSettingsPageUrl(String gid, boolean addUpParams) throws NotSessionKeyException {
        try {
            String url = new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GroupSettingsPageRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), gid)).getURI().toString();
            if (AppParamsManagerImpl.getInstance().isEmpty() || !addUpParams) {
                return url;
            }
            url = url + "&app.params=" + AppParamsManagerImpl.getInstance().popAppParams();
            AppParamsManagerImpl.getInstance().clear();
            return url;
        } catch (Throwable e) {
            Logger.m179e(e, "error Url");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getNotificationPageUrl() {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetNotificationsPageRequest(ConfigurationPreferences.getInstance().getWebServer())).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getNotificationPageUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getGroupPageUrl(String groupId) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetGroupPageRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), groupId)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getGroupPageUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getPhotoPaymentUrl(String aid, String pid, String fid) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new PhotoMarkPaymentRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), aid, pid, fid)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getPhotoPaymentUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getHappening(String happeningId) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new HappeningUrlRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), happeningId)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getHappening");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getPaymentUrl(String userId) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new PaymentRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), userId)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getPaymentUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getStickerPaymentUrl() {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new StickersPaymentRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl())).getURI().toString();
        } catch (Throwable e) {
            Logger.m178e(e);
            return "m.odnoklassniki.ru";
        }
    }

    public static String getGoToUrl(String originUrl, String info) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetRedirectRequest(ConfigurationPreferences.getInstance().getWebServer(), originUrl, info)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create getPaymentUrl");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getUrl(String methodPattern, String userId, Map<String, String> params) {
        try {
            if (TextUtils.isEmpty(userId)) {
                userId = JsonSessionTransportProvider.getInstance().getStateHolder().getUserId();
            }
            Long userIntId = Long.valueOf(Long.parseLong(userId));
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new GetUrlsRequester(ConfigurationPreferences.getInstance().getWebServer(), methodPattern.replace("<user_id>", String.valueOf(Long.valueOf(userIntId.longValue() == 265224201205L ? userIntId.longValue() : userIntId.longValue() ^ 265224201205L))), params)).getURI().toString();
        } catch (Throwable e) {
            Logger.m178e(e);
            return "";
        }
    }

    public static String getInviteGroupsPageUrl(String uid) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new InviteGroupsPageRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), uid)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create invite group url");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getFriendsShipPageUrl(String uid) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new SetRelationsPageRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), uid)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create friendship url");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getMakePresentPageUrl(String userId, String prId, String holidayId) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new MakePresentRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), userId, prId, holidayId)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create make present url");
            return "m.odnoklassniki.ru";
        }
    }

    public static String getMakePresentPageUrl(String prId) {
        return getMakePresentPageUrl(null, prId, null);
    }

    public static String getPresentPaymentUrl(int presentCost, @NonNull String completeLink) {
        try {
            return new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(new PresentPaymentRequest(JsonSessionTransportProvider.getInstance().getWebBaseUrl(), completeLink, presentCost)).getURI().toString();
        } catch (Throwable e) {
            Logger.m179e(e, "error create send present url");
            return "m.odnoklassniki.ru";
        }
    }
}

package ru.ok.android.services.processors.notification.tasks;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;
import org.json.JSONException;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public class LoadUserAvatarTask extends LoadNotificationIconTask {
    private final String senderId;

    public LoadUserAvatarTask(@NonNull String senderId, @NonNull NotificationSignal notificationSignal) {
        super(notificationSignal);
        this.senderId = senderId;
    }

    @Nullable
    public Uri getNotificationIconUri() throws Exception {
        UserInfo userInfo = loadUserInfo(this.senderId);
        if (userInfo == null || userInfo.getPicUrl() == null) {
            return null;
        }
        return Uri.parse(userInfo.getPicUrl());
    }

    @NonNull
    private static UserInfoRequest createLoadUserInfoRequest(@NonNull String senderId) {
        return new UserInfoRequest(new BaseStringParam(senderId), new RequestFieldsBuilder().addField(DeviceUtils.getUserAvatarPicFieldName()).build(), true, false);
    }

    @Nullable
    private static UserInfo loadUserInfo(@NonNull String senderId) throws BaseApiException, JSONException {
        List<UserInfo> userInfos = new JsonGetUsersInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createLoadUserInfoRequest(senderId))).parse();
        if (userInfos.size() > 0) {
            return (UserInfo) userInfos.get(0);
        }
        throw new ResultParsingException("user info not found");
    }
}

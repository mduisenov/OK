package ru.ok.android.services.processors.notification.tasks;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.groups.JsonGroupInfoParser;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.groups.GroupInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.GroupInfo;

public class LoadGroupAvatarTask extends LoadNotificationIconTask {
    private final String groupId;

    public LoadGroupAvatarTask(@NonNull String groupId, @NonNull NotificationSignal notificationSignal) {
        super(notificationSignal);
        this.groupId = groupId;
    }

    @Nullable
    public Uri getNotificationIconUri() throws Exception {
        GroupInfo groupInfo = loadGroupInfo(this.groupId);
        if (groupInfo == null || groupInfo.getBigPicUrl() == null) {
            return null;
        }
        return Uri.parse(groupInfo.getBigPicUrl());
    }

    @NonNull
    private static GroupInfoRequest createLoadGroupInfoRequest(@NonNull String senderId) {
        return new GroupInfoRequest(Arrays.asList(new String[]{senderId}), new RequestFieldsBuilder().addFields(FIELDS.GROUP_PHOTO, FIELDS.GROUP_MAIN_PHOTO).build(), false);
    }

    @Nullable
    private static GroupInfo loadGroupInfo(@NonNull String groupId) throws BaseApiException, JSONException {
        JSONArray jsonArray = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createLoadGroupInfoRequest(groupId)).getResultAsArray();
        if (jsonArray == null || jsonArray.length() <= 0) {
            return null;
        }
        return JsonGroupInfoParser.parse(jsonArray.getJSONObject(0));
    }
}

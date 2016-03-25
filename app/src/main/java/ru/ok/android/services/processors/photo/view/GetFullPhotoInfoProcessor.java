package ru.ok.android.services.processors.photo.view;

import android.os.Bundle;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.groups.JsonGroupInfoParser;
import ru.ok.java.api.json.photo.JsonGetPhotoAlbumInfoParser;
import ru.ok.java.api.json.photo.JsonGetPhotoInfoParser;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest;
import ru.ok.java.api.request.image.GetPhotoInfoRequest;
import ru.ok.java.api.request.image.GetPhotoInfoRequest.FIELDS;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class GetFullPhotoInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131623999)
    public void getFullPhotoInfo(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String photoId = bundleInput.getString("pid");
        String albumId = bundleInput.getString("aid");
        String groupId = bundleInput.getString("gid");
        String userId = bundleInput.getString("uid");
        boolean requestUserInfo = bundleInput.getBoolean("rui");
        boolean requestAlbumInfo = bundleInput.getBoolean("rai");
        boolean requestGroupInfo = bundleInput.getBoolean("rgi");
        bundleOutput.putString("pid", photoId);
        bundleOutput.putString("aid", albumId);
        bundleOutput.putString("gid", groupId);
        bundleOutput.putString("uid", userId);
        if (photoId != null) {
            BatchRequests requests = new BatchRequests();
            GetPhotoInfoRequest getPhotoInfoRequest = createPhotoInfoRequest(photoId, userId, groupId);
            requests.addRequest(getPhotoInfoRequest);
            if (requestGroupInfo && !TextUtils.isEmpty(groupId)) {
                requests.addRequest(createGroupInfoRequest(groupId));
            }
            if (requestUserInfo) {
                if (userId != null) {
                    BaseStringParam baseStringParam = new BaseStringParam(userId);
                } else {
                    RequestJSONParam requestJSONParam = new RequestJSONParam(new SupplierRequest(getPhotoInfoRequest.getUserIdSupplier()));
                }
                requests.addRequest(createUserInfoRequest(userIdParam));
            }
            if (requestAlbumInfo && !TextUtils.isEmpty(albumId)) {
                requests.addRequest(createAlbumInfoRequest(albumId, userId, groupId));
            }
            try {
                String str;
                BaseRequest batchRequest = new BatchRequest(requests);
                JSONObject json = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(batchRequest).getResultAsObject();
                if (json.has("users_getInfo_response")) {
                    JSONArray array = JsonUtil.getJsonArraySafely(json, "users_getInfo_response");
                    if (array != null) {
                        List<UserInfo> userInfos = new JsonGetUsersInfoParser(null).parser(array);
                        if (!userInfos.isEmpty()) {
                            UserInfo userInfo = new UserInfo((UserInfo) userInfos.get(0));
                            bundleOutput.putParcelable("unfo", userInfo);
                        }
                    }
                }
                if (json.has("group_getInfo_response")) {
                    JSONArray groupsArray = json.getJSONArray("group_getInfo_response");
                    if (groupsArray != null && groupsArray.length() > 0) {
                        bundleOutput.putParcelable("gnfo", JsonGroupInfoParser.parse(groupsArray.getJSONObject(0)));
                    }
                }
                if (json.has("photos_getAlbumInfo_response")) {
                    JSONObject albumsInfoJson = json.getJSONObject("photos_getAlbumInfo_response");
                    if (albumsInfoJson.length() > 0) {
                        str = "anfo";
                        bundleOutput.putParcelable(r29, JsonGetPhotoAlbumInfoParser.parse(albumsInfoJson.getJSONObject("album")));
                    }
                }
                if (json.has("photos_getPhotoInfo_response")) {
                    JsonGetPhotoInfoParser parser = new JsonGetPhotoInfoParser();
                    str = "pnfo";
                    bundleOutput.putParcelable(r29, JsonGetPhotoInfoParser.parse(json.getJSONObject("photos_getPhotoInfo_response")));
                }
                resultCode = -1;
            } catch (Exception e) {
                Logger.m176e("Unable to get full info for photo " + photoId);
            }
        }
        GlobalBus.send(2131624176, new BusEvent(bundleInput, bundleOutput, resultCode));
    }

    private GetPhotoInfoRequest createPhotoInfoRequest(String photoId, String fid, String gid) {
        GetPhotoInfoRequest getPhotoInfoRequest = new GetPhotoInfoRequest(photoId, fid, gid);
        RequestFieldsBuilder builder = new RequestFieldsBuilder().addField(FIELDS.USER_ID).addField(FIELDS.CREATED_MS);
        if (!TextUtils.isEmpty(gid)) {
            builder.withPrefix("group_");
        }
        getPhotoInfoRequest.setFields(builder.build());
        return getPhotoInfoRequest;
    }

    private GetPhotoAlbumInfoRequest createAlbumInfoRequest(String albumId, String userId, String groupId) {
        BaseRequestParam albumIdParam = new BaseStringParam(albumId);
        BaseRequestParam userIdParam = null;
        BaseRequestParam groupIdParam = null;
        if (!TextUtils.isEmpty(groupId)) {
            groupIdParam = new BaseStringParam(groupId);
        } else if (!TextUtils.isEmpty(userId)) {
            userIdParam = new BaseStringParam(userId);
        }
        GetPhotoAlbumInfoRequest getAlbumInfoRequest = new GetPhotoAlbumInfoRequest(albumIdParam, userIdParam, groupIdParam);
        RequestFieldsBuilder builder = new RequestFieldsBuilder().addFields(GetPhotoAlbumInfoRequest.FIELDS.ALBUM_ALL, GetPhotoAlbumInfoRequest.FIELDS.LIKE_SUMMARY);
        if (!TextUtils.isEmpty(groupId)) {
            builder.withPrefix("group_");
        }
        getAlbumInfoRequest.setFields(builder.build());
        return getAlbumInfoRequest;
    }

    private UserInfoRequest createUserInfoRequest(BaseRequestParam userIdParam) {
        return new UserInfoRequest(userIdParam, new RequestFieldsBuilder().addFields(UserInfoRequest.FIELDS.FIRST_NAME, UserInfoRequest.FIELDS.LAST_NAME).build(), true);
    }

    private GroupInfoRequest createGroupInfoRequest(String gid) {
        return new GroupInfoRequest(Arrays.asList(new String[]{gid}), new RequestFieldsBuilder().addFields(GroupInfoRequest.FIELDS.GROUP_ID, GroupInfoRequest.FIELDS.GROUP_ADD_PHOTOALBUM_ALLOWED, GroupInfoRequest.FIELDS.GROUP_CHANGE_AVATAR_ALLOWED, GroupInfoRequest.FIELDS.GROUP_DESCRIPTION, GroupInfoRequest.FIELDS.GROUP_NAME).build());
    }
}

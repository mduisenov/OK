package ru.ok.android.services.processors.photo.view;

import android.os.Bundle;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.groups.JsonGroupInfoParser;
import ru.ok.java.api.json.photo.JsonPhotoAlbumsInfoParser;
import ru.ok.java.api.json.photo.JsonPhotosInfoParser;
import ru.ok.java.api.json.users.JsonUserInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.groups.GroupInfoRequest.FIELDS;
import ru.ok.java.api.request.image.GetPhotoAlbumsRequest;
import ru.ok.java.api.request.image.GetPhotosRequest;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class GetPhotoAlbumsBatchProcessor {
    @Subscribe(on = 2131623944, to = 2131624001)
    public void getPhotoAlbumsBatch(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        BatchRequests requests = new BatchRequests();
        boolean requestOwnerInfo = bundleInput.getBoolean("gwnrnfo");
        boolean requestAlbumsInfo = bundleInput.getBoolean("ganfo");
        boolean requestPersonalMainPhoto = bundleInput.getBoolean("rtfp");
        boolean requestTagsMainPhoto = bundleInput.getBoolean("rtmp");
        PhotoOwner photoOwner = (PhotoOwner) bundleInput.getParcelable("wnrnfo");
        bundleOutput.putParcelable("wnrnfo", photoOwner);
        if (requestOwnerInfo) {
            if (photoOwner.getType() == 0) {
                requests.addRequest(getUserInfoRequest(photoOwner.getId()));
            } else {
                requests.addRequest(getGroupInfoRequest(photoOwner.getId()));
            }
        }
        if (requestAlbumsInfo) {
            requests.addRequest(getAlbumsInfoRequest(photoOwner, bundleInput));
        }
        if (requestPersonalMainPhoto) {
            requests.addRequest(getFakeAlbumRequest(photoOwner, null));
        }
        if (requestTagsMainPhoto) {
            requests.addRequest(getFakeAlbumRequest(photoOwner, "tags"));
        }
        try {
            JSONObject json = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(requests)).getResultAsObject();
            if (requestOwnerInfo) {
                if (photoOwner.getType() == 0) {
                    if (json.has("users_getInfo_response")) {
                        JSONArray usersArray = json.getJSONArray("users_getInfo_response");
                        if (usersArray != null && usersArray.length() > 0) {
                            photoOwner.setOwnerInfo(new UserInfo(new JsonUserInfoParser(usersArray.getJSONObject(0)).parse()));
                            bundleOutput.putParcelable("wnrnfo", photoOwner);
                        }
                    }
                } else {
                    if (json.has("group_getInfo_response")) {
                        JSONArray groupsArray = json.getJSONArray("group_getInfo_response");
                        if (groupsArray != null && groupsArray.length() > 0) {
                            photoOwner.setOwnerInfo(JsonGroupInfoParser.parse(groupsArray.getJSONObject(0)));
                            bundleOutput.putParcelable("wnrnfo", photoOwner);
                        }
                    }
                }
            }
            if (json.has("photos_getAlbums_response")) {
                bundleOutput.putParcelable("albmnfo", new JsonPhotoAlbumsInfoParser().parse(json.getJSONObject("photos_getAlbums_response")));
            }
            if (json.has("photos_getPhotos_response")) {
                JSONArray responses = json.getJSONArray("photos_getPhotos_response");
                JsonPhotosInfoParser parser = new JsonPhotosInfoParser();
                if (responses.length() == 1) {
                    bundleOutput.putParcelable(requestPersonalMainPhoto ? "mpnfo" : "tpnfo", parser.parse(responses.getJSONObject(0)));
                } else if (responses.length() > 1) {
                    bundleOutput.putParcelable("mpnfo", parser.parse(responses.getJSONObject(0)));
                    bundleOutput.putParcelable("tpnfo", parser.parse(responses.getJSONObject(1)));
                }
            }
            resultCode = -1;
        } catch (Throwable sexc) {
            if (sexc.getErrorCode() == 457 || sexc.getErrorCode() == 456 || sexc.getErrorCode() == 455) {
                resultCode = 2;
            }
            Logger.m178e(sexc);
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624178, new BusEvent(bundleInput, bundleOutput, resultCode));
    }

    private static final BaseRequest getGroupInfoRequest(String id) {
        return new GroupInfoRequest(Arrays.asList(new String[]{id}), new RequestFieldsBuilder().addFields(FIELDS.GROUP_NAME, FIELDS.GROUP_ADD_PHOTOALBUM_ALLOWED, FIELDS.GROUP_CHANGE_AVATAR_ALLOWED, FIELDS.GROUP_ID).build());
    }

    private static final BaseRequest getUserInfoRequest(String id) {
        return new UserInfoRequest(new BaseStringParam(id), new RequestFieldsBuilder().addField(UserInfoRequest.FIELDS.FIRST_NAME).addField(UserInfoRequest.FIELDS.LAST_NAME).addField(UserInfoRequest.FIELDS.NAME).addField(UserInfoRequest.FIELDS.UID).build(), false);
    }

    private static final BaseRequest getAlbumsInfoRequest(PhotoOwner photoOwner, Bundle data) {
        GetPhotoAlbumsRequest request;
        String anchor = data.getString("anchr");
        boolean forward = data.getBoolean("fwd", true);
        int count = data.getInt("cnt");
        boolean detectTotalCount = data.getBoolean("dtctcnt");
        String fields = data.getString("flds");
        if (photoOwner.getType() == 0) {
            request = new GetPhotoAlbumsRequest(null, photoOwner.getId(), null, anchor, forward, count, detectTotalCount);
        } else {
            request = new GetPhotoAlbumsRequest(null, null, photoOwner.getId(), anchor, forward, count, detectTotalCount);
        }
        request.setFields(fields);
        return request;
    }

    private static final BaseRequest getFakeAlbumRequest(PhotoOwner photoOwner, String albumId) {
        if (photoOwner.getType() == 0) {
            return new GetPhotosRequest(null, photoOwner.getId(), null, albumId, null, true, 1, true);
        }
        return new GetPhotosRequest(null, null, photoOwner.getId(), albumId, null, true, 1, true);
    }
}

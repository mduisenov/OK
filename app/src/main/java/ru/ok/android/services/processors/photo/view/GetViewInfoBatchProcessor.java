package ru.ok.android.services.processors.photo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.groups.JsonGroupInfoParser;
import ru.ok.java.api.json.photo.JsonGetPhotoAlbumInfoParser;
import ru.ok.java.api.json.photo.JsonGetPhotoInfoParser;
import ru.ok.java.api.json.photo.JsonPhotosInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest;
import ru.ok.java.api.request.image.GetPhotoInfoRequest;
import ru.ok.java.api.request.image.GetPhotoInfoRequest.FIELDS;
import ru.ok.java.api.request.image.GetPhotoInfosByIdsRequest;
import ru.ok.java.api.request.image.GetPhotosRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.GroupInfo;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotosInfo;

public final class GetViewInfoBatchProcessor {

    private static class ViewInfoBatchRequestParams {
        final String albumId;
        @Nullable
        final PageAnchor anchor;
        final int count;
        final String photoId;
        final PhotoOwner photoOwner;
        final String[] spids;

        ViewInfoBatchRequestParams(Bundle bundleInput) {
            this.photoId = bundleInput.getString("pid");
            this.photoOwner = (PhotoOwner) bundleInput.getParcelable("phwnr");
            this.anchor = (PageAnchor) bundleInput.getParcelable("anchor");
            this.albumId = bundleInput.getString("aid");
            this.count = bundleInput.getInt("cnt");
            this.spids = bundleInput.getStringArray("phtseq");
        }

        Bundle toOutputBundle() {
            Bundle bundleOutput = new Bundle();
            bundleOutput.putString("pid", this.photoId);
            bundleOutput.putString("aid", this.albumId);
            bundleOutput.putParcelable("phwnr", this.photoOwner);
            bundleOutput.putParcelable("anchor", this.anchor);
            return bundleOutput;
        }
    }

    @Subscribe(on = 2131623944, to = 2131624006)
    public void getViewInfoBatch(BusEvent event) {
        int resultCode = -2;
        ViewInfoBatchRequestParams params = new ViewInfoBatchRequestParams(event.bundleInput);
        Bundle bundleOutput = params.toOutputBundle();
        if (params.photoId != null) {
            BatchRequests requests = buildBatchRequests(params, event.bundleInput);
            if (requests.size() != 0) {
                resultCode = executeBatchRequests(requests, params, bundleOutput);
            } else {
                return;
            }
        }
        GlobalBus.send(2131624183, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }

    @NonNull
    private BatchRequests buildBatchRequests(@NonNull ViewInfoBatchRequestParams params, @NonNull Bundle bundleInput) {
        BatchRequests requests = new BatchRequests();
        String fields = buildViewInfoBatchRequestsDefaultFields(params.photoOwner.getType());
        if (params.spids == null || params.spids.length <= 0) {
            if (bundleInput.getBoolean("gphtnfo")) {
                populatePhotoInfoRequest(requests, params, fields);
            }
            populateNonSequenceRequest(requests, params, fields);
        } else {
            populatePhotoInfosByIdsRequest(requests, params, fields);
            populateSequenceRequest(requests, params, fields);
        }
        if (bundleInput.getBoolean("ganfo") && !TextUtils.isEmpty(params.albumId)) {
            requests.addRequest(createAlbumInfoRequest(params.albumId, params.photoOwner));
        }
        if (bundleInput.getBoolean("ggnfo")) {
            requests.addRequest(createGroupInfoRequest(params.photoOwner.getId()));
        }
        return requests;
    }

    private String buildViewInfoBatchRequestsDefaultFields(int photoOwnerType) {
        RequestFieldsBuilder fieldsBuilder = new RequestFieldsBuilder().addField(FIELDS.ALL).addField(FIELDS.STANDARD_WIDTH).addField(FIELDS.STANDARD_HEIGHT);
        if (photoOwnerType == 0) {
            fieldsBuilder.addField(FIELDS.TAG_COUNT);
        } else {
            fieldsBuilder.withPrefix("group_");
        }
        return fieldsBuilder.build();
    }

    private int executeBatchRequests(@NonNull BatchRequests batchRequests, @NonNull ViewInfoBatchRequestParams params, @NonNull Bundle bundleOutput) {
        try {
            parseJsonResult(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(batchRequests)).getResultAsObject(), params, bundleOutput);
            return -1;
        } catch (Throwable sexc) {
            Logger.m178e(sexc);
            if (sexc.getErrorCode() == 457 || sexc.getErrorCode() == 456 || sexc.getErrorCode() == 455) {
                return 4;
            }
            if (sexc.getErrorCode() == 300) {
                return 3;
            }
            return -2;
        } catch (NetworkException e) {
            return 5;
        } catch (Throwable exc) {
            Logger.m178e(exc);
            return -2;
        }
    }

    private void parseJsonResult(@NonNull JSONObject json, @NonNull ViewInfoBatchRequestParams params, @NonNull Bundle bundleOutput) throws ResultParsingException, JSONException {
        bundleOutput.putParcelable("albnfo", parsePhotosGetAlbumInfoJson(json));
        PhotosInfo[] photosInfos = parsePhotosGetPhotosJson(json);
        if (photosInfos != null) {
            bundleOutput.putParcelable("phtsbckw", photosInfos[0]);
            bundleOutput.putParcelable("phtsfwd", photosInfos[1]);
        }
        bundleOutput.putParcelable("phtinfo", parsePhotosGetPhotoInfoJson(json));
        GroupInfo groupInfo = parseGroupGetInfoJson(json);
        if (groupInfo != null) {
            params.photoOwner.setOwnerInfo(groupInfo);
            bundleOutput.putParcelable("phwnr", params.photoOwner);
        }
        ArrayList<PhotoInfo> photoInfos = parsePhotosGetInfoJson(json);
        if (photoInfos != null) {
            bundleOutput.putParcelableArrayList("phtseq", photoInfos);
        }
    }

    private PhotoInfo parsePhotosGetPhotoInfoJson(@NonNull JSONObject json) throws ResultParsingException {
        JSONObject photoInfoResponse = JsonUtil.getJsonObjectSafely(json, "photos_getPhotoInfo_response");
        return photoInfoResponse != null ? JsonGetPhotoInfoParser.parse(photoInfoResponse) : null;
    }

    @Nullable
    private ArrayList<PhotoInfo> parsePhotosGetInfoJson(@NonNull JSONObject json) throws JSONException, ResultParsingException {
        JSONObject photosResponse = JsonUtil.getJsonObjectSafely(json, "photos_getInfo_response");
        if (photosResponse != null) {
            JSONArray photosArray = JsonUtil.getJsonArraySafely(photosResponse, "photos");
            if (photosArray != null) {
                int length = photosArray.length();
                if (length > 0) {
                    ArrayList<PhotoInfo> arrayList = new ArrayList(length);
                    for (int i = 0; i < length; i++) {
                        arrayList.add(JsonGetPhotoInfoParser.parse(photosArray.getJSONObject(i)));
                    }
                    return arrayList;
                }
            }
        }
        return null;
    }

    @Nullable
    private GroupInfo parseGroupGetInfoJson(@NonNull JSONObject json) throws JSONException, ResultParsingException {
        JSONArray groupsArray = JsonUtil.getJsonArraySafely(json, "group_getInfo_response");
        if (groupsArray == null || groupsArray.length() <= 0) {
            return null;
        }
        return JsonGroupInfoParser.parse(groupsArray.getJSONObject(0));
    }

    @Nullable
    private PhotosInfo[] parsePhotosGetPhotosJson(@NonNull JSONObject json) throws JSONException, ResultParsingException {
        JSONArray photosJson = JsonUtil.getJsonArraySafely(json, "photos_getPhotos_response");
        if (photosJson == null) {
            return null;
        }
        JsonPhotosInfoParser parser = new JsonPhotosInfoParser();
        PhotosInfo backwardPhotosInfo = parser.parse(photosJson.getJSONObject(0));
        PhotosInfo forwardPhotosInfo = parser.parse(photosJson.getJSONObject(1));
        return new PhotosInfo[]{backwardPhotosInfo, forwardPhotosInfo};
    }

    @Nullable
    private PhotoAlbumInfo parsePhotosGetAlbumInfoJson(@NonNull JSONObject json) throws ResultParsingException {
        JSONObject albumJson = JsonUtil.getJsonObjectSafely(json, "photos_getAlbumInfo_response");
        return albumJson != null ? JsonGetPhotoAlbumInfoParser.parse(albumJson) : null;
    }

    private void populatePhotoInfoRequest(@NonNull BatchRequests requests, @NonNull ViewInfoBatchRequestParams params, @NonNull String fields) {
        String fid;
        String gid;
        if (params.photoOwner.getType() == 0) {
            fid = params.photoOwner.getId();
        } else {
            fid = null;
        }
        if (params.photoOwner.getType() == 1) {
            gid = params.photoOwner.getId();
        } else {
            gid = null;
        }
        GetPhotoInfoRequest getPhotoInfoRequest = new GetPhotoInfoRequest(params.photoId, fid, gid);
        getPhotoInfoRequest.setFields(fields);
        requests.addRequest(getPhotoInfoRequest);
    }

    private void populateNonSequenceRequest(@NonNull BatchRequests requests, @NonNull ViewInfoBatchRequestParams params, @NonNull String fields) {
        if (!TextUtils.isEmpty(params.albumId) || params.photoOwner.getType() != 1) {
            GetPhotosRequest backwardRequest = createPhotosRequest(params.photoOwner, params.albumId, params.anchor != null ? params.anchor.getBackwardAnchor() : null, false, params.count, false);
            backwardRequest.setFields(fields);
            requests.addRequest(backwardRequest);
            GetPhotosRequest forwardRequest = createPhotosRequest(params.photoOwner, params.albumId, params.anchor != null ? params.anchor.getForwardAnchor() : null, true, params.count, false);
            forwardRequest.setFields(fields);
            requests.addRequest(forwardRequest);
        }
    }

    private void populateSequenceRequest(@NonNull BatchRequests requests, @NonNull ViewInfoBatchRequestParams params, @NonNull String fields) {
        if (!TextUtils.isEmpty(params.albumId) || params.photoOwner.getType() != 1) {
            GetPhotosRequest backwardRequest = createPhotosRequest(params.photoOwner, params.albumId, params.anchor != null ? params.anchor.getBackwardAnchor() : null, false, params.count, false);
            backwardRequest.setFields(fields);
            requests.addRequest(backwardRequest);
            GetPhotosRequest forwardRequest = createPhotosRequest(params.photoOwner, params.albumId, params.anchor != null ? params.anchor.getForwardAnchor() : null, true, params.count, false);
            forwardRequest.setFields(fields);
            requests.addRequest(forwardRequest);
        }
    }

    private void populatePhotoInfosByIdsRequest(@NonNull BatchRequests requests, @NonNull ViewInfoBatchRequestParams params, @NonNull String fields) {
        if (params.spids != null && params.spids.length != 0) {
            String fid;
            if (params.photoOwner.getType() == 0) {
                fid = params.photoOwner.getId();
            } else {
                fid = null;
            }
            String gid;
            if (params.photoOwner.getType() == 1) {
                gid = params.photoOwner.getId();
            } else {
                gid = null;
            }
            for (GetPhotoInfosByIdsRequest spidsRequest : GetPhotoInfosByIdsRequest.create(null, fid, gid, params.albumId, params.spids, fields)) {
                requests.addRequest(spidsRequest);
            }
        }
    }

    @NonNull
    private GetPhotosRequest createPhotosRequest(PhotoOwner photoOwner, String albumId, String anchor, boolean forward, int count, boolean detectTotalCount) {
        if (photoOwner.getType() == 0) {
            return new GetPhotosRequest(null, photoOwner.getId(), null, albumId, anchor, forward, count, detectTotalCount);
        }
        return new GetPhotosRequest(null, null, photoOwner.getId(), albumId, anchor, forward, count, detectTotalCount);
    }

    @NonNull
    private BaseRequest createAlbumInfoRequest(String albumId, PhotoOwner photoOwner) {
        BaseRequestParam aidParam = null;
        if (!TextUtils.isEmpty(albumId)) {
            aidParam = new BaseStringParam(albumId);
        }
        if (photoOwner.getType() == 0) {
            return new GetPhotoAlbumInfoRequest(aidParam, new BaseStringParam(photoOwner.getId()), null);
        }
        return new GetPhotoAlbumInfoRequest(aidParam, null, new BaseStringParam(photoOwner.getId()));
    }

    @NonNull
    private BaseRequest createGroupInfoRequest(String gid) {
        return new GroupInfoRequest(Collections.singletonList(gid), new RequestFieldsBuilder().addFields(GroupInfoRequest.FIELDS.GROUP_ADD_PHOTOALBUM_ALLOWED, GroupInfoRequest.FIELDS.GROUP_CHANGE_AVATAR_ALLOWED, GroupInfoRequest.FIELDS.GROUP_DESCRIPTION, GroupInfoRequest.FIELDS.GROUP_DESCRIPTION, GroupInfoRequest.FIELDS.GROUP_NAME).build());
    }
}

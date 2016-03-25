package ru.ok.android.services.processors.photo.view;

import android.os.Bundle;
import java.util.Collections;
import org.json.JSONObject;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.photo.JsonPhotosInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest.FIELDS;
import ru.ok.java.api.request.image.GetPhotosRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.photo.PhotosInfo;

public final class GetAlbumInfoBatchProcessor {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @ru.ok.android.bus.annotation.Subscribe(on = 2131623944, to = 2131623998)
    public void getAlbumInfoBatch(ru.ok.android.bus.BusEvent r35) {
        /*
        r34 = this;
        r0 = r35;
        r6 = r0.bundleInput;
        r7 = new android.os.Bundle;
        r7.<init>();
        r24 = -2;
        r32 = "ganfo";
        r0 = r32;
        r17 = r6.getBoolean(r0);
        r32 = "gwnrnfo";
        r0 = r32;
        r18 = r6.getBoolean(r0);
        r32 = "gplist";
        r0 = r32;
        r19 = r6.getBoolean(r0);
        r32 = "gunfo";
        r0 = r32;
        r21 = r6.getBoolean(r0);
        r32 = "wnrnfo";
        r0 = r32;
        r15 = r6.getParcelable(r0);
        r15 = (ru.ok.android.model.image.PhotoOwner) r15;
        r32 = "rtmp";
        r0 = r32;
        r20 = r6.getBoolean(r0);
        r22 = new ru.ok.java.api.request.batch.BatchRequests;
        r22.<init>();
        r4 = 0;
        if (r17 == 0) goto L_0x0063;
    L_0x004b:
        r32 = "aid";
        r0 = r32;
        r2 = r6.getString(r0);
        r32 = android.text.TextUtils.isEmpty(r2);
        if (r32 != 0) goto L_0x0063;
    L_0x005a:
        r4 = getAlbumInfoRequest(r2, r15, r6);
        r0 = r22;
        r0.addRequest(r4);
    L_0x0063:
        if (r18 == 0) goto L_0x007f;
    L_0x0065:
        r32 = r15.getType();
        if (r32 != 0) goto L_0x00c7;
    L_0x006b:
        r32 = new ru.ok.java.api.request.param.BaseStringParam;
        r33 = r15.getId();
        r32.<init>(r33);
        r32 = getUserInfoRequest(r32);
        r0 = r22;
        r1 = r32;
        r0.addRequest(r1);
    L_0x007f:
        if (r19 == 0) goto L_0x008c;
    L_0x0081:
        r32 = getPhotosListRequest(r15, r6);
        r0 = r22;
        r1 = r32;
        r0.addRequest(r1);
    L_0x008c:
        if (r21 == 0) goto L_0x00ae;
    L_0x008e:
        r27 = 0;
        r32 = "uid";
        r0 = r32;
        r26 = r6.getString(r0);
        r32 = android.text.TextUtils.isEmpty(r26);
        if (r32 == 0) goto L_0x00d7;
    L_0x009f:
        if (r4 == 0) goto L_0x00a1;
    L_0x00a1:
        if (r27 == 0) goto L_0x00ae;
    L_0x00a3:
        r32 = getUserInfoRequest(r27);
        r0 = r22;
        r1 = r32;
        r0.addRequest(r1);
    L_0x00ae:
        if (r20 == 0) goto L_0x00c0;
    L_0x00b0:
        r32 = "tags";
        r0 = r32;
        r32 = getFakeAlbumRequest(r15, r0);
        r0 = r22;
        r1 = r32;
        r0.addRequest(r1);
    L_0x00c0:
        r32 = r22.size();
        if (r32 != 0) goto L_0x00e1;
    L_0x00c6:
        return;
    L_0x00c7:
        r32 = r15.getId();
        r32 = getGroupInfoRequest(r32);
        r0 = r22;
        r1 = r32;
        r0.addRequest(r1);
        goto L_0x007f;
    L_0x00d7:
        r27 = new ru.ok.java.api.request.param.BaseStringParam;
        r0 = r27;
        r1 = r26;
        r0.<init>(r1);
        goto L_0x00a1;
    L_0x00e1:
        r16 = new ru.ok.java.api.request.batch.BatchRequest;	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r16;
        r1 = r22;
        r0.<init>(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = ru.ok.android.services.transport.JsonSessionTransportProvider.getInstance();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r1 = r16;
        r13 = r0.execJsonHttpMethod(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r14 = r13.getResultAsObject();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "photos_getAlbumInfo_response";
        r0 = r32;
        r32 = r14.has(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 == 0) goto L_0x0121;
    L_0x0105:
        r32 = "photos_getAlbumInfo_response";
        r0 = r32;
        r32 = r14.getJSONObject(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r33 = "album";
        r9 = r32.getJSONObject(r33);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r3 = ru.ok.java.api.json.photo.JsonGetPhotoAlbumInfoParser.parse(r9);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "anfo";
        r0 = r32;
        r7.putParcelable(r0, r3);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x0121:
        r32 = "group_getInfo_response";
        r0 = r32;
        r32 = r14.has(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 == 0) goto L_0x0154;
    L_0x012c:
        r32 = "group_getInfo_response";
        r0 = r32;
        r12 = r14.getJSONArray(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r12 == 0) goto L_0x0154;
    L_0x0137:
        r32 = r12.length();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 <= 0) goto L_0x0154;
    L_0x013d:
        r32 = 0;
        r0 = r32;
        r11 = r12.getJSONObject(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r10 = ru.ok.java.api.json.groups.JsonGroupInfoParser.parse(r11);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r15.setOwnerInfo(r10);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "wnrnfo";
        r0 = r32;
        r7.putParcelable(r0, r15);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x0154:
        r32 = "users_getInfo_response";
        r0 = r32;
        r32 = r14.has(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 == 0) goto L_0x01a9;
    L_0x015f:
        r32 = "users_getInfo_response";
        r0 = r32;
        r31 = r14.getJSONArray(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r31 == 0) goto L_0x01a9;
    L_0x016a:
        r32 = r31.length();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 <= 0) goto L_0x01a9;
    L_0x0170:
        r32 = 0;
        r30 = r31.getJSONObject(r32);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = new ru.ok.java.api.json.users.JsonUserInfoParser;	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r1 = r30;
        r0.<init>(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r28 = r32.parse();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r29 = new ru.ok.model.UserInfo;	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r29;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = r15.getType();	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 != 0) goto L_0x019f;
    L_0x0192:
        r0 = r29;
        r15.setOwnerInfo(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "wnrnfo";
        r0 = r32;
        r7.putParcelable(r0, r15);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x019f:
        r32 = "unfo";
        r0 = r32;
        r1 = r29;
        r7.putParcelable(r0, r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x01a9:
        if (r19 != 0) goto L_0x01ad;
    L_0x01ab:
        if (r20 == 0) goto L_0x01f7;
    L_0x01ad:
        r32 = "photos_getPhotos_response";
        r0 = r32;
        r32 = r14.has(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        if (r32 == 0) goto L_0x01f7;
    L_0x01b8:
        r32 = "photos_getPhotos_response";
        r0 = r32;
        r32 = r14.get(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r0 = r0 instanceof org.json.JSONObject;	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = r0;
        if (r32 == 0) goto L_0x020a;
    L_0x01c9:
        r32 = "pnfo";
        r33 = "photos_getPhotos_response";
        r0 = r33;
        r33 = r14.getJSONObject(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r34;
        r1 = r33;
        r33 = r0.getPhotosFromJson(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r1 = r33;
        r7.putParcelable(r0, r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x01e4:
        r32 = "reset";
        r0 = r32;
        r23 = r6.getBoolean(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "reset";
        r0 = r32;
        r1 = r23;
        r7.putBoolean(r0, r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
    L_0x01f7:
        r24 = -1;
    L_0x01f9:
        r32 = 2131624175; // 0x7f0e00ef float:1.8875522E38 double:1.0531622747E-314;
        r33 = new ru.ok.android.bus.BusEvent;
        r0 = r33;
        r1 = r24;
        r0.<init>(r6, r7, r1);
        ru.ok.android.bus.GlobalBus.send(r32, r33);
        goto L_0x00c6;
    L_0x020a:
        r32 = "photos_getPhotos_response";
        r0 = r32;
        r32 = r14.get(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r0 = r0 instanceof org.json.JSONArray;	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = r0;
        if (r32 == 0) goto L_0x01e4;
    L_0x021b:
        r32 = "photos_getPhotos_response";
        r0 = r32;
        r5 = r14.getJSONArray(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "ptgsnfo";
        r33 = 1;
        r0 = r33;
        r33 = r5.getJSONObject(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r34;
        r1 = r33;
        r33 = r0.getPhotosFromJson(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r1 = r33;
        r7.putParcelable(r0, r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r32 = "pnfo";
        r33 = 0;
        r0 = r33;
        r33 = r5.getJSONObject(r0);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r34;
        r1 = r33;
        r33 = r0.getPhotosFromJson(r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        r0 = r32;
        r1 = r33;
        r7.putParcelable(r0, r1);	 Catch:{ ServerReturnErrorException -> 0x0259, Exception -> 0x0294 }
        goto L_0x01e4;
    L_0x0259:
        r25 = move-exception;
        r32 = r25.getErrorCode();
        r33 = 457; // 0x1c9 float:6.4E-43 double:2.26E-321;
        r0 = r32;
        r1 = r33;
        if (r0 == r1) goto L_0x027e;
    L_0x0266:
        r32 = r25.getErrorCode();
        r33 = 456; // 0x1c8 float:6.39E-43 double:2.253E-321;
        r0 = r32;
        r1 = r33;
        if (r0 == r1) goto L_0x027e;
    L_0x0272:
        r32 = r25.getErrorCode();
        r33 = 455; // 0x1c7 float:6.38E-43 double:2.25E-321;
        r0 = r32;
        r1 = r33;
        if (r0 != r1) goto L_0x0285;
    L_0x027e:
        r24 = 2;
    L_0x0280:
        ru.ok.android.utils.Logger.m178e(r25);
        goto L_0x01f9;
    L_0x0285:
        r32 = r25.getErrorCode();
        r33 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r0 = r32;
        r1 = r33;
        if (r0 != r1) goto L_0x0280;
    L_0x0291:
        r24 = 3;
        goto L_0x0280;
    L_0x0294:
        r8 = move-exception;
        ru.ok.android.utils.Logger.m178e(r8);
        goto L_0x01f9;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.photo.view.GetAlbumInfoBatchProcessor.getAlbumInfoBatch(ru.ok.android.bus.BusEvent):void");
    }

    private PhotosInfo getPhotosFromJson(JSONObject json) throws ResultParsingException {
        return new JsonPhotosInfoParser().parse(json);
    }

    private static GetPhotoAlbumInfoRequest getAlbumInfoRequest(String aid, PhotoOwner photoOwner, Bundle data) {
        GetPhotoAlbumInfoRequest request;
        RequestFieldsBuilder rfsb = new RequestFieldsBuilder().addFields(FIELDS.ALBUM_ALL, FIELDS.LIKE_SUMMARY);
        if (photoOwner.getType() == 1) {
            rfsb.withPrefix("group_");
        }
        String fields = rfsb.build();
        if (photoOwner.getType() == 0) {
            request = new GetPhotoAlbumInfoRequest(aid == null ? new BaseStringParam(aid) : new BaseStringParam(aid), new BaseStringParam(photoOwner.getId()), null);
        } else {
            request = new GetPhotoAlbumInfoRequest(new BaseStringParam(aid), null, new BaseStringParam(photoOwner.getId()));
        }
        request.setFields(fields);
        return request;
    }

    private static BaseRequest getGroupInfoRequest(String id) {
        return new GroupInfoRequest(Collections.singletonList(id), new RequestFieldsBuilder().addField(GroupInfoRequest.FIELDS.GROUP_NAME).addField(GroupInfoRequest.FIELDS.GROUP_ID).addField(GroupInfoRequest.FIELDS.GROUP_ADD_PHOTOALBUM_ALLOWED).addField(GroupInfoRequest.FIELDS.GROUP_CHANGE_AVATAR_ALLOWED).build());
    }

    private static BaseRequest getUserInfoRequest(BaseRequestParam id) {
        return new UserInfoRequest(id, new RequestFieldsBuilder().addField(UserInfoRequest.FIELDS.FIRST_NAME).addField(UserInfoRequest.FIELDS.LAST_NAME).addField(UserInfoRequest.FIELDS.NAME).addField(UserInfoRequest.FIELDS.UID).build(), false);
    }

    private static BaseRequest getPhotosListRequest(PhotoOwner photoOwner, Bundle data) {
        GetPhotosRequest request;
        String aid = data.getString("aid");
        int count = data.getInt("plcnt");
        String anchor = data.getString("anchr");
        boolean forward = data.getBoolean("fwd");
        boolean detectCount = data.getBoolean("dtctcnt");
        String fields = data.getString("plflds");
        if (photoOwner.getType() == 0) {
            request = new GetPhotosRequest(null, photoOwner.getId(), null, aid, anchor, forward, count, detectCount);
        } else {
            request = new GetPhotosRequest(null, null, photoOwner.getId(), aid, anchor, forward, count, detectCount);
        }
        request.setFields(fields);
        return request;
    }

    private static BaseRequest getFakeAlbumRequest(PhotoOwner photoOwner, String albumId) {
        if (photoOwner.getType() == 0) {
            return new GetPhotosRequest(null, photoOwner.getId(), null, albumId, null, true, 1, true);
        }
        return new GetPhotosRequest(null, null, photoOwner.getId(), albumId, null, true, 1, true);
    }
}

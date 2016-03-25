package ru.ok.android.services.processors;

import android.os.Bundle;
import org.json.JSONException;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.JsonPymkBatchParser;
import ru.ok.java.api.json.users.JsonPymkParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.friends.GetPymkRequest;
import ru.ok.java.api.request.friends.SuggestionsRequest;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.guest.UsersResult;

public final class PymkProcessor {
    @Subscribe(on = 2131623944, to = 2131623988)
    public final void loadPymk(BusEvent busEvent) {
        try {
            UsersResult guestResult = (UsersResult) new JsonPymkBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createBatchRequest("forward", busEvent.bundleInput.getString("key_anchor")))).parse();
            Bundle bundle = new Bundle();
            bundle.putParcelable("key_guest_result", guestResult);
            GlobalBus.send(2131624238, new BusEvent(busEvent.bundleInput, bundle, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624238, new BusEvent(busEvent.bundleInput, new Bundle(), -2));
        }
    }

    private static BaseRequest createBatchRequest(String pagingDirection, String pagingAnchor) {
        SuggestionsRequest guestRequest = new SuggestionsRequest(pagingDirection, pagingAnchor, null, 0);
        return new BatchRequest(new BatchRequests().addRequest(guestRequest).addRequest(new UserInfoRequest(new RequestJSONParam(new SupplierRequest(guestRequest.getUserIdsSupplier())), new RequestFieldsBuilder().addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(DeviceUtils.getUserAvatarPicFieldName()).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(FIELDS.CAN_VIDEO_CALL).addField(FIELDS.CAN_VIDEO_MAIL).build(), false)));
    }

    public static RequestFieldsBuilder getSuggestedFriendsFieldsBuilder() {
        return new RequestFieldsBuilder().addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(FIELDS.AGE).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(FIELDS.LOCATION).addField(DeviceUtils.getUserAvatarPicFieldName());
    }

    public static RequestFieldsBuilder getMutualFriendsFieldsBuilder() {
        return new RequestFieldsBuilder().addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(DeviceUtils.getUserAvatarPicFieldName());
    }

    public static Bundle loadPymkWithDetails(int usersCount, String userFields, int mutualFriendsCount, String mutualFriends) throws BaseApiException, JSONException {
        return JsonPymkParser.parsePymkWithDetails(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetPymkRequest(usersCount, userFields, mutualFriendsCount, mutualFriends)).getResultAsObject(), mutualFriendsCount);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @ru.ok.android.bus.annotation.Subscribe(on = 2131623944, to = 2131623989)
    public void process(ru.ok.android.bus.BusEvent r9) {
        /*
        r8 = this;
        r7 = 2131624166; // 0x7f0e00e6 float:1.8875504E38 double:1.0531622703E-314;
        r3 = 20;
        r4 = getSuggestedFriendsFieldsBuilder();	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r4 = r4.build();	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r5 = 3;
        r6 = getMutualFriendsFieldsBuilder();	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r6 = r6.build();	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r2 = loadPymkWithDetails(r3, r4, r5, r6);	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r3 = 2131624166; // 0x7f0e00e6 float:1.8875504E38 double:1.0531622703E-314;
        r4 = new ru.ok.android.bus.BusEvent;	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r5 = r9.bundleInput;	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        r6 = -1;
        r4.<init>(r5, r2, r6);	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
        ru.ok.android.bus.GlobalBus.send(r3, r4);	 Catch:{ BaseApiException -> 0x003a, JSONException -> 0x0029 }
    L_0x0028:
        return;
    L_0x0029:
        r0 = move-exception;
    L_0x002a:
        r1 = ru.ok.android.services.processors.base.CommandProcessor.createErrorBundle(r0);
        r3 = new ru.ok.android.bus.BusEvent;
        r4 = r9.bundleInput;
        r5 = -2;
        r3.<init>(r4, r1, r5);
        ru.ok.android.bus.GlobalBus.send(r7, r3);
        goto L_0x0028;
    L_0x003a:
        r0 = move-exception;
        goto L_0x002a;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.PymkProcessor.process(ru.ok.android.bus.BusEvent):void");
    }
}

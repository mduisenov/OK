package ru.ok.android.services.processors.friends;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.users.MutualFriendsBatchParser;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.friends.MutualFriendsRequest;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class MutualFriendsProcessor {
    private String getUserInfoFields() {
        return new RequestFieldsBuilder().addField(FIELDS.NAME).addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(DeviceUtils.getUserAvatarPicFieldName()).build();
    }

    @Subscribe(on = 2131623944, to = 2131624092)
    public void process(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        MutualFriendsRequest mutualFriendsRequest = new MutualFriendsRequest(event.bundleInput.getString("source_id"), event.bundleInput.getString("target_id"));
        try {
            ArrayList<UserInfo> result = new MutualFriendsBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(mutualFriendsRequest).addRequest(new UserInfoRequest(new RequestJSONParam(new SupplierRequest(MutualFriendsRequest.getSupplierId())), getUserInfoFields(), false)))).getResultAsObject()).parse();
            UsersStorageFacade.insertOrRewriteFriends(result, false, UserInfoValuesFiller.MUTUAL_FRIENDS);
            bundleOutput.putParcelableArrayList("mutual_friends", result);
            resultCode = -1;
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        GlobalBus.send(2131624232, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}

package ru.ok.android.services.processors.users;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.storage.Storages;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.json.users.ComplaintType;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.json.users.JsonUserCountersParser;
import ru.ok.java.api.json.users.JsonUserDeleteFriendParser;
import ru.ok.java.api.json.users.JsonUserInfoRelationsBatchParser;
import ru.ok.java.api.json.users.JsonUserInviteFriendParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SetStatusRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.friends.ComplaintToUserRequest;
import ru.ok.java.api.request.friends.UserDeleteFriendRequest;
import ru.ok.java.api.request.friends.UserInviteFriendRequest;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.stream.StreamIsSubscribeUserRequest;
import ru.ok.java.api.request.stream.StreamUserSubscribeRequest;
import ru.ok.java.api.request.users.MutualFriendsRequest;
import ru.ok.java.api.request.users.UserCountersRequest;
import ru.ok.java.api.request.users.UserInfoByRequest;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserPresentsRequest;
import ru.ok.java.api.request.users.UserRelationInfoRequest;
import ru.ok.java.api.response.users.UserInviteFriendResponse;
import ru.ok.java.api.response.users.UserInviteFriendResponse.UserInviteResult;
import ru.ok.java.api.response.users.UserRelationInfoResponse;
import ru.ok.java.api.response.users.UsersInfosBatchResponse;
import ru.ok.model.UserInfo;

public final class UsersProcessor {
    @Subscribe(on = 2131623944, to = 2131624074)
    public void getUserInfo(BusEvent event) {
        ArrayList<String> uids = event.bundleInput.getStringArrayList("USER_IDS");
        if (event.bundleInput.getBoolean("WITH_RELATIONS")) {
            try {
                if (uids.size() != 1) {
                    throw new IllegalArgumentException("We may request only one user with relation request");
                }
                fetchUserInfosWithRelations((String) uids.get(0), event);
                return;
            } catch (Throwable e) {
                Logger.m178e(e);
                GlobalBus.send(2131624221, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
                return;
            }
        }
        fetchUserInfos(uids, event);
    }

    private void fetchUserInfosWithRelations(String userId, BusEvent event) throws Exception {
        UserInfoValuesFiller filler = UserInfoValuesFiller.ALL_FOR_PROFILE;
        UserInfoByRequest requestingUserInfos = new UserInfoByRequest(userId, filler.getRequestFields());
        BaseRequest relationsInfoRequest = new UserRelationInfoRequest(userId);
        BaseRequest streamIsSubscribe = new StreamIsSubscribeUserRequest(userId);
        BaseRequest userCountersRequest = new UserCountersRequest(userId);
        BaseRequest presentsRequest = new UserPresentsRequest(userId);
        UsersInfosBatchResponse result = new JsonUserInfoRelationsBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(requestingUserInfos).addRequest(streamIsSubscribe, true).addRequest(userCountersRequest, true).addRequest(relationsInfoRequest, true).addRequest(presentsRequest, true).addRequest(new MutualFriendsRequest(userId, UserInfoValuesFiller.MUTUAL_FRIENDS.getRequestFields()), true))).getResultAsObject()).parse();
        UsersStorageFacade.insertUsers(result.userInfos, filler);
        UsersStorageFacade.insertUserRelations(userId, result.relation);
        UsersStorageFacade.syncSubscribeUserStreamRelations(result.subscribeValues);
        UsersStorageFacade.insertUserMutualFriends(userId, result.mutualFriends);
        if (result.counters != null) {
            UsersStorageFacade.updateUserCounters(userId, result.counters);
        }
        if (result.relationInfo != null) {
            UsersStorageFacade.insertUserRelationInfo(userId, result.relationInfo);
        }
        if (result.presents != null) {
            UsersStorageFacade.insertUserPresents(userId, result.presents);
        }
        UsersStorageFacade.insertUsers(result.mutualFriends, UserInfoValuesFiller.MUTUAL_FRIENDS);
        sendResponse(event, result.userInfos, result.relationInfo);
    }

    private void fetchUserInfos(List<String> uids, BusEvent event) throws Exception {
        UserInfoValuesFiller filler = UserInfoValuesFiller.ALL;
        ArrayList<UserInfo> result = new JsonGetUsersInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInfoRequest(new BaseStringParam(TextUtils.join(",", uids)), filler.getRequestFields(), true))).parse();
        UsersStorageFacade.insertUsers(result, filler);
        sendResponse(event, result, null);
    }

    private void sendResponse(BusEvent event, ArrayList<UserInfo> result, UserRelationInfoResponse relationInfoResponse) {
        Bundle output = new Bundle();
        output.putParcelableArrayList("USERS", result);
        if (relationInfoResponse != null) {
            output.putBoolean("USER_FRIEND", relationInfoResponse.isFriend);
            output.putBoolean("USER_BLOCKED", relationInfoResponse.isBlocks);
        }
        GlobalBus.send(2131624221, new BusEvent(event.bundleInput, output, -1));
    }

    @Subscribe(on = 2131623944, to = 2131624120)
    public void getUserCounters(BusEvent event) {
        String userId = event.bundleInput.getString("USER_ID");
        try {
            UsersStorageFacade.updateUserCounters(userId, new JsonUserCountersParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserCountersRequest(userId)).getResultAsObject()).parse());
            GlobalBus.send(2131624264, new BusEvent(event.bundleInput, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624264, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624122)
    public void inviteFriend(BusEvent event) {
        String userId = event.bundleInput.getString("USER_ID");
        try {
            int resultCode;
            if (((UserInviteResult) new JsonUserInviteFriendParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInviteFriendRequest(Arrays.asList(new String[]{userId}))).getResultAsObject()).parse().results.get(userId)) == UserInviteResult.SUCCESS) {
                resultCode = -1;
            } else {
                resultCode = -2;
            }
            if (resultCode == -1) {
                UsersStorageFacade.updateRelationInfoInvitation(userId);
            }
            GlobalBus.send(2131624266, new BusEvent(event.bundleInput, null, resultCode));
        } catch (Exception e) {
            GlobalBus.send(2131624266, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624123)
    public void inviteFriends(BusEvent event) {
        ArrayList<String> userIds = event.bundleInput.getStringArrayList("USER_IDS");
        try {
            UserInviteFriendResponse result = new JsonUserInviteFriendParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInviteFriendRequest(userIds)).getResultAsObject()).parse();
            Iterator i$ = userIds.iterator();
            while (i$.hasNext()) {
                int resultCode;
                String userId = (String) i$.next();
                if (((UserInviteResult) result.results.get(userId)) == UserInviteResult.SUCCESS) {
                    resultCode = -1;
                } else {
                    resultCode = -2;
                }
                if (resultCode == -1) {
                    UsersStorageFacade.updateRelationInfoInvitation(userId);
                }
            }
            GlobalBus.send(2131624266, new BusEvent(event.bundleInput));
        } catch (Exception e) {
            GlobalBus.send(2131624266, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624121)
    public void deleteFriend(BusEvent event) {
        String userId = event.bundleInput.getString("USER_ID");
        try {
            int resultCode;
            if (new JsonUserDeleteFriendParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserDeleteFriendRequest(Arrays.asList(new String[]{userId}))).getResultAsObject()).parse().results.size() == 0) {
                ArrayList<Pair<String, Boolean>> values = new ArrayList();
                values.add(new Pair(userId, Boolean.valueOf(true)));
                UsersStorageFacade.syncSubscribeUserStreamRelations(values);
                UsersStorageFacade.deleteFriend(userId);
                EventsManager.getInstance().updateNow();
                updateSubscriptionCache(userId, false);
                resultCode = -1;
            } else {
                resultCode = -2;
            }
            GlobalBus.send(2131624265, new BusEvent(event.bundleInput, null, resultCode));
        } catch (Exception e) {
            GlobalBus.send(2131624265, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623957)
    public void complaintToUser(BusEvent event) {
        String userId = event.bundleInput.getString("USER_ID");
        try {
            ComplaintType type = (ComplaintType) event.bundleInput.getSerializable("USERS_COMPLAINT_TYPE");
            boolean isAddToBlackList = event.bundleInput.getBoolean("USERS_ADD_TO_BLACKLIST", false);
            boolean resultValue = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ComplaintToUserRequest(userId, type, isAddToBlackList)).getResultAsObject().getBoolean("success");
            Bundle outBundle = new Bundle();
            outBundle.putBoolean("KEY_USER_COMPLAINT_RESULT_VALUE", resultValue);
            outBundle.putBoolean("USERS_ADD_TO_BLACKLIST", isAddToBlackList);
            GlobalBus.send(2131624137, new BusEvent(outBundle, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624137, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624111)
    public void subscribeToStream(BusEvent event) {
        String userId = event.bundleInput.getString("USER_ID");
        ArrayList<String> users = new ArrayList();
        users.add(userId);
        try {
            int resultCode;
            if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new StreamUserSubscribeRequest(users)).getResultAsObject().getBoolean("success")) {
                resultCode = -1;
                updateSubscriptionCache(userId, true);
            } else {
                resultCode = -2;
            }
            GlobalBus.send(2131624255, new BusEvent(event.bundleInput, resultCode));
        } catch (Exception e) {
            GlobalBus.send(2131624255, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624039)
    public void deleteUserStatus(BusEvent event) {
        try {
            if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SetStatusRequest("")).getHttpStatus() == 200) {
                Logger.m172d("delete status successfully");
                OdnoklassnikiApplication.getCurrentUser().status = null;
                UsersStorageFacade.insertUsers(Arrays.asList(new UserInfo[]{user}), UserInfoValuesFiller.STATUS);
                GlobalBus.send(2131624215, new BusEvent(null, -1));
                return;
            }
            GlobalBus.send(2131624215, new BusEvent(null, -2));
        } catch (Exception e) {
            Logger.m173d("delete error %s", e);
            GlobalBus.send(2131624215, new BusEvent(null, -2));
        }
    }

    private static void updateSubscriptionCache(String userId, boolean isSubscribed) {
        Context context = OdnoklassnikiApplication.getContext();
        if (context != null) {
            String cuid = OdnoklassnikiApplication.getCurrentUser().getId();
            if (!TextUtils.isEmpty(cuid)) {
                Storages.getInstance(context, cuid).getStreamSubscriptionManager().setSubscribedUser(userId, isSubscribed);
            }
        }
    }
}

package ru.ok.android.services.processors.friends;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.db.access.QueriesUsers.BestFriends;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.users.JsonFriendsDiffBatchParser;
import ru.ok.java.api.json.users.JsonGetSuggestionsBatchParser;
import ru.ok.java.api.json.users.JsonGetUsersInfoParser;
import ru.ok.java.api.json.users.UsersUidsParser;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.friends.BestFriendsRequest;
import ru.ok.java.api.request.friends.FriendsDiffRequest;
import ru.ok.java.api.request.friends.FriendsOnlineRequest;
import ru.ok.java.api.request.friends.SuggestionsRequest;
import ru.ok.java.api.request.param.RequestCollectionParam;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.users.FriendsDiffBatchResponse;
import ru.ok.java.api.response.users.FriendsDiffResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.UserInfo;

public final class GetFriendsProcessor {
    @Subscribe(on = 2131623944, to = 2131624119)
    public void getFriends(BusEvent event) {
        try {
            loadFriends(event.bundleInput.getInt("BEST_FRIENDS_COUNT"));
            GlobalBus.send(2131624263, new BusEvent(event.bundleInput, null, -1));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to fetch friends");
            GlobalBus.send(2131624263, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    public static void loadFriends(int bestFriendsCount) throws Exception {
        boolean hasUserInfoUpdates;
        boolean hasFriendIdsUpdates;
        boolean hasOnlineUpdates;
        List<String> userIds;
        Set<UserInfo> users;
        Context context = OdnoklassnikiApplication.getContext();
        SQLiteDatabase db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
        int currentCount = (int) DBStatementsFactory.getStatement(db, "SELECT COUNT(*) FROM friends f INNER JOIN users u ON f.friend_id = u.user_id").simpleQueryForLong();
        Logger.m173d("DB friends count: %d", Integer.valueOf(currentCount));
        String friendsIdsHash = currentCount > 0 ? Settings.getStrValue(context, "friends-ids-hash2") : null;
        FriendsDiffBatchResponse batchResult = JsonFriendsDiffBatchParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(new FriendsDiffRequest(currentCount > 0 ? Settings.getLongValue(context, "friends-diff-time2", 0) : 0, UserInfoValuesFiller.FRIENDS.getRequestFields() + ",relations", friendsIdsHash)).addRequest(new FriendsOnlineRequest(null)))).getResultAsObject());
        FriendsDiffResponse diff = batchResult.diffResponse;
        List<UserInfo> online = batchResult.onlineUsers;
        if (diff.friends != null) {
            if (!diff.friends.isEmpty()) {
                hasUserInfoUpdates = true;
                if (!TextUtils.isEmpty(friendsIdsHash)) {
                    if (!TextUtils.equals(diff.friendsIdsHash, friendsIdsHash)) {
                        hasFriendIdsUpdates = true;
                        hasOnlineUpdates = online == null && !online.isEmpty();
                        if (hasUserInfoUpdates || hasFriendIdsUpdates || hasOnlineUpdates) {
                            SQLiteUtils.beginTransaction(db);
                            if (hasUserInfoUpdates) {
                                try {
                                    UsersStorageFacade.updateUsers(db, diff.friends);
                                    UsersStorageFacade.rewriteRelatives(db, diff.friends, batchResult.relations);
                                } catch (Throwable th) {
                                    db.endTransaction();
                                }
                            }
                            if (TextUtils.isEmpty(friendsIdsHash)) {
                                userIds = new ArrayList();
                                if (diff.friends != null) {
                                    for (UserInfo friend : diff.friends) {
                                        userIds.add(friend.uid);
                                    }
                                }
                                UsersStorageFacade.updateFriends(db, userIds);
                            }
                            if (hasFriendIdsUpdates) {
                                UsersStorageFacade.updateFriends(db, diff.friendIds);
                            }
                            users = new HashSet();
                            if (online != null) {
                                users.addAll(online);
                            }
                            if (diff.onlineChangedFriends != null) {
                                users.addAll(diff.onlineChangedFriends);
                            }
                            UsersStorageFacade.updateFriendsOnline(db, users);
                            UsersStorageFacade.updateFriendsLastUpdate(db);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            context.getContentResolver().notifyChange(OdklProvider.friendsUri(), null);
                        }
                        Settings.storeStrValue(context, "friends-ids-hash2", diff.friendsIdsHash);
                        Settings.storeLongValue(context, "friends-diff-time2", diff.time);
                        updateBestFriends(bestFriendsCount);
                    }
                }
                hasFriendIdsUpdates = false;
                if (online == null) {
                }
                SQLiteUtils.beginTransaction(db);
                if (hasUserInfoUpdates) {
                    UsersStorageFacade.updateUsers(db, diff.friends);
                    UsersStorageFacade.rewriteRelatives(db, diff.friends, batchResult.relations);
                }
                if (TextUtils.isEmpty(friendsIdsHash)) {
                    userIds = new ArrayList();
                    if (diff.friends != null) {
                        while (i$.hasNext()) {
                            userIds.add(friend.uid);
                        }
                    }
                    UsersStorageFacade.updateFriends(db, userIds);
                }
                if (hasFriendIdsUpdates) {
                    UsersStorageFacade.updateFriends(db, diff.friendIds);
                }
                users = new HashSet();
                if (online != null) {
                    users.addAll(online);
                }
                if (diff.onlineChangedFriends != null) {
                    users.addAll(diff.onlineChangedFriends);
                }
                UsersStorageFacade.updateFriendsOnline(db, users);
                UsersStorageFacade.updateFriendsLastUpdate(db);
                db.setTransactionSuccessful();
                db.endTransaction();
                context.getContentResolver().notifyChange(OdklProvider.friendsUri(), null);
                Settings.storeStrValue(context, "friends-ids-hash2", diff.friendsIdsHash);
                Settings.storeLongValue(context, "friends-diff-time2", diff.time);
                updateBestFriends(bestFriendsCount);
            }
        }
        hasUserInfoUpdates = false;
        if (TextUtils.isEmpty(friendsIdsHash)) {
            if (TextUtils.equals(diff.friendsIdsHash, friendsIdsHash)) {
                hasFriendIdsUpdates = true;
                if (online == null) {
                }
                SQLiteUtils.beginTransaction(db);
                if (hasUserInfoUpdates) {
                    UsersStorageFacade.updateUsers(db, diff.friends);
                    UsersStorageFacade.rewriteRelatives(db, diff.friends, batchResult.relations);
                }
                if (TextUtils.isEmpty(friendsIdsHash)) {
                    userIds = new ArrayList();
                    if (diff.friends != null) {
                        while (i$.hasNext()) {
                            userIds.add(friend.uid);
                        }
                    }
                    UsersStorageFacade.updateFriends(db, userIds);
                }
                if (hasFriendIdsUpdates) {
                    UsersStorageFacade.updateFriends(db, diff.friendIds);
                }
                users = new HashSet();
                if (online != null) {
                    users.addAll(online);
                }
                if (diff.onlineChangedFriends != null) {
                    users.addAll(diff.onlineChangedFriends);
                }
                UsersStorageFacade.updateFriendsOnline(db, users);
                UsersStorageFacade.updateFriendsLastUpdate(db);
                db.setTransactionSuccessful();
                db.endTransaction();
                context.getContentResolver().notifyChange(OdklProvider.friendsUri(), null);
                Settings.storeStrValue(context, "friends-ids-hash2", diff.friendsIdsHash);
                Settings.storeLongValue(context, "friends-diff-time2", diff.time);
                updateBestFriends(bestFriendsCount);
            }
        }
        hasFriendIdsUpdates = false;
        if (online == null) {
        }
        SQLiteUtils.beginTransaction(db);
        if (hasUserInfoUpdates) {
            UsersStorageFacade.updateUsers(db, diff.friends);
            UsersStorageFacade.rewriteRelatives(db, diff.friends, batchResult.relations);
        }
        if (TextUtils.isEmpty(friendsIdsHash)) {
            userIds = new ArrayList();
            if (diff.friends != null) {
                while (i$.hasNext()) {
                    userIds.add(friend.uid);
                }
            }
            UsersStorageFacade.updateFriends(db, userIds);
        }
        if (hasFriendIdsUpdates) {
            UsersStorageFacade.updateFriends(db, diff.friendIds);
        }
        users = new HashSet();
        if (online != null) {
            users.addAll(online);
        }
        if (diff.onlineChangedFriends != null) {
            users.addAll(diff.onlineChangedFriends);
        }
        UsersStorageFacade.updateFriendsOnline(db, users);
        UsersStorageFacade.updateFriendsLastUpdate(db);
        db.setTransactionSuccessful();
        db.endTransaction();
        context.getContentResolver().notifyChange(OdklProvider.friendsUri(), null);
        Settings.storeStrValue(context, "friends-ids-hash2", diff.friendsIdsHash);
        Settings.storeLongValue(context, "friends-diff-time2", diff.time);
        updateBestFriends(bestFriendsCount);
    }

    private static void updateBestFriends(int count) {
        SQLiteDatabase db;
        if (count > 0) {
            try {
                List<String> uids = UsersUidsParser.parseUids(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BestFriendsRequest(count, FIELDS.UID.getName())));
                db = OdnoklassnikiApplication.getDatabase(OdnoklassnikiApplication.getContext());
                SQLiteUtils.beginTransaction(db);
                DBStatementsFactory.getStatement(db, BestFriends.QUERY_RESET).execute();
                SQLiteStatement statementSet = DBStatementsFactory.getStatement(db, BestFriends.QUERY_SET);
                for (int i = 0; i < uids.size(); i++) {
                    String uid = (String) uids.get(i);
                    statementSet.bindLong(1, (long) i);
                    statementSet.bindString(2, uid);
                    statementSet.execute();
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to update best friends");
            } catch (Throwable th) {
                db.endTransaction();
            }
        }
    }

    public static int getOnlineFriends() throws Exception {
        ArrayList<UserInfo> users = new JsonGetUsersInfoParser(null).parser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new FriendsOnlineRequest(new RequestFieldsBuilder().addField(FIELDS.NAME).addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(DeviceUtils.getUserAvatarPicFieldName()).build())).getResultAsArray());
        Context context = OdnoklassnikiApplication.getContext();
        SQLiteDatabase db = OdnoklassnikiApplication.getDatabase(context);
        db.beginTransaction();
        try {
            UsersStorageFacade.updateFriendsOnline(db, users);
            db.setTransactionSuccessful();
            ContentResolver cr = context.getContentResolver();
            cr.notifyChange(OdklProvider.friendsUri(), null);
            cr.notifyChange(Users.getContentUri(), null);
            return users.size();
        } finally {
            db.endTransaction();
        }
    }

    @Subscribe(on = 2131623944, to = 2131623986)
    public void getOnlineFriends(BusEvent event) {
        try {
            int count = getOnlineFriends();
            Bundle output = new Bundle();
            output.putInt("COUNT", count);
            GlobalBus.send(2131624165, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to fetch online friends");
            GlobalBus.send(2131624165, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    public static List<UserInfo> getFriendsSuggestions(int count, RequestFieldsBuilder builder) throws BaseApiException {
        SuggestionsRequest suggestionsRequest = new SuggestionsRequest(null, null, null, count * 2);
        ArrayList<UserInfo> suggestionUsers = new JsonGetSuggestionsBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(new BatchRequests().addRequest(suggestionsRequest).addRequest(new UserInfoRequest(new RequestJSONParam(new SupplierRequest(suggestionsRequest.getUserIdsSupplier())), builder.build(), false))))).parse();
        if (suggestionUsers.size() > 1) {
            Iterator<UserInfo> it = suggestionUsers.iterator();
            while (it.hasNext()) {
                if (URLUtil.isStubUrl(((UserInfo) it.next()).picUrl)) {
                    it.remove();
                }
                if (suggestionUsers.size() <= 1) {
                    break;
                }
            }
        }
        return suggestionUsers;
    }

    public static RequestFieldsBuilder getDefaultFriendsSuggestionsFields() {
        RequestFieldsBuilder builder = new RequestFieldsBuilder();
        builder.addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(DeviceUtils.getUserAvatarPicFieldName());
        return builder;
    }

    public static ArrayList<UserInfo> requestUsersInfos(List<String> uids, UserInfoValuesFiller filler) throws BaseApiException {
        return requestUsersInfos(uids, filler, true);
    }

    public static ArrayList<UserInfo> requestUsersInfos(List<String> uids, UserInfoValuesFiller filler, boolean setOnline) throws BaseApiException {
        String fields = filler.getRequestFields();
        if (uids.size() <= 100) {
            return requestUsers(uids, fields, setOnline);
        }
        ArrayList<UserInfo> users = requestUsers(uids.subList(0, 100), fields, setOnline);
        users.addAll(requestUsersInfos(uids.subList(100, uids.size()), filler, setOnline));
        return users;
    }

    private static ArrayList<UserInfo> requestUsers(List<String> uids, String fields, boolean setOnline) throws BaseApiException {
        if (uids == null || uids.isEmpty()) {
            return new ArrayList();
        }
        return new JsonGetUsersInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInfoRequest(new RequestCollectionParam(uids), fields, false, setOnline))).parse();
    }
}

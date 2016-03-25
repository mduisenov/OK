package ru.ok.android.services.processors.groups;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.GroupsStorageFacade;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.access.fillers.GroupInfoValueFiller;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.services.processors.base.BaseProcessorResult;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.storage.Storages;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.groups.GroupMembersParser;
import ru.ok.java.api.json.groups.JsonGroupInfoBatchParser;
import ru.ok.java.api.json.groups.JsonGroupInfoParser;
import ru.ok.java.api.json.groups.JsonGroupsInfoParser;
import ru.ok.java.api.json.groups.JsonGroupsTopCategoriesBatchParser;
import ru.ok.java.api.json.users.ComplaintType;
import ru.ok.java.api.json.users.JsonArrayUsersInfoParse;
import ru.ok.java.api.json.users.JsonUserInfoParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.TranslationsRequest;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.groups.CommunitiesGetMembersRequest;
import ru.ok.java.api.request.groups.CommunitiesJoinRequest;
import ru.ok.java.api.request.groups.ComplaintToGroupRequest;
import ru.ok.java.api.request.groups.GetCategoriesGroupsRequest;
import ru.ok.java.api.request.groups.GetGroupsUserStatusRequest;
import ru.ok.java.api.request.groups.GroupCountersRequest;
import ru.ok.java.api.request.groups.GroupCreateRequest;
import ru.ok.java.api.request.groups.GroupCreateType;
import ru.ok.java.api.request.groups.GroupFriendMembersRequest;
import ru.ok.java.api.request.groups.GroupGetMembersRequest;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.groups.GroupInviteRequest;
import ru.ok.java.api.request.groups.GroupJoinRequest;
import ru.ok.java.api.request.groups.GroupLeaveRequest;
import ru.ok.java.api.request.groups.GroupsTopCategoriesRequest;
import ru.ok.java.api.request.groups.UserGroupsInfoRequest;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.request.param.RequestCollectionParam;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.stream.StreamGroupSubscribeRequest;
import ru.ok.java.api.request.stream.StreamIsSubscribeGroupRequest;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.groups.GroupInfosBatchResponse;
import ru.ok.java.api.response.groups.GroupsTopCategoriesBatchResponse;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupUserStatus;
import ru.ok.model.GroupUserStatusInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.groups.GroupsTopCategoryItem;

public final class GroupsProcessor {

    public static class CategoriesGroupsProcessorResult extends BaseProcessorResult {
        public final LinkedHashMap<String, CategoryGroupPage> categories;

        private CategoriesGroupsProcessorResult(boolean isSuccess, ErrorType errorType, LinkedHashMap<String, CategoryGroupPage> categories) {
            super(isSuccess, errorType);
            this.categories = categories;
        }
    }

    public static class CategoryGroupPage {
        public final String anchor;
        public final List<GroupInfo> groups;
        public final List<GroupAdditionalInfo> groupsAdditionalInfos;
        public final boolean hasMore;
        public final String id;

        public CategoryGroupPage(String id, List<GroupInfo> groups, List<GroupAdditionalInfo> groupsAdditionalInfos, String anchor, boolean hasMore) {
            this.id = id;
            this.groups = groups;
            this.groupsAdditionalInfos = groupsAdditionalInfos;
            this.anchor = anchor;
            this.hasMore = hasMore;
        }
    }

    public static class GroupAdditionalInfo {
        public final List<UserInfo> friendMembers;
        public final long friendMembersCount;

        public GroupAdditionalInfo(long friendMembersCount, List<UserInfo> friendMembers) {
            this.friendMembersCount = friendMembersCount;
            this.friendMembers = friendMembers;
        }
    }

    public static class GroupCreateProcessorResult extends BaseProcessorResult {
        public final String groupId;

        public GroupCreateProcessorResult(boolean isSuccess, ErrorType errorType, String groupId) {
            super(isSuccess, errorType);
            this.groupId = groupId;
        }
    }

    public static class GroupTopCategoriesProcessorResult extends BaseProcessorResult {
        public final String anchor;
        public final List<GroupsTopCategoryItem> groupCategoryItems;
        public final boolean hasMore;

        public GroupTopCategoriesProcessorResult(boolean isSuccess, ErrorType errorType) {
            this(isSuccess, errorType, null, null, false);
        }

        public GroupTopCategoriesProcessorResult(boolean isSuccess, ErrorType errorType, List<GroupsTopCategoryItem> groupCategoryItems, String anchor, boolean hasMore) {
            super(isSuccess, errorType);
            this.anchor = anchor;
            this.hasMore = hasMore;
            this.groupCategoryItems = groupCategoryItems;
        }
    }

    public static class UserGroupsInfoProcessorResult extends BaseProcessorResult {
        public final String anchor;
        public final List<GroupInfo> groupsInfos;
        public final boolean hasMore;

        public UserGroupsInfoProcessorResult(boolean isSuccess, ErrorType errorType, List<GroupInfo> groupsInfos, String anchor, boolean hasMore) {
            super(isSuccess, errorType);
            this.groupsInfos = groupsInfos;
            this.anchor = anchor;
            this.hasMore = hasMore;
        }
    }

    @Subscribe(on = 2131623944, to = 2131623994)
    public void getGroupInfo(BusEvent event) {
        try {
            String groupId = event.bundleInput.getString("GROUP_ID");
            GroupInfosBatchResponse response = processGroupBatchResponse(groupId, JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(fillGroupInfoRequests(new BatchRequests(), groupId))));
            Bundle data = new Bundle();
            if (response.groups != null) {
                if (response.groups.size() > 0) {
                    GroupUserStatus currentUserGroupStatus;
                    boolean isCanPinTopic;
                    GroupInfo groupInfo = (GroupInfo) response.groups.get(0);
                    data.putBoolean("GROUP_RESULT_INFO_PRIVATE", groupInfo.isPrivateGroup());
                    data.putBoolean("CAN_POST_MEDIATOPIC", groupInfo.isCanPostMediaTopic());
                    data.putBoolean("CAN_SUGGEST_MEDIATOPIC", groupInfo.isCanSuggestMediaTopic());
                    data.putString("GROUP_RESULT_INFO_NAME", groupInfo.getName());
                    if (response.statusUsersInfoList != null) {
                        if (response.statusUsersInfoList.size() > 0) {
                            currentUserGroupStatus = ((GroupUserStatusInfo) response.statusUsersInfoList.get(0)).status;
                            isCanPinTopic = currentUserGroupStatus != GroupUserStatus.ADMIN || currentUserGroupStatus == GroupUserStatus.MODERATOR;
                            data.putBoolean("CAN_PIN_MEDIATOPIC", isCanPinTopic);
                        }
                    }
                    currentUserGroupStatus = null;
                    if (currentUserGroupStatus != GroupUserStatus.ADMIN) {
                    }
                    data.putBoolean("CAN_PIN_MEDIATOPIC", isCanPinTopic);
                }
            }
            GroupUserStatus status = GroupUserStatus.UNKNOWN;
            if (response.statusUsersInfoList != null) {
                if (response.statusUsersInfoList.size() > 0) {
                    for (GroupUserStatusInfo groupUserStatus : response.statusUsersInfoList) {
                        status = groupUserStatus.status;
                    }
                }
            }
            data.putSerializable("GROUP_RESULT_INFO_STATUS", status);
            GlobalBus.send(2131624171, new BusEvent(event.bundleInput, data, -1));
        } catch (Exception ex) {
            grayLogLog("GroupsProcessor.getGroupInfo error", ex);
            GlobalBus.send(2131624171, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    protected GroupInfosBatchResponse processGroupBatchResponse(String groupId, JsonHttpResult resultBatch) throws Exception {
        GroupInfosBatchResponse responseBatch = new JsonGroupInfoBatchParser(resultBatch.getResultAsObject()).parse();
        if (responseBatch.groups != null) {
            GroupsStorageFacade.insertGroups(responseBatch.groups, GroupInfoValueFiller.ALL);
        }
        if (responseBatch.counters != null) {
            GroupsStorageFacade.updateGroupCounters(groupId, responseBatch.counters);
        }
        GroupsStorageFacade.syncSubscribeGroupStreamRelations(responseBatch.getArraySubscribe());
        if (responseBatch.statusUsersInfoList != null) {
            GroupsStorageFacade.insertGroupUsersStatus(responseBatch.statusUsersInfoList);
        }
        if (responseBatch.admin != null) {
            UsersStorageFacade.insertUsers(Arrays.asList(new UserInfo[]{responseBatch.admin}), UserInfoValuesFiller.NAMES);
        }
        GroupsStorageFacade.updateGroupFriendsMembers(groupId, responseBatch.friendIds);
        return responseBatch;
    }

    private static BatchRequests fillGroupInfoRequests(BatchRequests requests, String groupId) {
        String fields = GroupInfoValueFiller.ALL.getRequestFields();
        String adminUserFields = new RequestFieldsBuilder().addFields(FIELDS.FIRST_NAME, FIELDS.LAST_NAME, FIELDS.NAME).build();
        GroupInfoRequest groupInfoRequest = new GroupInfoRequest(Arrays.asList(new String[]{groupId}), fields);
        BaseRequest groupCounters = new GroupCountersRequest(groupId);
        BaseRequest groupUserStatus = new GetGroupsUserStatusRequest(new BaseStringParam(TextUtils.join(",", Arrays.asList(new String[]{OdnoklassnikiApplication.getCurrentUser().uid}))), groupId);
        BaseRequest groupIsSubscribe = new StreamIsSubscribeGroupRequest(groupId);
        BaseRequest adminRequest = new UserInfoRequest(new RequestJSONParam(new SupplierRequest(groupInfoRequest.getUserIdsSupplier())), adminUserFields, false);
        BaseRequest translationsCategoryRequest = new TranslationsRequest("altgroup.category", null, new RequestJSONParam(new SupplierRequest(groupInfoRequest.getScopeSupplier())));
        BaseRequest translationsSubCategoryRequest = new TranslationsRequest("altgroup.subcategory", null, new RequestJSONParam(new SupplierRequest(groupInfoRequest.getSubcategorySupplier())));
        return requests.addRequest(groupInfoRequest).addRequest(groupCounters, true).addRequest(groupUserStatus, true).addRequest(groupIsSubscribe, true).addRequest(adminRequest, true).addRequest(translationsCategoryRequest, false).addRequest(translationsSubCategoryRequest, false).addRequest(new GroupFriendMembersRequest(groupId), true);
    }

    @Subscribe(on = 2131623944, to = 2131623995)
    public void groupInviteFriends(BusEvent event) {
        try {
            JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupInviteRequest(event.bundleInput.getString("GROUP_ID"), event.bundleInput.getStringArrayList("GROUP_FRIENDS_IDS")));
            GlobalBus.send(2131624172, new BusEvent(event.bundleInput, -1));
        } catch (Exception e) {
            grayLogLog("GroupsProcessor.groupInviteFriends error", e);
            Bundle outBundle = new Bundle();
            CommandProcessor.fillErrorBundle(outBundle, e);
            GlobalBus.send(2131624172, new BusEvent(event.bundleInput, outBundle, -2));
        }
    }

    private void requestGroupInfoForUpdate(String groupId) throws Exception {
        processGroupBatchResponse(groupId, JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(fillGroupInfoRequests(new BatchRequests(), groupId))));
    }

    @Subscribe(on = 2131623944, to = 2131623996)
    public void groupJoin(BusEvent event) {
        String groupId = event.bundleInput.getString("GROUP_ID");
        try {
            boolean invite = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupJoinRequest(groupId, event.bundleInput.getBoolean("GROUP_MAYBE", false))).getResultAsObject().getBoolean("success");
            if (invite) {
                updateSubscriptionCache(groupId, true);
            }
            requestGroupInfoForUpdate(groupId);
            Bundle outBundle = new Bundle();
            outBundle.putBoolean("GROUP_INVITE_RESULT_VALUE", invite);
            GlobalBus.send(2131624173, new BusEvent(event.bundleInput, outBundle, -1));
        } catch (Exception ex) {
            grayLogLog("GroupsProcessor.groupJoin error", ex);
            GlobalBus.send(2131624173, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623997)
    public void groupLeave(BusEvent event) {
        String groupId = event.bundleInput.getString("GROUP_ID");
        try {
            boolean leave = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupLeaveRequest(groupId)).getResultAsObject().getBoolean("success");
            if (leave) {
                updateSubscriptionCache(groupId, false);
            }
            requestGroupInfoForUpdate(groupId);
            Bundle outBundle = new Bundle();
            outBundle.putBoolean("GROUP_LEAVE_RESULT_VALUE", leave);
            GlobalBus.send(2131624174, new BusEvent(event.bundleInput, outBundle, -1));
        } catch (Exception ex) {
            grayLogLog("GroupsProcessor.groupLeave error", ex);
            GlobalBus.send(2131624174, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    private static void updateSubscriptionCache(String groupId, boolean isSubscribed) {
        Context context = OdnoklassnikiApplication.getContext();
        if (context != null) {
            String cuid = OdnoklassnikiApplication.getCurrentUser().getId();
            if (!TextUtils.isEmpty(cuid)) {
                Storages.getInstance(context, cuid).getStreamSubscriptionManager().setSubscribedGroup(groupId, isSubscribed);
            }
        }
    }

    @Subscribe(on = 2131623944, to = 2131624105)
    public void subscribeToStream(BusEvent event) {
        String gId = event.bundleInput.getString("GROUP_ID");
        try {
            ArrayList<String> groups = new ArrayList();
            groups.add(gId);
            boolean resultValue = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new StreamGroupSubscribeRequest(groups)).getResultAsObject().getBoolean("success");
            if (resultValue) {
                ArrayList<Pair<String, Boolean>> values = new ArrayList();
                values.add(new Pair(gId, Boolean.valueOf(true)));
                GroupsStorageFacade.syncSubscribeGroupStreamRelations(values);
                updateSubscriptionCache(gId, true);
            }
            Bundle outBundle = new Bundle();
            outBundle.putBoolean("GROUP_SUBSCRIBE_RESULT_VALUE", resultValue);
            GlobalBus.send(2131624247, new BusEvent(event.bundleInput, outBundle, -1));
        } catch (Exception ex) {
            GlobalBus.send(2131624247, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623956)
    public void complaintToGroup(BusEvent event) {
        try {
            boolean resultValue = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new ComplaintToGroupRequest(event.bundleInput.getString("GROUP_ID"), (ComplaintType) event.bundleInput.getSerializable("GROUP_COMPLAINT_TYPE"))).getResultAsObject().getBoolean("success");
            Bundle outBundle = new Bundle();
            outBundle.putBoolean("GROUP_COMPLAINT_RESULT_VALUE", resultValue);
            GlobalBus.send(2131624136, new BusEvent(event.bundleInput, outBundle, -1));
        } catch (Exception ex) {
            GlobalBus.send(2131624136, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623992)
    public void friendsInGroup(BusEvent event) {
        String groupId = event.bundleInput.getString("GROUP_ID");
        boolean fetchUserInfos = event.bundleInput.getBoolean("FETCH_USER_INFOS", false);
        boolean fetchGroupInfo = event.bundleInput.getBoolean("FETCH_USER_INFOS", false);
        try {
            GroupFriendMembersRequest request = new GroupFriendMembersRequest(groupId);
            ArrayList<String> result = new GroupMembersParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject()).parse();
            GroupsStorageFacade.updateGroupFriendsMembers(groupId, result);
            ArrayList<UserInfo> userInfos = null;
            if (fetchUserInfos) {
                userInfos = GetFriendsProcessor.requestUsersInfos(result, UserInfoValuesFiller.FRIENDS);
            }
            GroupInfo groupInfo = null;
            if (fetchGroupInfo) {
                String groupFields = new RequestFieldsBuilder().addFields(GroupInfoRequest.FIELDS.GROUP_MEMBERS_COUNT, GroupInfoRequest.FIELDS.GROUP_DESCRIPTION).build();
                ArrayList<GroupInfo> infos = new JsonGroupsInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupInfoRequest(Arrays.asList(new String[]{groupId}), groupFields)).getResultAsArray()).parse();
                if (!infos.isEmpty()) {
                    groupInfo = (GroupInfo) infos.get(0);
                }
            }
            Bundle output = new Bundle();
            output.putStringArrayList("GROUP_FRIENDS_IDS", result);
            output.putParcelable("GROUP_INFO", groupInfo);
            output.putParcelableArrayList("USER_INFOS", userInfos);
            GlobalBus.send(2131624169, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception ex) {
            grayLogLog("GroupsProcessor.friendsInGroup error", ex);
            Bundle errorBundle = CommandProcessor.createErrorBundle(ex);
            GlobalBus.send(2131624169, new BusEvent(event.bundleInput, errorBundle, -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623993)
    public void getGroupMembers(BusEvent event) {
        Bundle output = getGroupMembers(event.bundleInput.getString("GROUP_ID"), event.bundleInput.getString("anchor"), event.bundleInput.getString("direction"));
        if (output == null) {
            GlobalBus.send(2131624170, new BusEvent(event.bundleInput, new Bundle(), -2));
        } else {
            GlobalBus.send(2131624170, new BusEvent(event.bundleInput, output, -1));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623990)
    public void joinCommunity(BusEvent event) {
        Throwable e;
        try {
            int i;
            JSONObject jsonObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new CommunitiesJoinRequest(event.bundleInput.getString("GROUP_ID"), event.bundleInput.getInt("COMMUNITY_START_YEAR"), event.bundleInput.getInt("COMMUNITY_NED_YEAR"), null)).getResultAsObject();
            boolean requestAccepted = jsonObject.has("success") && jsonObject.optBoolean("success");
            Bundle bundle = new Bundle();
            if (requestAccepted) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624167, new BusEvent(bundle, i));
        } catch (JSONException e2) {
            e = e2;
            Logger.m178e(e);
            GlobalBus.send(2131624167, new BusEvent(new Bundle(), -2));
        } catch (BaseApiException e3) {
            e = e3;
            Logger.m178e(e);
            GlobalBus.send(2131624167, new BusEvent(new Bundle(), -2));
        }
    }

    public static Bundle getGroupMembers(String groupId, String anchor, String direction) {
        Throwable e;
        ArrayList<UserInfo> userInfos = new ArrayList();
        Bundle result = new Bundle();
        try {
            JSONObject membersIdsResultJson = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupGetMembersRequest(groupId, null, anchor, direction, 30)).getResultAsObject();
            String responseAnchor = JsonUtil.getStringSafely(membersIdsResultJson, "anchor");
            ArrayList<String> uids = new GroupMembersParser(membersIdsResultJson).parseIdsFromGetMembersResponse();
            JSONArray usersJsonArray = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserInfoRequest(new RequestCollectionParam(uids), getUserInfoDefaultFields().build(), false)).getResultAsArray();
            for (int i = 0; i < usersJsonArray.length(); i++) {
                userInfos.add(new JsonUserInfoParser(usersJsonArray.getJSONObject(i)).parse());
            }
            result.putParcelableArrayList("USERS", userInfos);
            result.putString("anchor", responseAnchor);
            return result;
        } catch (BaseApiException e2) {
            e = e2;
            grayLogLog("GroupsProcessor.getGroupMembers error", e);
            Logger.m178e(e);
            return CommandProcessor.createErrorBundle(e);
        } catch (JSONException e3) {
            e = e3;
            grayLogLog("GroupsProcessor.getGroupMembers error", e);
            Logger.m178e(e);
            return CommandProcessor.createErrorBundle(e);
        }
    }

    @Subscribe(on = 2131623944, to = 2131623955)
    public void getCommunityMembers(BusEvent event) {
        Bundle bundle = event.bundleInput;
        Bundle output = getCommunityMembers(bundle.getString("GROUP_ID"), bundle.getInt("start_year"), bundle.getInt("end_year"), bundle.getString("anchor"), bundle.getString("direction"), bundle.getInt("total_count", 0));
        GlobalBus.send(2131624135, new BusEvent(event.bundleInput, output, output.containsKey("ERROR_TYPE") ? -2 : -1));
    }

    public static RequestFieldsBuilder getUserInfoDefaultFields() {
        return new RequestFieldsBuilder().addField(FIELDS.FIRST_NAME).addField(FIELDS.LAST_NAME).addField(FIELDS.NAME).addField(FIELDS.GENDER).addField(FIELDS.AGE).addField(FIELDS.LOCATION).addField(FIELDS.ONLINE).addField(FIELDS.LAST_ONLINE).addField(DeviceUtils.getUserAvatarPicFieldName());
    }

    public static Bundle getCommunityMembers(String groupId, int startYear, int endYear, String anchor, String direction, int count) {
        Throwable e;
        ArrayList<UserInfo> userInfos = new ArrayList();
        try {
            JSONObject resultJson = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new CommunitiesGetMembersRequest(groupId, startYear, endYear, anchor, direction, count, getUserInfoDefaultFields().build(), null)).getResultAsObject();
            JSONArray usersJson = resultJson.getJSONArray("users");
            for (int i = 0; i < usersJson.length(); i++) {
                userInfos.add(new JsonUserInfoParser(usersJson.getJSONObject(i)).parse());
            }
            Bundle result = new Bundle();
            result.putParcelableArrayList("USERS", userInfos);
            result.putString("anchor", resultJson.getString("anchor"));
            result.putInt("total_count", resultJson.getInt("totalCount"));
            result.putBoolean("has_more", resultJson.getBoolean("has_more"));
            return result;
        } catch (BaseApiException e2) {
            e = e2;
            Logger.m178e(e);
            return CommandProcessor.createErrorBundle(e);
        } catch (JSONException e3) {
            e = e3;
            Logger.m178e(e);
            return CommandProcessor.createErrorBundle(e);
        }
    }

    public static GroupTopCategoriesProcessorResult getGroupsTopCategories(String anchor, String direction, int count) {
        try {
            BatchRequests requests = new BatchRequests();
            GroupsTopCategoriesRequest topCategoriesRequest = new GroupsTopCategoriesRequest(anchor, direction, count);
            BaseRequest translationCategoryRequest = new TranslationsRequest("altgroup.category", null, new RequestJSONParam(new SupplierRequest(topCategoriesRequest.getCategoryKeysSupplier())));
            requests.addRequest(topCategoriesRequest);
            requests.addRequest(translationCategoryRequest, false);
            GroupsTopCategoriesBatchResponse resp = new JsonGroupsTopCategoriesBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new BatchRequest(requests)).getResultAsObject()).parse();
            return new GroupTopCategoriesProcessorResult(true, null, resp.categories, resp.anchor, resp.hasMore);
        } catch (Exception e) {
            grayLogLog("GroupsProcessor.getGroupsTopCategories error", e);
            return new GroupTopCategoriesProcessorResult(false, ErrorType.fromException(e));
        }
    }

    public static UserGroupsInfoProcessorResult getUserGroupsInfo(String uid, String anchor, String direction, int count) {
        try {
            JSONObject resultObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UserGroupsInfoRequest(uid, anchor, direction, 100)).getResultAsObject();
            List<GroupInfo> groupInfos = null;
            JSONArray array = resultObject.optJSONArray("userGroups");
            if (array != null) {
                groupInfos = new JsonGroupsInfoParser(array, null).parse();
            }
            if (groupInfos != null && groupInfos.size() > 0) {
                GroupsStorageFacade.insertGroups(groupInfos, GroupInfoValueFiller.ALL);
            }
            return new UserGroupsInfoProcessorResult(true, null, groupInfos, JsonUtil.optStringOrNull(resultObject, "anchor"), JsonUtil.optBooleanOrFalse(resultObject, "has_more"));
        } catch (Exception e) {
            grayLogLog("GroupsProcessor.getUserGroupsInfo", e);
            return new UserGroupsInfoProcessorResult(false, ErrorType.fromException(e), null, null, false);
        }
    }

    public static CategoriesGroupsProcessorResult getCategoriesGroups(String categoryId, String anchor, String direction, int count, int friendMembersLimit) {
        try {
            JSONArray categoriesArray = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetCategoriesGroupsRequest(categoryId == null ? null : Collections.singletonList(categoryId), anchor, direction, count, friendMembersLimit)).getResultAsObject().optJSONArray("categories");
            LinkedHashMap<String, CategoryGroupPage> categories = null;
            if (categoriesArray != null) {
                categories = new LinkedHashMap();
                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryJsonObject = categoriesArray.getJSONObject(i);
                    String id = categoryJsonObject.getString("id");
                    List<GroupInfo> groups = null;
                    List<GroupAdditionalInfo> groupsAdditionalInfos = null;
                    JSONArray groupsArray = categoryJsonObject.getJSONArray("groups");
                    if (groupsArray != null) {
                        int size = groupsArray.length();
                        groups = new ArrayList(size);
                        groupsAdditionalInfos = new ArrayList(size);
                        for (int j = 0; j < size; j++) {
                            JSONObject groupObject = groupsArray.getJSONObject(j);
                            groups.add(JsonGroupInfoParser.parse(groupObject, null));
                            long friendsMembersCount = groupObject.optLong("friends_members_count", 0);
                            List parse = (friendsMembersCount == 0 || !groupObject.has("friends_members")) ? null : new JsonArrayUsersInfoParse(groupObject.getJSONArray("friends_members")).parse();
                            groupsAdditionalInfos.add(new GroupAdditionalInfo(friendsMembersCount, parse));
                        }
                    }
                    categories.put(id, new CategoryGroupPage(id, groups, groupsAdditionalInfos, JsonUtil.optStringOrNull(categoryJsonObject, "anchor"), JsonUtil.optBooleanOrFalse(categoryJsonObject, "has_more")));
                }
            }
            return new CategoriesGroupsProcessorResult(null, categories, null);
        } catch (Exception e) {
            grayLogLog("GroupsProcessor.getCategoriesGroups error", e);
            return new CategoriesGroupsProcessorResult(ErrorType.fromException(e), null, null);
        }
    }

    @Subscribe(on = 2131623944, to = 2131623991)
    public void groupCreate(BusEvent event) {
        GroupCreateProcessorResult result = groupCreate(GroupCreateType.valueOf(event.bundleInput.getString("GROUP_TYPE")), event.bundleInput.getString("GROUP_NAME"), event.bundleInput.getString("GROUP_DESCRIPTION"), event.bundleInput.getBoolean("GROUP_OPEN"));
        if (result.isSuccess) {
            Bundle output = new Bundle();
            output.putString("GROUP_ID", result.groupId);
            GlobalBus.send(2131624168, new BusEvent(event.bundleInput, output, -1));
            return;
        }
        Bundle errorBundle = new Bundle();
        errorBundle.putString("ERROR_TYPE", result.errorType.name());
        GlobalBus.send(2131624168, new BusEvent(event.bundleInput, errorBundle, -2));
    }

    public GroupCreateProcessorResult groupCreate(GroupCreateType type, String name, String description, boolean isOpen) {
        try {
            return new GroupCreateProcessorResult(true, null, JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GroupCreateRequest(type, name, description, isOpen)).getResultAsObject().getString("group_id"));
        } catch (Exception e) {
            grayLogLog("GroupsProcessor.groupCreate error", e);
            return new GroupCreateProcessorResult(false, ErrorType.fromException(e), null);
        }
    }

    private static void grayLogLog(String message, Exception e) {
        if (!(e instanceof NoConnectionException) && !(e instanceof SocketException)) {
            GrayLog.log(message, e);
        }
    }
}

package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.TwoSourcesDataLoader;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.QueriesUsers.FriendsList;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter.RelationItem;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Builder;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public final class FriendsLoader extends TwoSourcesDataLoader<FriendsLoaderBundle> {
    private static final Comparator<? super FriendWithIndex> BEST_FRIENDS_COMPARATOR;

    /* renamed from: ru.ok.android.ui.users.fragments.data.FriendsLoader.1 */
    static class C13151 implements Comparator<FriendWithIndex> {
        C13151() {
        }

        public int compare(FriendWithIndex a, FriendWithIndex b) {
            if (a.index > b.index) {
                return 1;
            }
            if (a.index < b.index) {
                return -1;
            }
            return 0;
        }
    }

    static final class FriendWithIndex {
        final int index;
        final UserInfo user;

        FriendWithIndex(UserInfo user, int index) {
            this.user = user;
            this.index = index;
        }
    }

    static {
        BEST_FRIENDS_COMPARATOR = new C13151();
    }

    public FriendsLoader(Context context, boolean performWebLoading) {
        super(context, performWebLoading);
    }

    protected FriendsLoaderBundle doLoadDatabase() {
        Cursor c = OdnoklassnikiApplication.getDatabase(getContext()).rawQuery(FriendsList.QUERY, null);
        List<UserInfoExtended> friends = new ArrayList();
        List<FriendWithIndex> bestFriendsPairs = new ArrayList();
        Map<RelativesType, Set<String>> relationsMap = new HashMap();
        Map<String, Set<RelativesType>> subRelationsMap = new HashMap();
        try {
            Builder builder = new Builder();
            Set<String> loadedUIDs = new HashSet();
            while (c.moveToNext()) {
                String uid = c.getString(0);
                if (!loadedUIDs.contains(uid)) {
                    loadedUIDs.add(uid);
                    String name = c.getString(1);
                    String firstName = c.getString(2);
                    String lastName = c.getString(3);
                    String urlPic = c.getString(4);
                    int gender = c.getInt(5);
                    String online = c.getString(6);
                    long lastOnline = c.getLong(7);
                    boolean canCall = c.getInt(8) > 0;
                    boolean canVMail = c.getInt(9) > 0;
                    boolean isPrivate = c.getInt(10) > 0;
                    boolean showLock = c.getInt(11) > 0;
                    boolean isBestFriend = c.getInt(12) > 0;
                    int bestFriendIndex = c.getInt(13);
                    builder.setUid(uid).setName(name).setFirstName(firstName).setLastName(lastName).setPicUrl(urlPic).setGenderType(UserGenderType.byInteger(gender)).setOnline(UserOnlineType.safeValueOf(online)).setLastOnline(lastOnline).setCanCall(canCall).setCanVMail(canVMail).setIsPrivate(isPrivate).setShowLock(showLock);
                    UserInfo user = builder.build();
                    friends.add(new UserInfoExtended(user));
                    if (isBestFriend) {
                        bestFriendsPairs.add(new FriendWithIndex(user, bestFriendIndex));
                    }
                }
                String relationTypeStr = c.getString(14);
                String relationSubTypeStr = c.getString(15);
                if (!TextUtils.isEmpty(relationTypeStr)) {
                    RelativesType relationType = RelativesType.safeValueOf(relationTypeStr);
                    if (relationType != null) {
                        RelativesType relativeSubtype = null;
                        if (!TextUtils.isEmpty(relationSubTypeStr)) {
                            relativeSubtype = RelativesType.valueOf(relationSubTypeStr);
                        }
                        if (relationType == RelativesType.SPOUSE) {
                            relativeSubtype = RelativesType.SPOUSE;
                            relationType = RelativesType.RELATIVE;
                        }
                        Set<String> uids = (Set) relationsMap.get(relationType);
                        if (uids == null) {
                            uids = new HashSet();
                            relationsMap.put(relationType, uids);
                        }
                        uids.add(uid);
                        if (relativeSubtype != null) {
                            Set<RelativesType> subTypes = (Set) subRelationsMap.get(uid);
                            if (subTypes == null) {
                                subTypes = new HashSet();
                                subRelationsMap.put(uid, subTypes);
                            }
                            subTypes.add(relativeSubtype);
                        }
                    }
                }
            }
            Collections.sort(friends, UserInfoExtended.COMPARATOR);
            Collections.sort(bestFriendsPairs, BEST_FRIENDS_COMPARATOR);
            List<UserInfo> bestFriends = new ArrayList();
            for (FriendWithIndex bestFriend : bestFriendsPairs) {
                bestFriends.add(bestFriend.user);
            }
            List<RelationItem> relations = new ArrayList();
            relations.add(new RelationItem(RelativesType.ALL, friends.size()));
            Iterator<Entry<RelativesType, Set<String>>> it = relationsMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<RelativesType, Set<String>> entry = (Entry) it.next();
                if (RelativeUtils.RELATIONS_ORDER.containsKey(entry.getKey())) {
                    relations.add(new RelationItem((RelativesType) entry.getKey(), ((Set) entry.getValue()).size()));
                } else {
                    it.remove();
                }
            }
            Collections.sort(relations, RelativeUtils.RELATIONS_COMPARATOR);
            return new FriendsLoaderBundle(new FriendsAdapterBundle(friends, relationsMap), subRelationsMap, relations, bestFriends);
        } finally {
            c.close();
        }
    }

    protected void doLoadWeb() throws Exception {
        GetFriendsProcessor.loadFriends(getContext().getResources().getInteger(2131427336));
        GlobalBus.send(2131624163, new BusEvent());
    }
}

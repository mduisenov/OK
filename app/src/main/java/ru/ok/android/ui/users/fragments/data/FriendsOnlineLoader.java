package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.TwoSourcesDataLoader;
import android.text.TextUtils;
import io.github.eterverda.sntp.SNTP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.QueriesUsers.OnlineList;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo.Builder;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public final class FriendsOnlineLoader extends TwoSourcesDataLoader<FriendsAdapterBundle> {
    public FriendsOnlineLoader(Context context, boolean performWebLoading) {
        super(context, performWebLoading);
    }

    protected void doLoadWeb() throws Exception {
        GetFriendsProcessor.getOnlineFriends();
        GlobalBus.send(2131624162, new BusEvent());
    }

    protected FriendsAdapterBundle doLoadDatabase() {
        Cursor c = OdnoklassnikiApplication.getDatabase(getContext()).rawQuery(OnlineList.QUERY, new String[]{String.valueOf(SNTP.safeCurrentTimeMillisFromCache())});
        List<UserInfoExtended> friends = new ArrayList();
        Map<RelativesType, Set<String>> relationsMap = new HashMap();
        Builder builder = new Builder();
        Set<String> loadedUIDs = new HashSet();
        while (c.moveToNext()) {
            try {
                String uid = c.getString(0);
                if (!loadedUIDs.contains(uid)) {
                    loadedUIDs.add(uid);
                    String name = c.getString(1);
                    String firstName = c.getString(2);
                    String lastName = c.getString(3);
                    String urlPic = c.getString(4);
                    long lastOnline = c.getLong(5);
                    boolean showLock = c.getLong(8) > 0;
                    UserOnlineType online = UserOnlineType.safeValueOf(c.getString(6));
                    builder.setUid(uid).setName(name).setFirstName(firstName).setLastName(lastName).setPicUrl(urlPic).setLastOnline(lastOnline).setShowLock(showLock).setOnline(online).setGenderType(UserGenderType.byInteger(c.getInt(7)));
                    friends.add(new UserInfoExtended(builder.build()));
                }
                String relationTypeString = c.getString(9);
                if (!TextUtils.isEmpty(relationTypeString)) {
                    RelativesType relationType = RelativesType.safeValueOf(relationTypeString);
                    if (relationType != null) {
                        if (relationType == RelativesType.SPOUSE) {
                            relationType = RelativesType.RELATIVE;
                        }
                        Set<String> uids = (Set) relationsMap.get(relationType);
                        if (uids == null) {
                            uids = new HashSet();
                            relationsMap.put(relationType, uids);
                        }
                        uids.add(uid);
                    }
                }
            } finally {
                c.close();
            }
        }
        return new FriendsAdapterBundle(friends, relationsMap);
    }
}

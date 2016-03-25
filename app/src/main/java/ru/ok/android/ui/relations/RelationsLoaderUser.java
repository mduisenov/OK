package ru.ok.android.ui.relations;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.services.processors.friends.GetRelativesProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.adapters.friends.FriendsContainer;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.JsonGetFriendsParser;
import ru.ok.java.api.request.friends.GetFriendsRequest;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.Relative;
import ru.ok.model.UserInfo;

public class RelationsLoaderUser extends AsyncTaskLoader<FriendsContainer> implements Comparator<UserInfo> {
    private final String mFid;

    public RelationsLoaderUser(Context context, String fid) {
        super(context);
        this.mFid = fid;
    }

    public FriendsContainer loadInBackground() {
        try {
            List<String> idsList = getFriendsValue(this.mFid);
            if (idsList.isEmpty()) {
                return new FriendsContainer(null, this.mFid, null, null, null, null);
            }
            List<UserInfo> userInfo = GetFriendsProcessor.requestUsersInfos(idsList, UserInfoValuesFiller.FRIENDS);
            Collections.sort(userInfo, this);
            List<Relative> relativeList = GetRelativesProcessor.getRelativesAll(idsList, this.mFid);
            Map<RelativesType, Set<String>> setMap = new HashMap();
            Map<String, Set<RelativesType>> subMap = new HashMap();
            for (Relative relative : relativeList) {
                try {
                    RelativesType rel = RelativesType.valueOf(relative.typeId);
                    RelativesType relativeSub = !TextUtils.isEmpty(relative.subtypeId) ? RelativesType.valueOf(relative.subtypeId) : null;
                    if (rel == RelativesType.SPOUSE) {
                        relativeSub = RelativesType.SPOUSE;
                    }
                    if (rel == RelativesType.SPOUSE) {
                        rel = RelativesType.RELATIVE;
                    }
                    Set<String> set = (Set) setMap.get(rel);
                    if (set == null) {
                        set = new HashSet();
                        setMap.put(rel, set);
                    }
                    set.addAll(Arrays.asList(relative.uids));
                    if (relativeSub != null) {
                        for (String uid : relative.uids) {
                            Set<RelativesType> relativesTypes = (Set) subMap.get(relative.uids);
                            if (relativesTypes == null) {
                                relativesTypes = new HashSet();
                                subMap.put(uid, relativesTypes);
                            }
                            relativesTypes.add(relativeSub);
                        }
                    }
                } catch (Throwable e) {
                    Logger.m178e(e);
                }
            }
            return new FriendsContainer(userInfo, this.mFid, setMap, null, subMap, null);
        } catch (Throwable e2) {
            Logger.m178e(e2);
            return new FriendsContainer(null, this.mFid, null, null, null, CommandProcessor.createErrorBundle(e2));
        }
    }

    private List<String> getFriendsValue(String fid) throws BaseApiException {
        return new JsonGetFriendsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetFriendsRequest(fid))).parse();
    }

    public int compare(UserInfo lhs, UserInfo rhs) {
        return (lhs == null || rhs == null) ? 0 : lhs.compareTo(rhs);
    }
}

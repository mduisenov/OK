package ru.ok.android.ui.adapters.friends;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;

public class FriendsContainer {
    public final Bundle errorBundle;
    public final Map<RelativesType, Set<String>> relativesSetMap;
    public final Map<String, Set<RelativesType>> relativesSubtypeMap;
    public final List<UserInfo> suggestionsFriend;
    public final String uid;
    public final List<UserInfo> userInfoList;

    public FriendsContainer(List<UserInfo> list, String uid, Map<RelativesType, Set<String>> map, List<UserInfo> list2, Map<String, Set<RelativesType>> map2, Bundle errorBundle) {
        List arrayList;
        Map hashMap;
        List arrayList2;
        Map hashMap2;
        this.uid = uid;
        if (list == null) {
            arrayList = new ArrayList();
        }
        this.userInfoList = arrayList;
        if (map == null) {
            hashMap = new HashMap();
        }
        this.relativesSetMap = hashMap;
        if (list2 == null) {
            arrayList2 = new ArrayList();
        }
        this.suggestionsFriend = arrayList2;
        if (map2 == null) {
            hashMap2 = new HashMap();
        }
        this.relativesSubtypeMap = hashMap2;
        this.errorBundle = errorBundle;
    }
}

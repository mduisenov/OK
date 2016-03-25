package ru.ok.android.ui.relations;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.GeneralDataLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.ui.adapters.friends.FriendsContainer;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;

public class RelationsLoaderMy extends GeneralDataLoader<FriendsContainer> {
    private List<UserInfo> friends;
    private final Map<RelativesType, Set<String>> setMap;
    private final Map<String, Set<RelativesType>> subMap;
    private List<UserInfo> suggestions;

    public RelationsLoaderMy(Context context) {
        super(context);
        this.setMap = new HashMap();
        this.subMap = new HashMap();
    }

    protected void onStartLoading() {
        super.onStartLoading();
        GlobalBus.register(this);
    }

    protected void onReset() {
        super.onReset();
        GlobalBus.unregister(this);
    }

    protected FriendsContainer loadData() {
        this.friends = UsersStorageFacade.queryFriends();
        this.setMap.clear();
        this.subMap.clear();
        UsersStorageFacade.fillUserRelations(this.setMap, this.subMap);
        return new FriendsContainer(this.friends, "my()", this.setMap, this.suggestions, this.subMap, null);
    }

    protected List<Uri> observableUris(FriendsContainer data) {
        return Arrays.asList(new Uri[]{OdklProvider.friendsUri(), OdklProvider.relativesUri()});
    }
}

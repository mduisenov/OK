package ru.ok.android.ui.users.fragments.data;

import android.support.annotation.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter.RelationItem;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;

public final class FriendsLoaderBundle {
    @NonNull
    public final FriendsAdapterBundle adapterBundle;
    @NonNull
    public final List<UserInfo> bestFriends;
    @NonNull
    public final List<RelationItem> relationCounts;
    @NonNull
    public final Map<String, Set<RelativesType>> subRelations;

    public FriendsLoaderBundle(@NonNull FriendsAdapterBundle adapterBundle, @NonNull Map<String, Set<RelativesType>> subRelations, @NonNull List<RelationItem> relationCounts, @NonNull List<UserInfo> bestFriends) {
        this.adapterBundle = adapterBundle;
        this.subRelations = subRelations;
        this.relationCounts = relationCounts;
        this.bestFriends = bestFriends;
    }
}

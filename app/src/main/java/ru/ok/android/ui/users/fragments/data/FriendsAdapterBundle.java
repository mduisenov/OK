package ru.ok.android.ui.users.fragments.data;

import android.support.annotation.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.java.api.request.relatives.RelativesType;

public final class FriendsAdapterBundle {
    @NonNull
    public final List<UserInfoExtended> friends;
    @NonNull
    public final Map<RelativesType, Set<String>> relations;

    public FriendsAdapterBundle(@NonNull List<UserInfoExtended> friends, @NonNull Map<RelativesType, Set<String>> relations) {
        this.friends = friends;
        this.relations = relations;
    }
}

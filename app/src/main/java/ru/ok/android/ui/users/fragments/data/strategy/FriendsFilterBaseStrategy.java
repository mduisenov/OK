package ru.ok.android.ui.users.fragments.data.strategy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.ui.users.fragments.data.FriendsAdapterBundle;
import ru.ok.android.ui.users.fragments.data.UserInfoExtended;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.relatives.RelativesType;

public abstract class FriendsFilterBaseStrategy<I> implements FriendsStrategy<I> {
    private Adapter adapter;
    protected final Context context;
    protected final List<UserInfoExtended> filteredUsers;
    private FriendsAdapterBundle friendsBundle;
    protected final LocalizationManager lm;
    private String query;
    protected RelativesType relationType;
    protected Map<String, Set<RelativesType>> subRelations;

    public FriendsFilterBaseStrategy(Context context) {
        this.filteredUsers = new ArrayList();
        this.context = context;
        this.lm = LocalizationManager.from(context);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public void updateSubRelations(Map<String, Set<RelativesType>> subRelations) {
        this.subRelations = subRelations;
    }

    public final void setQuery(String query) {
        this.query = !TextUtils.isEmpty(query) ? TranslateNormalizer.normalizeText4Search(query) : null;
        refilterUsers();
        this.adapter.notifyDataSetChanged();
    }

    public boolean isThatQuery(String query) {
        if (TextUtils.isEmpty(query) || TextUtils.isEmpty(this.query)) {
            return false;
        }
        return this.query.equals(TranslateNormalizer.normalizeText4Search(query));
    }

    void refilterUsers() {
        this.filteredUsers.clear();
        if (this.friendsBundle != null) {
            boolean queryEmpty = TextUtils.isEmpty(this.query);
            if (this.relationType != RelativesType.ALL && this.relationType != null && queryEmpty) {
                Set<String> userIds = (Set) this.friendsBundle.relations.get(this.relationType);
                if (userIds != null) {
                    for (UserInfoExtended user : this.friendsBundle.friends) {
                        if (userIds.contains(user.user.uid) && user.isUserPassQuery(this.query)) {
                            this.filteredUsers.add(user);
                        }
                    }
                }
            } else if (queryEmpty) {
                this.filteredUsers.addAll(this.friendsBundle.friends);
            } else {
                for (UserInfoExtended friend : this.friendsBundle.friends) {
                    if (friend.isUserPassQuery(this.query)) {
                        this.filteredUsers.add(friend);
                    }
                }
            }
        }
    }

    public void injectFilteredFriends(@Nullable List<UserInfoExtended> friends) {
        if (friends != null && !friends.isEmpty()) {
            boolean changed = false;
            for (UserInfoExtended friend : friends) {
                if (!this.filteredUsers.contains(friend)) {
                    this.filteredUsers.add(friend);
                    changed = true;
                }
            }
            if (changed) {
                Collections.sort(this.filteredUsers, UserInfoExtended.COMPARATOR);
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    public final void setRelationType(RelativesType relationType) {
        this.relationType = relationType;
        refilterUsers();
        this.adapter.notifyDataSetChanged();
    }

    public final void updateUsers(@Nullable FriendsAdapterBundle friendsBundle) {
        this.friendsBundle = friendsBundle;
        refilterUsers();
        this.adapter.notifyDataSetChanged();
    }
}

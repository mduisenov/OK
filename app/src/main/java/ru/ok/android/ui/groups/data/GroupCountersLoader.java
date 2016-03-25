package ru.ok.android.ui.groups.data;

import android.content.Context;
import android.support.v4.content.GeneralDataLoader;
import ru.ok.android.db.access.GroupsStorageFacade;
import ru.ok.java.api.response.groups.GroupCounters;

public final class GroupCountersLoader extends GeneralDataLoader<GroupCounters> {
    private final String groupId;

    public GroupCountersLoader(Context context, String groupId) {
        super(context);
        this.groupId = groupId;
    }

    protected GroupCounters loadData() {
        return GroupsStorageFacade.queryGroupCounters(this.groupId);
    }
}

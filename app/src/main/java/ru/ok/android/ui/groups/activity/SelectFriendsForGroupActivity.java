package ru.ok.android.ui.groups.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.db.provider.OdklContract.GroupMembers;
import ru.ok.android.ui.users.activity.SelectFriendsFilteredActivity;

public final class SelectFriendsForGroupActivity extends SelectFriendsFilteredActivity {
    private List<String> allowedIds;
    private List<String> alreadyInGroupIds;
    private final LoaderCallbacks<Cursor> friendsIdsLoaderCallback;

    /* renamed from: ru.ok.android.ui.groups.activity.SelectFriendsForGroupActivity.1 */
    class C09101 implements LoaderCallbacks<Cursor> {
        C09101() {
        }

        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new CursorLoader(SelectFriendsForGroupActivity.this, GroupMembers.getContentUri(), new String[]{"gm_user_id"}, "gm_group_id = ?", new String[]{SelectFriendsForGroupActivity.this.getGroupId()}, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0 && SelectFriendsForGroupActivity.this.findFriendsListFragment() != null) {
                SelectFriendsForGroupActivity.this.alreadyInGroupIds = new ArrayList();
                while (cursor.moveToNext()) {
                    SelectFriendsForGroupActivity.this.alreadyInGroupIds.add(cursor.getString(0));
                }
                SelectFriendsForGroupActivity.this.updateAllowedIds(SelectFriendsForGroupActivity.this.allowedIds);
            }
        }

        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

    public SelectFriendsForGroupActivity() {
        this.friendsIdsLoaderCallback = new C09101();
    }

    private String getGroupId() {
        return getIntent().getStringExtra("GROUP_ID");
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        getSupportLoaderManager().initLoader(2, null, this.friendsIdsLoaderCallback);
    }

    protected void updateAllowedIds(List<String> allowedIds) {
        this.allowedIds = allowedIds;
        if (allowedIds != null) {
            List<String> ids = new ArrayList();
            if (allowedIds != null) {
                ids.addAll(allowedIds);
            }
            if (this.alreadyInGroupIds != null) {
                ids.removeAll(this.alreadyInGroupIds);
            } else {
                getSupportLoaderManager().getLoader(2).forceLoad();
            }
            super.updateAllowedIds(ids);
        }
    }
}

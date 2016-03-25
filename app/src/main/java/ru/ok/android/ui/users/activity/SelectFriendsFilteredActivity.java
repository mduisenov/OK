package ru.ok.android.ui.users.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.db.provider.OdklContract.UserPrivacySettings;
import ru.ok.android.fragments.FriendsListWithPrivacyFilterFragment;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.ui.users.fragments.FriendsListFilteredFragment;
import ru.ok.android.ui.users.fragments.FriendsListWithFilterNoNavigationFragment;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.friends.FriendsFilter;

public class SelectFriendsFilteredActivity extends SelectFriendsActivity {
    private FriendsFilter friendsFilter;
    private int privacySettingId;
    final LoaderCallbacks<Cursor> privacySettingsLoaderCallback;

    /* renamed from: ru.ok.android.ui.users.activity.SelectFriendsFilteredActivity.1 */
    class C12911 implements LoaderCallbacks<Cursor> {
        private final UIHandler uiHandler;

        /* renamed from: ru.ok.android.ui.users.activity.SelectFriendsFilteredActivity.1.1 */
        class C12901 extends AsyncTask<Void, Void, Void> {
            final /* synthetic */ Cursor val$cursor;

            C12901(Cursor cursor) {
                this.val$cursor = cursor;
            }

            protected Void doInBackground(Void... params) {
                if (this.val$cursor != null && this.val$cursor.moveToFirst()) {
                    ArrayList<String> allowedUids = new ArrayList();
                    while (!this.val$cursor.isAfterLast()) {
                        if (this.val$cursor.getInt(1) != 3) {
                            allowedUids.add(this.val$cursor.getString(0));
                        }
                        this.val$cursor.moveToNext();
                    }
                    C12911.this.uiHandler.postSetEnabledUids(allowedUids);
                }
                return null;
            }
        }

        /* renamed from: ru.ok.android.ui.users.activity.SelectFriendsFilteredActivity.1.UIHandler */
        class UIHandler extends Handler {
            UIHandler() {
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        SelectFriendsFilteredActivity.this.updateAllowedIds((ArrayList) msg.obj);
                    default:
                }
            }

            void postSetEnabledUids(ArrayList<String> enabledUids) {
                sendMessage(Message.obtain(this, 1, enabledUids));
            }
        }

        C12911() {
            this.uiHandler = new UIHandler();
        }

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PrivacySettingsLoader(SelectFriendsFilteredActivity.this, SelectFriendsFilteredActivity.this.privacySettingId);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            new C12901(cursor).execute(new Void[0]);
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            Logger.m172d("");
        }
    }

    static class PrivacySettingsLoader extends CursorLoader {
        public PrivacySettingsLoader(Context context, int privacySettingId) {
            super(context, UserPrivacySettings.getUri(privacySettingId), new String[]{"uid", "privacy_mode"}, null, null, null);
        }
    }

    public SelectFriendsFilteredActivity() {
        this.privacySettingsLoaderCallback = new C12911();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        switch (getIntent().getIntExtra("select_target", 0)) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                this.privacySettingId = 1;
                this.friendsFilter = FriendsFilter.MARK_IN_TOPICS;
                break;
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                this.privacySettingId = 4;
                this.friendsFilter = FriendsFilter.GROUPS_INVITE;
                break;
            default:
                this.privacySettingId = -1;
                this.friendsFilter = null;
                break;
        }
        super.onCreateLocalized(savedInstanceState);
        if (this.privacySettingId != -1) {
            initPrivacySettingsLoader();
        }
        if (this.friendsFilter != null) {
            FriendsListWithPrivacyFilterFragment.sendFriendsFilterRequest(this.friendsFilter);
        }
    }

    protected Class<? extends FriendsListFilteredFragment> getFriendsListFragmentClass() {
        return FriendsListWithFilterNoNavigationFragment.class;
    }

    protected Bundle createFriendsListFragmentArgs(Intent intent) {
        Bundle args = super.createFriendsListFragmentArgs(intent);
        if (this.friendsFilter != null) {
            FriendsListWithPrivacyFilterFragment.fillArgs(args, this.friendsFilter);
        }
        return args;
    }

    protected void initPrivacySettingsLoader() {
        getSupportLoaderManager().initLoader(1, null, this.privacySettingsLoaderCallback);
    }

    protected void updateAllowedIds(List<String> allowedIds) {
        FriendsListFilteredFragment friendsListFragment = findFriendsListFragment();
        if (friendsListFragment != null) {
            friendsListFragment.updateEnabledIds(allowedIds);
        }
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}

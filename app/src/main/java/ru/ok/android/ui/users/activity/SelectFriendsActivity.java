package ru.ok.android.ui.users.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.ui.users.fragments.FriendsListFilteredFragment;
import ru.ok.android.ui.users.fragments.FriendsListNoNavigationFragment;
import ru.ok.android.utils.Logger;

public class SelectFriendsActivity extends ShowDialogFragmentActivity {
    protected Class<? extends FriendsListFilteredFragment> getFriendsListFragmentClass() {
        return FriendsListNoNavigationFragment.class;
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(getLayoutId());
        showFriendsListFragment();
        setResult(0);
    }

    protected void showFriendsListFragment() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("friends_list");
            if (fragment == null) {
                fragment = (Fragment) getFriendsListFragmentClass().newInstance();
                fragment.setArguments(createFriendsListFragmentArgs(getIntent()));
            }
            getSupportFragmentManager().beginTransaction().replace(2131624880, fragment, "friends_list").commit();
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to create fragment");
        }
    }

    protected Bundle createFriendsListFragmentArgs(Intent intent) {
        return FriendsListFilteredFragment.newArguments(true, null, SelectionsMode.MEDIA_TOPICS, (UsersSelectionParams) intent.getParcelableExtra("selection_params"), getTitleId());
    }

    private int getTitleId() {
        return getIntent().getIntExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131165394);
    }

    protected int getLayoutId() {
        return 2130903355;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.m173d("id: %d", Integer.valueOf(item.getItemId()));
        if (item.getItemId() != 2131625470) {
            return false;
        }
        FriendsListFilteredFragment friendsListFragment = findFriendsListFragment();
        if (friendsListFragment == null) {
            return true;
        }
        processSelectionParams(new ArrayList(friendsListFragment.getSelectedIds()));
        return true;
    }

    protected void processSelectionParams(ArrayList<String> selectedIds) {
        Intent result = new Intent();
        result.fillIn(getIntent(), 3);
        result.putExtra("selected_ids", selectedIds);
        setResult(-1, result);
        finish();
    }

    protected FriendsListFilteredFragment findFriendsListFragment() {
        return (FriendsListFilteredFragment) getSupportFragmentManager().findFragmentByTag("friends_list");
    }
}

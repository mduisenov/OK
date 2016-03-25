package ru.ok.android.ui.users.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import ru.ok.android.fragments.FriendsChooseForConversationFragment;
import ru.ok.android.fragments.UsersListFragment.UsersListFragmentListener;
import ru.ok.android.ui.activity.ShowDialogFragmentActivity;

public final class SearchFriendsActivity extends ShowDialogFragmentActivity implements UsersListFragmentListener {
    public void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView();
        FriendsChooseForConversationFragment fragment = (FriendsChooseForConversationFragment) getSupportFragmentManager().findFragmentById(2131624639);
        if (fragment == null) {
            fragment = FriendsChooseForConversationFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(2131624639, fragment).commit();
        }
        if (getIntent().getBooleanExtra("return_user_id", false)) {
            fragment.setListener(this);
        }
    }

    protected void onDestroy() {
        FriendsChooseForConversationFragment fragment = (FriendsChooseForConversationFragment) getSupportFragmentManager().findFragmentById(2131624639);
        if (fragment != null) {
            fragment.setListener(null);
        }
        super.onDestroy();
    }

    public void onUserSelected(String userId) {
        Intent data = new Intent();
        data.putExtra("user_id", userId);
        setResult(-1, data);
        finish();
    }

    public void onListCrated(RecyclerView listView) {
    }
}

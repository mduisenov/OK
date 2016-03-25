package ru.ok.android.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.model.UserInfo;

public class MutualFriendsDialog extends UsersListDialog {
    private ListView listView;
    private ProgressBar progressBar;

    public MutualFriendsDialog() {
        GlobalBus.register(this);
    }

    public void showProgressBar() {
        if (this.listView != null) {
            this.progressBar = new ProgressBar(getActivity());
            this.progressBar.setVisibility(0);
            this.listView.addFooterView(this.progressBar);
        }
    }

    public void hideProgressBar() {
        if (this.listView != null && this.progressBar != null) {
            this.listView.removeFooterView(this.progressBar);
        }
    }

    public static MutualFriendsDialog createInstance(ArrayList<UserInfo> mutualFriends, String title, String uid, Boolean isLoading) {
        Bundle args = UsersListDialog.getNewInstanceArguments(mutualFriends, title);
        args.putString("uid", uid);
        args.putBoolean("is_loading", isLoading.booleanValue());
        MutualFriendsDialog dialog = new MutualFriendsDialog();
        dialog.setArguments(args);
        return dialog;
    }

    private boolean getIsLoading() {
        return getArguments().getBoolean("is_loading", false);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = (MaterialDialog) super.onCreateDialog(savedInstanceState);
        this.listView = dialog.getListView();
        if (getIsLoading()) {
            showProgressBar();
        }
        return dialog;
    }

    private String getUid() {
        return getArguments().getString("uid");
    }

    @Subscribe(on = 2131623946, to = 2131624232)
    public void updateUsers(BusEvent event) {
        String uid = getUid();
        String targetId = event.bundleInput.getString("target_id");
        if (uid != null && targetId != null && targetId.equals(uid)) {
            hideProgressBar();
            ArrayList<UserInfo> mutualFriendsAll = event.bundleOutput.getParcelableArrayList("mutual_friends");
            ArrayList<UserInfo> mutualFriends = this.adapter.getUsers();
            if (mutualFriendsAll != null) {
                Iterator i$ = mutualFriendsAll.iterator();
                while (i$.hasNext()) {
                    UserInfo mutualFriend = (UserInfo) i$.next();
                    if (!mutualFriends.contains(mutualFriend)) {
                        mutualFriends.add(mutualFriend);
                    }
                }
            }
            this.adapter.setUsers(mutualFriends);
            this.adapter.notifyDataSetChanged();
        }
    }
}

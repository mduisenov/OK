package ru.ok.android.ui.fragments.messages;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.ArrayList;
import ru.ok.android.ui.users.fragments.UsersByIdFragment;
import ru.ok.android.utils.localization.LocalizationManager;

public final class UsersBlockedFragment extends UsersByIdFragment {
    public static UsersBlockedFragment newInstance(ArrayList<String> userIds) {
        Bundle args = new Bundle();
        UsersByIdFragment.fillArguments(args, userIds, 2131165791, true, null);
        UsersBlockedFragment result = new UsersBlockedFragment();
        result.setArguments(args);
        return result;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setNegativeButton(LocalizationManager.from(getActivity()).getString(2131165595), null);
        builder.setView(super.onCreateView(LayoutInflater.from(getActivity()), null, savedInstanceState));
        builder.setTitle(getStringLocalized(2131166806));
        return builder.create();
    }

    protected int getLayoutId() {
        return 2130903562;
    }
}

package ru.ok.android.ui.dialogs;

import android.os.Bundle;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;

public class BlockUserFragmentDialog extends YesNoQuestionDialogFragment {
    public static BlockUserFragmentDialog newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        BlockUserFragmentDialog result = new BlockUserFragmentDialog();
        result.setArguments(args);
        return result;
    }

    public String getUserId() {
        return getArguments().getString("user_id");
    }

    protected String getTitle() {
        return LocalizationManager.getString(getActivity(), 2131166728);
    }

    String getQuestion() {
        return LocalizationManager.getString(getActivity(), 2131166727);
    }

    void onNotifyYesResult() {
        BusUsersHelper.complaintToUser(getUserId(), null, true);
        UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_BLOCK_USER);
    }
}

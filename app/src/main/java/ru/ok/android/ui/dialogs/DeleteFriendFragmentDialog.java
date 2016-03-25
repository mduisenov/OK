package ru.ok.android.ui.dialogs;

import android.os.Bundle;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;

public class DeleteFriendFragmentDialog extends YesNoQuestionDialogFragment {
    public static DeleteFriendFragmentDialog newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        DeleteFriendFragmentDialog result = new DeleteFriendFragmentDialog();
        result.setArguments(args);
        return result;
    }

    public String getUserId() {
        return getArguments().getString("user_id");
    }

    protected String getTitle() {
        return LocalizationManager.getString(getActivity(), 2131165671);
    }

    String getQuestion() {
        return LocalizationManager.getString(getActivity(), 2131165679);
    }

    void onNotifyYesResult() {
        BusUsersHelper.deleteFriend(getUserId());
        UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_DELETE_FRIENDS);
    }
}

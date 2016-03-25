package ru.ok.android.ui.dialogs;

import android.content.Context;
import android.view.View;
import ru.ok.model.UserInfo;

public class UsersDoBase {
    protected View anchor;
    protected Context context;
    protected OnCallUserSelectListener onCallUserSelectListener;
    protected OnGoToMainPageSelectListener onGoToMainPageSelectListener;
    protected UserInfo user;

    public interface OnCallUserSelectListener {
        void onCallUserSelect(UserInfo userInfo, View view);
    }

    public interface OnGoToMainPageSelectListener {
        void onGoToMainPageSelect(UserInfo userInfo, View view);
    }

    public UsersDoBase(Context context, UserInfo user, View anchor) {
        this.context = context;
        this.user = user;
        this.anchor = anchor;
    }

    public void setOnGoToMainPageSelectListener(OnGoToMainPageSelectListener onGoToMainPageSelectListener) {
        this.onGoToMainPageSelectListener = onGoToMainPageSelectListener;
    }

    public void setOnCallUserSelectListener(OnCallUserSelectListener onCallUserSelectListener) {
        this.onCallUserSelectListener = onCallUserSelectListener;
    }
}

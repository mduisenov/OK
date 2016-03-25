package ru.ok.android.ui.custom.cards.search;

import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class UserViewsHolderCardBig extends UserViewsHolder implements OnClickListener {
    public UserViewsHolderCardBig(View view) {
        super(view);
        this.avatarView.setRoundCornersEnabled(true);
    }

    protected int getEmptyImageResId(UserInfo userInfo) {
        return userInfo.genderType == UserGenderType.FEMALE ? 2130837655 : 2130837656;
    }
}

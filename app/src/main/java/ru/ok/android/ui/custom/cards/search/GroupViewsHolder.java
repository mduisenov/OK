package ru.ok.android.ui.custom.cards.search;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;

public class GroupViewsHolder extends CardViewHolder {
    private AsyncDraweeView avatarView;
    private TextView membersCountView;
    private TextView nameView;
    private View officialView;
    private View privateGroupView;

    public GroupViewsHolder(View view) {
        super(view);
        this.avatarView = (AsyncDraweeView) view.findViewById(2131624657);
        this.avatarView.setErrorImageResId(2130837663);
        this.avatarView.setEmptyImageResId(2130837663);
        this.officialView = view.findViewById(2131624658);
        this.nameView = (TextView) view.findViewById(C0263R.id.name);
        this.membersCountView = (TextView) view.findViewById(2131624664);
        this.privateGroupView = view.findViewById(2131624659);
    }

    public void update(GroupInfo groupInfo) {
        int i;
        int i2 = 0;
        int membersCountRes = StringUtils.plural((long) groupInfo.getMembersCount(), 2131166186, 2131166187, 2131166188);
        this.membersCountView.setText(LocalizationManager.getString(this.itemView.getContext(), membersCountRes, Integer.valueOf(count)));
        View view = this.officialView;
        if (groupInfo.isPremium()) {
            i = 0;
        } else {
            i = 8;
        }
        view.setVisibility(i);
        this.nameView.setText(groupInfo.getName());
        Uri avatarUrl = groupInfo.getAvatarUrl();
        if (avatarUrl == null) {
            this.avatarView.setImageResource(2130837663);
        } else {
            this.avatarView.setUri(avatarUrl);
        }
        View view2 = this.privateGroupView;
        if (!groupInfo.isPrivateGroup()) {
            i2 = 8;
        }
        view2.setVisibility(i2);
    }
}

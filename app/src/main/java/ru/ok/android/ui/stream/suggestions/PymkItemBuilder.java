package ru.ok.android.ui.stream.suggestions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.PymkFragment.PymkItemHolder;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.dialogs.MutualFriendsDialog;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.fragments.messages.view.PymkMutualFriendsView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.MutualFriendsPreviewInfo;
import ru.ok.model.UserInfo;

public class PymkItemBuilder {
    private OnClickListener avatarClickListener;
    private OnClickListener cancelClickListener;
    private final Context context;
    private OnClickListener inviteClickListener;
    private boolean isCancelable;
    private boolean isRequestMutualFriendsEnabled;
    private ArrayList<UserInfo> mutualFriends;
    private OnClickListener mutualFriendsClickListener;
    private MutualFriendsPreviewInfo mutualInfo;
    private String tag;
    private UserInfo userInfo;
    private OnClickListener wholeItemClickListener;

    public PymkItemBuilder(UserInfo userInfo, Context context) {
        this.isCancelable = true;
        this.isRequestMutualFriendsEnabled = false;
        this.userInfo = userInfo;
        this.context = context;
        this.tag = userInfo.getId();
    }

    public PymkItemBuilder setOnAvatarClickListener(OnClickListener avatarClickListener) {
        this.avatarClickListener = avatarClickListener;
        return this;
    }

    public PymkItemBuilder setOnMutualFriendsClickListener(OnClickListener mutualFriendsClickListener) {
        this.mutualFriendsClickListener = mutualFriendsClickListener;
        return this;
    }

    public PymkItemBuilder setOnInviteClickListener(OnClickListener inviteClickListener) {
        this.inviteClickListener = inviteClickListener;
        return this;
    }

    public PymkItemBuilder setIsCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
        return this;
    }

    public PymkItemBuilder setMutualInfo(MutualFriendsPreviewInfo mutualInfo) {
        this.mutualInfo = mutualInfo;
        return this;
    }

    public String getDividerTag() {
        return getDividerTag(this.tag);
    }

    public static String getDividerTag(String tag) {
        return tag == null ? null : "divider" + tag;
    }

    public static String getUidByView(View view) {
        return (String) view.getTag();
    }

    public static String getUidByView(ViewParent viewParent) {
        return getUidByView((View) viewParent);
    }

    private void attachListeners(ViewGroup view) {
        if (this.cancelClickListener != null) {
            view.findViewById(C0263R.id.cancel).setOnClickListener(this.cancelClickListener);
        }
        if (this.inviteClickListener != null) {
            view.findViewById(2131624926).setOnClickListener(this.inviteClickListener);
        }
        if (this.avatarClickListener != null) {
            view.findViewById(2131624657).setOnClickListener(this.avatarClickListener);
        }
        if (this.mutualFriendsClickListener != null) {
            view.findViewById(2131625374).setOnClickListener(this.mutualFriendsClickListener);
        }
        if (this.wholeItemClickListener != null) {
            view.setOnClickListener(this.wholeItemClickListener);
        }
    }

    public View create(LayoutInflater inflater, ViewGroup container) {
        ViewGroup suggestedFriendView = (ViewGroup) inflater.inflate(2130903527, container, false);
        attachListeners(suggestedFriendView);
        ((ParticipantsPreviewView) suggestedFriendView.findViewById(2131625374)).setMaxAvatars(3);
        container.addView(suggestedFriendView);
        View divider = inflater.inflate(2130903171, container, false);
        divider.setTag(getDividerTag());
        container.addView(divider);
        if (!this.isCancelable) {
            suggestedFriendView.findViewById(C0263R.id.cancel).setVisibility(8);
        }
        if (this.isRequestMutualFriendsEnabled) {
            requestMutualFriendsForUser(this.userInfo.getId());
        }
        return suggestedFriendView;
    }

    public static void requestMutualFriendsForUser(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("source_id", OdnoklassnikiApplication.getCurrentUser().uid);
        bundle.putString("target_id", uid);
        GlobalBus.send(2131624092, new BusEvent(bundle));
    }

    public static void showUserInfo(Activity activity, String uid) {
        NavigationHelper.showUserInfo(activity, uid);
    }

    public static void showMutualFriends(PymkMutualFriendsView view, Context context, Fragment fragment, boolean loadMore) {
        showMutualFriends((ArrayList) view.getParticipants(), context, fragment, view.getUid(), loadMore);
    }

    public static void showMutualFriends(ArrayList<UserInfo> mutualFriends, Context context, Fragment fragment, String uid, boolean loadMore) {
        MutualFriendsDialog.createInstance(mutualFriends, LocalizationManager.getString(context, 2131166242), uid, Boolean.valueOf(loadMore)).show(fragment.getFragmentManager(), "mutual_friends_list");
    }

    protected void fillName(TextView nameTextView) {
        nameTextView.setText(this.userInfo.getName());
    }

    protected void fillAvatar(AvatarImageView avatar) {
        avatar.requestDisallowInterceptTouchEvent(false);
        avatar.setUser(this.userInfo);
        ImageViewManager.getInstance().displayImage(this.userInfo.getPicUrl(), avatar, this.userInfo.isMan(), null);
    }

    protected void fillAgeAndLocation(TextView ageAndLocationTextView) {
        String ageAndLocation = Utils.getAgeAndLocationText(this.context, this.userInfo);
        if (ageAndLocation.length() > 0) {
            ageAndLocationTextView.setText(ageAndLocation);
            ageAndLocationTextView.setVisibility(0);
        }
    }

    protected void fillMutualFriends(PymkMutualFriendsView pymkMutualFriendsView) {
        if (this.mutualFriends != null) {
            pymkMutualFriendsView.setParticipants(this.mutualFriends, true);
        } else if (this.mutualInfo != null) {
            pymkMutualFriendsView.setParticipants(this.mutualInfo);
        }
        pymkMutualFriendsView.setUid(this.userInfo.getId());
    }

    public void fillView(PymkItemHolder holder) {
        fillName(holder.nameTextView);
        fillAgeAndLocation(holder.ageAndLocationTextView);
        fillMutualFriends(holder.pymkMutualFriendsView);
        fillAvatar(holder.avatarImageView);
    }

    public PymkItemBuilder setWholeItemClickListener(OnClickListener wholeItemClickListener) {
        this.wholeItemClickListener = wholeItemClickListener;
        return this;
    }
}

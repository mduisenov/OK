package ru.ok.android.ui.stream.friendship;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.custom.UsersStripView;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.stream.friendship.FriendShipDataHolder.FriendshipDataHolderListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.viewcache.StreamViewCache;
import ru.ok.android.ui.users.fragments.UsersByIdFragment;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class UserCommonFriendsViewHolder extends ViewHolder implements OnClickListener, FriendshipDataHolderListener {
    private final FriendShipDataHolder dataHolder;
    private HandleBlocker imageBlocker;
    public final RoundAvatarImageView imageView;
    public final UsersStripView imagesContainer;
    public final TextView nameView;
    public final TextView textExt;
    public UserInfo userInfo;
    private final StreamViewCache viewCache;

    public UserCommonFriendsViewHolder(View view, StreamViewCache viewCache, FriendShipDataHolder dataHolder, HandleBlocker imageBlocker) {
        super(view);
        this.imageView = (RoundAvatarImageView) view.findViewById(2131624657);
        this.imageView.setIsAlpha(true);
        this.nameView = (TextView) view.findViewById(2131625379);
        this.textExt = (TextView) view.findViewById(2131625380);
        this.imagesContainer = (UsersStripView) view.findViewById(2131625126);
        if (this.imagesContainer != null) {
            this.imagesContainer.setHandleBlocker(imageBlocker);
            this.imagesContainer.setOnClickListener(this);
        }
        this.viewCache = viewCache;
        this.dataHolder = dataHolder;
        this.imageBlocker = imageBlocker;
    }

    public void setUser(UserInfo user) {
        this.userInfo = user;
        ImageViewManager.getInstance().displayImage(user.picUrl, this.imageView, user.genderType == UserGenderType.MALE, this.imageBlocker);
        this.nameView.setText(user.getConcatName());
        this.imageView.setTag(user);
        this.viewCache.collectAndClearChildViews(this.imagesContainer);
        if (this.textExt != null) {
            List<UserInfo> mutual = this.dataHolder.getMutualFriends(user.getId());
            if (mutual == null) {
                this.dataHolder.addListener(this);
                this.textExt.setText(null);
                this.imagesContainer.setVisibility(8);
                return;
            }
            processMutual(mutual);
        }
    }

    protected void processMutual(List<UserInfo> mutual) {
        int mutualCount = mutual.size();
        if (mutualCount < 2) {
            List<UserInfo> users = Collections.emptyList();
            this.imagesContainer.setVisibility(8);
            displayAgeLocation();
            this.imagesContainer.setUsers(users, 0);
            return;
        }
        int textResId = StringUtils.plural((long) mutualCount, 2131165610, 2131165611, 2131165612);
        this.imagesContainer.setVisibility(0);
        this.textExt.setText(this.textExt.getContext().getString(textResId, new Object[]{Integer.valueOf(mutualCount)}));
        this.imagesContainer.setUsers(mutual, mutualCount);
    }

    private void displayAgeLocation() {
        StringBuilder sb = new StringBuilder();
        if (!(this.userInfo.age == -1 || this.userInfo.age == 0)) {
            int ageFormat = StringUtils.plural((long) this.userInfo.age, 2131165364, 2131165365, 2131165366);
            sb.append(LocalizationManager.getString(this.textExt.getContext(), ageFormat, Integer.valueOf(this.userInfo.age)));
        }
        if (!(this.userInfo.location == null || TextUtils.isEmpty(this.userInfo.location.city))) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(this.userInfo.location.city);
        }
        this.textExt.setText(sb);
    }

    public void onClick(View view) {
        List<UserInfo> mutual = this.dataHolder.getMutualFriends(this.userInfo.uid);
        if (mutual != null && !mutual.isEmpty()) {
            FragmentActivity activity = (FragmentActivity) view.getContext();
            ArrayList<String> userIds = new ArrayList();
            for (UserInfo info : mutual) {
                userIds.add(info.getId());
            }
            StreamStats.clickMutualFriends();
            if (userIds.size() == 1) {
                NavigationHelper.showUserInfo(activity, (String) userIds.get(0));
                StreamStats.clickUser("mutual_friends");
                return;
            }
            UsersByIdFragment fragment = UsersByIdFragment.newInstanceCommonFriends(userIds, 2131166242, "mutual_friends");
            fragment.setTargetFragment(fragment, 0);
            fragment.show(activity.getSupportFragmentManager(), "users-list");
        }
    }

    public void onMutualFriendsLoaded(String userId, List<UserInfo> mutualFriends) {
        if (TextUtils.equals(userId, this.userInfo.uid)) {
            this.viewCache.collectAndClearChildViews(this.imagesContainer);
            processMutual(mutualFriends);
        }
    }
}

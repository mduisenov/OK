package ru.ok.android.ui.adapters.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.adapters.ScrollLoadRecyclerViewBlocker;
import ru.ok.android.ui.custom.MessageCheckBox;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.imageview.AvatarImageView.OnClickToUserImageListener;
import ru.ok.android.ui.users.UserEnabledSelectionParams;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class UserInfosController implements OnCheckedChangeListener {
    private final OnClickToUserImageListener avatarClickListener;
    protected final Context context;
    private ContextMenuOpenListener contextMenuOpenListener;
    private final UserInfosControllerListener controllerListener;
    private int dimenDotsTouchDelegateExtend;
    protected final boolean doShowSelection;
    private OnClickListener dotsClickListener;
    private final boolean dotsEnabled;
    private final ScrollLoadRecyclerViewBlocker imageLoadBlocker;
    protected final SelectionsMode mode;
    private final ScrollLoadRecyclerViewBlocker onlineScrollBlocker;
    private final Blocker scrollBlocker;
    protected final Set<String> selectedIds;
    protected UsersSelectionParams selectedUsersParams;
    private String selectionUser;
    private final boolean showLastOnline;

    public interface UserInfosControllerListener {
        void onUserSelectionChanged(boolean z);
    }

    /* renamed from: ru.ok.android.ui.adapters.friends.UserInfosController.1 */
    class C05831 implements OnClickListener {
        C05831() {
        }

        public void onClick(View contextMenuButton) {
            if (UserInfosController.this.contextMenuOpenListener != null) {
                UserInfosController.this.contextMenuOpenListener.onContextMenuButtonClick((UserInfo) contextMenuButton.getTag(), contextMenuButton);
            }
        }
    }

    public class Blocker extends OnScrollListener {
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            UserInfosController.this.imageLoadBlocker.onScrollStateChanged(view, scrollState);
            UserInfosController.this.onlineScrollBlocker.onScrollStateChanged(view, scrollState);
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            UserInfosController.this.imageLoadBlocker.onScrolled(recyclerView, dx, dy);
            UserInfosController.this.onlineScrollBlocker.onScrolled(recyclerView, dx, dy);
        }
    }

    public class UserInfoViewHolder extends CardViewHolder {
        AvatarImageView avatar;
        View dots;
        MessageCheckBox messageCheckBox;
        View onlineView;
        View separator;
        View textBlock;
        TextView textInclude;
        TextView textName;
        View wholeRow;

        public UserInfoViewHolder(View view) {
            super(view);
            this.wholeRow = view;
            this.separator = view.findViewById(2131624718);
            this.textName = (TextView) view.findViewById(2131624977);
            this.textInclude = (TextView) view.findViewById(2131624978);
            this.avatar = (AvatarImageView) view.findViewById(2131624657);
            if (UserInfosController.this.avatarClickListener != null) {
                this.avatar.setOnClickToImageListener(UserInfosController.this.avatarClickListener);
                this.avatar.setBackgroundResource(2130837662);
            }
            this.onlineView = view.findViewById(2131624634);
            this.messageCheckBox = (MessageCheckBox) view.findViewById(2131624976);
            this.textBlock = view.findViewById(2131624894);
            this.dots = view.findViewById(2131624874);
        }

        void setAlpha(float alpha) {
            this.messageCheckBox.setAlpha(alpha);
            this.avatar.setAlpha(alpha);
            this.textBlock.setAlpha(alpha);
        }
    }

    private class CheckableViewHolder extends UserInfoViewHolder {
        final CompoundButton checkBox;

        CheckableViewHolder(View view) {
            super(view);
            this.checkBox = (CompoundButton) view.findViewById(2131624976);
            this.checkBox.setVisibility(0);
            this.checkBox.setOnCheckedChangeListener(UserInfosController.this);
        }
    }

    public interface ContextMenuOpenListener {
        void onContextMenuButtonClick(UserInfo userInfo, View view);
    }

    public enum SelectionsMode {
        SINGLE,
        MULTI,
        MEDIA_TOPICS
    }

    public UserInfosController(Context context, OnClickToUserImageListener avatarClickListener, UserInfosControllerListener controllerListener, boolean doShowSelection, SelectionsMode mode, UsersSelectionParams selectedUsersParams, ArrayList<String> selectedIds, boolean dotsEnabled, boolean showLastOnline) {
        this.imageLoadBlocker = ScrollLoadRecyclerViewBlocker.forIdleAndTouchIdle();
        this.onlineScrollBlocker = ScrollLoadRecyclerViewBlocker.forIdleOnly();
        this.scrollBlocker = new Blocker();
        this.dotsClickListener = new C05831();
        this.context = context;
        this.dimenDotsTouchDelegateExtend = context.getResources().getDimensionPixelSize(2131230952);
        this.avatarClickListener = avatarClickListener;
        this.controllerListener = controllerListener;
        this.dotsEnabled = dotsEnabled;
        this.doShowSelection = doShowSelection;
        this.mode = mode;
        this.selectedUsersParams = selectedUsersParams;
        if (selectedIds != null) {
            this.selectedIds = new HashSet(selectedIds);
        } else if (selectedUsersParams == null) {
            this.selectedIds = new HashSet();
        } else {
            this.selectedIds = new HashSet(selectedUsersParams.getSelectedIds());
        }
        this.showLastOnline = showLastOnline;
        onCheckedChanged(null, false);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.controllerListener != null && this.selectedUsersParams != null) {
            Set<String> selected = new HashSet(this.selectedIds);
            for (String id : this.selectedUsersParams.getSelectedIds()) {
                if (!this.selectedUsersParams.isEnabled(id)) {
                    selected.remove(id);
                }
            }
            this.controllerListener.onUserSelectionChanged(!selected.isEmpty());
        }
    }

    public OnScrollListener getScrollBlocker() {
        return this.scrollBlocker;
    }

    public boolean isEnabled(String id) {
        if (this.mode == SelectionsMode.SINGLE) {
            return true;
        }
        if (!this.selectedUsersParams.isEnabled(id)) {
            return false;
        }
        if (isLimitReached()) {
            return this.selectedIds.contains(id);
        }
        return true;
    }

    public void bindView(int position, UserInfoViewHolder holder, UserInfo user, int count) {
        updateGenderImage(holder, user);
        updateNameBlock(holder, user);
        updateOnline(holder, user);
        bindViewState(position, holder, user, count);
        ViewUtil.setVisibility(holder.separator, position == count + -1);
        if (this.dotsEnabled) {
            holder.dots.setTag(user);
        }
    }

    public void bindViewState(int position, UserInfoViewHolder holder, UserInfo user, int count) {
        boolean isMultiSelectMode;
        boolean isUserChecked;
        boolean isUserSelected;
        boolean isUserDisabled;
        if (this.mode == SelectionsMode.MULTI || this.mode == SelectionsMode.MEDIA_TOPICS) {
            isMultiSelectMode = true;
        } else {
            isMultiSelectMode = false;
        }
        String uid = user.uid;
        if (isMultiSelectMode && this.selectedIds.contains(uid)) {
            isUserChecked = true;
        } else {
            isUserChecked = false;
        }
        if (isUserChecked || (!isMultiSelectMode && TextUtils.equals(uid, this.selectionUser))) {
            isUserSelected = true;
        } else {
            isUserSelected = false;
        }
        boolean usersParamsEnabled;
        if (this.selectedUsersParams == null || !this.selectedUsersParams.isEnabled(uid)) {
            usersParamsEnabled = false;
        } else {
            usersParamsEnabled = true;
        }
        if (!isMultiSelectMode || (usersParamsEnabled && (!isLimitReached() || isUserChecked))) {
            isUserDisabled = false;
        } else {
            isUserDisabled = true;
        }
        updateForItemState(holder, isUserSelected, isUserChecked, isUserDisabled);
    }

    private void updateForItemState(UserInfoViewHolder holder, boolean isSelected, boolean isChecked, boolean isUserDisabled) {
        if (holder instanceof CheckableViewHolder) {
            boolean z;
            CheckableViewHolder checkableViewHolder = (CheckableViewHolder) holder;
            checkableViewHolder.checkBox.setChecked(isChecked);
            CompoundButton compoundButton = checkableViewHolder.checkBox;
            if (isUserDisabled) {
                z = false;
            } else {
                z = true;
            }
            compoundButton.setEnabled(z);
            ViewUtil.gone(checkableViewHolder.dots);
        }
        if (DeviceUtils.isTablet(this.context)) {
            holder.wholeRow.setActivated(isSelected);
        }
        holder.setAlpha(isUserDisabled ? 0.35f : 1.0f);
    }

    private boolean isLimitReached() {
        int maxSelectedCount = this.selectedUsersParams == null ? 0 : this.selectedUsersParams.getMaxSelectedCount();
        if (maxSelectedCount <= 0 || this.selectedIds.size() < maxSelectedCount) {
            return false;
        }
        return true;
    }

    private void updateNameBlock(UserInfoViewHolder holder, UserInfo user) {
        holder.textName.setText(user.getConcatName());
        if (TextUtils.isEmpty(user.getTag())) {
            holder.textInclude.setVisibility(8);
            return;
        }
        holder.textInclude.setText(user.getTag());
        holder.textInclude.setVisibility(0);
    }

    private void updateGenderImage(UserInfoViewHolder holder, UserInfo user) {
        holder.avatar.setUser(user);
        ImageViewManager.getInstance().displayImage(user.picUrl, holder.avatar, user.genderType == UserGenderType.MALE, this.imageLoadBlocker);
    }

    public UserInfoViewHolder newViewHolder(View view) {
        UserInfoViewHolder viewHolder = (this.mode == SelectionsMode.MULTI || this.mode == SelectionsMode.MEDIA_TOPICS) ? new CheckableViewHolder(view) : new UserInfoViewHolder(view);
        view.setTag(viewHolder);
        ViewUtil.setVisibility(viewHolder.dots, this.dotsEnabled);
        if (this.dotsEnabled) {
            int d = this.dimenDotsTouchDelegateExtend;
            ViewUtil.setTouchDelegate(viewHolder.dots, d, d, d, d);
            viewHolder.dots.setOnClickListener(this.dotsClickListener);
        }
        return viewHolder;
    }

    public View newView(ViewGroup parent) {
        return LocalizationManager.inflate(this.context, 2130903254, parent, false);
    }

    public UserInfoViewHolder onCreateViewHolder(ViewGroup parent) {
        return newViewHolder(newView(parent));
    }

    private void updateOnline(UserInfoViewHolder holder, UserInfo user) {
        Utils.updateOnlineView(holder.onlineView, Utils.onlineStatus(user));
        if (this.showLastOnline) {
            Utils.setTextViewTextWithVisibility(holder.textInclude, DateFormatter.formatDeltaTimePast(this.context, user.lastOnline, false, false));
        }
    }

    public void setSelectionUser(String selectionUser) {
        this.selectionUser = selectionUser;
    }

    public void updateEnabledIds(Collection<String> enabledIds) {
        updateSelectionParams(new UserEnabledSelectionParams(this.selectedUsersParams, enabledIds));
    }

    private void updateSelectionParams(UsersSelectionParams params) {
        this.selectedUsersParams = params;
    }

    public void toggleSelectedUser(String uid) {
        if (this.selectedIds.contains(uid)) {
            this.selectedIds.remove(uid);
        } else {
            this.selectedIds.add(uid);
        }
    }

    public void setUserSelected(String uid, boolean isSelected) {
        if (isSelected) {
            this.selectedIds.add(uid);
        } else {
            this.selectedIds.remove(uid);
        }
    }

    public Set<String> getSelectedIds() {
        if (this.mode == SelectionsMode.MULTI || this.mode == SelectionsMode.MEDIA_TOPICS) {
            return this.selectedIds;
        }
        return Collections.emptySet();
    }

    public UsersSelectionParams getSelectionParams() {
        if (this.mode == SelectionsMode.MULTI || this.mode == SelectionsMode.MEDIA_TOPICS) {
            return this.selectedUsersParams;
        }
        return new UsersSelectionParams();
    }

    public void setContextMenuOpenListener(ContextMenuOpenListener contextMenuOpenListener) {
        this.contextMenuOpenListener = contextMenuOpenListener;
    }
}

package ru.ok.android.ui.adapters.friends;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.ui.adapters.friends.UserInfosController.ContextMenuOpenListener;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UserInfosController.UserInfoViewHolder;
import ru.ok.android.ui.adapters.friends.UserInfosController.UserInfosControllerListener;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DividerItem;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AvatarImageView.OnClickToUserImageListener;
import ru.ok.android.ui.dialogs.UserDoActionBox;
import ru.ok.android.ui.users.CursorSwapper;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.SingleOnChangeRecyclerAdapterDataObserver;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.model.UserInfo;

public class UsersInfoCursorAdapter extends BaseUserInfoCursorAdapter implements SectionIndexer, ItemClickListenerControllerProvider, ContextMenuOpenListener, OnItemClickListener, CursorSwapper {
    private static final Object PAYLOAD_SELECTION;
    private final Activity activity;
    private AlphabetIndexer alphaIndexer;
    private final UserInfosController controller;
    protected final RecyclerItemClickListenerController itemClickListenerController;
    private int itemCountEnabled;
    private final AdapterDataObserver itemIsEnabledSupportObserver;
    private ArrayList<Integer> itemsEnabledAdapterPositions;
    private final boolean showSeparator;
    private UserInfoItemClickListener userInfoItemClickListener;

    public interface UsersInfoCursorAdapterListener extends OnClickToUserImageListener {
    }

    public interface UserInfoItemClickListener {
        void onUserItemClick(View view, int i, UserInfo userInfo);
    }

    /* renamed from: ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter.1 */
    class C05841 extends SingleOnChangeRecyclerAdapterDataObserver {
        C05841() {
        }

        public void onDataSetChanged() {
            int itemCount = super.getItemCount();
            UsersInfoCursorAdapter.this.itemCountEnabled = 0;
            UsersInfoCursorAdapter.this.itemsEnabledAdapterPositions.clear();
            for (int i = 0; i < itemCount; i++) {
                if (UsersInfoCursorAdapter.this.isEnabled(i)) {
                    UsersInfoCursorAdapter.this.itemsEnabledAdapterPositions.add(Integer.valueOf(i));
                    UsersInfoCursorAdapter.this.itemCountEnabled = UsersInfoCursorAdapter.this.itemCountEnabled + 1;
                }
            }
        }
    }

    static {
        PAYLOAD_SELECTION = new Object();
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }

    public void onItemClick(View view, int position) {
        if (this.userInfoItemClickListener != null) {
            if (!this.showSeparator || position % 2 == 0) {
                this.userInfoItemClickListener.onUserItemClick(view, position, UsersStorageFacade.cursor2User((Cursor) getItem(((Integer) this.itemsEnabledAdapterPositions.get(getUserInfoPosition(position))).intValue())));
            }
        }
    }

    public UsersInfoCursorAdapter(Activity activity, Cursor c, boolean doShowSelection, SelectionsMode mode, UsersSelectionParams selectedUsersParams, ArrayList<String> selectedIds, UsersInfoCursorAdapterListener avatarListener, UserInfosControllerListener listener) {
        this(activity, c, doShowSelection, mode, selectedUsersParams, selectedIds, avatarListener, listener, true, true, false);
    }

    public UsersInfoCursorAdapter(Activity activity, Cursor c, boolean doShowSelection, SelectionsMode mode, UsersSelectionParams selectedUsersParams, ArrayList<String> selectedIds, UsersInfoCursorAdapterListener avatarListener, UserInfosControllerListener listener, boolean dotsEnabled, boolean showSeparator, boolean showLastOnline) {
        super(c, true);
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.itemCountEnabled = 0;
        this.itemsEnabledAdapterPositions = new ArrayList();
        this.itemIsEnabledSupportObserver = new C05841();
        this.activity = activity;
        this.controller = new UserInfosController(activity, avatarListener, listener, doShowSelection, mode, selectedUsersParams, selectedIds, dotsEnabled, showLastOnline);
        this.controller.setContextMenuOpenListener(this);
        this.showSeparator = showSeparator;
        this.itemClickListenerController.addItemClickListener(this);
        registerAdapterDataObserver(this.itemIsEnabledSupportObserver);
    }

    public OnScrollListener getScrollBlocker() {
        return this.controller.getScrollBlocker();
    }

    private void bindUserInfo(ViewHolder holder, int adapterPosition, int cursorEnabledPosition) {
        this.controller.bindView(adapterPosition, (UserInfoViewHolder) holder, UsersStorageFacade.cursor2User((Cursor) getItem(((Integer) this.itemsEnabledAdapterPositions.get(cursorEnabledPosition)).intValue())), getItemCount());
    }

    private void bindUserInfoSelection(ViewHolder holder, int adapterPosition, int cursorEnabledPosition) {
        this.controller.bindViewState(adapterPosition, (UserInfoViewHolder) holder, UsersStorageFacade.cursor2User((Cursor) getItem(((Integer) this.itemsEnabledAdapterPositions.get(cursorEnabledPosition)).intValue())), getItemCount());
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!this.showSeparator) {
            bindUserInfo(holder, position, position);
        } else if (position % 2 == 0) {
            bindUserInfo(holder, position, position / 2);
        }
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public void onBindViewHolder(ViewHolder holder, int position, List payloads) {
        if (payloads == null || !payloads.contains(PAYLOAD_SELECTION)) {
            super.onBindViewHolder(holder, position, payloads);
        } else if (!this.showSeparator) {
            bindUserInfoSelection(holder, position, position);
        } else if (position % 2 == 0) {
            bindUserInfoSelection(holder, position, position / 2);
        }
    }

    public int getUserInfoPosition(int position) {
        return this.showSeparator ? position / 2 : position;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (2131624299 == viewType) {
            return this.controller.onCreateViewHolder(parent);
        }
        if (2131624300 == viewType) {
            return new CardViewHolder(DividerItem.newView(parent));
        }
        return null;
    }

    public void setSelectionUserId(String userId) {
        Set<String> wasSelectedIds = this.controller.getSelectedIds();
        this.controller.setSelectionUser(userId);
        if (userId != null) {
            notifyItemChangedByUserId(userId);
        } else if (wasSelectedIds != null) {
            for (String id : wasSelectedIds) {
                notifyItemChangedByUserId(id);
            }
        }
    }

    private int getUidNotifyPosition(String userId) {
        int i = 0;
        while (i < this.itemsEnabledAdapterPositions.size()) {
            Cursor c = (Cursor) getItem(((Integer) this.itemsEnabledAdapterPositions.get(i)).intValue());
            int adapterPosition = i;
            if (!userId.equals(c.getString(c.getColumnIndex("user_id")))) {
                i++;
            } else if (this.showSeparator) {
                return adapterPosition * 2;
            } else {
                return adapterPosition;
            }
        }
        return -1;
    }

    private void notifyItemChangedByUserId(String userId) {
        int pos = getUidNotifyPosition(userId);
        if (pos != -1) {
            notifyItemChanged(pos);
        }
    }

    public void updateEnabledIds(Collection<String> enabledIds) {
        this.controller.updateEnabledIds(enabledIds);
        notifyDataSetChanged();
    }

    public void toggleUserSelection(String uid) {
        this.controller.toggleSelectedUser(uid);
        int notifyPosition = getUidNotifyPosition(uid);
        if (notifyPosition != -1) {
            notifyItemChanged(notifyPosition, PAYLOAD_SELECTION);
        }
    }

    public void setUserSelected(String uid, boolean isSelected) {
        Set<String> selectedIds = this.controller.getSelectedIds();
        boolean oldIsSelected = selectedIds != null && selectedIds.contains(uid);
        if (isSelected != oldIsSelected) {
            this.controller.setUserSelected(uid, isSelected);
            notifyItemChangedByUserId(uid);
        }
    }

    public Set<String> getSelectedIds() {
        return this.controller.getSelectedIds();
    }

    public UsersSelectionParams getSelectionParams() {
        return this.controller.getSelectionParams();
    }

    public boolean isEnabled(int position) {
        Cursor c = (Cursor) getItem(position);
        return this.controller.isEnabled(c.getString(c.getColumnIndex("user_id")));
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            this.alphaIndexer = new AlphabetIndexer(newCursor, newCursor.getColumnIndex("user_n_first_name"), createAlphabet(newCursor));
        }
        return super.swapCursor(newCursor);
    }

    private static String createAlphabet(Cursor newCursor) {
        Set<Character> set = new TreeSet();
        while (newCursor.moveToNext()) {
            int index = newCursor.getColumnIndex("user_n_first_name");
            if (index < 0) {
                break;
            }
            String name = TranslateNormalizer.normalizeText4Sorting(newCursor.getString(index));
            if (name.length() > 0) {
                set.add(Character.valueOf(name.charAt(0)));
            }
        }
        newCursor.moveToPosition(-1);
        StringBuilder builder = new StringBuilder();
        for (Character ch : set) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public int getPositionForSection(int section) {
        if (this.alphaIndexer == null) {
            return 0;
        }
        int position = this.alphaIndexer.getPositionForSection(section);
        Logger.m172d("selection = " + section + "  " + position + "  " + this.alphaIndexer.getSections().length);
        return position;
    }

    public int getSectionForPosition(int position) {
        if (this.alphaIndexer == null) {
            return 0;
        }
        Logger.m172d("position = " + position + "  " + this.alphaIndexer.getSectionForPosition(position));
        return this.alphaIndexer.getSectionForPosition(position);
    }

    public void showUserContextMenu(UserInfo user, View anchor) {
        UserDoActionBox doBox = new UserDoActionBox(this.activity, user, anchor);
        doBox.setOnGoToMainPageSelectListener(this.onGoToMainPageSelectListener);
        doBox.setOnCallUserSelectListener(this.onCallUserSelectListener);
        doBox.show();
    }

    public Object[] getSections() {
        if (this.alphaIndexer != null) {
            return this.alphaIndexer.getSections();
        }
        return new String[]{""};
    }

    public void onContextMenuButtonClick(UserInfo user, View contextMenuButton) {
        showUserContextMenu(user, contextMenuButton);
    }

    public int getItemCount() {
        int count = this.itemCountEnabled;
        return (!this.showSeparator || count <= 1) ? count : (count * 2) - 1;
    }

    public int getUsersCount() {
        return this.itemCountEnabled;
    }

    public int getItemViewType(int position) {
        return (this.showSeparator && position % 2 == 1) ? 2131624300 : 2131624299;
    }

    public void setUserInfoItemClickListener(UserInfoItemClickListener userInfoItemClickListener) {
        this.userInfoItemClickListener = userInfoItemClickListener;
    }
}

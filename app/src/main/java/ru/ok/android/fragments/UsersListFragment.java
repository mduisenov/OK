package ru.ok.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UserInfosController.UserInfosControllerListener;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter.UserInfoItemClickListener;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter.UsersInfoCursorAdapterListener;
import ru.ok.android.ui.dialogs.UsersDoBase.OnCallUserSelectListener;
import ru.ok.android.ui.dialogs.UsersDoBase.OnGoToMainPageSelectListener;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.UserInfo;

public abstract class UsersListFragment extends RefreshableContentCursorRecyclerFragment<UsersInfoCursorAdapter> implements UserInfosControllerListener, UsersInfoCursorAdapterListener, OnCallUserSelectListener, OnGoToMainPageSelectListener {
    protected boolean doShowSelection;
    protected UsersListFragmentListener listener;
    protected ArrayList<String> selectedIds;

    public interface UsersListFragmentListener {
        void onListCrated(RecyclerView recyclerView);

        void onUserSelected(String str);
    }

    /* renamed from: ru.ok.android.fragments.UsersListFragment.1 */
    class C02911 implements UserInfoItemClickListener {
        final /* synthetic */ boolean val$isMultiSelectMode;

        C02911(boolean z) {
            this.val$isMultiSelectMode = z;
        }

        public void onUserItemClick(View view, int position, UserInfo user) {
            if (user != null) {
                String uid = user.uid;
                UsersListFragment.this.setSelectedUser(uid);
                UsersListFragment.this.notifyOnSelectedUser(uid);
                if (this.val$isMultiSelectMode && UsersListFragment.this.adapter != null) {
                    ((UsersInfoCursorAdapter) UsersListFragment.this.adapter).toggleUserSelection(uid);
                }
            }
        }
    }

    protected static void initArguments(Bundle arguments, boolean doShowSelection, String selectedUserId, SelectionsMode selectionsMode, UsersSelectionParams selectionParams) {
        arguments.putBoolean("show_selection", doShowSelection);
        arguments.putString("select_mode", selectionsMode.name());
        arguments.putParcelable("selection_params", selectionParams);
        if (!TextUtils.isEmpty(selectedUserId)) {
            arguments.putString("user_id", selectedUserId);
        }
    }

    protected SelectionsMode getSelectionsMode() {
        String name = getArguments() == null ? "" : getArguments().getString("select_mode");
        return TextUtils.isEmpty(name) ? SelectionsMode.SINGLE : SelectionsMode.valueOf(name);
    }

    public UsersSelectionParams getSelectionParams() {
        return (UsersSelectionParams) getArguments().getParcelable("selection_params");
    }

    public void updateEnabledIds(Collection<String> enabledIds) {
        if (this.adapter != null) {
            ((UsersInfoCursorAdapter) this.adapter).updateEnabledIds(enabledIds);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.selectedIds = savedInstanceState.getStringArrayList("selected_ids");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.doShowSelection = getArguments().getBoolean("show_selection", false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setListener(UsersListFragmentListener listener) {
        this.listener = listener;
    }

    protected void notifyOnSelectedUser(String uid) {
        UsersListFragmentListener listener = this.listener;
        if (listener != null) {
            listener.onUserSelected(uid);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(2131493208);
        ((UsersInfoCursorAdapter) this.adapter).setUserInfoItemClickListener(new C02911(getSelectionsMode() != SelectionsMode.SINGLE));
        if (this.listener != null) {
            this.listener.onListCrated(this.recyclerView);
        }
    }

    public String getSelectedUserId() {
        return getArguments().getString("user_id");
    }

    public void setSelectedUser(String uid) {
        getArguments().putString("user_id", uid);
        if (this.doShowSelection && this.adapter != null) {
            ((UsersInfoCursorAdapter) this.adapter).setSelectionUserId(uid);
        }
    }

    public Set<String> getSelectedIds() {
        SelectionsMode mode = getSelectionsMode();
        if (mode == SelectionsMode.MULTI || mode == SelectionsMode.MEDIA_TOPICS) {
            return ((UsersInfoCursorAdapter) this.adapter).getSelectedIds();
        }
        return Collections.emptySet();
    }

    protected void onContentChanged() {
        Logger.m173d("[%s]", getClass().getSimpleName());
        if (this.adapter != null) {
            ((UsersInfoCursorAdapter) this.adapter).notifyDataSetChanged();
        }
    }

    protected UsersInfoCursorAdapter createRecyclerAdapter() {
        boolean isMultiSelect;
        boolean z;
        SelectionsMode selectionsMode = getSelectionsMode();
        if (selectionsMode == SelectionsMode.MEDIA_TOPICS || selectionsMode == SelectionsMode.MULTI) {
            isMultiSelect = true;
        } else {
            isMultiSelect = false;
        }
        Activity activity = getActivity();
        boolean z2 = this.doShowSelection;
        UsersSelectionParams selectionParams = getSelectionParams();
        ArrayList arrayList = this.selectedIds;
        if (isMultiSelect) {
            z = false;
        } else {
            z = true;
        }
        UsersInfoCursorAdapter adapter = new UsersInfoCursorAdapter(activity, null, z2, selectionsMode, selectionParams, arrayList, this, this, z, true, false);
        adapter.setOnGoToMainPageSelectListener(this);
        adapter.setOnCallUserSelectListener(this);
        if (this.doShowSelection) {
            adapter.setSelectionUserId(getSelectedUserId());
        }
        return adapter;
    }

    public void onGoToMainPageSelect(UserInfo userInfo, View view) {
        NavigationHelper.showUserInfo(getActivity(), userInfo.uid);
    }

    public void onCallUserSelect(UserInfo userInfo, View view) {
        NavigationHelper.onCallUser(getActivity(), userInfo.uid);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.adapter != null) {
            getArguments().putParcelable("selection_params", ((UsersInfoCursorAdapter) this.adapter).getSelectionParams());
            outState.putStringArrayList("selected_ids", new ArrayList(((UsersInfoCursorAdapter) this.adapter).getSelectedIds()));
        }
        super.onSaveInstanceState(outState);
    }

    public void onClickToUserImage(UserInfo user, View view) {
        NavigationHelper.showUserInfo(getActivity(), user.uid);
    }

    public void onUserSelectionChanged(boolean atLeastOneEnabledSelected) {
    }

    protected int getLayoutId() {
        if (DeviceUtils.isSmall(getContext()) || !DeviceUtils.isShowTabbar()) {
            return super.getLayoutId();
        }
        return 2130903364;
    }
}

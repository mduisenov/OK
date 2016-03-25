package ru.ok.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter.UserInfoItemClickListener;
import ru.ok.android.ui.custom.MessageCheckBox;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.model.UserInfo;
import ru.ok.onelog.phonebook.FriendsByPhonebookEventFactory;
import ru.ok.onelog.phonebook.FriendsByPhonebookMethod;

public class RecommendedUsersFragment extends UsersListFragment implements OnMenuItemClickListener, UserInfoItemClickListener {
    public static ArrayList<String> savedSelectedIds;
    private int count;
    private MenuItem doneItem;
    private View headerView;
    private ArrayList<String> ids;
    private boolean isMultiSelectMode;
    private boolean isWaitingForResult;
    private ArrayList<String> listAllUsersIds;
    private int loaderId;
    private boolean permissionAlreadyAsked;
    private MessageCheckBox selectAllCheckBox;
    private View swipeRefreshView;
    private TextView usersCount;
    private UsersInfoCursorAdapter usersInfoCursorAdapter;

    /* renamed from: ru.ok.android.fragments.RecommendedUsersFragment.1 */
    class C02841 implements OnStubButtonClickListener {
        C02841() {
        }

        public void onStubButtonClick(Type type) {
            RecommendedUsersFragment.this.requestUsers();
        }
    }

    /* renamed from: ru.ok.android.fragments.RecommendedUsersFragment.2 */
    class C02852 implements OnTouchListener {
        C02852() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 1) {
                RecommendedUsersFragment.this.selectAllCheckBox.toggle();
                RecommendedUsersFragment.this.setAllSelected(RecommendedUsersFragment.this.selectAllCheckBox.isChecked());
                RecommendedUsersFragment.this.updateDoneButton();
            }
            return true;
        }
    }

    public RecommendedUsersFragment() {
        this.isMultiSelectMode = true;
        this.isWaitingForResult = true;
        this.permissionAlreadyAsked = false;
    }

    static {
        savedSelectedIds = null;
    }

    private void updateLayoutInfoAndSelectAll(Cursor cursor) {
        this.count = cursor.getCount();
        if (this.count == 0) {
            this.headerView.setVisibility(8);
            this.swipeRefreshView.setVisibility(8);
        } else {
            this.headerView.setVisibility(0);
            this.swipeRefreshView.setVisibility(0);
        }
        this.listAllUsersIds = new ArrayList();
        if (this.usersCount != null) {
            int resId = StringUtils.plural((long) this.count, 2131166807, 2131166808, 2131166809);
            this.usersCount.setText(getStringLocalized(resId, Integer.valueOf(this.count)));
            while (cursor.moveToNext()) {
                this.listAllUsersIds.add(cursor.getString(cursor.getColumnIndex("user_id")));
            }
            Iterator i$;
            if (this.selectedIds == null) {
                this.selectAllCheckBox.setChecked(true);
                i$ = this.listAllUsersIds.iterator();
                while (i$.hasNext()) {
                    this.usersInfoCursorAdapter.setUserSelected((String) i$.next(), true);
                }
            } else {
                if (this.selectedIds.size() == this.count) {
                    this.selectAllCheckBox.setChecked(true);
                }
                i$ = this.selectedIds.iterator();
                while (i$.hasNext()) {
                    this.usersInfoCursorAdapter.setUserSelected((String) i$.next(), true);
                }
            }
        }
        updateDoneButton();
    }

    @Subscribe(on = 2131623946, to = 2131624219)
    public void getRecommendedFriendsIds(BusEvent event) {
        Bundle bundle = event.bundleOutput;
        if (event.resultCode != -1 || bundle == null) {
            ErrorType errorType = ErrorType.from(bundle);
            this.emptyView.setState(State.LOADED);
            this.emptyView.setVisibility(0);
            if (errorType == ErrorType.NO_INTERNET) {
                this.emptyView.setType(Type.NO_INTERNET);
                return;
            } else {
                this.emptyView.setType(Type.ERROR);
                return;
            }
        }
        getLoaderManager().restartLoader(this.loaderId, bundle, this);
        if (bundle.getInt("COUNT", 0) == 0) {
            this.emptyView.setVisibility(0);
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(Type.SEARCH);
            this.isWaitingForResult = false;
            if (this.headerView != null) {
                this.headerView.setVisibility(8);
            }
        }
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        updateLayoutInfoAndSelectAll(cursor);
        super.onLoadFinished((Loader) cursorLoader, cursor);
        if (this.isWaitingForResult) {
            this.emptyView.setState(State.LOADING);
        } else {
            this.emptyView.setState(State.LOADED);
        }
    }

    public static Fragment newInstance(Bundle arguments) {
        Fragment fragment = new RecommendedUsersFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    protected void initRefresh(View fragmentMainView) {
        super.initRefresh(fragmentMainView);
    }

    public void onClickToUserImage(UserInfo user, View view) {
        onUserItemClick(user);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.emptyView.setType(Type.SEARCH);
        this.emptyView.setButtonClickListener(new C02841());
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689494, menu)) {
            this.doneItem = menu.findItem(2131625470);
            this.doneItem.setEnabled(true);
            this.doneItem.setOnMenuItemClickListener(this);
            updateDoneButton();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (PermissionUtils.getGrantResult(grantResults) == 0) {
            requestUsers();
        } else {
            this.isWaitingForResult = false;
            getLoaderManager().restartLoader(this.loaderId, new Bundle(), this);
        }
    }

    private void requestUsers() {
        if (this.emptyView != null) {
            this.emptyView.setState(State.LOADING);
        }
        GlobalBus.send(2131624065, new BusEvent());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.selectedIds == null && savedSelectedIds != null) {
            this.selectedIds = savedSelectedIds;
        }
        setHasOptionsMenu(true);
        Activity activity = getActivity();
        if (VERSION.SDK_INT >= 23) {
            if (PermissionUtils.checkSelfPermission(activity, "android.permission.READ_CONTACTS") != 0) {
                if (this.permissionAlreadyAsked) {
                    requestUsers();
                    return;
                }
                this.permissionAlreadyAsked = true;
                requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 1);
                return;
            }
        }
        requestUsers();
    }

    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        String selection;
        Uri uri = Users.getContentUri();
        this.loaderId = loaderId;
        this.ids = bundle.getStringArrayList("USER_IDS");
        if (this.ids == null || this.ids.size() <= 0) {
            selection = "user_id = -1";
        } else {
            selection = "user_id in ('" + TextUtils.join("','", this.ids) + "')";
        }
        return new CursorLoader(getActivity(), uri, null, selection, null, "user_n_first_name, user_n_last_name");
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        return null;
    }

    public boolean handleBack() {
        goToMainActivity();
        return super.handleBack();
    }

    protected UsersInfoCursorAdapter createRecyclerAdapter() {
        this.usersInfoCursorAdapter = new UsersInfoCursorAdapter(getActivity(), null, true, SelectionsMode.MULTI, new UsersSelectionParams(), new ArrayList(), this, this, false, true, false);
        this.usersInfoCursorAdapter.setOnCallUserSelectListener(this);
        return this.usersInfoCursorAdapter;
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166018);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ItemAnimator itemAnimator = this.recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setSupportsChangeAnimations(false);
        }
        this.selectAllCheckBox = (MessageCheckBox) view.findViewById(2131625161);
        OnTouchListener onHeaderClickListener = new C02852();
        this.headerView = view.findViewById(2131625158);
        this.headerView.setOnTouchListener(onHeaderClickListener);
        this.selectAllCheckBox.setOnTouchListener(onHeaderClickListener);
        this.usersCount = (TextView) view.findViewById(2131625159);
        this.usersCount.setText(getStringLocalized(2131166807, Integer.valueOf(this.usersInfoCursorAdapter.getUsersCount())));
        if (this.emptyView != null) {
            this.emptyView.setType(Type.SEARCH);
            this.emptyView.setState(State.LOADING);
        }
        this.swipeRefreshView = view.findViewById(2131624611);
        this.swipeRefreshView.setVisibility(8);
        this.swipeRefreshView.setEnabled(false);
        this.usersInfoCursorAdapter.setUserInfoItemClickListener(this);
    }

    private void onUserItemClick(UserInfo user) {
        String uid = user.uid;
        setSelectedUser(uid);
        notifyOnSelectedUser(uid);
        if (this.isMultiSelectMode && this.usersInfoCursorAdapter != null) {
            this.usersInfoCursorAdapter.toggleUserSelection(uid);
        }
        this.selectAllCheckBox.setChecked(getSelectedCount() == this.count);
        updateDoneButton();
    }

    public int getSelectedCount() {
        int size = 0;
        if (this.usersInfoCursorAdapter != null) {
            for (String id : this.usersInfoCursorAdapter.getSelectedIds()) {
                if (id != null) {
                    size++;
                }
            }
        }
        return size;
    }

    private void updateDoneButton() {
        if (this.doneItem != null && this.usersInfoCursorAdapter != null) {
            if (getSelectedCount() == 0) {
                this.doneItem.setTitle(getStringLocalized(2131166569));
                this.doneItem.setIcon(null);
                return;
            }
            this.doneItem.setTitle(getStringLocalized(2131166017));
            this.doneItem.setIcon(2130837587);
        }
    }

    private void setAllSelected(boolean isSelected) {
        if (this.usersInfoCursorAdapter != null && this.listAllUsersIds != null) {
            Iterator i$ = this.listAllUsersIds.iterator();
            while (i$.hasNext()) {
                this.usersInfoCursorAdapter.setUserSelected((String) i$.next(), isSelected);
            }
        }
    }

    public int getCount() {
        return this.count;
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (this.usersInfoCursorAdapter != null) {
            if (getSelectedCount() > 0) {
                if (getSelectedCount() == this.count) {
                    OneLog.log(FriendsByPhonebookEventFactory.get(FriendsByPhonebookMethod.invited_all));
                } else {
                    OneLog.log(FriendsByPhonebookEventFactory.get(FriendsByPhonebookMethod.invited_some));
                }
                ArrayList<String> ids = new ArrayList(this.usersInfoCursorAdapter.getSelectedIds());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("USER_IDS", ids);
                GlobalBus.send(2131624123, new BusEvent(bundle));
            } else {
                OneLog.log(FriendsByPhonebookEventFactory.get(FriendsByPhonebookMethod.skipped));
            }
        }
        goToMainActivity();
        getActivity().finish();
        return true;
    }

    private void goToMainActivity() {
        Intent intent = NavigationHelper.createIntent(getActivity(), Tag.feed, Source.other_user);
        intent.setFlags(268468224);
        NavigationHelper.startActivityWithoutDuplicate(getContext(), intent);
    }

    protected int getLayoutId() {
        return 2130903362;
    }

    public void onBackPressed() {
        if (getSelectedCount() == getCount()) {
            OneLog.log(FriendsByPhonebookEventFactory.get(FriendsByPhonebookMethod.closed_without_choice));
        } else {
            OneLog.log(FriendsByPhonebookEventFactory.get(FriendsByPhonebookMethod.closed_with_choice));
        }
        Bundle arguments = getArguments();
        boolean backToPreviousActivity = false;
        if (arguments != null) {
            backToPreviousActivity = arguments.getBoolean("KEY_BACK_TO_PREVIOUS_ACTIVITY", false);
        }
        if (backToPreviousActivity) {
            savedSelectedIds = new ArrayList(this.usersInfoCursorAdapter.getSelectedIds());
            return;
        }
        savedSelectedIds = null;
        goToMainActivity();
    }

    public void onUserItemClick(View view, int position, UserInfo userInfo) {
        if (userInfo != null) {
            onUserItemClick(userInfo);
        }
    }
}

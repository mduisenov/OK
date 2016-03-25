package ru.ok.android.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.users.fragments.FriendsListFilteredFragment;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.UserInfo;

public final class FriendsChooseForConversationFragment extends FriendsListFilteredFragment implements OnCloseListener {

    /* renamed from: ru.ok.android.fragments.FriendsChooseForConversationFragment.1 */
    class C02761 extends OnScrollListener {
        C02761() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                KeyBoardUtils.hideKeyBoard(FriendsChooseForConversationFragment.this.getActivity());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    public static FriendsChooseForConversationFragment newInstance() {
        Bundle args = new Bundle();
        FriendsChooseForConversationFragment result = new FriendsChooseForConversationFragment();
        result.setArguments(args);
        return result;
    }

    public void onGoToMainPageSelect(UserInfo userInfo, View view) {
        NavigationHelper.showUserInfo(getActivity(), userInfo.uid);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(2131625463));
        searchView.setIconified(false);
        searchView.setOnCloseListener(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((RecyclerView) view.findViewById(2131624731)).addOnScrollListener(new C02761());
        return view;
    }

    protected String getTitle() {
        return getString(2131166479);
    }

    protected boolean isDoneButtonActive() {
        return false;
    }

    protected void notifyOnSelectedUser(String uid) {
        super.notifyOnSelectedUser(uid);
        if (this.listener == null) {
            NavigationHelper.showMessagesForUser(getActivity(), uid);
        }
    }

    public boolean onClose() {
        NavigationHelper.finishActivity(getActivity());
        return false;
    }

    protected int getLayoutId() {
        return 2130903365;
    }
}

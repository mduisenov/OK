package ru.ok.android.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.friends.FriendsFilterProcessor;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.users.fragments.FriendsListFilteredFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.java.api.request.friends.FriendsFilter;

public class FriendsListWithPrivacyFilterFragment extends FriendsListFilteredFragment {

    static class RefreshableListFragmentHelperWrapper extends RefreshableRecyclerFragmentHelper {
        private final RefreshableRecyclerFragmentHelper targetHelper;

        public RefreshableListFragmentHelperWrapper(RefreshableRecyclerFragmentHelper targetHelper) {
            super(null, null, null, 0);
            this.targetHelper = targetHelper;
        }

        public boolean onStartRefresh(boolean manual) {
            return this.targetHelper.onStartRefresh(manual);
        }

        public void notifyRefreshSuccessful(Boolean isEmpty) {
            this.targetHelper.notifyRefreshSuccessful(isEmpty);
        }

        public void notifyRefreshFailed(ErrorType error) {
            this.targetHelper.notifyRefreshFailed(error);
        }

        public RefreshProvider getRefreshProvider() {
            return this.targetHelper.getRefreshProvider();
        }

        public void setRefreshProvider(RefreshProvider refreshProvider) {
            this.targetHelper.setRefreshProvider(refreshProvider);
        }

        public <TAdapter extends Adapter & ImageBlockerRecyclerProvider> void onFragmentCreateView(View mainFragmentView, TAdapter adapter) {
            this.targetHelper.onFragmentCreateView(mainFragmentView, adapter);
        }

        protected RecyclerView initListView(View mainView, int resEmptyText) {
            return this.targetHelper.initListView(mainView, resEmptyText);
        }
    }

    /* renamed from: ru.ok.android.fragments.FriendsListWithPrivacyFilterFragment.1 */
    class C02771 extends RefreshableListFragmentHelperWrapper {
        final /* synthetic */ FriendsFilter val$filter;

        C02771(RefreshableRecyclerFragmentHelper x0, FriendsFilter friendsFilter) {
            this.val$filter = friendsFilter;
            super(x0);
        }

        public boolean onStartRefresh(boolean manual) {
            boolean value = super.onStartRefresh(manual);
            FriendsListWithPrivacyFilterFragment.sendFriendsFilterRequest(this.val$filter);
            return value;
        }
    }

    public static void fillArgs(Bundle args, FriendsFilter filter) {
        args.putInt("friends_filter", filter.ordinal());
    }

    protected FriendsFilter getFriendsFilter() {
        Bundle args = getArguments();
        if (args != null) {
            int ordinal = args.getInt("friends_filter", -1);
            if (ordinal != -1) {
                return FriendsFilter.values()[ordinal];
            }
        }
        return null;
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        FriendsFilter filter = getFriendsFilter();
        RefreshableRecyclerFragmentHelper targetHelper = super.createRefreshHelper();
        if (filter != null) {
            return new C02771(targetHelper, filter);
        }
        Logger.m184w("Filter not specified!");
        return targetHelper;
    }

    public static void sendFriendsFilterRequest(FriendsFilter filter) {
        Logger.m172d("requesting for friends.filter...");
        Bundle args = new Bundle();
        FriendsFilterProcessor.fillInputBundle(args, filter);
        GlobalBus.send(2131623981, new BusEvent(args));
    }
}

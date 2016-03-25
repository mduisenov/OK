package ru.ok.android.ui.users.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.cards.listcard.CardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemType;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.utils.ItemCountChangedDataObserver;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.model.UserInfo;
import ru.ok.model.guest.UserInfoGuest;
import ru.ok.model.guest.UsersResult;

public class FragmentGuest extends BaseRefreshFragment implements OnStubButtonClickListener {
    public static int COLOR_GRAY;
    private Runnable afterLayoutSetDataRunnable;
    private GuestAdapter cardListAdapter;
    private SmartEmptyViewAnimated emptyView;
    private RecyclerView listView;
    private LinearLayoutManager recyclerLayoutManager;
    private RunGuest runGuest;

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentGuest.1 */
    class C12941 implements Runnable {
        C12941() {
        }

        public void run() {
            if (FragmentGuest.this.recyclerLayoutManager != null && FragmentGuest.this.recyclerLayoutManager.findLastVisibleItemPosition() == FragmentGuest.this.cardListAdapter.getItemCount() - 1 && FragmentGuest.this.runGuest.isHasMore) {
                FragmentGuest.this.runGuest.loadNext();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentGuest.2 */
    class C12952 extends ItemCountChangedDataObserver {
        C12952() {
        }

        public void onItemCountMayChange() {
            if (FragmentGuest.this.emptyView != null) {
                FragmentGuest.this.emptyView.setType(Type.GUESTS);
                FragmentGuest.this.emptyView.setVisibility(FragmentGuest.this.cardListAdapter.getItemCount() == 0 ? 0 : 8);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentGuest.3 */
    class C12963 implements OnItemClickListener {
        C12963() {
        }

        public void onItemClick(View view, int position) {
            UserInfo item = FragmentGuest.this.cardListAdapter.getItem(position);
            if (item instanceof UserInfo) {
                NavigationHelper.showUserInfo(FragmentGuest.this.getActivity(), item.uid);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentGuest.4 */
    class C12974 extends OnScrollListener {
        C12974() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (FragmentGuest.this.recyclerLayoutManager.findLastVisibleItemPosition() > recyclerView.getAdapter().getItemCount() - 3) {
                FragmentGuest.this.runGuest.loadNext();
            }
        }
    }

    class GuestAdapter extends CardListAdapter {
        GuestAdapter(LocalizedActivity context) {
            super(context);
        }

        public boolean headerIsEnable() {
            return false;
        }

        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);
            if (type == 0) {
                return 13;
            }
            return type;
        }

        public void onBindViewHolder(CardViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (getItemViewType(position) == 13) {
                ((UserViewsHolder) holder).infoView.setTextColor(((UserInfoGuest) ((AbsListItem) this.mData.get(position)).getObject()).isNew ? -65536 : FragmentGuest.COLOR_GRAY);
            }
        }
    }

    class RunGuest implements Runnable {
        final List<Bundle> bundles;
        List<UserInfo> guestUsers;
        volatile boolean isHasMore;
        volatile boolean isLoading;
        public final CardItem progressItem;

        RunGuest() {
            this.progressItem = new CardItem().setType(CardItem.Type.progressBar);
            this.isLoading = false;
            this.isHasMore = true;
            this.guestUsers = new ArrayList();
            this.bundles = new ArrayList();
        }

        private void setHasMore(boolean isHasMore) {
            this.isHasMore = isHasMore;
            this.progressItem.setEnable(isHasMore);
        }

        public List<UserInfo> onGuestResult(BusEvent busEvent) {
            List<UserInfo> list;
            synchronized (this.bundles) {
                if (!this.bundles.isEmpty() && Utils.equalBundles(busEvent.bundleInput, (Bundle) this.bundles.get(this.bundles.size() - 1))) {
                    boolean z;
                    List<UserInfoGuest> guests = new ArrayList();
                    UsersResult guestResult = (UsersResult) busEvent.bundleOutput.getParcelable("key_guest_result");
                    if (busEvent.resultCode == -1 && guestResult.hasMore && !guestResult.users.isEmpty()) {
                        z = true;
                    } else {
                        z = false;
                    }
                    setHasMore(z);
                    if (busEvent.resultCode == -2) {
                        list = this.guestUsers;
                    } else {
                        if (!(guestResult.users == null || guestResult.users.isEmpty())) {
                            Iterator i$ = guestResult.users.iterator();
                            while (i$.hasNext()) {
                                guests.add((UserInfoGuest) i$.next());
                            }
                            this.guestUsers.addAll(guests);
                        }
                        if (this.isHasMore) {
                            Bundle bundle = new Bundle();
                            bundle.putString("key_anchor", guestResult.pagingAnchor);
                            this.bundles.add(bundle);
                        }
                    }
                }
                this.isLoading = false;
                list = this.guestUsers;
            }
            return list;
        }

        public void loadNext() {
            synchronized (this.bundles) {
                if (!(this.bundles.isEmpty() || this.isLoading || !this.isHasMore)) {
                    this.isLoading = true;
                    GlobalBus.send(2131623984, new BusEvent((Bundle) this.bundles.get(this.bundles.size() - 1)));
                }
            }
        }

        public void run() {
            synchronized (this.bundles) {
                this.bundles.clear();
                this.guestUsers.clear();
                this.isLoading = false;
                setHasMore(true);
                this.bundles.add(new Bundle());
                loadNext();
            }
        }
    }

    public FragmentGuest() {
        this.runGuest = new RunGuest();
        this.afterLayoutSetDataRunnable = new C12941();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.cardListAdapter = new GuestAdapter((LocalizedActivity) getActivity());
        loadGuestFirstPage();
    }

    protected int getLayoutId() {
        return 2130903564;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(getLayoutId(), null);
        this.listView = (RecyclerView) view.findViewById(2131624731);
        this.recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.listView.setLayoutManager(this.recyclerLayoutManager);
        if (!DeviceUtils.isSmall(getContext())) {
            this.listView.setBackgroundResource(2131493183);
        }
        this.listView.setAdapter(this.cardListAdapter);
        this.cardListAdapter.registerAdapterDataObserver(new C12952());
        this.cardListAdapter.getItemClickListenerController().addItemClickListener(new C12963());
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        if (this.emptyView != null) {
            this.emptyView.setState(State.LOADING);
            this.emptyView.setButtonClickListener(this);
        }
        this.listView.addOnScrollListener(new C12974());
        return view;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        List<CardItem> cards = new ArrayList();
        cards.add(new CardItem().setInfoList(this.runGuest.guestUsers));
        if (!this.runGuest.guestUsers.isEmpty()) {
            cards.add(this.runGuest.progressItem);
        }
        this.cardListAdapter.setData(cards);
    }

    public void onStubButtonClick(Type type) {
        loadGuestFirstPage();
    }

    public void onRefresh() {
        loadGuestFirstPage();
    }

    protected String getTitle() {
        return getStringLocalized(2131165969);
    }

    public void loadGuestFirstPage() {
        this.cardListAdapter.setData(new ArrayList(1));
        this.runGuest.run();
        if (this.emptyView != null) {
            this.emptyView.setState(State.LOADING);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624240)
    public final void onRemoveGuest(BusEvent busEvent) {
        String uid = busEvent.bundleOutput.getString("key_uid");
        for (UserInfo userInfo : this.runGuest.guestUsers) {
            if (TextUtils.equals(userInfo.uid, uid)) {
                this.runGuest.guestUsers.remove(userInfo);
                break;
            }
        }
        setData(this.runGuest.guestUsers);
    }

    private void setData(List<UserInfo> usersInfo) {
        List<CardItem> cards = new ArrayList();
        cards.add(new CardItem().setInfoList((List) usersInfo, ItemType.guest));
        if (!this.runGuest.guestUsers.isEmpty()) {
            cards.add(this.runGuest.progressItem);
        }
        this.cardListAdapter.setData(cards);
        this.listView.post(this.afterLayoutSetDataRunnable);
    }

    @Subscribe(on = 2131623946, to = 2131624236)
    public final void onGuestResult(BusEvent busEvent) {
        setData(this.runGuest.onGuestResult(busEvent));
        if (this.emptyView != null) {
            this.emptyView.setState(State.LOADED);
            if (busEvent.resultCode == -2) {
                this.emptyView.setVisibility(0);
                this.emptyView.setType(busEvent.bundleOutput.getBoolean("key_guest_error_type") ? Type.NO_INTERNET : Type.ERROR);
            }
        }
        if (this.refreshProvider != null) {
            this.refreshProvider.refreshCompleted();
        }
    }

    static {
        COLOR_GRAY = -6184543;
    }
}

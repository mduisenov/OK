package ru.ok.android.ui.presents.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.presents.DeletedPresentsManager;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.presents.adapter.TopMarginItemDecoration;
import ru.ok.android.ui.presents.adapter.UserPresentsAdapter;
import ru.ok.android.ui.presents.adapter.UserPresentsAdapter.Listener;
import ru.ok.android.ui.stream.list.StreamItemAdapter;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView.OnPlayTrackListener;
import ru.ok.android.ui.swiperefresh.OkSwipeRefreshLayout;
import ru.ok.android.ui.utils.LoadItemAdapter;
import ru.ok.android.ui.utils.RecyclerMergeAdapter;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusMusicHelper;
import ru.ok.android.utils.bus.BusPresentsHelper;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.menuitems.SlidingMenuHelper;
import ru.ok.java.api.request.presents.PresentsRequest.Direction;
import ru.ok.java.api.response.presents.PresentsResponse;
import ru.ok.model.UserInfo;
import ru.ok.model.presents.PresentInfo;
import ru.ok.model.wmf.Track;

public class UserPresentsFragment extends BaseRefreshFragment implements Listener, OnPlayTrackListener {
    private DeletedPresentsManager deletedPresentsManager;
    private Direction direction;
    private SmartEmptyViewAnimated emptyView;
    private boolean friendMode;
    private String lastAnchor;
    private LoadItemAdapter loadItemAdapter;
    private boolean noMoreItems;
    private final OnStubButtonClickListener onRetryClicked;
    private final OnScrollListener onScrollListener;
    private final PlayerStateHolder playerStateHolder;
    private UserPresentsAdapter presentsAdapter;
    private RecyclerView recyclerView;
    private OkSwipeRefreshLayout swipeRefreshLayout;
    private String token;
    private String userId;

    /* renamed from: ru.ok.android.ui.presents.fragment.UserPresentsFragment.1 */
    class C11741 extends OnScrollListener {
        C11741() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!UserPresentsFragment.this.loadItemAdapter.isLoading() && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 3 && !UserPresentsFragment.this.noMoreItems) {
                UserPresentsFragment.this.loadPresents(UserPresentsFragment.this.lastAnchor);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.presents.fragment.UserPresentsFragment.2 */
    class C11752 implements OnStubButtonClickListener {
        C11752() {
        }

        public void onStubButtonClick(Type type) {
            UserPresentsFragment.this.loadPresents(null);
        }
    }

    public UserPresentsFragment() {
        this.playerStateHolder = new PlayerStateHolder();
        this.onScrollListener = new C11741();
        this.onRetryClicked = new C11752();
    }

    @NonNull
    public static Bundle newArguments(@Nullable String userId, @NonNull Direction direction, @Nullable String token) {
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        args.putString("token", token);
        args.putSerializable("direction", direction);
        return args;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playerStateHolder.init();
    }

    public void onDestroy() {
        super.onDestroy();
        this.playerStateHolder.close();
    }

    public void onResume() {
        super.onResume();
        updateSubtitle();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean z;
        View v = LocalizationManager.inflate(getActivity(), getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) v.findViewById(C0263R.id.empty_view);
        this.recyclerView = (RecyclerView) v.findViewById(2131624731);
        this.swipeRefreshLayout = (OkSwipeRefreshLayout) v.findViewById(2131624611);
        this.emptyView.setButtonClickListener(this.onRetryClicked);
        this.userId = getArguments().getString("user_id");
        this.direction = (Direction) getArguments().getSerializable("direction");
        this.token = getArguments().getString("token");
        if (this.userId == null || this.userId.equals(OdnoklassnikiApplication.getCurrentUser().getId())) {
            z = false;
        } else {
            z = true;
        }
        this.friendMode = z;
        this.presentsAdapter = new UserPresentsAdapter(this.direction, getActivity(), this, this.playerStateHolder, this);
        this.loadItemAdapter = new LoadItemAdapter(getActivity());
        this.presentsAdapter.setFriendMode(this.friendMode);
        this.deletedPresentsManager = Storages.getInstance(getActivity(), OdnoklassnikiApplication.getCurrentUser().getId()).getDeletedPresentsManager();
        Adapter adapter = new RecyclerMergeAdapter(true).addAdapter(this.presentsAdapter).addAdapter(this.loadItemAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setOnScrollListener(this.onScrollListener);
        this.recyclerView.addItemDecoration(new TopMarginItemDecoration((int) Utils.dipToPixels(4.0f)));
        restoreState(savedInstanceState);
        if (this.presentsAdapter.getItemCount() == 0) {
            loadPresents(null);
        }
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("presents", new ArrayList(this.presentsAdapter.getPresents()));
        outState.putBoolean("no_more_items", this.noMoreItems);
        if (this.lastAnchor != null) {
            outState.putString("anchor", this.lastAnchor);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            String presentId = data.getStringExtra("dialog_args_present_id");
            this.presentsAdapter.deletePresent(presentId);
            this.deletedPresentsManager.deletePresent(presentId);
        }
    }

    protected CharSequence getTitle() {
        if (this.userId != null) {
            return getStringLocalized(2131166599);
        }
        if (this.direction == Direction.ACCEPTED) {
            return getStringLocalized(2131166397);
        }
        return getStringLocalized(2131166400);
    }

    protected int getLayoutId() {
        return 2130903204;
    }

    public void onRefresh() {
        loadPresents(null);
    }

    @Subscribe(on = 2131623946, to = 2131624187)
    public void onPresentsLoaded(@NonNull BusEvent event) {
        if (TextUtils.equals(event.bundleInput.getString("EXTRA_USER_ID"), this.userId) && this.direction == event.bundleInput.getSerializable("EXTRA_PRESENT_DIRECTION")) {
            String requestedAnchor = event.bundleInput.getString("EXTRA_ANCHOR");
            if (requestedAnchor == null) {
                this.swipeRefreshLayout.setRefreshing(false);
            } else {
                this.loadItemAdapter.setLoading(false);
            }
            if (event.resultCode == -1) {
                PresentsResponse response = (PresentsResponse) event.bundleOutput.getParcelable("EXTRA_PRESENTS_RESPONSE");
                if (requestedAnchor == null) {
                    setPresents(response);
                } else {
                    appendPresents(response);
                }
                this.lastAnchor = response.anchor;
                return;
            }
            Type type = NetUtils.isConnectionAvailable(getContext(), false) ? Type.ERROR : Type.NO_INTERNET;
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(type);
        }
    }

    public void choosePresent(@NonNull String userId) {
        NavigationHelper.showExternalUrlPage(getActivity(), SendPresentShortLinkBuilder.choosePresentWithSelectedUser(userId).setSection("thankYou").setToken(this.token).build(), false);
    }

    public void chooseUser(@NonNull String presentId) {
        NavigationHelper.showExternalUrlPage(getActivity(), SendPresentShortLinkBuilder.chooseUserWithSelectedPresent(presentId).setToken(this.token).build(), false);
    }

    public void hidePresent(@NonNull String presentId) {
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(2131165974, 2131165973, 2131166881, 2131165476, 1);
        dialog.setTargetFragment(this, 1);
        dialog.getArguments().putString("dialog_args_present_id", presentId);
        dialog.show(getFragmentManager(), "hide-present");
    }

    public void clickPresent(@NonNull String presentId, @Nullable String holidayId) {
        NavigationHelper.showExternalUrlPage(getActivity(), StreamItemAdapter.buildMakePresentRequest(null, presentId, holidayId), false, SlidingMenuHelper.Type.friend_presents);
    }

    public void clickUser(@NonNull String userId) {
        NavigationHelper.showUserInfo(getActivity(), userId);
    }

    public void onPlayTrack(long trackId) {
        BusMusicHelper.getCustomTrack(trackId);
    }

    @Subscribe(on = 2131623946, to = 2131624199)
    public void onGetCustomTrack(@NonNull BusEvent event) {
        if (event != null) {
            Track[] tracks = (Track[]) event.bundleOutput.getParcelableArray("key_places_complaint_result");
            if (tracks != null && tracks.length > 0) {
                MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), 0, new ArrayList(Arrays.asList(tracks)), MusicListType.NO_DIRECTION);
            }
        }
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            List<PresentInfo> presents = savedInstanceState.getParcelableArrayList("presents");
            this.lastAnchor = savedInstanceState.getString("anchor");
            this.noMoreItems = savedInstanceState.getBoolean("no_more_items");
            setPresents(new PresentsResponse(this.lastAnchor, presents));
        }
    }

    private void loadPresents(@Nullable String anchor) {
        if (this.presentsAdapter.getItemCount() == 0) {
            this.emptyView.setVisibility(0);
            this.emptyView.setState(State.LOADING);
        } else if (anchor != null) {
            this.loadItemAdapter.setLoading(true);
        }
        BusPresentsHelper.loadPresents(this.userId, anchor, this.direction);
    }

    private void setPresents(@NonNull PresentsResponse response) {
        List<PresentInfo> filteredPresents = filterDeletedPresents(response.presents);
        if (filteredPresents.size() > 0) {
            this.emptyView.setVisibility(8);
            this.presentsAdapter.setPresents(filteredPresents);
            this.noMoreItems = false;
            updateSubtitle();
            return;
        }
        if (this.friendMode) {
            this.emptyView.setType(Type.FRIEND_PRESENTS);
        } else if (this.direction == Direction.SENT) {
            this.emptyView.setType(Type.MY_SENT_PRESENTS);
        } else {
            this.emptyView.setType(Type.MY_RECEIVED_PRESENTS);
        }
        this.emptyView.setState(State.LOADED);
        this.emptyView.setVisibility(0);
    }

    private void updateSubtitle() {
        if (this.userId != null && this.presentsAdapter.getPresents().size() > 0) {
            UserInfo receiver = ((PresentInfo) this.presentsAdapter.getPresents().get(0)).receiver;
            if (receiver != null && receiver.name != null) {
                safeGetSupportActionBar().setSubtitle(receiver.name);
            }
        }
    }

    private void appendPresents(@NonNull PresentsResponse response) {
        List<PresentInfo> filteredPresents = filterDeletedPresents(response.presents);
        this.emptyView.setVisibility(8);
        this.presentsAdapter.appendPresents(filteredPresents);
        if (filteredPresents.size() == 0) {
            this.noMoreItems = true;
        }
    }

    @NonNull
    private List<PresentInfo> filterDeletedPresents(@NonNull List<PresentInfo> presents) {
        List<PresentInfo> filtered = new ArrayList();
        for (PresentInfo presentInfo : presents) {
            if (!this.deletedPresentsManager.isPresentDeleted(presentInfo.id)) {
                filtered.add(presentInfo);
            }
        }
        return filtered;
    }
}

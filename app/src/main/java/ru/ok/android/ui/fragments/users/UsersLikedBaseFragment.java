package ru.ok.android.ui.fragments.users;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Collections;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.processors.discussions.data.UsersLikesParcelable;
import ru.ok.android.services.utils.users.OnlineUsersManager;
import ru.ok.android.ui.adapters.friends.UserInfosAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreController;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.users.loader.LikesBaseLoader;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;

public abstract class UsersLikedBaseFragment extends BaseFragment implements LoaderCallbacks<UsersLikesParcelable>, OnItemClickListener, LoadMoreAdapterListener {
    private LikesBaseLoader _loader;
    private boolean _selfLike;
    private UserInfosAdapter adapter;
    private SmartEmptyView emptyView;
    private LoadMoreRecyclerAdapter loadMoreAdapter;
    private LinearLayoutManager recyclerLayoutManager;
    private RecyclerView recyclerView;

    protected abstract LikesBaseLoader createLoader();

    protected static Bundle newArguments() {
        return newArguments(false);
    }

    protected static Bundle newArguments(boolean selfLike) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fragment_is_dialog", true);
        bundle.putBoolean("selfLike", selfLike);
        return bundle;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(2130903563, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(2131624731);
        this.recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.recyclerView.setLayoutManager(this.recyclerLayoutManager);
        this.adapter = new UserInfosAdapter(getActivity(), null, null);
        this.loadMoreAdapter = new LoadMoreRecyclerAdapter(getActivity(), this.adapter, this, LoadMoreMode.BOTTOM, null);
        this.recyclerView.setAdapter(this.loadMoreAdapter);
        this.adapter.getItemClickListenerController().addItemClickListener(this);
        this.emptyView = (SmartEmptyView) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setVisibility(8);
        registerForContextMenu(this.recyclerView);
        if (getDialog() != null) {
            getDialog().setTitle(getTitle());
        }
        this._selfLike = getArguments().getBoolean("selfLike", false);
        OnlineUsersManager.getInstance().getOnlineUsers();
        this.emptyView.setWebState(WebState.PROGRESS);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public void updateActionBarState() {
        super.updateActionBarState();
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    protected String getTitle() {
        return LocalizationManager.from(getActivity()).getString(2131166039);
    }

    protected int getLayoutId() {
        return 2130903563;
    }

    public Loader<UsersLikesParcelable> onCreateLoader(int id, Bundle bundle) {
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.LOADING);
        Loader createLoader = createLoader();
        this._loader = createLoader;
        return createLoader;
    }

    public void onLoadFinished(Loader<UsersLikesParcelable> loader, UsersLikesParcelable data) {
        boolean z;
        int i = 0;
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        if (!(currentUser == null || !this._selfLike || this.adapter.getUsers().containsKey(currentUser.uid))) {
            data.getUsers().add(0, currentUser);
        }
        this.adapter.setUsers(data == null ? Collections.emptyList() : data.getUsers());
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        boolean allLoaded = ((LikesBaseLoader) loader).isAllLoaded();
        LoadMoreController controller = this.loadMoreAdapter.getController();
        if (allLoaded) {
            z = false;
        } else {
            z = true;
        }
        controller.setBottomAutoLoad(z);
        this.loadMoreAdapter.getController().setBottomPermanentState(allLoaded ? LoadMoreState.DISABLED : LoadMoreState.LOAD_POSSIBLE_NO_LABEL);
        this.emptyView.setWebState(this.adapter.getItemCount() > 0 ? WebState.HAS_DATA : WebState.EMPTY);
        SmartEmptyView smartEmptyView = this.emptyView;
        if (!(data == null || data.getUsers().isEmpty())) {
            i = 8;
        }
        smartEmptyView.setVisibility(i);
    }

    public void onLoaderReset(Loader<UsersLikesParcelable> loader) {
        this.adapter.setUsers(Collections.emptyList());
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        this._loader.loadPreviousPortion();
    }

    public void onItemClick(View view, int position) {
        NavigationHelper.showUserInfo(getActivity(), this.adapter.getItem(position).uid);
    }
}

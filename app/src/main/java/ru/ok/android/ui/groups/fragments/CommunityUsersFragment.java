package ru.ok.android.ui.groups.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.GeneralDataLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import java.util.HashMap;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.RefreshableContentRecyclerFragment;
import ru.ok.android.fragments.RefreshableRecyclerFragmentHelper;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.onelog.search.UserPreviewClickEvent;
import ru.ok.onelog.search.UserPreviewUsageFactory;
import ru.ok.onelog.search.UsersScreenType;

public class CommunityUsersFragment extends RefreshableContentRecyclerFragment<CommunityUsersAdapter, Bundle> implements LoadMoreAdapterListener {
    private String anchor;
    private OnClickListener avatarItemClick;
    private SmartEmptyViewAnimated emptyView;
    private boolean hasMore;
    private LoadMoreRecyclerAdapter loadMoreRecyclerAdapter;
    private Loader<Bundle> loader;
    private UsersScreenType screenSource;

    /* renamed from: ru.ok.android.ui.groups.fragments.CommunityUsersFragment.1 */
    class C09171 implements OnClickListener {
        C09171() {
        }

        public void onClick(View v) {
            String uid = (String) v.getTag();
            if (uid != null) {
                NavigationHelper.showUserInfo(CommunityUsersFragment.this.getActivity(), uid);
            }
            OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.show_user_info, CommunityUsersFragment.this.screenSource));
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.CommunityUsersFragment.2 */
    class C09182 extends GeneralDataLoader<Bundle> {
        C09182(Context x0) {
            super(x0);
        }

        protected Bundle loadData() {
            int startYear = CommunityUsersFragment.this.getStartYear();
            int endYear = CommunityUsersFragment.this.getEndYear();
            if (startYear == 0 || endYear == 0) {
                return GroupsProcessor.getGroupMembers(CommunityUsersFragment.this.getGroupId(), CommunityUsersFragment.this.anchor, null);
            }
            return GroupsProcessor.getCommunityMembers(CommunityUsersFragment.this.getGroupId(), startYear, endYear, CommunityUsersFragment.this.anchor, null, 30);
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.CommunityUsersFragment.3 */
    class C09193 implements OnStubButtonClickListener {
        C09193() {
        }

        public void onStubButtonClick(Type type) {
            if (CommunityUsersFragment.this.loader != null) {
                CommunityUsersFragment.this.loader.forceLoad();
            }
        }
    }

    public class CommunityUsersAdapter extends Adapter<UserHolder> implements ImageBlockerRecyclerProvider, AdapterItemViewTypeMaxValueProvider {
        private HashMap<String, Boolean> invitedUids;
        private ArrayList<UserInfo> users;

        public class UserHolder extends ViewHolder {
            public TextView ageAndLocationTextView;
            public AvatarImageView avatarImageView;
            private OnClickListener inviteClickListener;
            public View inviteFriendButton;
            public TextView nameTextView;

            /* renamed from: ru.ok.android.ui.groups.fragments.CommunityUsersFragment.CommunityUsersAdapter.UserHolder.1 */
            class C09201 implements OnClickListener {
                C09201() {
                }

                public void onClick(View v) {
                    String uid = (String) v.getTag();
                    BusUsersHelper.inviteFriend(uid);
                    if (uid != null) {
                        CommunityUsersAdapter.this.invitedUids.put(uid, Boolean.valueOf(true));
                        UserHolder.this.ageAndLocationTextView.setText(LocalizationManager.getString(CommunityUsersFragment.this.getContext(), 2131166009));
                        UserHolder.this.inviteFriendButton.setVisibility(8);
                    }
                    OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.invite, CommunityUsersFragment.this.screenSource));
                }
            }

            public UserHolder(View itemView) {
                super(itemView);
                this.inviteClickListener = new C09201();
                this.nameTextView = (TextView) itemView.findViewById(C0263R.id.name);
                this.ageAndLocationTextView = (TextView) itemView.findViewById(2131624692);
                this.avatarImageView = (AvatarImageView) itemView.findViewById(2131624657);
                this.avatarImageView.setOnClickListener(CommunityUsersFragment.this.avatarItemClick);
                this.inviteFriendButton = itemView.findViewById(2131624693);
            }

            public void updateView(UserInfo user) {
                this.nameTextView.setText(user.getName());
                if (CommunityUsersAdapter.this.isInvited(user.getId())) {
                    this.ageAndLocationTextView.setText(LocalizationManager.getString(CommunityUsersFragment.this.getContext(), 2131166009));
                    this.inviteFriendButton.setVisibility(8);
                } else {
                    this.ageAndLocationTextView.setText(Utils.getAgeAndLocationText(CommunityUsersFragment.this.getContext(), user));
                    this.inviteFriendButton.setVisibility(0);
                }
                this.avatarImageView.setUser(user);
                this.avatarImageView.setTag(user.getId());
                this.inviteFriendButton.setTag(user.getId());
                this.inviteFriendButton.setOnClickListener(this.inviteClickListener);
                ImageViewManager.getInstance().displayImage(user.getPicUrl(), this.avatarImageView, user.isMan(), null);
            }
        }

        public CommunityUsersAdapter() {
            this.users = new ArrayList();
            this.invitedUids = new HashMap();
        }

        public void addUsers(ArrayList<UserInfo> users) {
            this.users.addAll(users);
        }

        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserHolder(LocalizationManager.inflate(CommunityUsersFragment.this.getContext(), CommunityUsersFragment.this.getItemLayoutId(), parent, false));
        }

        public void onBindViewHolder(UserHolder holder, int position) {
            holder.updateView((UserInfo) this.users.get(position));
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getItemCount() {
            return this.users == null ? 0 : this.users.size();
        }

        private boolean isInvited(String uid) {
            Boolean isInvited = (Boolean) this.invitedUids.get(uid);
            return isInvited != null && isInvited.booleanValue();
        }

        public int getItemViewTypeMaxValue() {
            return 0;
        }

        public OnScrollListener getScrollBlocker() {
            return null;
        }
    }

    public class CommunityUsersRefreshHelper extends RefreshableRecyclerFragmentHelper {
        public CommunityUsersRefreshHelper(BaseFragment fragment, Context context, String refreshSettingsName, int emptyViewTextResId) {
            super(fragment, context, refreshSettingsName, emptyViewTextResId);
        }

        protected boolean onStartRefresh(boolean byPullGesture) {
            return false;
        }
    }

    public CommunityUsersFragment() {
        this.screenSource = UsersScreenType.community_users;
        this.avatarItemClick = new C09171();
    }

    private int getStartYear() {
        return getArguments().getInt("start_year", 0);
    }

    private int getEndYear() {
        return getArguments().getInt("end_year", 0);
    }

    private String getGroupId() {
        return getArguments().getString("group_id");
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        return new CommunityUsersRefreshHelper(this, getActivity(), "community_users_list_update", 2131166268);
    }

    protected void onContentChanged() {
    }

    protected CharSequence getTitle() {
        String title = getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
        return title != null ? title : super.getTitle();
    }

    public Loader<Bundle> onCreateLoader(int id, Bundle args) {
        this.loader = new C09182(getContext());
        return this.loader;
    }

    protected int getItemLayoutId() {
        return 2130903128;
    }

    protected CommunityUsersAdapter createRecyclerAdapter() {
        return new CommunityUsersAdapter();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setType(Type.SEARCH);
        this.emptyView.setButtonClickListener(new C09193());
        this.loadMoreRecyclerAdapter = new LoadMoreRecyclerAdapter(getContext(), this.adapter, this, LoadMoreMode.BOTTOM);
        this.loadMoreRecyclerAdapter.getController().setBottomAutoLoad(true);
        this.loadMoreRecyclerAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        this.recyclerView.setAdapter(this.loadMoreRecyclerAdapter);
        return view;
    }

    protected void initRefresh(View fragmentMainView) {
        super.initRefresh(fragmentMainView);
        if (this.refreshHelper != null) {
            this.refreshHelper.getRefreshProvider().setRefreshEnabled(false);
        }
    }

    public void onLoadFinished(Loader<Bundle> loader, Bundle data) {
        if (data != null) {
            this.anchor = data.getString("anchor");
            ArrayList<UserInfo> users = data.getParcelableArrayList("USERS");
            if (users != null) {
                this.hasMore = data.getBoolean("has_more");
                ((CommunityUsersAdapter) this.adapter).addUsers(users);
                ((CommunityUsersAdapter) this.adapter).notifyDataSetChanged();
                this.loadMoreRecyclerAdapter.getController().setBottomPermanentState(this.hasMore ? LoadMoreState.LOAD_POSSIBLE : LoadMoreState.DISABLED);
                this.loadMoreRecyclerAdapter.getController().setBottomAutoLoad(this.hasMore);
                this.loadMoreRecyclerAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
                if (!this.hasMore && ((CommunityUsersAdapter) this.adapter).getItemCount() == 0) {
                    this.emptyView.setState(State.LOADED);
                    return;
                }
                return;
            }
            boolean noInternet = ErrorType.from(data) == ErrorType.NO_INTERNET;
            this.loadMoreRecyclerAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
            if (((CommunityUsersAdapter) this.adapter).getItemCount() > 0) {
                this.loadMoreRecyclerAdapter.getController().setBottomPermanentState(LoadMoreState.LOAD_POSSIBLE);
                Toast.makeText(getContext(), noInternet ? 2131165984 : 2131166539, 1).show();
                return;
            }
            this.loadMoreRecyclerAdapter.getController().setBottomPermanentState(LoadMoreState.DISABLED);
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(noInternet ? Type.NO_INTERNET : Type.ERROR);
            return;
        }
        this.emptyView.setState(State.LOADED);
        this.emptyView.setType(Type.ERROR);
    }

    public void onLoaderReset(Loader<Bundle> loader) {
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        this.loader.forceLoad();
    }
}

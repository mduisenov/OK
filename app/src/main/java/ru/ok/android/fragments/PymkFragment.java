package ru.ok.android.fragments;

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
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.PymkProcessor;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.view.PymkMutualFriendsView;
import ru.ok.android.ui.stream.suggestions.PymkItemBuilder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.users.JsonPymkParser;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.model.MutualFriendsPreviewInfo;
import ru.ok.model.UserInfo;
import ru.ok.onelog.search.UserPreviewClickEvent;
import ru.ok.onelog.search.UserPreviewUsageFactory;
import ru.ok.onelog.search.UsersScreenType;

public class PymkFragment extends RefreshableContentRecyclerFragment<PymkAdapter, Bundle> {
    private OnClickListener avatarClickListener;
    private SmartEmptyViewAnimated emptyView;
    private OnClickListener inviteClickListener;
    private Loader<Bundle> loader;
    private OnClickListener mutualFriendsClickListener;
    private HashMap<String, MutualFriendsPreviewInfo> mutualInfos;
    private UsersScreenType screenSource;
    private OnClickListener wholeItemClickListener;

    /* renamed from: ru.ok.android.fragments.PymkFragment.1 */
    class C02781 extends GeneralDataLoader<Bundle> {
        C02781(Context x0) {
            super(x0);
        }

        protected Bundle loadData() {
            Exception e;
            try {
                return PymkProcessor.loadPymkWithDetails(20, PymkProcessor.getSuggestedFriendsFieldsBuilder().build(), 3, PymkProcessor.getMutualFriendsFieldsBuilder().build());
            } catch (BaseApiException e2) {
                e = e2;
                return CommandProcessor.createErrorBundle(e);
            } catch (JSONException e3) {
                e = e3;
                return CommandProcessor.createErrorBundle(e);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.PymkFragment.2 */
    class C02792 implements OnStubButtonClickListener {
        C02792() {
        }

        public void onStubButtonClick(Type type) {
            if (PymkFragment.this.loader != null) {
                PymkFragment.this.loader.forceLoad();
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.PymkFragment.3 */
    class C02803 implements OnClickListener {
        C02803() {
        }

        public void onClick(View v) {
            String uid = PymkItemBuilder.getUidByView(v.getParent());
            if (uid != null) {
                PymkItemBuilder.showUserInfo(PymkFragment.this.getActivity(), uid);
            }
            OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.show_user_info, PymkFragment.this.screenSource));
        }
    }

    /* renamed from: ru.ok.android.fragments.PymkFragment.4 */
    class C02814 implements OnClickListener {
        C02814() {
        }

        public void onClick(View v) {
            String uid = PymkItemBuilder.getUidByView(v);
            if (uid != null) {
                PymkItemBuilder.showUserInfo(PymkFragment.this.getActivity(), uid);
            }
            OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.show_user_info, PymkFragment.this.screenSource));
        }
    }

    /* renamed from: ru.ok.android.fragments.PymkFragment.5 */
    class C02825 implements OnClickListener {
        C02825() {
        }

        public void onClick(View v) {
            PymkMutualFriendsView pymkView = (PymkMutualFriendsView) v;
            String uid = pymkView.getUid();
            boolean loadMore = ((PymkAdapter) PymkFragment.this.adapter).getMutualFriends(uid) == null && pymkView.getTotalCount() != null && pymkView.getParticipants().size() < pymkView.getTotalCount().intValue();
            PymkItemBuilder.showMutualFriends((PymkMutualFriendsView) v, PymkFragment.this.getContext(), PymkFragment.this, loadMore);
            if (loadMore) {
                PymkItemBuilder.requestMutualFriendsForUser(uid);
            }
            OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.show_mutual_friends, PymkFragment.this.screenSource));
        }
    }

    /* renamed from: ru.ok.android.fragments.PymkFragment.6 */
    class C02836 implements OnClickListener {
        C02836() {
        }

        public void onClick(View v) {
            String uid = PymkItemBuilder.getUidByView(v.getParent());
            if (uid != null) {
                PymkFragment.sendInvitationRequest(uid, PymkFragment.this.getContext());
                ((PymkAdapter) PymkFragment.this.adapter).removeUser(uid);
            }
            OneLog.log(UserPreviewUsageFactory.get(UserPreviewClickEvent.invite, PymkFragment.this.screenSource));
        }
    }

    public class PymkAdapter extends Adapter<PymkItemHolder> implements ImageBlockerRecyclerProvider {
        private LayoutInflater inflater;
        private HashMap<String, ArrayList<UserInfo>> mutualFriendsHashMap;
        private List<UserInfo> users;

        public PymkAdapter() {
            this.inflater = (LayoutInflater) PymkFragment.this.getContext().getSystemService("layout_inflater");
            this.users = new ArrayList();
            this.mutualFriendsHashMap = new HashMap();
        }

        public PymkItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup itemView = new LinearLayout(PymkFragment.this.getContext());
            itemView.setLayoutParams(new LayoutParams(-1, -2));
            ((LinearLayout) itemView).setOrientation(1);
            return new PymkItemHolder(itemView);
        }

        public void onBindViewHolder(PymkItemHolder holder, int position) {
            UserInfo user = (UserInfo) this.users.get(position);
            PymkItemBuilder builder = new PymkItemBuilder(user, PymkFragment.this.getContext()).setOnMutualFriendsClickListener(PymkFragment.this.mutualFriendsClickListener).setOnInviteClickListener(PymkFragment.this.inviteClickListener).setOnAvatarClickListener(PymkFragment.this.avatarClickListener).setIsCancelable(false).setWholeItemClickListener(PymkFragment.this.wholeItemClickListener);
            if (PymkFragment.this.mutualInfos != null) {
                builder.setMutualInfo((MutualFriendsPreviewInfo) PymkFragment.this.mutualInfos.get(user.getId()));
            }
            if (!holder.isInitialized()) {
                builder.create(this.inflater, (ViewGroup) holder.itemView);
                holder.initialize();
            }
            holder.container.setTag(user.getId());
            builder.fillView(holder);
        }

        public void setUsers(List<UserInfo> users) {
            this.users = users;
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getItemCount() {
            return this.users == null ? 0 : this.users.size();
        }

        public ArrayList<UserInfo> getMutualFriends(String uid) {
            return (ArrayList) this.mutualFriendsHashMap.get(uid);
        }

        public void removeUser(String uid) {
            int index = getIndexByUid(uid);
            if (index != -1) {
                this.users.remove(index);
                notifyItemRemoved(index);
            }
        }

        private int getIndexByUid(String uid) {
            for (int i = 0; i < getItemCount(); i++) {
                if (((UserInfo) this.users.get(i)).getId().equals(uid)) {
                    return i;
                }
            }
            return -1;
        }

        public OnScrollListener getScrollBlocker() {
            return null;
        }
    }

    public class PymkItemHolder extends ViewHolder {
        public TextView ageAndLocationTextView;
        public AvatarImageView avatarImageView;
        public ViewGroup container;
        private boolean initialized;
        public TextView nameTextView;
        public PymkMutualFriendsView pymkMutualFriendsView;

        public PymkItemHolder(View itemView) {
            super(itemView);
        }

        public void initialize() {
            this.initialized = true;
            this.container = (ViewGroup) ((ViewGroup) this.itemView).getChildAt(0);
            this.nameTextView = (TextView) this.container.findViewById(C0263R.id.name);
            this.avatarImageView = (AvatarImageView) this.container.findViewById(2131624657);
            this.ageAndLocationTextView = (TextView) this.container.findViewById(2131624692);
            this.pymkMutualFriendsView = (PymkMutualFriendsView) this.container.findViewById(2131625374);
        }

        public boolean isInitialized() {
            return this.initialized;
        }
    }

    public class PymkRefreshHelper extends RefreshableRecyclerFragmentHelper {
        public PymkRefreshHelper(BaseFragment fragment, Context context, String refreshSettingsName, int emptyViewTextResId) {
            super(fragment, context, refreshSettingsName, emptyViewTextResId);
        }

        protected boolean onStartRefresh(boolean byPullGesture) {
            return false;
        }
    }

    public PymkFragment() {
        this.screenSource = UsersScreenType.pymk;
        this.avatarClickListener = new C02803();
        this.wholeItemClickListener = new C02814();
        this.mutualFriendsClickListener = new C02825();
        this.inviteClickListener = new C02836();
    }

    protected void initRefresh(View fragmentMainView) {
        super.initRefresh(fragmentMainView);
        if (this.refreshHelper != null) {
            this.refreshHelper.getRefreshProvider().setRefreshEnabled(false);
        }
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166669);
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        return new PymkRefreshHelper(this, getActivity(), "suggested_friends_list_update", 2131166268);
    }

    protected void onContentChanged() {
    }

    public Loader<Bundle> onCreateLoader(int id, Bundle args) {
        GetFriendsProcessor.getDefaultFriendsSuggestionsFields().addField(FIELDS.AGE).addField(FIELDS.LOCATION);
        this.loader = new C02781(getContext());
        return this.loader;
    }

    protected PymkAdapter createRecyclerAdapter() {
        return new PymkAdapter();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setType(Type.SEARCH);
        this.emptyView.setButtonClickListener(new C02792());
        return view;
    }

    public void onLoadFinished(Loader<Bundle> loader, Bundle data) {
        if (data != null) {
            ArrayList<UserInfo> users = data.getParcelableArrayList(JsonPymkParser.KEY_USERS);
            if (users != null) {
                this.mutualInfos = (HashMap) data.getSerializable(JsonPymkParser.KEY_MUTUAL_FRIENDS_INFO);
                ((PymkAdapter) this.adapter).setUsers(users);
                ((PymkAdapter) this.adapter).notifyDataSetChanged();
                if (((PymkAdapter) this.adapter).getItemCount() == 0) {
                    this.emptyView.setState(State.LOADED);
                    return;
                }
                return;
            }
            ErrorType errorType = ErrorType.from(data);
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(errorType == ErrorType.NO_INTERNET ? Type.NO_INTERNET : Type.ERROR);
            return;
        }
        this.emptyView.setState(State.LOADED);
        this.emptyView.setType(Type.ERROR);
    }

    public void onLoaderReset(Loader loader) {
    }

    public static void sendInvitationRequest(String uid, Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", uid);
        GlobalBus.send(2131624122, new BusEvent(bundle));
        Toast.makeText(context, LocalizationManager.getString(context, 2131166014), 0).show();
    }
}

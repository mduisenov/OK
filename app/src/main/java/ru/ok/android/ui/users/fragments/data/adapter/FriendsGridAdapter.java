package ru.ok.android.ui.users.fragments.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DoActionBoxUser;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.layout.UniformHorizontalLayout;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsStrategy;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.RecyclerMergeHeaderAdapter.HeaderTextProvider;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.HeaderViewProvider;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator.ViewHolderHeader;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class FriendsGridAdapter extends Adapter<ViewHolder> implements OnClickListener, AdapterItemViewTypeMaxValueProvider, HeaderTextProvider, HeaderViewProvider {
    private final boolean alwaysShowWriteMessage;
    private final int columnsCount;
    private final String headerText;
    private final LayoutInflater li;
    private final int padding;
    private final List<ViewHolderFriend> pool;
    private final FriendsStrategy<List<UserInfo>> strategy;

    /* renamed from: ru.ok.android.ui.users.fragments.data.adapter.FriendsGridAdapter.1 */
    class C13321 extends DoActionBoxUser {
        final /* synthetic */ View val$v;

        C13321(View x0, UserInfo x1, boolean x2, boolean x3, View view) {
            this.val$v = view;
            super(x0, x1, x2, x3);
        }

        public void show() {
            this.quickAction.show(this.val$v);
        }
    }

    static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        final UniformHorizontalLayout ll;

        public ViewHolder(UniformHorizontalLayout ll) {
            super(ll);
            this.ll = ll;
        }
    }

    final class ViewHolderFriend extends android.support.v7.widget.RecyclerView.ViewHolder {
        final AsyncDraweeView avatar;
        final View dots;
        final TextView header;
        final TextView info;
        final TextView name;
        final View onlineView;
        final View privateProfile;

        public ViewHolderFriend(View view) {
            super(view);
            this.avatar = (AsyncDraweeView) view.findViewById(2131624657);
            this.onlineView = view.findViewById(2131624634);
            this.name = (TextView) view.findViewById(C0263R.id.name);
            this.header = (TextView) view.findViewById(2131624871);
            this.info = (TextView) view.findViewById(C0263R.id.info);
            this.privateProfile = view.findViewById(2131624666);
            view.setOnClickListener(FriendsGridAdapter.this);
            this.dots = view.findViewById(2131624874);
            if (this.dots != null) {
                int dtouch = view.getResources().getDimensionPixelSize(2131230952);
                ViewUtil.setTouchDelegate(this.dots, dtouch, dtouch, dtouch, dtouch);
                this.dots.setOnClickListener(FriendsGridAdapter.this);
            }
        }

        void bindUser(UserInfo user, boolean alwaysShowWriteMessage) {
            int i;
            boolean canCall = true;
            this.avatar.setEmptyImageResId(user.genderType == UserGenderType.FEMALE ? 2130837927 : 2130838321);
            this.avatar.setUri(!URLUtil.isStubUrl(user.picUrl) ? Uri.parse(user.picUrl) : null);
            this.name.setText(user.getAnyName());
            View view = this.privateProfile;
            if (user.showLock) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
            Utils.updateOnlineView(this.onlineView, Utils.onlineStatus(user));
            this.info.setText(DateFormatter.formatDeltaTimePast(this.itemView.getContext(), user.lastOnline, false, false));
            this.itemView.setTag(user.uid);
            if (this.dots != null) {
                boolean canWrite;
                if (alwaysShowWriteMessage || !user.privateProfile) {
                    canWrite = true;
                } else {
                    canWrite = false;
                }
                if (!(alwaysShowWriteMessage && Utils.userCanCall(user))) {
                    canCall = false;
                }
                if (canWrite || canCall) {
                    this.dots.setTag(2131624316, Boolean.valueOf(canWrite));
                    this.dots.setTag(2131624315, Boolean.valueOf(canCall));
                    this.dots.setTag(user);
                    this.dots.setVisibility(0);
                    return;
                }
                this.dots.setVisibility(8);
            }
        }
    }

    public FriendsGridAdapter(Context context, int columnsCount, FriendsStrategy<List<UserInfo>> strategy, int headerTextId, int leftPadding, boolean alwaysShowWriteMessage) {
        this.pool = new ArrayList();
        this.li = LayoutInflater.from(context);
        this.columnsCount = columnsCount;
        this.padding = leftPadding;
        this.alwaysShowWriteMessage = alwaysShowWriteMessage;
        this.headerText = headerTextId != 0 ? LocalizationManager.getString(context, headerTextId) : "";
        this.strategy = strategy;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UniformHorizontalLayout ll = new UniformHorizontalLayout(parent.getContext(), this.columnsCount);
        ll.setPadding(this.padding, 0, 0, 0);
        return new ViewHolder(ll);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        UniformHorizontalLayout ll = holder.ll;
        for (int i = 0; i < ll.getChildCount(); i++) {
            this.pool.add((ViewHolderFriend) ll.getChildAt(i).getTag(2131624349));
        }
        ll.removeAllViews();
        for (UserInfo user : (List) this.strategy.getItem(position)) {
            ViewHolderFriend vh = !this.pool.isEmpty() ? (ViewHolderFriend) this.pool.remove(0) : null;
            if (vh == null) {
                View item = this.li.inflate(2130903211, ll, false);
                vh = new ViewHolderFriend(item);
                item.setTag(2131624349, vh);
            }
            vh.bindUser(user, this.alwaysShowWriteMessage);
            ll.addView(vh.itemView);
        }
    }

    public int getItemViewType(int position) {
        return 2131624359;
    }

    public int getItemViewTypeMaxValue() {
        return 2131624359;
    }

    public long getItemId(int position) {
        return (long) ((List) this.strategy.getItem(position)).hashCode();
    }

    public int getItemCount() {
        return this.strategy.getItemsCount();
    }

    public String getHeader(int position) {
        return this.strategy.getItemHeader(position);
    }

    public ViewHolderHeader newHeaderView(int position, ViewGroup parent) {
        return new ViewHolderHeader(this.li.inflate(2130903208, parent, false));
    }

    public int getHeaderViewType(int position) {
        return 0;
    }

    public void bindHeaderView(ViewHolderHeader view, int position) {
        ((TextView) view.view).setText(String.valueOf(getHeader(position)));
    }

    public int getAnchorViewId(int position) {
        return 0;
    }

    public CharSequence getHeaderName() {
        return this.headerText;
    }

    public void onClick(View v) {
        if (v.getId() == 2131624874) {
            UserInfo userInfo = (UserInfo) v.getTag();
            if (userInfo != null) {
                new C13321(v, userInfo, ((Boolean) v.getTag(2131624316)).booleanValue(), ((Boolean) v.getTag(2131624315)).booleanValue(), v).show();
                return;
            }
            return;
        }
        NavigationHelper.showUserInfo((Activity) v.getContext(), (String) v.getTag());
    }
}

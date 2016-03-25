package ru.ok.android.ui.users.fragments.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsStrategy;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.RecyclerMergeHeaderAdapter.HeaderTextProvider;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class FriendsListAdapter extends Adapter<ViewHolder> implements OnClickListener, AdapterItemViewTypeMaxValueProvider, HeaderTextProvider {
    private final boolean alwaysShowWriteMessage;
    protected final Context context;
    private final String headerText;
    private final boolean hidePrivateProfileIcon;
    private final Map<String, CharSequence> infoStringsCache;
    protected final LayoutInflater li;
    private final int rowLayoutId;
    protected final FriendsStrategy<UserInfo> strategy;

    /* renamed from: ru.ok.android.ui.users.fragments.data.adapter.FriendsListAdapter.1 */
    class C13331 extends AdapterDataObserver {
        C13331() {
        }

        public void onChanged() {
            super.onChanged();
            FriendsListAdapter.this.infoStringsCache.clear();
        }
    }

    final class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        final AsyncDraweeView avatar;
        final TextView header;
        final TextView info;
        final TextView name;
        final View onlineView;
        final View privateProfile;
        final View writeMessage;

        public ViewHolder(View view) {
            super(view);
            this.avatar = (AsyncDraweeView) view.findViewById(2131624657);
            this.onlineView = view.findViewById(2131624634);
            this.name = (TextView) view.findViewById(C0263R.id.name);
            this.header = (TextView) view.findViewById(2131624871);
            this.info = (TextView) view.findViewById(C0263R.id.info);
            this.privateProfile = view.findViewById(2131624666);
            this.writeMessage = view.findViewById(2131624872);
            view.setOnClickListener(FriendsListAdapter.this);
            this.writeMessage.setOnClickListener(FriendsListAdapter.this);
            View avatarContainer = view.findViewById(2131624540);
            if (avatarContainer != null) {
                avatarContainer.setClickable(false);
                avatarContainer.setBackgroundDrawable(null);
            }
        }

        void bindUser(UserInfo user, boolean alwaysShowWriteMessage, CharSequence infoString) {
            int i = 8;
            this.avatar.setEmptyImageResId(user.genderType == UserGenderType.FEMALE ? 2130837927 : 2130838321);
            this.avatar.setUri(!URLUtil.isStubUrl(user.picUrl) ? Uri.parse(user.picUrl) : null);
            this.name.setText(user.getAnyName());
            if (this.privateProfile != null) {
                int i2;
                View view = this.privateProfile;
                if (FriendsListAdapter.this.hidePrivateProfileIcon || !user.showLock) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                view.setVisibility(i2);
            }
            Utils.updateOnlineView(this.onlineView, Utils.onlineStatus(user));
            this.itemView.setTag(user.uid);
            this.writeMessage.setTag(user.uid);
            View view2 = this.writeMessage;
            if (alwaysShowWriteMessage || !user.privateProfile) {
                i = 0;
            }
            view2.setVisibility(i);
            Utils.setTextViewTextWithVisibility(this.info, infoString);
        }
    }

    public FriendsListAdapter(Context context, int rowLayoutId, FriendsStrategy strategy, int headerTextId, boolean alwaysShowWriteMessage, boolean hidePrivateProfileIcon) {
        this.infoStringsCache = new HashMap();
        this.context = context;
        this.li = LayoutInflater.from(context);
        this.rowLayoutId = rowLayoutId;
        this.strategy = strategy;
        this.alwaysShowWriteMessage = alwaysShowWriteMessage;
        this.hidePrivateProfileIcon = hidePrivateProfileIcon;
        this.headerText = headerTextId != 0 ? LocalizationManager.getString(context, headerTextId) : null;
        registerAdapterDataObserver(new C13331());
    }

    public final void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo user = (UserInfo) this.strategy.getItem(position);
        holder.bindUser(user, this.alwaysShowWriteMessage, getInfoString(user));
    }

    public final void onClick(View v) {
        String userId = (String) v.getTag();
        Activity activity = (Activity) v.getContext();
        if (v.getId() == 2131624872) {
            NavigationHelper.showMessagesForUser(activity, userId);
        } else {
            NavigationHelper.showUserInfo(activity, userId);
        }
    }

    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this.li.inflate(this.rowLayoutId, parent, false));
    }

    public final int getItemViewType(int position) {
        return 2131624366;
    }

    public int getItemViewTypeMaxValue() {
        return 2131624366;
    }

    public final long getItemId(int position) {
        return (long) ((UserInfo) this.strategy.getItem(position)).uid.hashCode();
    }

    public int getItemCount() {
        return this.strategy.getItemsCount();
    }

    final CharSequence getInfoString(UserInfo user) {
        String uid = user.uid;
        if (this.infoStringsCache.containsKey(uid)) {
            return (CharSequence) this.infoStringsCache.get(uid);
        }
        CharSequence infoString = this.strategy.buildInfoString(user);
        this.infoStringsCache.put(uid, infoString);
        return infoString;
    }

    public CharSequence getHeaderName() {
        return this.headerText;
    }
}

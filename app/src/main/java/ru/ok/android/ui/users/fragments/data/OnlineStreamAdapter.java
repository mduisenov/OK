package ru.ok.android.ui.users.fragments.data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.custom.online.OnlineDrawable;
import ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class OnlineStreamAdapter extends BaseCursorRecyclerAdapter<ViewHolder> implements ItemClickListenerControllerProvider {
    private UserInfo animationInUserInfo;
    private final HandleBlocker blocker;
    private boolean collapsed;
    private Set<String> expandedUsersUids;
    private RecyclerItemClickListenerController itemClickListenerController;
    private OnlineFriendsStreamFragment onlineFriendsStreamFragment;
    private final Queue<OnlineOnPreDrawListener> preDrawListeners;

    private class OnlineOnPreDrawListener implements OnPreDrawListener {
        private ViewHolder holder;

        private OnlineOnPreDrawListener() {
        }

        public boolean onPreDraw() {
            this.holder.name.getViewTreeObserver().removeOnPreDrawListener(this);
            OnlineStreamAdapter.this.freeOnPreDrawListener(this);
            int height = this.holder.name.getHeight();
            int lineCount = this.holder.name.getLineCount();
            MarginLayoutParams lp = (MarginLayoutParams) this.holder.status.getLayoutParams();
            if (lineCount == 0) {
                lp.topMargin = this.holder.name.getTop();
            } else {
                lp.topMargin = this.holder.name.getTop() + (((height / lineCount) - this.holder.status.getHeight()) / 2);
            }
            this.holder.status.setLayoutParams(lp);
            if (OnlineStreamAdapter.this.expandedUsersUids.contains(this.holder.user.uid)) {
                int translationX = (-this.holder.name.getRight()) + this.holder.avatar.getRight();
                this.holder.name.setTranslationX((float) translationX);
                this.holder.name.setAlpha(0.5f);
                this.holder.status.setTranslationX((float) translationX);
            }
            return false;
        }

        public void setViewHolder(ViewHolder holder) {
            this.holder = holder;
            holder.name.getViewTreeObserver().addOnPreDrawListener(this);
        }
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        final UrlImageView avatar;
        final TextView name;
        public View options;
        public ViewStub optionsStub;
        public View profile;
        private OnClickListener profileListener;
        final View row;
        final ImageView status;
        public UserInfo user;
        public View writeMessage;
        private OnClickListener writeMessageListener;

        /* renamed from: ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter.ViewHolder.1 */
        class C13181 implements OnClickListener {
            C13181() {
            }

            public void onClick(View v) {
                Logger.m173d("%s", ((ViewHolder) v.getTag()).user);
                ViewHolder.this.animateOptionsOut();
                NavigationHelper.showMessagesForUser((Activity) v.getContext(), user.uid);
                StatisticManager.getInstance().addStatisticEvent("stream-online-friend-write-message-clicked", new Pair[0]);
            }
        }

        /* renamed from: ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter.ViewHolder.2 */
        class C13192 implements OnClickListener {
            C13192() {
            }

            public void onClick(View v) {
                Logger.m173d("%s", ((ViewHolder) v.getTag()).user);
                NavigationHelper.showUserInfo((Activity) v.getContext(), user.uid);
                StatisticManager.getInstance().addStatisticEvent("stream-online-friend-profile-clicked", new Pair[0]);
            }
        }

        /* renamed from: ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter.ViewHolder.3 */
        class C13203 implements OnPreDrawListener {
            final /* synthetic */ long val$startDelayMs;

            C13203(long j) {
                this.val$startDelayMs = j;
            }

            public boolean onPreDraw() {
                ViewHolder.this.options.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewHolder.this.animateOptionsIn(this.val$startDelayMs);
                return true;
            }
        }

        /* renamed from: ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter.ViewHolder.4 */
        class C13214 extends AnimatorListenerAdapter {
            C13214() {
            }

            public void onAnimationEnd(Animator animation) {
                ViewHolder.this.options.setVisibility(8);
            }
        }

        private OnClickListener getWriteMessageListener() {
            if (this.writeMessageListener == null) {
                this.writeMessageListener = new C13181();
            }
            return this.writeMessageListener;
        }

        private OnClickListener getProfileListener() {
            if (this.profileListener == null) {
                this.profileListener = new C13192();
            }
            return this.profileListener;
        }

        public ViewHolder(View view) {
            super(view);
            this.row = view;
            this.avatar = (UrlImageView) view.findViewById(2131624657);
            this.name = (TextView) view.findViewById(C0263R.id.name);
            this.status = (ImageView) view.findViewById(2131624717);
            this.optionsStub = (ViewStub) view.findViewById(2131624875);
            if (this.name != null) {
                this.avatar.bringToFront();
            }
        }

        public void animateOptionsIn(long startDelayMs) {
            inflateStubIfNeeded();
            if (this.options != null) {
                this.options.setVisibility(0);
                OnlineStreamAdapter.this.expandedUsersUids.add(this.user.uid);
                if (this.options.getWidth() <= 0) {
                    this.options.getViewTreeObserver().addOnPreDrawListener(new C13203(startDelayMs));
                    return;
                }
                this.options.setTranslationX((float) this.options.getWidth());
                ObjectAnimator.ofFloat(this.options, "translationX", new float[]{(float) deltaOptions, 0.0f}).setDuration(140);
                int deltaName = (-this.name.getRight()) + this.avatar.getRight();
                ObjectAnimator.ofFloat(this.name, "translationX", new float[]{0.0f, (float) deltaName}).setDuration(140);
                ObjectAnimator.ofFloat(this.status, "translationX", new float[]{0.0f, (float) deltaName}).setDuration(140);
                ObjectAnimator.ofFloat(this.name, "alpha", new float[]{0.5f}).setDuration(140);
                AnimatorSet set = new AnimatorSet();
                set.setStartDelay(startDelayMs);
                set.playTogether(Arrays.asList(new Animator[]{animatorOptions, animatorName, animatorIcon, nameAlpha}));
                set.start();
            }
        }

        public void animateOptionsOut() {
            OnlineStreamAdapter.this.expandedUsersUids.remove(this.user.uid);
            int deltaX = this.options.getWidth();
            ObjectAnimator.ofFloat(this.options, "translationX", new float[]{0.0f, (float) deltaX}).setDuration(140);
            ObjectAnimator.ofFloat(this.name, "translationX", new float[]{(float) (-deltaX), 0.0f}).setDuration(140);
            ObjectAnimator animatorIcon = ObjectAnimator.ofFloat(this.status, "translationX", new float[]{(float) (-deltaX), 0.0f});
            animatorIcon.setDuration(140);
            animatorIcon.setStartDelay(70);
            ObjectAnimator.ofFloat(this.name, "alpha", new float[]{1.0f}).setDuration(140);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(Arrays.asList(new Animator[]{animatorOptions, animatorName, animatorIcon, nameAlpha}));
            set.start();
            set.addListener(new C13214());
        }

        private void inflateStubIfNeeded() {
            if (this.optionsStub != null) {
                this.options = this.optionsStub.inflate();
                this.optionsStub = null;
                this.writeMessage = this.options.findViewById(2131624872);
                this.profile = this.options.findViewById(2131624878);
                this.writeMessage.setOnClickListener(getWriteMessageListener());
                this.writeMessage.setTag(this);
                this.profile.setOnClickListener(getProfileListener());
                this.profile.setTag(this);
            }
        }
    }

    public OnlineStreamAdapter(OnlineFriendsStreamFragment onlineFriendsStreamFragment, Context context, HandleBlocker blocker) {
        super(null);
        this.expandedUsersUids = new HashSet();
        this.preDrawListeners = new LinkedList();
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.onlineFriendsStreamFragment = onlineFriendsStreamFragment;
        this.blocker = blocker;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LocalizationManager.inflate(parent.getContext(), this.collapsed ? 2130903215 : 2130903214, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo user = UsersStorageFacade.cursor2User(getItemCursor(position));
        if (holder.name != null) {
            holder.name.setText(user.getAnyName());
        }
        ImageViewManager.getInstance().displayImage(user.picUrl, holder.avatar, user.genderType == UserGenderType.MALE ? 2130838321 : 2130837927, null);
        int drawableResourceId = Utils.getUserOnlineDrawableResId(Utils.onlineStatus(user));
        OnlineDrawable onlineDrawable = null;
        if (drawableResourceId != 0) {
            onlineDrawable = new OnlineDrawable(holder.itemView.getContext().getResources().getDrawable(drawableResourceId));
            onlineDrawable.setAnimationBlocker(this.blocker);
        }
        holder.status.setBackgroundDrawable(onlineDrawable);
        holder.status.setTranslationX(0.0f);
        boolean isExpanded = this.expandedUsersUids.contains(user.uid);
        if (holder.name != null) {
            getPreDrawListener().setViewHolder(holder);
            if (!isExpanded) {
                holder.name.setTranslationX(0.0f);
                holder.name.setAlpha(1.0f);
            }
        }
        if (holder.options != null) {
            View view = holder.options;
            int i = (!isExpanded || this.onlineFriendsStreamFragment.isCollapseAnimating()) ? 8 : 0;
            view.setVisibility(i);
        }
        holder.user = user;
        if (this.animationInUserInfo != null && TextUtils.equals(this.animationInUserInfo.uid, user.uid)) {
            holder.animateOptionsIn(300);
            this.animationInUserInfo = null;
        }
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public int getItemViewType(int position) {
        return this.collapsed ? 1 : 0;
    }

    private OnlineOnPreDrawListener getPreDrawListener() {
        if (this.preDrawListeners.isEmpty()) {
            return new OnlineOnPreDrawListener();
        }
        return (OnlineOnPreDrawListener) this.preDrawListeners.poll();
    }

    private void freeOnPreDrawListener(OnlineOnPreDrawListener listener) {
        this.preDrawListeners.offer(listener);
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        this.expandedUsersUids.clear();
        notifyDataSetChanged();
    }

    public void setAnimationInUserInfo(UserInfo user) {
        this.animationInUserInfo = user;
    }

    public void closeChildOptionsIfPossible(View child, ViewHolder exceptHolder) {
        ViewHolder tag = child.getTag();
        if (tag != exceptHolder && (tag instanceof ViewHolder)) {
            ViewHolder holder = tag;
            if (holder.options != null && holder.options.getVisibility() == 0) {
                holder.animateOptionsOut();
            }
        }
    }

    public Set<String> getExpandedUsersUids() {
        return this.expandedUsersUids;
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}

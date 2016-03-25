package ru.ok.android.ui.users.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.UsersListFragment;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.utils.users.OnlineUsersManager;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.adapters.ScrollLoadRecyclerViewBlocker;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.users.CursorSwapper;
import ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter;
import ru.ok.android.ui.users.fragments.data.OnlineStreamAdapter.ViewHolder;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.animation.SimpleAnimationListener;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.FrameInterceptTouchListenerLayout;

public final class OnlineFriendsStreamFragment extends OnlineUsersFragment implements OnClickListener, OnTouchListener {
    private static float flingSpeedThreshold;
    private View animatingPanel;
    private final ScrollLoadRecyclerViewBlocker blocker;
    private boolean collapseAnimating;
    private AnimationListener collapseAnimationListener;
    private boolean collapsed;
    private int collapsedWidth;
    private FrameInterceptTouchListenerLayout container;
    private AnimationListener expandAnimationListener;
    private int expandedWidth;
    private ViewGroup fragmentRootContainer;
    private GestureDetector gestureDetector;
    private Handler handler;
    private View icon;
    private OnlineStreamAdapter onlineAdapter;
    private TextView textMessage;
    private boolean wasAutohide;

    /* renamed from: ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment.1 */
    class C13101 extends Handler {
        C13101() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    OnlineUsersManager.getInstance().getOnlineUsers();
                    sendEmptyMessageDelayed(0, 120000);
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    OnlineFriendsStreamFragment.this.collapsed = true;
                    OnlineFriendsStreamFragment.this.updateForCurrentState();
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment.2 */
    class C13112 extends SimpleOnGestureListener {
        C13112() {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) < OnlineFriendsStreamFragment.flingSpeedThreshold) {
                return false;
            }
            boolean newCollapsed;
            if (velocityX > 0.0f) {
                newCollapsed = true;
            } else {
                newCollapsed = false;
            }
            OnlineFriendsStreamFragment.this.wasAutohide = false;
            OnlineFriendsStreamFragment.this.handler.removeMessages(1);
            if (newCollapsed == OnlineFriendsStreamFragment.this.collapsed) {
                return false;
            }
            OnlineFriendsStreamFragment.this.collapsed = newCollapsed;
            OnlineFriendsStreamFragment.this.updateForCurrentState();
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment.3 */
    class C13123 implements OnItemClickListener {
        C13123() {
        }

        public void onItemClick(View view, int position) {
            boolean z = false;
            ViewHolder holder = (ViewHolder) OnlineFriendsStreamFragment.this.recyclerView.findViewHolderForAdapterPosition(position);
            StatisticManager.getInstance().addStatisticEvent("stream-online-friend-options-visibility-changed", new Pair("collapsed", String.valueOf(OnlineFriendsStreamFragment.this.collapsed)));
            if (holder != null) {
                if (OnlineFriendsStreamFragment.this.collapsed) {
                    OnlineFriendsStreamFragment onlineFriendsStreamFragment = OnlineFriendsStreamFragment.this;
                    if (!OnlineFriendsStreamFragment.this.collapsed) {
                        z = true;
                    }
                    onlineFriendsStreamFragment.collapsed = z;
                    OnlineFriendsStreamFragment.this.onlineAdapter.getExpandedUsersUids().clear();
                    OnlineFriendsStreamFragment.this.onlineAdapter.setAnimationInUserInfo(holder.user);
                    OnlineFriendsStreamFragment.this.updateForCurrentState();
                    OnlineFriendsStreamFragment.this.handler.sendEmptyMessageDelayed(1, 3000);
                } else if (holder.options == null || holder.options.getVisibility() == 8) {
                    for (int i = 0; i < OnlineFriendsStreamFragment.this.recyclerView.getChildCount(); i++) {
                        View child = OnlineFriendsStreamFragment.this.recyclerView.getChildAt(i);
                        if (child != null) {
                            OnlineFriendsStreamFragment.this.onlineAdapter.closeChildOptionsIfPossible(child, holder);
                        }
                    }
                    holder.animateOptionsIn(0);
                } else {
                    holder.animateOptionsOut();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment.4 */
    class C13134 extends SimpleAnimationListener {
        C13134() {
        }

        public void onAnimationStart(Animation animation) {
            OnlineFriendsStreamFragment.this.collapseAnimating = true;
        }

        public void onAnimationEnd(Animation animation) {
            OnlineFriendsStreamFragment.this.updateHeaderText();
            OnlineFriendsStreamFragment.this.onlineAdapter.setCollapsed(false);
            OnlineFriendsStreamFragment.this.collapseAnimating = false;
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.OnlineFriendsStreamFragment.5 */
    class C13145 extends SimpleAnimationListener {
        C13145() {
        }

        public void onAnimationStart(Animation animation) {
            OnlineFriendsStreamFragment.this.collapseAnimating = true;
            OnlineFriendsStreamFragment.this.onlineAdapter.setCollapsed(true);
            OnlineFriendsStreamFragment.this.updateHeaderText();
        }

        public void onAnimationEnd(Animation animation) {
            OnlineFriendsStreamFragment.this.collapseAnimating = false;
        }
    }

    private class ViewChangeWidthAnimation extends Animation {
        private int endWidth;
        private int startWidth;

        public ViewChangeWidthAnimation(int startWidth, int endWidth) {
            this.startWidth = startWidth;
            this.endWidth = endWidth;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            OnlineFriendsStreamFragment.this.container.getLayoutParams().width = interpolatedTime == 1.0f ? this.endWidth : this.startWidth + ((int) (((float) (this.endWidth - this.startWidth)) * interpolatedTime));
            OnlineFriendsStreamFragment.this.container.requestLayout();
        }

        public boolean willChangeBounds() {
            return true;
        }
    }

    public OnlineFriendsStreamFragment() {
        this.blocker = ScrollLoadRecyclerViewBlocker.forIdleOnly();
        this.handler = new C13101();
    }

    public static Bundle createArguments() {
        Bundle args = new Bundle();
        UsersListFragment.initArguments(args, false, null, SelectionsMode.SINGLE, null);
        args.putBoolean("only_friends", true);
        return args;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.collapsed = Settings.getBoolValue(getActivity(), "stream_friends_online_collapsed", false);
        this.expandedWidth = getResources().getDimensionPixelOffset(2131231190);
        this.collapsedWidth = getResources().getDimensionPixelOffset(2131231189);
        this.container = (FrameInterceptTouchListenerLayout) view.findViewById(C0263R.id.container);
        this.container.setInterceptTouchListener(this);
        this.gestureDetector = new GestureDetector(getActivity(), new C13112());
        View headContainer = view.findViewById(2131625384);
        headContainer.setOnClickListener(this);
        this.textMessage = (TextView) headContainer.findViewById(2131625385);
        this.animatingPanel = view.findViewById(2131625383);
        this.icon = this.animatingPanel.findViewById(C0176R.id.icon);
        ViewGroup fragmentNoSaveStateLayout = (ViewGroup) this.container.getParent();
        fragmentNoSaveStateLayout.setClipChildren(false);
        this.fragmentRootContainer = (ViewGroup) fragmentNoSaveStateLayout.getParent();
        this.fragmentRootContainer.setClipChildren(false);
        this.fragmentRootContainer.bringToFront();
        ((ItemClickListenerControllerProvider) this.recyclerView.getAdapter()).getItemClickListenerController().addItemClickListener(new C13123());
        this.recyclerView.addOnScrollListener(this.blocker);
        updateForCurrentStateImmediate();
        if (flingSpeedThreshold == 0.0f) {
            flingSpeedThreshold = Utils.dipToPixels(1000.0f);
        }
    }

    public int getExpandedWidth() {
        return this.expandedWidth;
    }

    public int getCollapsedWidth() {
        return this.collapsedWidth;
    }

    public boolean isCollapsed() {
        return this.collapsed;
    }

    protected int getLayoutId() {
        return 2130903517;
    }

    public void onResume() {
        super.onResume();
        this.handler.sendEmptyMessageDelayed(0, 120000);
    }

    public void onPause() {
        super.onPause();
        Settings.storeBoolValue(getActivity(), "stream_friends_online_collapsed", this.collapsed);
        this.handler.removeMessages(0);
    }

    protected CursorSwapper createOnlyFriendAdapter() {
        CursorSwapper onlineStreamAdapter = new OnlineStreamAdapter(this, getActivity(), this.blocker);
        this.onlineAdapter = onlineStreamAdapter;
        return onlineStreamAdapter;
    }

    protected boolean isSortByLastOnline() {
        return false;
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        super.onLoadFinished((Loader) cursorLoader, cursor);
        updateHeaderText();
    }

    private void updateHeaderText() {
        int count = ((UsersInfoCursorAdapter) this.adapter).getUsersCount();
        String countStr = count >= 100 ? "99+" : String.valueOf(count);
        this.textMessage.setText(getStringLocalized(this.collapsed ? 2131166645 : 2131166644, countStr));
    }

    public void onClick(View v) {
        boolean z;
        this.handler.removeMessages(1);
        if (this.collapsed) {
            z = false;
        } else {
            z = true;
        }
        this.collapsed = z;
        StatisticManager instance = StatisticManager.getInstance();
        String str = "stream-online-friends-visibility-changed";
        Pair[] pairArr = new Pair[1];
        pairArr[0] = new Pair("visibility", this.collapsed ? "collapsed" : "expanded");
        instance.addStatisticEvent(str, pairArr);
        updateForCurrentState();
    }

    public boolean onTouch(View v, MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                this.wasAutohide = this.handler.hasMessages(1);
                if (this.wasAutohide) {
                    this.handler.removeMessages(1);
                    break;
                }
                break;
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.wasAutohide) {
                    this.handler.sendEmptyMessageDelayed(1, 3000);
                    this.wasAutohide = false;
                    break;
                }
                break;
        }
        return false;
    }

    private void updateForCurrentStateImmediate() {
        updateHeaderText();
        LayoutParams lp = this.container.getLayoutParams();
        lp.width = this.collapsed ? this.collapsedWidth : this.expandedWidth;
        this.container.setLayoutParams(lp);
        this.onlineAdapter.setCollapsed(this.collapsed);
        this.icon.setRotation(this.collapsed ? 180.0f : 0.0f);
    }

    private void updateForCurrentState() {
        int iconAngle;
        int startWidth;
        int endWidth;
        AnimationListener animationListener;
        if (this.collapsed) {
            iconAngle = 180;
            startWidth = this.expandedWidth;
            endWidth = this.collapsedWidth;
            animationListener = getCollapseAnimationListener();
        } else {
            iconAngle = 0;
            startWidth = this.collapsedWidth;
            endWidth = this.expandedWidth;
            animationListener = getExpandAnimationListener();
        }
        Animation animation = new ViewChangeWidthAnimation(startWidth, endWidth);
        animation.setDuration(300);
        animation.setAnimationListener(animationListener);
        this.fragmentRootContainer.startAnimation(animation);
        this.icon.animate().rotation((float) iconAngle).setDuration(300).start();
    }

    private AnimationListener getExpandAnimationListener() {
        if (this.expandAnimationListener == null) {
            this.expandAnimationListener = new C13134();
        }
        return this.expandAnimationListener;
    }

    private AnimationListener getCollapseAnimationListener() {
        if (this.collapseAnimationListener == null) {
            this.collapseAnimationListener = new C13145();
        }
        return this.collapseAnimationListener;
    }

    public boolean isCollapseAnimating() {
        return this.collapseAnimating;
    }
}

package ru.ok.android.ui.stream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.State;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Observable;
import java.util.Observer;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.TouchObserverHeader;
import ru.ok.android.ui.custom.TouchObserverHeader.ScrollListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.scroll.ScrollTopView.OnClickScrollListener;
import ru.ok.android.ui.stream.data.StreamData;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.ui.users.fragments.profiles.ProfileBaseFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileAccessInfo;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileType;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class BaseProfilesStreamListFragment<TProfileFragment extends ProfileBaseFragment> extends BaseStreamListFragment implements OnClickScrollListener, ProfileLoadCallBack {
    private final String SCROLL_VALUE_KEY;
    protected ProfileAccessInfo accessInfo;
    Drawable actionBarBgDrawable;
    protected int blockedInfoViewHeight;
    private Callback drawableCallback;
    Drawable gradientDrawable;
    protected boolean isProfileLoadError;
    protected TouchObserverHeader overlayHeaderView;
    protected TProfileFragment profileFragment;
    protected ErrorType profileLoadErrorType;
    protected View profileRightContainer;
    private ru.ok.android.ui.stream.BaseProfilesStreamListFragment$ru.ok.android.ui.stream.BaseProfilesStreamListFragment.ProfileStreamHeaderDecoration profileStreamHeaderDecoration;
    protected View profileTopContainer;
    protected ProfileType profileType;
    protected Observer profileViewChangeObserver;
    protected Observer profileViewMeasureObserver;
    protected ru.ok.android.ui.stream.BaseProfilesStreamListFragment$ru.ok.android.ui.stream.BaseProfilesStreamListFragment.RecyclerScrollHolder recyclerScrollHolder;
    private int toolbarHeight;

    /* renamed from: ru.ok.android.ui.stream.BaseProfilesStreamListFragment.1 */
    class C12111 implements Callback {
        C12111() {
        }

        public void invalidateDrawable(Drawable who) {
            ((AppCompatActivity) BaseProfilesStreamListFragment.this.getActivity()).getSupportActionBar().setBackgroundDrawable(who);
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseProfilesStreamListFragment.2 */
    static /* synthetic */ class C12122 {
        static final /* synthetic */ int[] f117x22ae40df;

        static {
            f117x22ae40df = new int[ErrorType.values().length];
            try {
                f117x22ae40df[ErrorType.NO_INTERNET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f117x22ae40df[ErrorType.RESTRICTED_ACCESS_ACTION_BLOCKED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f117x22ae40df[ErrorType.USER_BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f117x22ae40df[ErrorType.RESTRICTED_ACCESS_FOR_NON_FRIENDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f117x22ae40df[ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f117x22ae40df[ErrorType.YOU_ARE_IN_BLACK_LIST.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private class ProfileStreamHeaderDecoration extends ItemDecoration {
        private int headerHeight;

        private ProfileStreamHeaderDecoration() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildPosition(view) == 0) {
                outRect.top += this.headerHeight;
            }
        }

        public void setHeaderHeight(int newHeaderHeight) {
            this.headerHeight = newHeaderHeight;
        }
    }

    class ProfileViewChangeObserver implements Observer {

        /* renamed from: ru.ok.android.ui.stream.BaseProfilesStreamListFragment.ProfileViewChangeObserver.1 */
        class C12131 implements ScrollListener {
            C12131() {
            }

            public void onScroll(float distanceX, float distanceY) {
                BaseProfilesStreamListFragment.this.recyclerView.scrollBy(0, (int) distanceY);
            }

            public boolean onFling(float velocityX, float velocityY) {
                return BaseProfilesStreamListFragment.this.recyclerView.fling(0, -((int) velocityY));
            }
        }

        ProfileViewChangeObserver() {
        }

        public void update(Observable observable, Object data) {
            View fragmentView = (View) data;
            int height = fragmentView.getMeasuredHeight();
            BaseProfilesStreamListFragment.this.onHeaderFragmentSizeChanged(fragmentView.getMeasuredWidth(), height);
            BaseProfilesStreamListFragment.this.updateHeaderHeight(height);
            BaseProfilesStreamListFragment.this.updateOverlayHeightAndTranslation();
            if (BaseProfilesStreamListFragment.this.overlayHeaderView != null) {
                BaseProfilesStreamListFragment.this.overlayHeaderView.setTouchObserverView(fragmentView);
                BaseProfilesStreamListFragment.this.overlayHeaderView.setScrollListener(new C12131());
            }
            if (BaseProfilesStreamListFragment.this.profileTopContainer != null && BaseProfilesStreamListFragment.this.recyclerLayoutManager.findFirstVisibleItemPosition() > 0) {
                BaseProfilesStreamListFragment.this.recyclerScrollHolder.onScrolled(BaseProfilesStreamListFragment.this.recyclerView, 0, ((int) (((float) BaseProfilesStreamListFragment.this.profileTopContainer.getMeasuredHeight()) * ((Float) BaseProfilesStreamListFragment.this.profileTopContainer.getTag()).floatValue())) * 2);
            }
        }
    }

    class ProfileViewMeasureObserver implements Observer {
        ProfileViewMeasureObserver() {
        }

        public void update(Observable observable, Object data) {
            BaseProfilesStreamListFragment.this.updateHeaderHeight(((View) data).getMeasuredHeight());
        }
    }

    class RecyclerScrollHolder extends OnScrollListener {
        RecyclerScrollHolder() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            BaseProfilesStreamListFragment.this.onScrolling(-dy);
        }
    }

    protected abstract TProfileFragment createProfileFragment();

    public BaseProfilesStreamListFragment() {
        this.SCROLL_VALUE_KEY = "scroll_value_key";
        this.drawableCallback = new C12111();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.toolbarHeight = DimenUtils.getToolbarHeight(getContext());
    }

    public void onRefresh() {
        if (this.profileFragment != null) {
            this.profileFragment.updateProfile();
            if (this.swipeRefresh != null) {
                this.swipeRefresh.setRefreshing(true);
                return;
            }
            return;
        }
        super.onRefresh();
    }

    public void onProfileInfoLoad(ProfileType type, ProfileAccessInfo accessInfo) {
        boolean somethingChanged = true;
        Logger.m173d("%s", accessInfo);
        if (!this.isProfileLoadError && this.profileType == type && ((this.accessInfo != null || accessInfo == null) && (this.accessInfo == null || this.accessInfo.equals(accessInfo)))) {
            somethingChanged = false;
        }
        this.isProfileLoadError = false;
        this.accessInfo = accessInfo;
        this.profileType = type;
        if (somethingChanged) {
            initStream();
        }
    }

    public void onProfileInfoLoadError(ProfileType type, ErrorType errorType) {
        boolean somethingChanged;
        Logger.m173d("type=%s", type);
        if (this.isProfileLoadError && this.profileType == type) {
            somethingChanged = false;
        } else {
            somethingChanged = true;
        }
        this.profileType = type;
        this.isProfileLoadError = true;
        this.profileLoadErrorType = errorType;
        this.accessInfo = null;
        if (somethingChanged) {
            initStream();
        }
        if (this.swipeRefresh != null) {
            this.swipeRefresh.setRefreshing(false);
        }
    }

    public void onProfileRefresh(ProfileType type, ProfileAccessInfo info) {
        if (!info.isDisabled && !info.isBlocked && (!info.isPrivate || info.isFriendOrMember)) {
            super.onRefresh();
        } else if (this.swipeRefresh != null) {
            this.swipeRefresh.setRefreshing(false);
        }
    }

    public void onStreamRefreshed(StreamData data, int benchmarkSeqId) {
        super.onStreamRefreshed(data, benchmarkSeqId);
        if (this.profileTopContainer != null) {
            this.profileTopContainer.setTranslationY(0.0f);
            updateOverlayHeightAndTranslation();
        }
    }

    public void onScrollTopClick(int count) {
        super.onScrollTopClick(count);
    }

    protected void initDataFragment(Bundle savedInstanceState) {
        super.initDataFragment(savedInstanceState);
        this.dataFragment.setInitOnCreate(false);
    }

    protected void initStream() {
        int titleResId = 2131165947;
        if (this.accessInfo != null) {
            if (this.accessInfo.canAccess()) {
                hideStreamBlockedInfo();
                this.emptyView.setType(Type.EMPTY);
                this.emptyView.setState(SmartEmptyViewAnimated.State.LOADING);
                this.dataFragment.init();
                return;
            }
            int messageResId;
            this.dataFragment.reset();
            if (this.streamItemRecyclerAdapter != null) {
                this.streamItemRecyclerAdapter.setItems(null);
                this.streamItemRecyclerAdapter.clear();
                this.streamItemRecyclerAdapter.notifyDataSetChanged();
            }
            if (this.accessInfo.isDisabled) {
                titleResId = this.profileType == ProfileType.GROUP ? 2131165945 : 2131166405;
                if (this.profileType == ProfileType.GROUP) {
                    messageResId = 2131165946;
                } else {
                    messageResId = 2131166406;
                }
            } else if (!this.accessInfo.isBlocked) {
                if (this.profileType != ProfileType.GROUP) {
                    titleResId = 2131166407;
                }
                messageResId = this.profileType == ProfileType.GROUP ? 2131166002 : 2131166001;
            } else if (this.accessInfo.isPrivate) {
                if (this.profileType != ProfileType.GROUP) {
                    titleResId = 2131166407;
                }
                messageResId = this.profileType == ProfileType.GROUP ? 2131165386 : 2131165385;
            } else {
                titleResId = 2131166003;
                messageResId = this.profileType == ProfileType.GROUP ? 2131166886 : 2131166885;
            }
            showStreamBlockedInfo(titleResId, messageResId);
        } else if (this.isProfileLoadError) {
            Type type;
            SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
            if (this.profileLoadErrorType == null) {
                type = Type.ERROR;
            } else {
                type = convertProfileLoadErrorType(this.profileLoadErrorType);
            }
            smartEmptyViewAnimated.setType(type);
            this.emptyView.setState(SmartEmptyViewAnimated.State.LOADED);
            if (this.streamItemRecyclerAdapter == null || this.streamItemRecyclerAdapter.getItemCount() <= 0) {
                if ((this.profileFragment instanceof ProfileGroupFragment) && !((ProfileGroupFragment) this.profileFragment).isProfileInfoAvailable()) {
                    this.emptyView.setPadding(this.emptyView.getPaddingLeft(), (this.emptyView.getPaddingTop() + this.toolbarHeight) + getResources().getDimensionPixelSize(2131231116), this.emptyView.getPaddingRight(), getResources().getDimensionPixelSize(2131231194));
                }
                this.emptyView.setVisibility(0);
                this.recyclerView.setVisibility(8);
            } else {
                this.emptyView.setVisibility(8);
                this.recyclerView.setVisibility(0);
            }
            hideStreamBlockedInfo();
        }
    }

    private Type convertProfileLoadErrorType(ErrorType errorType) {
        switch (C12122.f117x22ae40df[errorType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Type.NO_INTERNET;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.profileType == ProfileType.GROUP) {
                    return Type.GROUP_BLOCKED;
                }
                return Type.USER_BLOCKED;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
                return Type.RESTRICTED_ACCESS_FOR_FRIENDS;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return Type.RESTRICTED_YOU_ARE_IN_BLACK_LIST;
            default:
                return Type.ERROR;
        }
    }

    protected void hideStreamBlockedInfo() {
        View rootView = getView();
        if (rootView != null) {
            View view = rootView.findViewById(2131625271);
            if (view != null && !(view instanceof ViewStub)) {
                view.setVisibility(8);
            }
        }
    }

    protected void showStreamBlockedInfo(int titleResId, int messageResId) {
        View rootView = getView();
        if (rootView != null) {
            Context context = rootView.getContext();
            this.emptyView.setType(Type.EMPTY);
            this.emptyView.setVisibility(8);
            View view = rootView.findViewById(2131625271);
            if (view instanceof ViewStub) {
                view = ((ViewStub) view).inflate();
            }
            if (view != null) {
                if (this.blockedInfoViewHeight == 0 && this.overlayHeaderView != null) {
                    onHeaderFragmentSizeChanged(this.overlayHeaderView.getWidth(), this.overlayHeaderView.getHeight());
                }
                TextView titleTextView = (TextView) view.findViewById(2131625324);
                TextView messageTextView = (TextView) view.findViewById(2131625325);
                if (titleTextView != null) {
                    titleTextView.setText(LocalizationManager.getString(context, titleResId));
                }
                if (messageTextView != null) {
                    messageTextView.setText(LocalizationManager.getString(context, messageResId));
                    return;
                }
                return;
            }
            Toast.makeText(context, LocalizationManager.getString(context, messageResId), 1).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = super.onCreateView(inflater, container, savedInstanceState);
        this.profileTopContainer = mainView.findViewById(2131625270);
        this.profileRightContainer = mainView.findViewById(2131625272);
        this.profileFragment = createProfileFragment();
        if (this.profileRightContainer != null) {
            addProfileFragment(this.profileFragment, 2131625272);
        } else if (this.profileTopContainer != null) {
            addProfileFragment(this.profileFragment, 2131625270);
            this.profileStreamHeaderDecoration = new ProfileStreamHeaderDecoration();
            this.recyclerView.addItemDecoration(this.profileStreamHeaderDecoration);
            this.overlayHeaderView = (TouchObserverHeader) mainView.findViewById(2131624932);
        }
        if (!DeviceUtils.isSmall(getContext())) {
            int emptyViewPaddingBottom = DimenUtils.getToolbarHeight(getContext());
            if (DeviceUtils.needSlidingMenuFixForWebView(getContext())) {
                emptyViewPaddingBottom += getResources().getDimensionPixelSize(2131231194);
            }
            this.emptyView.setPadding(this.emptyView.getPaddingLeft(), this.emptyView.getPaddingTop(), this.emptyView.getPaddingRight(), emptyViewPaddingBottom);
        }
        return mainView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.profileTopContainer != null) {
            outState.putFloat("scroll_value_key", this.profileTopContainer.getTranslationY() / ((float) this.profileTopContainer.getMeasuredHeight()));
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        float transactionY = 0.0f;
        super.onViewCreated(view, savedInstanceState);
        if (this.profileTopContainer != null) {
            if (savedInstanceState != null) {
                transactionY = savedInstanceState.getFloat("scroll_value_key", 0.0f);
            }
            this.profileTopContainer.setTag(Float.valueOf(transactionY));
        }
        this.recyclerScrollHolder = new RecyclerScrollHolder();
        this.recyclerViewScrollListeners.addListener(this.recyclerScrollHolder);
        if (DeviceUtils.getType(getContext()) == DeviceLayoutType.SMALL) {
            initOverlayActionBar(MotionEventCompat.ACTION_MASK, 0);
        }
    }

    private void initOverlayActionBar(int startGradientAlpha, int startActionBarAlpha) {
        this.gradientDrawable = getResources().getDrawable(2130837608);
        this.actionBarBgDrawable = new ColorDrawable(getResources().getColor(2131493081));
        Drawable actionBarBackgroundDrawable = new LayerDrawable(new Drawable[]{this.gradientDrawable, this.actionBarBgDrawable});
        this.actionBarBgDrawable.setAlpha(startActionBarAlpha);
        this.gradientDrawable.setAlpha(startGradientAlpha);
        ((BaseCompatToolbarActivity) getActivity()).getAppBarLayout().setBackgroundDrawable(actionBarBackgroundDrawable);
        if (VERSION.SDK_INT < 16) {
            actionBarBackgroundDrawable.setCallback(this.drawableCallback);
        }
    }

    private void updateHeaderHeight(int height) {
        if (this.profileStreamHeaderDecoration != null) {
            this.profileStreamHeaderDecoration.setHeaderHeight(height);
        }
        updateOverlayHeightAndTranslation();
    }

    protected void onHeaderFragmentSizeChanged(int width, int height) {
        if (this.profileTopContainer != null) {
            int remainingHeight = (getView().getMeasuredHeight() - height) - getToolBarHeight();
            int emptyViewTopMargin = height;
            if (DeviceUtils.getType(getContext()) != DeviceLayoutType.SMALL) {
                emptyViewTopMargin += this.toolbarHeight;
            }
            ((MarginLayoutParams) this.emptyView.getLayoutParams()).topMargin = emptyViewTopMargin;
            this.emptyView.requestLayout();
            View blockedInfoView = getView().findViewById(2131625271);
            this.blockedInfoViewHeight = remainingHeight;
            if (blockedInfoView != null) {
                updateBlockedInfoViewSize(blockedInfoView, width, this.blockedInfoViewHeight);
            }
        }
    }

    private int getToolBarHeight() {
        Activity activity = getActivity();
        if (activity == null || !BaseCompatToolbarActivity.isUseTabbar(activity)) {
            return 0;
        }
        View view = ((BaseTabbarManager) activity).getTabbarView();
        if (view != null) {
            return view.getMeasuredHeight();
        }
        return 0;
    }

    private void updateBlockedInfoViewSize(View view, int width, int height) {
        view.setLayoutParams(new LayoutParams(width, height, 80));
    }

    protected void addProfileFragment(TProfileFragment profileFragment, int containerViewId) {
        if (this.profileTopContainer != null) {
            Observer profileViewChangeObserver = new ProfileViewChangeObserver();
            this.profileViewChangeObserver = profileViewChangeObserver;
            profileFragment.addViewChangeObserver(profileViewChangeObserver);
            profileViewChangeObserver = new ProfileViewMeasureObserver();
            this.profileViewMeasureObserver = profileViewChangeObserver;
            profileFragment.addMeasureViewChangeObserver(profileViewChangeObserver);
        }
        getChildFragmentManager().beginTransaction().replace(containerViewId, profileFragment).commit();
    }

    protected void onScrolling(int deltaY) {
        BaseCompatToolbarActivity activity = (BaseCompatToolbarActivity) getActivity();
        if (this.profileTopContainer == null || DeviceUtils.getType(getContext()) != DeviceLayoutType.SMALL) {
            activity.setToolbarTitleTextAlpha(MotionEventCompat.ACTION_MASK);
            activity.setShadowAlpha(MotionEventCompat.ACTION_MASK);
            return;
        }
        float resultDelta = 0.0f;
        float translationY = this.profileTopContainer.getTranslationY();
        int profileTopHeight = this.profileTopContainer.getMeasuredHeight();
        boolean needTranslateProfile = true;
        if (deltaY >= 0 || (2.0f * translationY) + ((float) profileTopHeight) <= 0.0f) {
            int firstVisible = ((LinearLayoutManager) this.recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            if (deltaY <= 0 || firstVisible != 0 || translationY >= 0.0f) {
                needTranslateProfile = false;
            } else {
                resultDelta = ((float) deltaY) / 2.0f;
            }
        } else {
            resultDelta = ((float) deltaY) / 2.0f;
        }
        updateOverlayHeightAndTranslation();
        if (needTranslateProfile) {
            float newTranslationY = Math.min(Math.max(translationY + resultDelta, (float) ((-profileTopHeight) / 2)), 0.0f);
            this.profileTopContainer.setTranslationY(newTranslationY);
            int newAlpha = Math.min(MotionEventCompat.ACTION_MASK, ((int) (255.0f * Math.abs(newTranslationY / ((float) this.profileTopContainer.getMeasuredHeight())))) * 2);
            if (this.actionBarBgDrawable != null) {
                this.actionBarBgDrawable.setAlpha(newAlpha);
                this.gradientDrawable.setAlpha(255 - newAlpha);
            }
            activity.setToolbarTitleTextAlpha(newAlpha);
            activity.setShadowAlpha(newAlpha);
        }
    }

    private void updateOverlayHeightAndTranslation() {
        if (this.overlayHeaderView != null) {
            int bottom = calculateOverlayBottom();
            float translationY = this.profileTopContainer.getTranslationY();
            int height = (int) (((float) bottom) - translationY);
            if (DeviceUtils.getType(getContext()) != DeviceLayoutType.SMALL) {
                height += this.toolbarHeight;
            }
            ViewGroup.LayoutParams lp = this.overlayHeaderView.getLayoutParams();
            if (lp == null) {
                lp = new LayoutParams(-1, LinearLayoutManager.INVALID_OFFSET);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.emptyView.getLayoutParams();
                marginLayoutParams.topMargin = height;
                if (!DeviceUtils.isSmall(getContext())) {
                    marginLayoutParams.bottomMargin = height;
                }
            }
            if (lp.height != height) {
                lp.height = height;
                this.overlayHeaderView.setLayoutParams(lp);
                this.overlayHeaderView.setMeasuredDimensionSuper(this.overlayHeaderView.getMeasuredWidth(), height);
                this.overlayHeaderView.setTranslationY(translationY);
            }
        }
    }

    private int calculateOverlayBottom() {
        int bottom = 0;
        View firstStreamView = (this.recyclerView == null || this.recyclerView.getChildCount() == 0) ? null : this.recyclerView.getChildAt(0);
        if (firstStreamView != null) {
            return Math.max(firstStreamView.getTop(), 0);
        }
        if (this.profileFragment == null) {
            return 0;
        }
        View profileView = this.profileFragment.getView();
        if (profileView != null) {
            bottom = profileView.getBottom();
        }
        return bottom;
    }

    @NonNull
    protected Type getEmptyViewType() {
        return Type.STREAM_PROFILE;
    }
}

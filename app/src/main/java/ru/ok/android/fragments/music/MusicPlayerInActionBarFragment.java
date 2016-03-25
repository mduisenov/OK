package ru.ok.android.fragments.music;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.music.view.FloatingPlayerButton;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.fragments.PlayerDataUpdateReceiver;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.ui.tabbar.Tabbar.OnTranslationChangeListener;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.KeyBoardUtils.OnKeyboardForceHiddenListener;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.animation.PlayerAnimationHelper;

public abstract class MusicPlayerInActionBarFragment extends BaseFragment implements AnimatorListener, OnClickListener, OnKeyboardForceHiddenListener {
    private boolean buttonAnimationCanceled;
    private PlayerDataUpdateReceiver dataUpdateReceiver;
    private FloatingPlayerButton floatingPlayerButton;
    private boolean searchIsFocused;
    private OnTranslationChangeListener tabbarTranslationListener;

    /* renamed from: ru.ok.android.fragments.music.MusicPlayerInActionBarFragment.1 */
    class C03161 implements OnTranslationChangeListener {
        final /* synthetic */ OdklTabbar val$tabbar;

        C03161(OdklTabbar odklTabbar) {
            this.val$tabbar = odklTabbar;
        }

        public void onTranslationChanged(float translationX, float translationY, float translationZ) {
            MusicPlayerInActionBarFragment.this.floatingPlayerButton.setHideAmount((int) translationY, this.val$tabbar.getHeight());
        }
    }

    public MusicPlayerInActionBarFragment() {
        this.buttonAnimationCanceled = false;
        this.searchIsFocused = true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.floatingPlayerButton != null) {
            this.floatingPlayerButton.resetAnimation();
        }
    }

    public void onClick(View v) {
        if (getActivity() != null && !getDataUpdateReceiver().isRevealAnimationQueued()) {
            if (PlayerAnimationHelper.isAnimationEnabled()) {
                getDataUpdateReceiver().queueRevealAnimation();
            }
            NavigationHelper.showMusicPlayer(getActivity(), true);
        }
    }

    private ViewGroup getFloatingButtonContainer() {
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof BaseCompatToolbarActivity)) {
            return null;
        }
        return ((BaseCompatToolbarActivity) getActivity()).getFullContainer();
    }

    public void updateMusicState() {
        getActivity().startService(MusicService.getStateIntent(getActivity(), getDataUpdateReceiver()));
        if (this.floatingPlayerButton != null && getDataUpdateReceiver().updateFloatingPlayerButton(this.floatingPlayerButton) && this.floatingPlayerButton.getParent() == null) {
            addPlayerButton();
        }
    }

    protected void onHideFragment() {
        super.onHideFragment();
        if (!isFragmentVisible() && isPlayFloatingButtonRequired()) {
            removePlayerButton();
        }
    }

    protected final void onShowFragment() {
        super.onShowFragment();
        updateMusicState();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyBoardUtils.addOnKeyboardHiddenListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        KeyBoardUtils.removeOnKeyboardHiddenListner(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflateFloatingPlayerButton(inflater);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void inflateFloatingPlayerButton() {
        Context context = getContext();
        if (context != null) {
            inflateFloatingPlayerButton(LayoutInflater.from(context));
        }
    }

    public void inflateFloatingPlayerButton(LayoutInflater inflater) {
        if (DeviceUtils.isSmall(getContext()) && isPlayFloatingButtonRequired() && this.floatingPlayerButton == null) {
            this.floatingPlayerButton = (FloatingPlayerButton) inflater.inflate(2130903395, getFloatingButtonContainer(), false);
            this.floatingPlayerButton.setOnClickListener(this);
        }
    }

    public boolean isPlayFloatingButtonRequired() {
        return true;
    }

    public void onPause() {
        super.onPause();
        updateMusicState();
    }

    public void onResume() {
        super.onResume();
        updateMusicState();
    }

    protected PlayerDataUpdateReceiver getDataUpdateReceiver() {
        if (this.dataUpdateReceiver == null) {
            this.dataUpdateReceiver = new PlayerDataUpdateReceiver(this);
        }
        return this.dataUpdateReceiver;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(2131625511);
        if (getContext() != null && searchItem != null) {
            ((SearchView) MenuItemCompat.getActionView(searchItem)).clearFocus();
        }
    }

    protected void setSearchIsFocused(boolean isSearchFocused) {
        this.searchIsFocused = isSearchFocused;
    }

    public void onKeyboardForceHidden() {
        setSearchIsFocused(false);
    }

    public void onStop() {
        super.onStop();
        getDataUpdateReceiver().unSubscribe();
        removePlayerButton();
    }

    private void removePlayerButton() {
        if (this.floatingPlayerButton != null) {
            ViewGroup parent = (ViewGroup) this.floatingPlayerButton.getParent();
            if (parent != null) {
                parent.removeView(this.floatingPlayerButton);
            }
        }
    }

    public void onStart() {
        super.onStart();
        getDataUpdateReceiver().subscribe();
        addPlayerButton();
    }

    private void addPlayerButton() {
        ViewGroup container = getFloatingButtonContainer();
        if (this.floatingPlayerButton != null && container != null && !isHidden()) {
            if (this.floatingPlayerButton.getParent() == null) {
                container.addView(this.floatingPlayerButton);
            }
            this.floatingPlayerButton.resetAnimation();
            OdklTabbar tabbar = ((BaseTabbarManager) getActivity()).getTabbarView();
            OnTranslationChangeListener c03161 = new C03161(tabbar);
            this.tabbarTranslationListener = c03161;
            tabbar.addWeakTranslationListener(c03161);
        }
    }

    protected MusicFragmentMode getMode() {
        MusicFragmentMode mode = (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
        if (mode == null) {
            return MusicFragmentMode.STANDARD;
        }
        return mode;
    }

    public void onAnimationStart(Animator animation) {
        this.buttonAnimationCanceled = false;
    }

    public void onAnimationEnd(Animator animation) {
        if (!this.buttonAnimationCanceled) {
            this.floatingPlayerButton.removeAnimationListener(this);
        }
    }

    public void onAnimationCancel(Animator animation) {
        this.buttonAnimationCanceled = true;
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public void onMediaPlayerState(BusEvent event) {
    }
}

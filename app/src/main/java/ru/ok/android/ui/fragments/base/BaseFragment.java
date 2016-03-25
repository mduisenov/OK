package ru.ok.android.ui.fragments.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.statistics.liveInternet.LiveInternetStatisticManager;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.activity.compat.CoordinatorManager;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.indexing.Action;
import ru.ok.android.utils.localization.base.LocalizedFragment;

public abstract class BaseFragment extends LocalizedFragment {
    protected static final Animation NESTED_FRAGMENT_EXIT_DUMMY_ANIMATION;
    private Action cacheIndexingAction;
    private GoogleApiClient client;
    private WebLinksProcessor webLinksProcessor;

    interface ChildFragmentVisitor {
        void visitBaseFragment(BaseFragment baseFragment);
    }

    /* renamed from: ru.ok.android.ui.fragments.base.BaseFragment.1 */
    class C08141 implements ChildFragmentVisitor {
        C08141() {
        }

        public void visitBaseFragment(BaseFragment fragment) {
            fragment.onHideFragment();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.base.BaseFragment.2 */
    class C08152 implements ChildFragmentVisitor {
        C08152() {
        }

        public void visitBaseFragment(BaseFragment fragment) {
            fragment.onShowFragment();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.base.BaseFragment.3 */
    class C08163 implements Runnable {
        final /* synthetic */ ActionBar val$actionBar;

        C08163(ActionBar actionBar) {
            this.val$actionBar = actionBar;
        }

        public void run() {
            if (BaseFragment.this.isFragmentVisible()) {
                View customView = BaseFragment.this.getActionBarCustomView();
                if (customView != null) {
                    this.val$actionBar.setDisplayShowCustomEnabled(true);
                    this.val$actionBar.setCustomView(customView, new LayoutParams(-1, (int) BaseFragment.this.getContext().getResources().getDimension(2131230920), 8388659));
                    return;
                }
                CharSequence title = BaseFragment.this.getTitle();
                if (!TextUtils.equals(this.val$actionBar.getTitle(), title)) {
                    this.val$actionBar.setTitle(title);
                }
                CharSequence subtitle = BaseFragment.this.getSubtitle();
                if (!TextUtils.equals(this.val$actionBar.getSubtitle(), subtitle)) {
                    this.val$actionBar.setSubtitle(subtitle);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.base.BaseFragment.4 */
    class C08174 implements ChildFragmentVisitor {
        final /* synthetic */ AtomicReference val$result;

        C08174(AtomicReference atomicReference) {
            this.val$result = atomicReference;
        }

        public void visitBaseFragment(BaseFragment fragment) {
            if (!((Boolean) this.val$result.get()).booleanValue() && fragment.isFragmentVisible() && fragment.handleBack()) {
                this.val$result.set(Boolean.valueOf(true));
            }
        }
    }

    static {
        NESTED_FRAGMENT_EXIT_DUMMY_ANIMATION = new AlphaAnimation(1.0f, 1.0f);
        NESTED_FRAGMENT_EXIT_DUMMY_ANIMATION.setDuration(300);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        updateActionBarState();
    }

    protected boolean isIndexingFragment() {
        return false;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startUpdateLiveInternetStatistics();
        if (isIndexingFragment()) {
            this.client = new Builder(getActivity()).addApi(AppIndex.APP_INDEX_API).build();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void onStart() {
        super.onStart();
        if (this.client != null) {
            this.client.connect();
            onAppIndexingStart(this.client);
        }
    }

    public void onStop() {
        super.onStop();
        if (this.client != null) {
            onAppIndexingStop(this.client);
            this.client.disconnect();
        }
    }

    protected Action getIndexingAction() {
        if (this.cacheIndexingAction == null) {
            this.cacheIndexingAction = createIndexingAction();
        }
        return this.cacheIndexingAction;
    }

    protected Action createIndexingAction() {
        return null;
    }

    private void onAppIndexingStart(GoogleApiClient client) {
        Action action = getIndexingAction();
        Activity activity = getActivity();
        if (action != null && activity != null) {
            AppIndex.AppIndexApi.view(client, activity, action.getAppUri(), action.getTitle(), action.getWebUri(), null);
        }
    }

    private void onAppIndexingStop(GoogleApiClient client) {
        Action action = getIndexingAction();
        Activity activity = getActivity();
        if (action != null && activity != null) {
            AppIndex.AppIndexApi.viewEnd(client, activity, action.getAppUri());
        }
    }

    protected void restartAppIndexing() {
        if (this.client != null) {
            if (this.cacheIndexingAction != null) {
                Activity activity = getActivity();
                if (activity != null) {
                    AppIndex.AppIndexApi.viewEnd(this.client, activity, this.cacheIndexingAction.getAppUri());
                    this.cacheIndexingAction = null;
                }
            }
            onAppIndexingStart(this.client);
        }
    }

    public final void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (getActivity() != null) {
            if (hidden) {
                onHideFragment();
                walkThroughChildFragments(new C08141());
                return;
            }
            onShowFragment();
            walkThroughChildFragments(new C08152());
        }
    }

    protected void onHideFragment() {
        if (!isFragmentVisible()) {
            dispatchRemoveFab();
        }
    }

    protected void removeFab() {
    }

    protected void onShowFragment() {
        updateActionBarState();
        dispatchEnsureFab();
    }

    private void dispatchRemoveFab() {
        if (getCoordinatorManager() != null) {
            removeFab();
        }
    }

    private void dispatchEnsureFab() {
        if (getCoordinatorManager() != null) {
            ensureFab();
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                List<Fragment> fragments = fm.getFragments();
                if (fragments != null) {
                    int size = fragments.size();
                    for (int i = 0; i < size; i++) {
                        Fragment f = (Fragment) fragments.get(i);
                        if (f != null && f != this && f.getId() == getId() && (f instanceof BaseFragment)) {
                            ((BaseFragment) f).removeFab();
                        }
                    }
                }
            }
        }
    }

    public void updateActionBarState() {
        if (getActivity() != null && getParentFragment() == null && !isHidden()) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (!getShowsDialog()) {
                    getActivity().runOnUiThread(new C08163(actionBar));
                }
            }
        }
    }

    @Nullable
    protected ActionBar safeGetSupportActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            return null;
        }
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            actionBar = null;
        }
        return actionBar;
    }

    protected void setTitle(CharSequence title) {
        ActionBar actionBar = safeGetSupportActionBar();
        if (actionBar != null && !TextUtils.equals(actionBar.getTitle(), title)) {
            actionBar.setTitle(title);
        }
    }

    protected void setSubTitle(CharSequence subtitle) {
        ActionBar actionBar = safeGetSupportActionBar();
        if (actionBar != null && !TextUtils.equals(actionBar.getSubtitle(), subtitle)) {
            actionBar.setSubtitle(subtitle);
        }
    }

    protected void setTitleIfVisible(CharSequence title) {
        if (isFragmentVisible()) {
            setTitle(title);
        }
    }

    protected void setSubTitleIfVisible(CharSequence title) {
        if (isFragmentVisible()) {
            setSubTitle(title);
        }
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131165394);
    }

    protected CharSequence getSubtitle() {
        return null;
    }

    protected View getActionBarCustomView() {
        return null;
    }

    protected void startUpdateLiveInternetStatistics() {
        LiveInternetStatisticManager.getInstance().addEvent(getActivity());
    }

    protected ServiceHelper getServiceHelper() {
        return Utils.getServiceHelper();
    }

    public void onResume() {
        super.onResume();
        updateActionBarState();
        if (!isFragmentViewVisible(this) || isFragmentOverlayed()) {
            dispatchRemoveFab();
        } else {
            dispatchEnsureFab();
        }
    }

    private boolean isFragmentOverlayed() {
        List<Fragment> fragments = getFragmentManager().getFragments();
        int id = getId();
        if (!DeviceUtils.isSmall(getContext()) && (id == 2131625149 || id == 2131625150 || id == 2131625148)) {
            Fragment fullScreenContainer = getFragmentManager().findFragmentById(2131624880);
            if (fullScreenContainer != null && isFragmentViewVisible(fullScreenContainer)) {
                return true;
            }
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = (Fragment) fragments.get(i);
            if (fragment != null) {
                if (fragment == this) {
                    return false;
                }
                if (fragment.getId() == id && isFragmentViewVisible(fragment)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void ensureFab() {
    }

    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    protected void hideKeyboard() {
        if (getActivity() != null) {
            KeyBoardUtils.hideKeyBoard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
        }
    }

    public boolean handleBack() {
        AtomicReference<Boolean> result = new AtomicReference(Boolean.valueOf(false));
        walkThroughChildFragments(new C08174(result));
        return ((Boolean) result.get()).booleanValue();
    }

    public void onLocalizationChanged() {
        super.onLocalizationChanged();
        updateActionBarState();
    }

    @Subscribe(on = 2131623946, to = 2131624233)
    public final void onConnectionAvailable(Object event) {
        onInternetAvailable();
    }

    protected void onInternetAvailable() {
    }

    public void onContextMenuClosed() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalBus.register(this);
    }

    public void onDestroy() {
        GlobalBus.unregister(this);
        super.onDestroy();
    }

    protected boolean isFragmentVisible() {
        return isResumed() && !isHidden() && (getParentFragment() == null || (getParentFragment().isResumed() && !getParentFragment().isHidden()));
    }

    protected void showToastIfVisible(int resourceId, int duration) {
        if (isResumed() && isVisible()) {
            Toast.makeText(getActivity(), getStringLocalized(resourceId), duration).show();
        }
    }

    protected void showTimedToastIfVisible(int resourceId, int duration) {
        if (isResumed() && isVisible()) {
            TimeToast.show(getActivity(), resourceId, duration);
        }
    }

    protected void showTimedToastIfVisible(String text, int duration) {
        if (isResumed() && isVisible()) {
            TimeToast.show(getActivity(), text, duration);
        }
    }

    public final WebLinksProcessor getWebLinksProcessor() {
        if (this.webLinksProcessor == null) {
            this.webLinksProcessor = new WebLinksProcessor(getActivity(), false);
        }
        return this.webLinksProcessor;
    }

    protected final ActionBar getSupportActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        return activity != null ? activity.getSupportActionBar() : null;
    }

    public void appBarExpand() {
        BaseCompatToolbarActivity activity = (BaseCompatToolbarActivity) getActivity();
        if (activity != null) {
            activity.appBarExpandAnimated();
        }
    }

    @Nullable
    protected CoordinatorManager getCoordinatorManager() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return ((BaseCompatToolbarActivity) activity).getCoordinatorManager();
    }

    private void walkThroughChildFragments(ChildFragmentVisitor visitor) {
        FragmentManager manager = getChildFragmentManager();
        if (manager != null) {
            List<Fragment> fragments = manager.getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null && (fragment instanceof BaseFragment)) {
                        visitor.visitBaseFragment((BaseFragment) fragment);
                    }
                }
            }
        }
    }

    private static boolean isFragmentViewVisible(Fragment f) {
        return f.isAdded() && !f.isHidden() && f.getView() != null && f.getView().getVisibility() == 0;
    }
}

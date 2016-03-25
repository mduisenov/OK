package ru.ok.android.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.OnRepeatClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.RefreshProviderOnRefreshListener;

public abstract class RefreshableRecyclerFragmentHelper implements OnRepeatClickListener, OnStubButtonClickListener, RefreshProviderOnRefreshListener {
    protected final Context context;
    private final int emptyViewTextResId;
    private final BaseFragment fragment;
    protected RefreshableListListener listener;
    private RecyclerView refreshListView;
    private RefreshProvider refreshProvider;
    private final String refreshSettingsName;
    private final List<ErrorType> silentErrorTypes;
    private SmartEmptyView smartEmptyView;
    private SmartEmptyViewAnimated smartEmptyViewAnimated;

    public interface RefreshableListListener {
        void onFinishedRefresh(boolean z, ErrorType errorType);

        void onStartedRefresh(boolean z);
    }

    protected abstract boolean onStartRefresh(boolean z);

    public void onStubButtonClick(Type type) {
        startRefresh(false);
    }

    public RefreshableRecyclerFragmentHelper(BaseFragment fragment, Context context, String refreshSettingsName, int emptyViewTextResId) {
        this.silentErrorTypes = new ArrayList();
        this.fragment = fragment;
        this.context = context;
        this.refreshSettingsName = refreshSettingsName;
        this.emptyViewTextResId = emptyViewTextResId;
        this.silentErrorTypes.add(ErrorType.GENERAL);
    }

    public <TAdapter extends Adapter & ImageBlockerRecyclerProvider> void onFragmentCreateView(View mainFragmentView, TAdapter adapter) {
        RecyclerView refreshableRecyclerView = initListView(mainFragmentView, this.emptyViewTextResId);
        if (adapter != null) {
            refreshableRecyclerView.addOnScrollListener(((ImageBlockerRecyclerProvider) adapter).getScrollBlocker());
        }
        this.refreshListView = refreshableRecyclerView;
    }

    public void onFragmentDestroyView() {
    }

    protected RecyclerView initListView(View mainView, int resEmptyText) {
        RecyclerView recyclerView = (RecyclerView) mainView.findViewById(2131624731);
        View emptyView = mainView.findViewById(C0263R.id.empty_view);
        if (emptyView != null) {
            if (emptyView instanceof SmartEmptyView) {
                this.smartEmptyView = (SmartEmptyView) emptyView;
                this.smartEmptyView.setEmptyText(resEmptyText);
                this.smartEmptyView.setOnRepeatClickListener(this);
            }
            if (emptyView instanceof SmartEmptyViewAnimated) {
                this.smartEmptyViewAnimated = (SmartEmptyViewAnimated) emptyView;
                this.smartEmptyViewAnimated.setButtonClickListener(this);
            }
        }
        return recyclerView;
    }

    public void startRefresh(boolean byPullGesture) {
        RefreshableListListener listener = this.listener;
        if (listener != null) {
            listener.onStartedRefresh(byPullGesture);
        }
        if (!onStartRefresh(byPullGesture) && listener != null) {
            listener.onFinishedRefresh(true, null);
        }
    }

    public void notifyRefreshSuccessful(Boolean isEmpty) {
        if (!(isEmpty == null || this.smartEmptyView == null)) {
            this.smartEmptyView.setWebState(isEmpty.booleanValue() ? WebState.EMPTY : WebState.HAS_DATA);
        }
        if (this.refreshProvider != null) {
            this.refreshProvider.refreshCompleted();
        }
        RefreshableListListener listener = this.listener;
        if (listener != null) {
            listener.onFinishedRefresh(false, null);
        }
    }

    public void notifyRefreshFailed(ErrorType error) {
        if (this.refreshProvider != null) {
            this.refreshProvider.refreshCompleted();
        }
        if (error == null) {
            error = ErrorType.GENERAL;
        }
        if (this.smartEmptyView != null) {
            int msgId = error.getDefaultErrorMessage();
            if (msgId > 0) {
                this.smartEmptyView.setErrorText(msgId);
                if (!(this.fragment == null || !this.fragment.isVisible() || this.silentErrorTypes.contains(error))) {
                    TimeToast.show(this.context, msgId, 1);
                }
            }
            this.smartEmptyView.setWebState(error == ErrorType.TRANSPORT ? WebState.EMPTY : WebState.ERROR);
        }
        RefreshableListListener listener = this.listener;
        if (listener != null) {
            listener.onFinishedRefresh(true, null);
        }
    }

    public void onRetryClick(SmartEmptyView emptyView) {
        onRefresh();
    }

    public void onRefresh() {
        startRefresh(true);
    }

    public RefreshProvider getRefreshProvider() {
        return this.refreshProvider;
    }

    public void setRefreshProvider(RefreshProvider refreshProvider) {
        this.refreshProvider = refreshProvider;
        refreshProvider.setOnRefreshListener(this);
    }
}

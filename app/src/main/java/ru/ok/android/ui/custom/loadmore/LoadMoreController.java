package ru.ok.android.ui.custom.loadmore;

import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.utils.Logger;

public class LoadMoreController {
    private boolean autoLoadSuppressed;
    public boolean bottomAutoLoad;
    final LoadMoreViewData bottomViewData;
    final LoadMoreAdapterListener listener;
    private LoadMoreConditionCallback loadMoreConditionCallback;
    private final LoadMoreStateChangedListener loadMoreStateChangedListener;
    final LoadMoreMode mode;
    public boolean topAutoLoad;
    final LoadMoreViewData topViewData;

    public interface LoadMoreStateChangedListener {
        void onBottomAutoLoadChanged();

        void onTopAutoLoadChanged();
    }

    public LoadMoreController(LoadMoreAdapterListener listener, LoadMoreMode mode, LoadMoreConditionCallback loadMoreConditionCallback, LoadMoreStateChangedListener stateChangedListener) {
        LoadMoreViewData loadMoreViewData;
        LoadMoreViewData loadMoreViewData2 = null;
        this.bottomAutoLoad = true;
        this.topAutoLoad = true;
        this.listener = listener;
        this.mode = mode;
        this.loadMoreStateChangedListener = stateChangedListener;
        if (loadMoreConditionCallback == null) {
            loadMoreConditionCallback = new LoadMoreConditionCallbackImpl();
        }
        this.loadMoreConditionCallback = loadMoreConditionCallback;
        if (mode.hasTopAdditionalView) {
            loadMoreViewData = new LoadMoreViewData();
        } else {
            loadMoreViewData = null;
        }
        this.topViewData = loadMoreViewData;
        if (mode.hasBottomAdditionalView) {
            loadMoreViewData2 = new LoadMoreViewData();
        }
        this.bottomViewData = loadMoreViewData2;
    }

    public boolean isTopViewAdded() {
        return this.mode.hasTopAdditionalView && this.topAutoLoad;
    }

    public boolean isBottomViewAdded() {
        return this.mode.hasBottomAdditionalView && this.bottomAutoLoad;
    }

    public void setTopPermanentState(LoadMoreState permanentState) {
        this.topViewData.setPermanentState(permanentState);
    }

    public void setBottomPermanentState(LoadMoreState permanentState) {
        this.bottomViewData.setPermanentState(permanentState);
    }

    public LoadMoreState getTopCurrentState() {
        return this.topViewData.getCurrentState();
    }

    public LoadMoreState getTopPermanentState() {
        return this.topViewData.getPermanentState();
    }

    public LoadMoreState getBottomPermanentState() {
        return this.bottomViewData.getPermanentState();
    }

    public LoadMoreState getBottomCurrentState() {
        return this.bottomViewData.getCurrentState();
    }

    public void setTopCurrentState(LoadMoreState currentState) {
        if (this.topViewData != null) {
            this.topViewData.setCurrentState(currentState);
        }
    }

    public void setBottomCurrentState(LoadMoreState currentState) {
        if (this.bottomViewData != null) {
            this.bottomViewData.setCurrentState(currentState);
        }
    }

    public void setBottomAutoLoad(boolean bottomAutoLoad) {
        if (this.bottomAutoLoad != bottomAutoLoad) {
            this.bottomAutoLoad = bottomAutoLoad;
            this.loadMoreStateChangedListener.onBottomAutoLoadChanged();
        }
    }

    public void setTopAutoLoad(boolean topAutoLoad) {
        if (this.topAutoLoad != topAutoLoad) {
            this.topAutoLoad = topAutoLoad;
            this.loadMoreStateChangedListener.onTopAutoLoadChanged();
        }
    }

    public int getExtraTopElements() {
        return isTopViewAdded() ? 1 : 0;
    }

    public int getExtraBottomElements() {
        return isBottomViewAdded() ? 1 : 0;
    }

    public void setTopMessageForState(LoadMoreState state, int resourceId) {
        this.topViewData.setMessageForState(state, resourceId);
    }

    public void setBottomMessageForState(LoadMoreState state, int resourceId) {
        this.bottomViewData.setMessageForState(state, resourceId);
    }

    public void setAutoLoadSuppressed(boolean autoLoadSuppressed) {
        this.autoLoadSuppressed = autoLoadSuppressed;
    }

    public void startTopLoading() {
        if (this.topViewData.getCurrentState() != LoadMoreState.LOADING) {
            this.topViewData.setCurrentState(LoadMoreState.LOADING);
            this.listener.onLoadMoreTopClicked();
        }
    }

    public void startBottomLoading() {
        if (this.bottomViewData.getCurrentState() != LoadMoreState.LOADING) {
            this.bottomViewData.setCurrentState(LoadMoreState.LOADING);
            this.listener.onLoadMoreBottomClicked();
        }
    }

    public boolean onBindViewHolder(int position, int itemCount) {
        if (isTopViewAdded() && this.loadMoreConditionCallback.isTimeToLoadTop(position, itemCount) && this.topViewData.getCurrentState() == LoadMoreState.IDLE && LoadMoreState.isLoadPossibleState(this.topViewData.getPermanentState()) && this.topAutoLoad && !this.autoLoadSuppressed) {
            this.topViewData.setCurrentState(LoadMoreState.LOADING);
            Logger.m182v("onLoadMoreTopClicked");
            this.listener.onLoadMoreTopClicked();
        }
        if (isBottomViewAdded() && this.loadMoreConditionCallback.isTimeToLoadBottom(position, itemCount) && this.bottomViewData.getCurrentState() == LoadMoreState.IDLE && LoadMoreState.isLoadPossibleState(this.bottomViewData.getPermanentState()) && !this.autoLoadSuppressed) {
            this.bottomViewData.setCurrentState(LoadMoreState.LOADING);
            Logger.m182v("onLoadMoreBottomClicked");
            this.listener.onLoadMoreBottomClicked();
        }
        return isTopView(position) || isBottomView(position, itemCount);
    }

    public boolean isTopView(int position) {
        return position == 0 && isTopViewAdded();
    }

    public boolean isBottomView(int position, int itemCount) {
        return position == itemCount + -1 && isBottomViewAdded();
    }

    boolean isTopOrBottomView(int position, int itemCount) {
        return isTopView(position) || isBottomView(position, itemCount);
    }

    public boolean isEmpty(int itemCount) {
        int additional = 0;
        if (isTopViewAdded()) {
            additional = 0 + 1;
        }
        if (isBottomViewAdded()) {
            additional++;
        }
        return itemCount <= additional;
    }

    public int getDataPosition(int viewPosition) {
        return isTopViewAdded() ? viewPosition - 1 : viewPosition;
    }

    public int getLoadMoreAdditionalCount() {
        int additional = 0;
        if (this.mode.hasTopAdditionalView && this.topAutoLoad) {
            additional = 0 + 1;
        }
        if (this.mode.hasBottomAdditionalView && this.bottomAutoLoad) {
            return additional + 1;
        }
        return additional;
    }

    public void setConditionCallback(LoadMoreConditionCallback conditionCallback) {
        this.loadMoreConditionCallback = conditionCallback;
    }
}

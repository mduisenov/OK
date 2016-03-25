package ru.ok.android.ui.video.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.utils.Logger;

public abstract class BaseRecycleFragment extends BaseFragment implements OnRefreshListener {
    private View contentView;
    private ViewStub emptyStub;
    private TextView emptyStubView;
    private ErrorType errorType;
    private int mContentTopClearance;
    protected RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private int shortAnimationDuration;
    protected ProgressBar spinner;

    /* renamed from: ru.ok.android.ui.video.fragments.BaseRecycleFragment.1 */
    class C13571 extends AnimatorListenerAdapter {
        C13571() {
        }

        public void onAnimationStart(Animator animation) {
            BaseRecycleFragment.this.contentView.setAlpha(0.0f);
            BaseRecycleFragment.this.contentView.setVisibility(0);
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.BaseRecycleFragment.2 */
    class C13582 extends AnimatorListenerAdapter {
        C13582() {
        }

        public void onAnimationEnd(Animator animation) {
            BaseRecycleFragment.this.spinner.setVisibility(8);
        }
    }

    protected abstract int getColumnCount();

    public BaseRecycleFragment() {
        this.mContentTopClearance = 0;
    }

    protected int getLayoutId() {
        return 2130903195;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(getLayoutId(), container, false);
        FragmentActivity activity = getActivity();
        if ((activity instanceof VideoActivity) && VERSION.SDK_INT >= 19) {
            mainView.setPadding(0, mainView.getPaddingTop() + ((VideoActivity) activity).getStatusBarHeight(), 0, 0);
        }
        this.refreshLayout = (SwipeRefreshLayout) mainView.findViewById(2131625510);
        this.spinner = (ProgressBar) mainView.findViewById(2131624536);
        this.recyclerView = (RecyclerView) mainView.findViewById(2131624835);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (this.refreshLayout != null) {
            this.refreshLayout.setOnRefreshListener(this);
            this.contentView = this.refreshLayout;
        } else {
            this.contentView = this.recyclerView;
        }
        this.emptyStub = (ViewStub) mainView.findViewById(2131624836);
        this.contentView.setVisibility(8);
        this.shortAnimationDuration = getResources().getInteger(17694720);
        return mainView;
    }

    protected void onRepeatVideoClick() {
        Logger.m172d("repeat");
    }

    public void setContentTopClearance(int clearance) {
        if (this.mContentTopClearance != clearance) {
            this.mContentTopClearance = clearance;
            this.recyclerView.setPadding(this.recyclerView.getPaddingLeft(), this.mContentTopClearance, this.recyclerView.getPaddingRight(), this.recyclerView.getPaddingBottom());
        }
    }

    public void setRefreshing(boolean value) {
        if (this.refreshLayout != null) {
            this.refreshLayout.setRefreshing(value);
        }
    }

    protected void hideProgress() {
        if (this.spinner.getVisibility() != 8) {
            crossFade();
        }
    }

    protected void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    protected void clearErrorType() {
        this.errorType = null;
    }

    public void onRefresh() {
        Logger.m176e("on refresh data");
    }

    protected void showEmpty() {
        int resTextId;
        hideProgress();
        setRefreshing(false);
        if (this.emptyStubView == null) {
            this.emptyStubView = (TextView) this.emptyStub.inflate();
        }
        if (this.errorType == null) {
            resTextId = getEmptyText();
        } else {
            resTextId = this.errorType.getDefaultErrorMessage();
        }
        this.emptyStubView.setText(resTextId);
        this.emptyStubView.setAlpha(0.0f);
        this.emptyStubView.setVisibility(0);
        this.emptyStubView.animate().alpha(1.0f).setDuration((long) this.shortAnimationDuration).setListener(null);
    }

    protected void hideEmpty() {
        if (this.emptyStubView != null) {
            this.emptyStubView.setVisibility(8);
        }
    }

    protected int getEmptyText() {
        return 2131165565;
    }

    protected void crossFade() {
        this.contentView.animate().alpha(1.0f).setDuration((long) this.shortAnimationDuration).setListener(new C13571());
        this.spinner.animate().alpha(0.0f).setDuration((long) this.shortAnimationDuration).setListener(new C13582());
    }
}

package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import ru.ok.android.ui.custom.ProgressWheelView;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class AbstractAttachPhotoView extends AbstractPhotoView {
    protected View mDraggableContentView;
    protected ProgressWheelView mProgressView;

    protected abstract int getDraggableContentViewId();

    public AbstractAttachPhotoView(Context context) {
        super(context);
        this.mDraggableContentView = LocalizationManager.inflate(getContext(), getDraggableContentViewId(), (ViewGroup) this, false);
        addView(this.mDraggableContentView, new LayoutParams(-1, -1));
        this.mProgressView = (ProgressWheelView) findViewById(2131624548);
        initStubView();
    }

    protected void onDragStart() {
        if (this.mDecorViewsHandler != null) {
            this.mDecorViewsHandler.setDecorVisibility(false, false);
            this.mDecorViewsHandler.setVisibilityChangeLocked(true);
        }
        this.mDraggableContentView.setBackgroundColor(0);
    }

    protected void onBounceBack() {
        this.mDraggableContentView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    protected void onUpdateScroll() {
        updateScrollAlpha();
    }

    protected final void updateScrollAlpha() {
        this.mDecorViewsHandler.setBackgroundDrawableAlpha((int) (((double) (100.0f * (1.0f - Math.abs(((float) getScrollY()) / ((float) getHeight()))))) * 2.55d));
    }

    protected void onTapped() {
        onViewTap();
    }

    public void updateProgress(int progress) {
        this.mProgressView.setProgress((int) ((3.6d * ((double) progress)) / 100.0d));
    }

    public void setProgress(int progress) {
        this.mProgressView.setSpinProgress(progress);
        this.mProgressView.invalidate();
    }

    public ProgressWheelView getProgressView() {
        return this.mProgressView;
    }
}

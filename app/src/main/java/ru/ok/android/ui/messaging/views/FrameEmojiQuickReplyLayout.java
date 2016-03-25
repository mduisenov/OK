package ru.ok.android.ui.messaging.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import ru.ok.android.emoji.PanelLayoutController.PanelViewPresenter;
import ru.ok.android.emoji.PanelPresenterSizeListener;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.ViewUtil;

public final class FrameEmojiQuickReplyLayout extends FrameLayout implements PanelViewPresenter {
    private View emojiView;
    private final int minEmojiHeight;

    /* renamed from: ru.ok.android.ui.messaging.views.FrameEmojiQuickReplyLayout.1 */
    class C10611 extends AnimatorListenerAdapter {
        C10611() {
        }

        public void onAnimationEnd(Animator animation) {
            FrameEmojiQuickReplyLayout.this.emojiView.setVisibility(8);
        }
    }

    public FrameEmojiQuickReplyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.minEmojiHeight = (int) Utils.dipToPixels(getContext(), 150.0f);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        View mainChild = getChildAt(0);
        measureChild(mainChild, widthMeasureSpec, heightMeasureSpec);
        int additionalSpace = 0;
        if (this.emojiView != null && this.emojiView.getVisibility() == 0) {
            int restHeight = MeasureSpec.getSize(heightMeasureSpec) - mainChild.getMeasuredHeight();
            if (restHeight < this.minEmojiHeight) {
                measureChild(mainChild, widthMeasureSpec, MeasureSpec.makeMeasureSpec(height - this.minEmojiHeight, LinearLayoutManager.INVALID_OFFSET));
                restHeight = this.minEmojiHeight;
            }
            this.emojiView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), 1073741824), MeasureSpec.makeMeasureSpec(Math.min(restHeight, this.emojiView.getLayoutParams().height), 1073741824));
            additionalSpace = this.emojiView.getMeasuredHeight();
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mainChild.getMeasuredHeight() + additionalSpace);
    }

    public void showPanelView(View emojiView, int height) {
        if (this.emojiView == null) {
            this.emojiView = emojiView;
            LayoutParams lp = new LayoutParams(-1, height);
            lp.gravity = 80;
            addView(emojiView, lp);
        }
        ViewUtil.createHeightAnimator(emojiView, 0, height, 220).start();
        emojiView.setVisibility(0);
    }

    public void setSizeListener(PanelPresenterSizeListener listener) {
    }

    public void hidePanelView(View emojiView) {
        ValueAnimator animator = ViewUtil.createHeightAnimator(emojiView, emojiView.getLayoutParams().height, 0, 220);
        animator.addListener(new C10611());
        animator.start();
    }
}

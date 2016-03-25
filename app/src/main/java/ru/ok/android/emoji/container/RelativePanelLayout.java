package ru.ok.android.emoji.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import ru.ok.android.emoji.PanelLayoutController.PanelViewPresenter;
import ru.ok.android.emoji.PanelPresenterSizeListener;

public final class RelativePanelLayout extends RelativeLayout implements PanelViewPresenter {
    private View emojiView;
    int prevHeight;
    private PanelPresenterSizeListener sizeListener;

    public RelativePanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setSizeListener(PanelPresenterSizeListener sizeListener) {
        this.sizeListener = sizeListener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.sizeListener != null) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (height != this.prevHeight) {
                this.sizeListener.onSizeChanged(this);
                this.prevHeight = height;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void showPanelView(View emojiView, int height) {
        LayoutParams lp = (LayoutParams) emojiView.getLayoutParams();
        if (this.emojiView == null) {
            this.emojiView = emojiView;
            lp = new LayoutParams(0, 0);
            lp.addRule(12);
            addView(emojiView, lp);
        }
        lp.width = -1;
        lp.height = height;
        lp.bottomMargin = -height;
        emojiView.setLayoutParams(lp);
        emojiView.setVisibility(0);
        setPadding(0, 0, 0, height);
    }

    public void hidePanelView(View emojiView) {
        emojiView.setVisibility(8);
        setPadding(0, 0, 0, 0);
    }
}

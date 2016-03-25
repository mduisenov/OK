package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import ru.ok.android.ui.custom.layout.RelativeSetPressedLayout;

public final class BubbleRelativeLayout extends RelativeSetPressedLayout {
    private ViewGroup actionsBlock;
    private int actonsBlockGap;
    private boolean alignLeft;
    private View author;
    private View bubble;
    private View checkbox;
    private int checkboxPaddingRight;
    private View comment;
    private Object customTag;
    private final Handler handler;
    private int initialPaddingLeft;
    private int initialPaddingRight;
    private View isNew;
    private View likesCount;
    private final int maxRowWidth;
    private boolean moved;
    private View repliedTo;
    private View reply;
    private View status;

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.BubbleRelativeLayout.1 */
    class C08591 extends Handler {
        C08591() {
        }

        public void handleMessage(Message msg) {
            BubbleRelativeLayout.this.setPressedSuper();
        }
    }

    public BubbleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new C08591();
        this.maxRowWidth = (int) TypedValue.applyDimension(1, 400.0f, getResources().getDisplayMetrics());
        this.checkboxPaddingRight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.actonsBlockGap = (int) TypedValue.applyDimension(1, 5.0f, getResources().getDisplayMetrics());
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.author = findViewById(2131624696);
        this.status = findViewById(2131624717);
        this.isNew = findViewById(2131624694);
        this.actionsBlock = (ViewGroup) findViewById(2131624702);
        this.likesCount = findViewById(2131625083);
        this.reply = findViewById(2131625085);
        this.repliedTo = findViewById(2131624698);
        this.comment = findViewById(2131624887);
        this.checkbox = findViewById(2131624713);
        this.initialPaddingLeft = getPaddingLeft();
        this.initialPaddingRight = getPaddingRight();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.author != null) {
            updatePaddingForTop(this.author);
        }
        if (this.author == null || this.author.getVisibility() != 0) {
            updatePaddingForTop(this.repliedTo);
            if (this.repliedTo.getVisibility() == 8) {
                updatePaddingForTop(this.comment);
            } else {
                updatePaddingForMiddle(this.comment);
            }
        } else {
            updatePaddingForMiddle(this.repliedTo);
            updatePaddingForMiddle(this.comment);
        }
        View audioView = findViewById(2131624624);
        if (audioView != null && audioView.getVisibility() == 0) {
            updatePaddingForTop(audioView);
        }
        if (hasVisibleChildren(this.actionsBlock)) {
            updatePaddingForBottom(this.actionsBlock);
        } else {
            this.actionsBlock.setPadding(0, 0, 0, this.bubble.getPaddingBottom() - ((MarginLayoutParams) this.actionsBlock.getLayoutParams()).topMargin);
        }
        if (this.actionsBlock.getVisibility() == 0 && ((this.likesCount != null && this.likesCount.getVisibility() == 0) || (this.reply != null && this.reply.getVisibility() == 0))) {
            this.actionsBlock.setPadding(this.actionsBlock.getPaddingLeft() - this.actonsBlockGap, this.actionsBlock.getPaddingTop(), this.actionsBlock.getPaddingRight(), this.actionsBlock.getPaddingBottom());
        }
        View internal = findViewById(2131624700);
        if (!(internal == null || this.moved)) {
            this.moved = true;
            updatePaddingForMiddleInternal(internal);
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int paddingLeft = this.initialPaddingLeft;
        int paddingRight = this.initialPaddingRight;
        boolean checkboxVisible = this.checkbox.getVisibility() != 8;
        if (widthSize > this.maxRowWidth) {
            int padding = widthSize - Math.max(this.maxRowWidth, (widthSize * 3) / 4);
            if (checkboxVisible) {
                padding -= this.checkbox.getWidth();
            }
            if (!this.alignLeft) {
                paddingLeft = padding;
                ((MarginLayoutParams) this.checkbox.getLayoutParams()).leftMargin = 0;
            } else if (checkboxVisible) {
                ((MarginLayoutParams) this.checkbox.getLayoutParams()).leftMargin = padding;
            } else {
                paddingRight = padding;
            }
        }
        if (checkboxVisible) {
            paddingRight = this.checkboxPaddingRight;
        }
        setPadding(paddingLeft, getPaddingTop(), paddingRight, getPaddingBottom());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean hasVisibleChildren(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getVisibility() == 0) {
                return true;
            }
        }
        return false;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int measuredWidth = this.actionsBlock.getMeasuredWidth();
        int measuredWidth2 = (this.author == null || this.author.getVisibility() != 0) ? 0 : this.author.getMeasuredWidth();
        int bubbleWidth = Math.max(measuredWidth, measuredWidth2);
        if (this.comment.getVisibility() == 0) {
            bubbleWidth = Math.max(bubbleWidth, this.comment.getMeasuredWidth());
        }
        if (this.repliedTo.getVisibility() == 0) {
            bubbleWidth = Math.max(this.repliedTo.getMeasuredWidth(), bubbleWidth);
        }
        bubbleWidth = chooseWidth(2131624708, chooseWidth(2131624624, chooseWidth(2131624705, chooseWidth(2131624700, bubbleWidth, false), true), true), true);
        int bubbleBottom = this.actionsBlock.getBottom();
        if (this.alignLeft) {
            this.bubble.layout(this.bubble.getLeft(), getPaddingTop(), this.bubble.getLeft() + bubbleWidth, bubbleBottom);
            if (this.actionsBlock.getVisibility() == 0) {
                this.actionsBlock.measure(MeasureSpec.makeMeasureSpec(bubbleWidth, 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
                this.actionsBlock.layout(this.bubble.getLeft(), this.actionsBlock.getTop(), this.bubble.getRight(), this.actionsBlock.getBottom());
            }
        } else {
            this.bubble.layout(this.bubble.getRight() - bubbleWidth, getPaddingTop(), this.bubble.getRight(), bubbleBottom);
            if (this.actionsBlock.getVisibility() == 0) {
                this.actionsBlock.measure(MeasureSpec.makeMeasureSpec(bubbleWidth, 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
                this.actionsBlock.layout(this.bubble.getLeft(), this.actionsBlock.getTop(), this.bubble.getRight(), this.actionsBlock.getBottom());
            }
            if (this.actionsBlock.getVisibility() == 0) {
                this.actionsBlock.layout(this.bubble.getLeft(), this.actionsBlock.getTop(), this.actionsBlock.getRight(), this.actionsBlock.getBottom());
            }
            if (this.status != null && this.status.getVisibility() == 0) {
                int statusLeft = (((this.bubble.getLeft() - this.bubble.getPaddingLeft()) - this.status.getMeasuredWidth()) + this.status.getPaddingLeft()) + this.status.getPaddingRight();
                int statusTop = this.bubble.getTop() + this.bubble.getPaddingTop();
                this.status.layout(statusLeft, statusTop, this.status.getMeasuredWidth() + statusLeft, this.status.getMeasuredHeight() + statusTop);
            }
        }
        if (this.isNew.getVisibility() == 0) {
            int left = (this.bubble.getRight() - this.isNew.getMeasuredWidth()) - ((this.checkboxPaddingRight * 4) / 2);
            int top = this.bubble.getTop() + (this.checkboxPaddingRight * 2);
            this.isNew.layout(left, top, this.isNew.getWidth() + left, this.isNew.getHeight() + top);
        }
    }

    private int chooseWidth(int viewId, int currentWidth, boolean decreaseByViewPadding) {
        View view = findViewById(viewId);
        if (view == null || view.getVisibility() != 0) {
            return currentWidth;
        }
        int width = this.bubble.getPaddingLeft() + this.bubble.getPaddingRight();
        if (decreaseByViewPadding) {
            width -= view.getPaddingLeft() + view.getPaddingRight();
        }
        return Math.max(view.getMeasuredWidth() + width, currentWidth);
    }

    private void updatePaddingForTop(View view) {
        view.setPadding(this.bubble.getPaddingLeft(), this.bubble.getPaddingTop(), this.bubble.getPaddingRight(), 0);
    }

    private void updatePaddingForMiddle(View view) {
        view.setPadding(this.bubble.getPaddingLeft(), 0, this.bubble.getPaddingRight(), 0);
    }

    private void updatePaddingForMiddleInternal(View view) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), this.bubble.getPaddingRight(), view.getPaddingBottom());
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        lp.leftMargin += this.bubble.getPaddingLeft();
        lp.topMargin += this.bubble.getPaddingTop();
    }

    private void updatePaddingForBottom(View view) {
        view.setPadding(this.bubble.getPaddingLeft(), view.getPaddingTop(), this.bubble.getPaddingRight(), this.bubble.getPaddingBottom());
    }

    public void setPressed(boolean pressed) {
        this.handler.removeMessages(0);
        if (pressed) {
            super.setPressed(pressed);
        } else {
            this.handler.sendEmptyMessageDelayed(0, (long) ViewConfiguration.getPressedStateDuration());
        }
    }

    public void setCustomTag(Object tag) {
        if ((tag == null || !tag.equals(this.customTag)) && this.handler.hasMessages(0)) {
            super.setPressed(false);
            this.handler.removeMessages(0);
        }
        this.customTag = tag;
    }

    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        this.bubble.setPressed(pressed);
    }

    private void setPressedSuper() {
        super.setPressed(false);
    }

    public void setAlignLeft(boolean alignLeft) {
        this.alignLeft = alignLeft;
    }
}

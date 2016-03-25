package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.ui.stream.mediatopic.PollAnswerState;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public class PollAnswerLayout extends ViewGroup {
    final TextView answerTextView;
    int centerY;
    final TextView countTextView;
    private final PollAnswerDrawable iconBackground;
    final ImageView iconView;
    final ProgressBar progressView;

    public PollAnswerLayout(Context context) {
        this(context, null);
    }

    public PollAnswerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 2130772003, 2131296581);
    }

    public PollAnswerLayout(Context context, AttributeSet attrs, int defAttr, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.PollAnswerLayout, defAttr, defStyle);
        int layoutResId = a.getResourceId(0, 2130903399);
        a.recycle();
        inflate(context, layoutResId, this);
        this.iconView = (ImageView) findViewById(2131625245);
        this.iconBackground = new PollAnswerDrawable(context);
        this.iconView.setBackgroundDrawable(this.iconBackground);
        this.answerTextView = (TextView) findViewById(2131625246);
        this.progressView = (ProgressBar) findViewById(2131625247);
        this.countTextView = (TextView) findViewById(2131625248);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int widthUsed = 0;
        int iconContentHalfHeight = 0;
        int iconCenterY = 0;
        int iconBottomMargin = 0;
        int answerTextContentHalfHeight = 0;
        int answerTextCenterY = 0;
        int answerTextBottomMargin = 0;
        int maxRightColumnWidth = 0;
        if (!(this.iconView == null || this.iconView.getVisibility() == 8)) {
            measureChildWithMargins(this.iconView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) this.iconView.getLayoutParams();
            widthUsed = 0 + ((this.iconView.getMeasuredWidth() + lp.leftMargin) + lp.rightMargin);
            int iconHeight = this.iconView.getMeasuredHeight();
            iconBottomMargin = lp.bottomMargin;
            iconContentHalfHeight = iconHeight >> 1;
            iconCenterY = lp.topMargin + iconContentHalfHeight;
        }
        if (!(this.answerTextView == null || this.answerTextView.getVisibility() == 8)) {
            measureChildWithMargins(this.answerTextView, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);
            int answerTextHeight = this.answerTextView.getMeasuredHeight();
            lp = (MarginLayoutParams) this.answerTextView.getLayoutParams();
            answerTextBottomMargin = lp.bottomMargin;
            answerTextContentHalfHeight = answerTextHeight >> 1;
            answerTextCenterY = lp.topMargin + answerTextContentHalfHeight;
            width = this.answerTextView.getMeasuredWidth();
            if (width > 0) {
                maxRightColumnWidth = width;
            }
        }
        int centerY = Math.max(iconCenterY, answerTextCenterY);
        int iconBottom = (centerY + iconContentHalfHeight) + iconBottomMargin;
        int heightUsed = (centerY + answerTextContentHalfHeight) + answerTextBottomMargin;
        boolean progressVisible = (this.progressView == null || this.progressView.getVisibility() == 8) ? false : true;
        if (progressVisible) {
            measureChildWithMargins(this.progressView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
            lp = (MarginLayoutParams) this.progressView.getLayoutParams();
            heightUsed += (this.progressView.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin;
            width = this.progressView.getMeasuredWidth();
            if (width > maxRightColumnWidth) {
                maxRightColumnWidth = width;
            }
        }
        if (!(this.countTextView == null || this.countTextView.getVisibility() == 8)) {
            measureChildWithMargins(this.countTextView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
            lp = (MarginLayoutParams) this.countTextView.getLayoutParams();
            heightUsed += ((progressVisible ? lp.topMargin : 0) + this.countTextView.getMeasuredHeight()) + lp.bottomMargin;
            width = this.countTextView.getMeasuredWidth();
            if (width > maxRightColumnWidth) {
                maxRightColumnWidth = width;
            }
        }
        setMeasuredDimension(MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE ? (getPaddingLeft() + (widthUsed + maxRightColumnWidth)) + getPaddingRight() : MeasureSpec.getSize(widthMeasureSpec), (getPaddingTop() + Math.max(iconBottom, heightUsed)) + getPaddingBottom());
        this.centerY = centerY;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean progressVisible;
        int width = r - l;
        int height = b - t;
        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        int iconRight = leftPadding;
        if (this.iconView != null) {
            if (this.iconView.getVisibility() != 8) {
                MarginLayoutParams lp = (MarginLayoutParams) this.iconView.getLayoutParams();
                int iconWidth = this.iconView.getMeasuredWidth();
                int iconHeight = this.iconView.getMeasuredHeight();
                int iconLeft = leftPadding + lp.leftMargin;
                int iconTop = (this.centerY + topPadding) - (iconHeight >> 1);
                iconRight = iconLeft + iconWidth;
                this.iconView.layout(iconLeft, iconTop, iconRight, iconTop + iconHeight);
                iconRight += lp.rightMargin;
            }
        }
        int usedHeight = topPadding;
        if (this.answerTextView != null) {
            if (this.answerTextView.getVisibility() != 8) {
                lp = (MarginLayoutParams) this.answerTextView.getLayoutParams();
                int answerTextHeight = this.answerTextView.getMeasuredHeight();
                int left = iconRight + lp.leftMargin;
                int top = (this.centerY + topPadding) - (answerTextHeight >> 1);
                int bottom = top + answerTextHeight;
                usedHeight = bottom;
                this.answerTextView.layout(left, top, left + this.answerTextView.getMeasuredWidth(), bottom);
            }
        }
        if (this.progressView != null) {
            if (this.progressView.getVisibility() != 8) {
                progressVisible = true;
                if (progressVisible) {
                    lp = (MarginLayoutParams) this.progressView.getLayoutParams();
                    left = iconRight + lp.leftMargin;
                    top = usedHeight + lp.topMargin;
                    bottom = top + this.progressView.getMeasuredHeight();
                    usedHeight = bottom;
                    this.progressView.layout(left, top, left + this.progressView.getMeasuredWidth(), bottom);
                }
                if (this.countTextView != null) {
                    if (this.countTextView.getVisibility() != 8) {
                        lp = (MarginLayoutParams) this.countTextView.getLayoutParams();
                        left = iconRight + lp.leftMargin;
                        top = usedHeight + (progressVisible ? lp.topMargin : 0);
                        bottom = top + this.countTextView.getMeasuredHeight();
                        usedHeight = bottom;
                        this.countTextView.layout(left, top, left + this.countTextView.getMeasuredWidth(), bottom);
                    }
                }
            }
        }
        progressVisible = false;
        if (progressVisible) {
            lp = (MarginLayoutParams) this.progressView.getLayoutParams();
            left = iconRight + lp.leftMargin;
            top = usedHeight + lp.topMargin;
            bottom = top + this.progressView.getMeasuredHeight();
            usedHeight = bottom;
            this.progressView.layout(left, top, left + this.progressView.getMeasuredWidth(), bottom);
        }
        if (this.countTextView != null) {
            if (this.countTextView.getVisibility() != 8) {
                lp = (MarginLayoutParams) this.countTextView.getLayoutParams();
                left = iconRight + lp.leftMargin;
                if (progressVisible) {
                }
                top = usedHeight + (progressVisible ? lp.topMargin : 0);
                bottom = top + this.countTextView.getMeasuredHeight();
                usedHeight = bottom;
                this.countTextView.layout(left, top, left + this.countTextView.getMeasuredWidth(), bottom);
            }
        }
    }

    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams ? p : new MarginLayoutParams(p);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(-2, -2);
    }

    public void setAnswerInfo(String text, int count, int maxVotes, boolean isMultiAnswersAllowed, boolean isSelected, PollAnswerState state) {
        if (this.iconView != null) {
            this.iconView.setImageResource(isMultiAnswersAllowed ? 2130838681 : 2130838684);
            this.iconView.setSelected(isSelected);
        }
        if (this.answerTextView != null) {
            this.answerTextView.setText(text);
            this.answerTextView.setSelected(isSelected);
        }
        if (this.countTextView != null) {
            this.countTextView.setText(LocalizationManager.from(getContext()).getString(StringUtils.plural(count, 2131166653, 2131166652, 2131166650, 2131166651), Integer.valueOf(count)));
        }
        if (this.progressView != null) {
            if (count > 0) {
                this.progressView.setProgress(maxVotes > 0 ? (this.progressView.getMax() * count) / maxVotes : 0);
                this.progressView.setVisibility(0);
            } else {
                this.progressView.setVisibility(8);
            }
        }
        if (this.iconBackground != null) {
            this.iconBackground.setAnswerState(state);
        }
    }
}

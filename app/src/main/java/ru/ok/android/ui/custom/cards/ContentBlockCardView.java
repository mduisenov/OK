package ru.ok.android.ui.custom.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContentBlockCardView extends LinearLayout {
    private View contentView;
    private TextView expandView;
    private CharSequence expandViewText;
    private boolean showExpandView;
    private boolean showTitle;
    private CharSequence title;
    private TextView titleView;

    public ContentBlockCardView(Context context) {
        super(context);
        onCreate();
    }

    public ContentBlockCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public ContentBlockCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    private void onCreate() {
        setOrientation(1);
        setBackgroundResource(2130837760);
        int padding = getResources().getDimensionPixelSize(2131230902);
        setPadding(padding, 0, padding, 0);
    }

    public void setContentView(View view) {
        setContentView(view, new LayoutParams(-1, -2));
    }

    public void setContentView(View view, LayoutParams layoutParams) {
        if (this.contentView != view) {
            if (this.contentView != null) {
                removeView(this.contentView);
            }
            addView(view, this.titleView == null ? 0 : 1, layoutParams);
        }
    }

    private void updateView() {
        updateTitle();
        updateExpandView();
        invalidate();
    }

    private void updateTitle() {
        if (this.showTitle && this.titleView == null) {
            this.titleView = createTitleTextView();
            addView(this.titleView, 0, new LayoutParams(-1, getResources().getDimensionPixelSize(2131230907)));
        } else if (this.titleView != null) {
            removeView(this.titleView);
            this.titleView = null;
        }
        if (this.titleView != null) {
            this.titleView.setText(this.title);
        }
    }

    private TextView createTitleTextView() {
        TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(2131492935));
        textView.setBackgroundResource(2130837761);
        textView.setGravity(16);
        textView.setIncludeFontPadding(false);
        textView.setTextSize(getResources().getDimension(2131230908));
        return textView;
    }

    private void updateExpandView() {
        if (this.showExpandView && this.expandView == null) {
            this.expandView = createExpandView();
            addView(this.expandView, new LayoutParams(-1, getResources().getDimensionPixelSize(2131230895)));
        } else if (this.expandView != null) {
            removeView(this.expandView);
            this.expandView = null;
        }
        if (this.expandView != null) {
            this.expandView.setText(this.title);
        }
    }

    private TextView createExpandView() {
        TextView expandView = new TextView(getContext());
        expandView.setGravity(16);
        expandView.setTextSize(16.0f);
        expandView.setBackgroundResource(2130837762);
        expandView.setTextColor(getResources().getColor(2131492933));
        return expandView;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        updateView();
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        updateView();
    }

    public CharSequence getExpandViewText() {
        return this.expandViewText;
    }

    public void setExpandViewText(CharSequence expandViewText) {
        this.expandViewText = expandViewText;
        updateView();
    }

    public void setShowExpandView(boolean showExpandView) {
        this.showExpandView = showExpandView;
        updateView();
    }
}

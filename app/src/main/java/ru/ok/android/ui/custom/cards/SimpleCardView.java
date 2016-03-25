package ru.ok.android.ui.custom.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SimpleCardView extends RelativeLayout {
    public SimpleCardView(Context context) {
        super(context);
        onCreate();
    }

    public SimpleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public SimpleCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    private void onCreate() {
        setBackgroundResource(2130837760);
        int padding = getResources().getDimensionPixelSize(2131230902);
        setPadding(padding, padding, padding, padding);
    }
}

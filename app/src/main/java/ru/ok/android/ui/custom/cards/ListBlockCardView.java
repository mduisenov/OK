package ru.ok.android.ui.custom.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

public class ListBlockCardView extends ContentBlockCardView {
    private ListView listView;

    public ListBlockCardView(Context context) {
        super(context);
        onCreate();
    }

    public ListBlockCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public ListBlockCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    private void onCreate() {
        this.listView = new ListView(getContext());
        setContentView(this.listView, new LayoutParams(-1, -1));
    }

    public ListView getListView() {
        return this.listView;
    }
}

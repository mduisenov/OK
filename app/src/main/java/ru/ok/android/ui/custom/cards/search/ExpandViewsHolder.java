package ru.ok.android.ui.custom.cards.search;

import android.view.View;
import android.widget.TextView;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;

public class ExpandViewsHolder extends CardViewHolder {
    private TextView titleView;

    public ExpandViewsHolder(View view) {
        super(view);
        this.titleView = (TextView) view.findViewById(2131624662);
    }

    public void update(CharSequence title) {
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        this.titleView.setText(title);
    }
}

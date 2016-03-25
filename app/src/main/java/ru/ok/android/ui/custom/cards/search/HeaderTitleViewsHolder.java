package ru.ok.android.ui.custom.cards.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;

public class HeaderTitleViewsHolder extends CardViewHolder {
    public final TextView titleView;

    public static View newView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(2130903120, parent, false);
    }

    public HeaderTitleViewsHolder(View view) {
        super(view);
        this.titleView = (TextView) view;
    }

    public void update(CharSequence title) {
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        this.titleView.setText(title);
    }
}

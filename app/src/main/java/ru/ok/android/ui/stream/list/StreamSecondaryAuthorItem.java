package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.UserInfo;

public class StreamSecondaryAuthorItem extends StreamItem {
    private final UserInfo userInfo;

    static class SecondAuthorViewHolder extends ViewHolder {
        final TextView text;

        SecondAuthorViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view);
            this.text = (TextView) view.findViewById(C0263R.id.text);
            this.text.setOnClickListener(streamItemViewController.getUserClickListener());
            this.text.setTag(2131624343, "2nd_author");
        }
    }

    protected StreamSecondaryAuthorItem(FeedWithState feed, UserInfo userInfo) {
        super(35, 3, 3, feed);
        this.userInfo = userInfo;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903500, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        SecondAuthorViewHolder viewHolder = (SecondAuthorViewHolder) holder;
        viewHolder.text.setText(this.userInfo.getAnyName());
        viewHolder.text.setTag(2131624354, this.userInfo);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new SecondAuthorViewHolder(view, streamItemViewController);
    }

    boolean sharePressedState() {
        return false;
    }
}

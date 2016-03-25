package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.UserInfo;

public class StreamUserNamesItem extends StreamItemAdjustablePaddings {
    private final CharSequence text;
    private final ArrayList<UserInfo> users;

    static class UserNamesViewHolder extends ViewHolder {
        final TextView namesTextView;

        public UserNamesViewHolder(View view) {
            super(view);
            this.namesTextView = (TextView) view.findViewById(2131625381);
        }
    }

    protected StreamUserNamesItem(FeedWithState feed, CharSequence text, ArrayList<UserInfo> users) {
        super(19, 3, 1, feed);
        this.text = text;
        this.users = users;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903511, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof UserNamesViewHolder) {
            UserNamesViewHolder viewHolder = (UserNamesViewHolder) holder;
            viewHolder.namesTextView.setText(this.text);
            viewHolder.itemView.setTag(2131624322, this.feedWithState);
            viewHolder.itemView.setTag(2131624346, this.users);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static UserNamesViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        UserNamesViewHolder holder = new UserNamesViewHolder(view);
        holder.itemView.setOnClickListener(streamItemViewController.getUserNamesClickListener());
        return holder;
    }

    boolean sharePressedState() {
        return false;
    }
}

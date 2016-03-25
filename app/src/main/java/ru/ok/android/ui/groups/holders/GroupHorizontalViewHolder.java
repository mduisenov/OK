package ru.ok.android.ui.groups.holders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.NotificationsView;

public class GroupHorizontalViewHolder extends ViewHolder {
    public final SimpleDraweeView image;
    public final TextView title;
    public final NotificationsView unreadEvents;

    public GroupHorizontalViewHolder(View view) {
        super(view);
        this.image = (SimpleDraweeView) view.findViewById(C0263R.id.image);
        this.title = (TextView) view.findViewById(C0176R.id.title);
        this.unreadEvents = (NotificationsView) view.findViewById(2131624893);
    }
}

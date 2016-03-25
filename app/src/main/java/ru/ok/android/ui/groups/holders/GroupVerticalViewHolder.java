package ru.ok.android.ui.groups.holders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;

public class GroupVerticalViewHolder extends ViewHolder {
    public final TextView friendsMembersCount;
    public final SimpleDraweeView image;
    public final TextView join;
    public final TextView joined;
    public final TextView membersCount;
    public final ParticipantsPreviewView participants;
    public final TextView title;
    public final NotificationsView unreadEvents;

    public GroupVerticalViewHolder(View view) {
        super(view);
        this.image = (SimpleDraweeView) view.findViewById(C0263R.id.image);
        this.title = (TextView) view.findViewById(C0176R.id.title);
        this.membersCount = (TextView) view.findViewById(2131624664);
        this.friendsMembersCount = (TextView) view.findViewById(2131624895);
        this.join = (TextView) view.findViewById(2131624896);
        this.joined = (TextView) view.findViewById(2131624897);
        this.participants = (ParticipantsPreviewView) view.findViewById(2131624879);
        this.unreadEvents = (NotificationsView) view.findViewById(2131624893);
    }
}

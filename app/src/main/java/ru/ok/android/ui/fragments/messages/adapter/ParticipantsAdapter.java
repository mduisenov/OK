package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class ParticipantsAdapter extends Adapter<ViewHolder> implements OnClickListener {
    private Conversation conversation;
    private final LayoutInflater li;
    private final ParticipantsAdapterListener listener;
    private final List<UserInfo> participants;

    public interface ParticipantsAdapterListener {
        void onKickUser(UserInfo userInfo);

        void onUserClicked(UserInfo userInfo);
    }

    protected static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        private final AvatarImageView avatar;
        private final ImageButton kickUser;
        private final TextView lastOnline;
        private final TextView name;
        private final TextView owner;

        public ViewHolder(View view, OnClickListener listener) {
            super(view);
            this.avatar = (AvatarImageView) view.findViewById(2131624657);
            this.name = (TextView) view.findViewById(C0263R.id.name);
            this.lastOnline = (TextView) view.findViewById(2131624992);
            this.owner = (TextView) view.findViewById(2131624782);
            this.kickUser = (ImageButton) view.findViewById(2131624991);
            this.kickUser.setOnClickListener(listener);
            view.setOnClickListener(listener);
        }
    }

    public ParticipantsAdapter(Context context, ParticipantsAdapterListener listener) {
        this.participants = new ArrayList();
        this.listener = listener;
        this.li = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public int getItemCount() {
        return this.participants.size();
    }

    public UserInfo getItem(int position) {
        return (UserInfo) this.participants.get(position);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this.li.inflate(2130903262, parent, false), this);
    }

    public long getItemId(int position) {
        return (long) ((UserInfo) this.participants.get(position)).uid.hashCode();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean z;
        int i;
        int i2 = 0;
        UserInfo user = getItem(position);
        holder.name.setText(user.getAnyName());
        holder.avatar.setUser(user);
        ImageViewManager instance = ImageViewManager.getInstance();
        String str = user.picUrl;
        AvatarImageView access$100 = holder.avatar;
        if (user.genderType == UserGenderType.MALE) {
            z = true;
        } else {
            z = false;
        }
        instance.displayImage(str, access$100, z, null);
        ImageButton access$200 = holder.kickUser;
        if (canKickUser(user)) {
            i = 0;
        } else {
            i = 8;
        }
        access$200.setVisibility(i);
        holder.kickUser.setTag(user);
        holder.itemView.setTag(user);
        Utils.setTextViewTextWithVisibility(holder.lastOnline, DateFormatter.formatDeltaTimePast(holder.lastOnline.getContext(), user.lastOnline, false, false));
        TextView access$400 = holder.owner;
        if (!TextUtils.equals(user.uid, this.conversation.getOwnerId())) {
            i2 = 8;
        }
        access$400.setVisibility(i2);
    }

    private boolean canKickUser(UserInfo user) {
        if (this.conversation == null) {
            return false;
        }
        String uid = user.uid;
        for (Participant participant : this.conversation.getParticipantsList()) {
            if (TextUtils.equals(uid, participant.getId())) {
                return participant.getCanKick();
            }
        }
        return false;
    }

    public void setParticipants(Conversation conversation, List<UserInfo> participants) {
        this.participants.clear();
        this.participants.addAll(participants);
        this.conversation = conversation;
        notifyDataSetChanged();
    }

    @Nullable
    public List<Participant> getParticipants() {
        return this.conversation != null ? this.conversation.getParticipantsList() : null;
    }

    public void onClick(View v) {
        UserInfo user = (UserInfo) v.getTag();
        if (v.getId() == 2131624991) {
            this.listener.onKickUser(user);
        } else {
            this.listener.onUserClicked(user);
        }
    }
}

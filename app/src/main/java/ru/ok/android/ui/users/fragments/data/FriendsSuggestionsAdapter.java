package ru.ok.android.ui.users.fragments.data;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;

public final class FriendsSuggestionsAdapter extends Adapter<ViewHolder> implements AdapterItemViewTypeMaxValueProvider {
    private final int count;
    private final HandleBlocker handleBlocker;
    private final boolean isSmall;
    private final LayoutInflater li;
    private final LocalizationManager lm;
    private final List<UserInfo> suggestionUsers;

    static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
        final ParticipantsPreviewView participants;
        final TextView title;

        public ViewHolder(View view, HandleBlocker handleBlocked) {
            super(view);
            this.title = (TextView) view.findViewById(C0176R.id.title);
            this.participants = (ParticipantsPreviewView) view.findViewById(2131624879);
            this.participants.setHandleBlocker(handleBlocked);
            view.setOnClickListener(this);
        }

        public void onClick(View v) {
            NavigationHelper.showPymk((Activity) v.getContext());
        }
    }

    public FriendsSuggestionsAdapter(Context context, HandleBlocker handleBlocker, int count, boolean isSmall) {
        this.suggestionUsers = new ArrayList();
        this.count = count;
        this.isSmall = isSmall;
        this.li = LayoutInflater.from(context);
        this.lm = LocalizationManager.from(context);
        this.handleBlocker = handleBlocker;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder result = new ViewHolder(this.li.inflate(2130903217, parent, false), this.handleBlocker);
        if (!this.isSmall) {
            result.itemView.setBackgroundColor(-1);
        }
        result.participants.setMaxAvatars(this.count);
        return result;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.participants.setParticipants(this.suggestionUsers, false);
        holder.title.setText(this.lm.getString(2131166669));
    }

    public int getItemCount() {
        return this.suggestionUsers.isEmpty() ? 0 : 1;
    }

    public int getItemViewType(int position) {
        return 2131624367;
    }

    public int getItemViewTypeMaxValue() {
        return 2131624367;
    }

    public void updateUsers(List<UserInfo> suggestions) {
        this.suggestionUsers.clear();
        if (suggestions != null) {
            this.suggestionUsers.addAll(suggestions);
        }
        notifyDataSetChanged();
    }
}

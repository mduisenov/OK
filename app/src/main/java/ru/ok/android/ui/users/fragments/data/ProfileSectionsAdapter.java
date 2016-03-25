package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.base.profile.ProfileSectionItem;
import ru.ok.android.ui.utils.BindArrayAdapter;
import ru.ok.android.utils.NumberFormatUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class ProfileSectionsAdapter<P extends ProfileSectionItem<C>, C> extends BindArrayAdapter<P, ViewHolder> {
    private C counters;

    protected static class ViewHolder {
        final TextView count;
        final TextView title;

        public ViewHolder(View view) {
            this.title = (TextView) view.findViewById(C0176R.id.title);
            this.count = (TextView) view.findViewById(2131624446);
        }
    }

    public ProfileSectionsAdapter(Context context) {
        super(context, new ArrayList());
    }

    public int getItemCount(P item) {
        if (this.counters != null) {
            return item.getCount(this.counters);
        }
        return 0;
    }

    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    protected int getItemResourceId() {
        return 2130903560;
    }

    protected void bindView(ViewHolder holder, P item) {
        holder.title.setText(LocalizationManager.getString(getContext(), item.getNameResourceId()));
        int count = this.counters != null ? item.getCount(this.counters) : 0;
        Utils.setTextViewTextWithVisibilityState(holder.count, count > 0 ? NumberFormatUtil.getFormatFrenchText(count) : null, 4);
    }

    public void setCounters(C counters) {
        this.counters = counters;
        notifyDataSetChanged();
    }
}

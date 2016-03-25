package ru.ok.android.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public class TagsSpinnerAdapter extends BaseAdapter implements OnNavigationListener {
    protected static int countPadding;
    protected final Context context;
    private final List<SpinnerTextCountItem> data;
    private GroupTagSelectListener listener;
    private SubtitleProvider subtitleProvider;

    public interface GroupTagSelectListener {
        boolean onGroupTagSelected(int i, Long l);
    }

    private class Holder {
        public TextView countText;
        public TextView subtitleText;
        public TextView titleView;

        private Holder() {
        }
    }

    public static class SpinnerTextCountItem {
        public String count;
        public String dropDownCount;
        public String dropDownText;
        public Long tagId;
        public String text;

        public SpinnerTextCountItem(String text, String count, Long tagId) {
            this(text, text, count, count, tagId);
        }

        public SpinnerTextCountItem(String text, String dropDownText, String count, String dropDownCount, Long tagId) {
            this.text = text;
            this.dropDownText = dropDownText;
            this.count = count;
            this.dropDownCount = dropDownCount;
            this.tagId = tagId;
        }
    }

    public interface SubtitleProvider {
        CharSequence getSpinnerSubtitle();
    }

    public TagsSpinnerAdapter(Context context) {
        this.data = new ArrayList();
        this.context = context;
        if (countPadding == 0) {
            countPadding = context.getResources().getDimensionPixelOffset(2131231152);
        }
    }

    public int getCount() {
        return this.data.size();
    }

    public SpinnerTextCountItem getItem(int position) {
        return (SpinnerTextCountItem) this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean onNavigationItemSelected(int position, long id) {
        return this.listener != null ? this.listener.onGroupTagSelected(position, ((SpinnerTextCountItem) this.data.get(position)).tagId) : false;
    }

    public void setData(@NonNull List<SpinnerTextCountItem> items) {
        this.data.clear();
        this.data.addAll(items);
    }

    public void setGroupTagsSelectListener(GroupTagSelectListener listener) {
        this.listener = listener;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        int countTextWidth = 0;
        Holder holder = view == null ? null : (Holder) view.getTag(2131624307);
        if (holder == null) {
            holder = new Holder();
            view = LocalizationManager.inflate(this.context, 2130903042, parent, false);
            holder.titleView = (TextView) view.findViewById(C0263R.id.name);
            holder.countText = (TextView) view.findViewById(2131624446);
            view.setTag(2131624307, holder);
        }
        SpinnerTextCountItem item = getItem(position);
        holder.titleView.setText(item.dropDownText);
        if (holder.countText.getVisibility() == 0) {
            countTextWidth = countPadding;
        }
        Utils.setTextViewTextWithVisibility(holder.countText, item.dropDownCount);
        holder.titleView.setPadding(holder.titleView.getPaddingLeft(), holder.titleView.getPaddingTop(), countTextWidth, holder.titleView.getPaddingBottom());
        return view;
    }

    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = null;
        } else {
            holder = (Holder) view.getTag(2131624306);
        }
        if (holder == null) {
            holder = new Holder();
            view = LocalizationManager.inflate(this.context, 2130903044, parent, false);
            holder.titleView = (TextView) view.findViewById(C0263R.id.name);
            holder.subtitleText = (TextView) view.findViewById(C0158R.id.subtitle);
            view.setTag(2131624306, holder);
        }
        holder.titleView.setText(getItem(position).text);
        if (this.subtitleProvider == null) {
            holder.subtitleText.setVisibility(8);
        } else {
            holder.subtitleText.setText(this.subtitleProvider.getSpinnerSubtitle());
            holder.subtitleText.setVisibility(0);
        }
        return view;
    }

    public void setSubtitleProvider(SubtitleProvider subtitleProvider) {
        this.subtitleProvider = subtitleProvider;
    }
}

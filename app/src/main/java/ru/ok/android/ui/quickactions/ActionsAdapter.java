package ru.ok.android.ui.quickactions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.utils.localization.LocalizationManager;

class ActionsAdapter extends BaseAdapter {
    protected final Context context;
    private List<ActionItem> data;

    private static class ViewHolder {
        public ImageView image;
        public TextView text;

        private ViewHolder() {
        }

        public static ViewHolder createViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(2131624493);
            holder.image = (ImageView) convertView.findViewById(2131624492);
            return holder;
        }
    }

    public ActionsAdapter(Context context, Collection<? extends ActionItem> data) {
        this.data = new ArrayList();
        this.context = context;
        this.data.addAll(data);
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int position) {
        return this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }
        bindView((ViewHolder) convertView.getTag(), (ActionItem) getItem(position));
        return convertView;
    }

    protected View newView(ViewGroup parent) {
        View convertView = LocalizationManager.inflate(this.context, 2130903070, parent, false);
        convertView.setTag(ViewHolder.createViewHolder(convertView));
        return convertView;
    }

    protected void bindView(ViewHolder holder, ActionItem item) {
        holder.text.setText(LocalizationManager.getString(this.context, item.getTitleResourceId()));
        if (item.getIconResourceId() != 0) {
            holder.image.setImageResource(item.getIconResourceId());
        }
    }
}

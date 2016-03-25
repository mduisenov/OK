package ru.ok.android.ui.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;
import ru.ok.android.ui.base.profile.ProfileSectionItem;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class BindArrayAdapter<T extends ProfileSectionItem, Holder> extends ArrayAdapter<T> {
    private final List<T> data;

    protected abstract void bindView(Holder holder, T t);

    protected abstract Holder createViewHolder(View view);

    protected abstract int getItemResourceId();

    public BindArrayAdapter(Context context, List<T> data) {
        super(context, 0, data);
        this.data = data;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }
        bindView(convertView.getTag(), (ProfileSectionItem) getItem(position));
        return convertView;
    }

    private View newView(ViewGroup parent) {
        View result = LocalizationManager.inflate(getContext(), getItemResourceId(), parent, false);
        result.setTag(createViewHolder(result));
        return result;
    }

    public void swapData(List<T> data) {
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return this.data;
    }
}

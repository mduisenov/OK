package ru.ok.android.ui.custom.animationlist;

import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class DataChangeBindAdapter<D, I, H> extends DataChangeAdapter<D> {
    protected abstract void bindView(H h, I i, int i2);

    protected abstract H createViewHolder(View view);

    public abstract I getItem(int i);

    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag(2131624356) != null) {
            convertView = newView(position, parent);
        }
        bindView(convertView.getTag(), getItem(position), position);
        return convertView;
    }

    protected View newView(int position, ViewGroup parent) {
        int rowLayoutId = getRowLayoutId();
        if (rowLayoutId == -1) {
            throw new IllegalStateException("You should override newView or getRowLayoutId method");
        }
        View result = LocalizationManager.inflate(parent.getContext(), rowLayoutId, parent, false);
        result.setTag(createViewHolder(result));
        return result;
    }

    protected int getRowLayoutId() {
        return -1;
    }
}

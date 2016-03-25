package ru.ok.android.ui.adapters.spinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class BaseNavigationSpinnerAdapter extends BaseAdapter {
    private static int countPadding;
    private final Context context;

    private class Holder {
        public TextView countText;
        public TextView titleView;

        private Holder() {
        }
    }

    protected abstract String getCountText(int i);

    protected abstract String getItemText(int i);

    protected BaseNavigationSpinnerAdapter(Context context) {
        this.context = context;
        if (countPadding == 0) {
            countPadding = context.getResources().getDimensionPixelOffset(2131231152);
        }
    }

    public View getBaseView(int position, View view, ViewGroup parent, int layoutResId, int nameTextAppearanceResId, int countTextAppearanceResId) {
        int countTextWidth = 0;
        Holder holder = view == null ? new Holder() : (Holder) view.getTag();
        if (view == null) {
            view = LocalizationManager.inflate(this.context, layoutResId, parent, false);
            holder.titleView = (TextView) view.findViewById(C0263R.id.name);
            holder.countText = (TextView) view.findViewById(2131624446);
            view.setTag(holder);
        }
        holder.countText.setTextAppearance(this.context, countTextAppearanceResId);
        holder.titleView.setTextAppearance(this.context, nameTextAppearanceResId);
        holder.titleView.setText(getItemText(position));
        String countText = getCountText(position);
        if (holder.countText.getVisibility() == 0) {
            countTextWidth = countPadding;
        }
        Utils.setTextViewTextWithVisibility(holder.countText, countText);
        holder.titleView.setPadding(holder.titleView.getPaddingLeft(), holder.titleView.getPaddingTop(), countTextWidth, holder.titleView.getPaddingBottom());
        return view;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        return getBaseView(position, view, parent, 2130903042, 2131296674, 2131296673);
    }

    public View getView(int position, View view, ViewGroup parent) {
        return getBaseView(position, null, parent, 2130903043, 2131296675, 2131296672);
    }

    protected Context getContext() {
        return this.context;
    }
}

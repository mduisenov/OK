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

public class TopCategoriesSpinnerAdapter extends BaseAdapter implements OnNavigationListener {
    protected final Context context;
    private final List<Item> data;
    private TopCategoriesSpinnerAdapterListener listener;

    private class Holder {
        public TextView subtitleText;
        public TextView titleView;

        private Holder() {
        }
    }

    public static class Item {
        public String dropDownText;
        public String id;
        public String subtitle;
        public String title;

        public Item(String title, String subtitle, String dropDownText, String id) {
            this.title = title;
            this.subtitle = subtitle;
            this.dropDownText = dropDownText;
            this.id = id;
        }
    }

    public interface TopCategoriesSpinnerAdapterListener {
        boolean onTopCategorySelected(int i, String str);
    }

    public TopCategoriesSpinnerAdapter(Context context) {
        this.data = new ArrayList();
        this.context = context;
    }

    public int getCount() {
        return this.data.size();
    }

    public Item getItem(int position) {
        return (Item) this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean onNavigationItemSelected(int position, long id) {
        return this.listener != null ? this.listener.onTopCategorySelected(position, ((Item) this.data.get(position)).id) : false;
    }

    public void setData(@NonNull List<Item> items) {
        this.data.clear();
        this.data.addAll(items);
    }

    public void setListener(TopCategoriesSpinnerAdapterListener listener) {
        this.listener = listener;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        Holder holder = view == null ? null : (Holder) view.getTag(2131624307);
        if (holder == null) {
            holder = new Holder();
            view = LocalizationManager.inflate(this.context, 2130903041, parent, false);
            holder.titleView = (TextView) view.findViewById(C0263R.id.name);
            view.setTag(2131624307, holder);
        }
        holder.titleView.setText(getItem(position).dropDownText);
        return view;
    }

    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = view == null ? null : (Holder) view.getTag(2131624306);
        if (holder == null) {
            holder = new Holder();
            view = LocalizationManager.inflate(this.context, 2130903044, parent, false);
            holder.titleView = (TextView) view.findViewById(C0263R.id.name);
            holder.subtitleText = (TextView) view.findViewById(C0158R.id.subtitle);
            view.setTag(2131624306, holder);
        }
        Item item = getItem(position);
        holder.titleView.setText(item.title);
        Utils.setTextViewTextWithVisibility(holder.subtitleText, item.subtitle);
        return view;
    }
}

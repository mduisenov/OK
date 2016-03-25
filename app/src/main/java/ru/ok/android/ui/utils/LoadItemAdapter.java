package ru.ok.android.ui.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class LoadItemAdapter extends Adapter<ViewHolder> implements AdapterItemViewTypeMaxValueProvider {
    private final LayoutInflater li;
    private boolean loading;

    static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public LoadItemAdapter(Context context) {
        this.li = LayoutInflater.from(context);
    }

    public void setLoading(boolean loading) {
        if (this.loading != loading) {
            this.loading = loading;
            notifyDataSetChanged();
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this.li.inflate(2130903272, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    public int getItemViewType(int position) {
        return 2131624363;
    }

    public int getItemViewTypeMaxValue() {
        return 2131624363;
    }

    public int getItemCount() {
        return this.loading ? 1 : 0;
    }

    public boolean isLoading() {
        return this.loading;
    }
}

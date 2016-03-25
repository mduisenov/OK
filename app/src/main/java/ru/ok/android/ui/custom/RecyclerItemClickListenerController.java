package ru.ok.android.ui.custom;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import java.util.ArrayList;
import java.util.List;

public class RecyclerItemClickListenerController implements OnClickListener, OnLongClickListener {
    private final List<OnItemClickListener> itemClickListeners;
    private final List<OnItemLongClickListener> itemLongClickListeners;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int i);
    }

    public RecyclerItemClickListenerController() {
        this.itemClickListeners = new ArrayList();
        this.itemLongClickListeners = new ArrayList();
    }

    public void onClick(View v) {
        Object tagPosition = v.getTag(2131624292);
        if (tagPosition != null && (tagPosition instanceof Integer)) {
            fireOnItemClick(v, ((Integer) tagPosition).intValue());
        }
    }

    private void fireOnItemClick(View v, int position) {
        for (int i = 0; i < this.itemClickListeners.size(); i++) {
            ((OnItemClickListener) this.itemClickListeners.get(i)).onItemClick(v, position);
        }
    }

    public boolean onLongClick(View v) {
        Object tagPosition = v.getTag(2131624292);
        if (tagPosition != null && (tagPosition instanceof Integer)) {
            int position = ((Integer) tagPosition).intValue();
            for (int i = 0; i < this.itemLongClickListeners.size(); i++) {
                if (((OnItemLongClickListener) this.itemLongClickListeners.get(i)).onItemLongClick(v, position)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(2131624292, Integer.valueOf(position));
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    public void addItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListeners.add(itemClickListener);
    }

    public void addItemLongClickListener(OnItemLongClickListener itemClickListener) {
        this.itemLongClickListeners.add(itemClickListener);
    }
}

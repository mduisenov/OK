package ru.ok.android.widget.attach;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.messages.Attachment;

public abstract class BaseAttachAdapter<VH extends Holder> extends BaseAdapter {
    private final List<Attachment> attachments;
    protected int oneColumnSize;
    protected final int remoteItemLayoutResourceId;
    protected int twoColumnsSize;

    protected static abstract class Holder {
        @NonNull
        public final View itemView;

        public Holder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        protected View getItemView() {
            return this.itemView;
        }
    }

    protected abstract void bindViewHolder(@NonNull VH vh, int i);

    @NonNull
    protected abstract VH createViewHolder(ViewGroup viewGroup, int i);

    public BaseAttachAdapter(int remoteItemLayoutResourceId) {
        this.attachments = new ArrayList();
        this.remoteItemLayoutResourceId = remoteItemLayoutResourceId;
    }

    public void setColumnsSize(int twoColumnsSize, int oneColumnSize) {
        this.twoColumnsSize = twoColumnsSize;
        this.oneColumnSize = oneColumnSize;
    }

    public void setData(List<Attachment> attachments) {
        this.attachments.clear();
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (!attachment.isDeleted()) {
                    this.attachments.add(attachment);
                }
            }
        }
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        View itemView = convertView;
        if (itemView == null) {
            holder = createViewHolder(parent, position);
            itemView = holder.getItemView();
            itemView.setTag(holder);
        } else {
            Holder holder2 = (Holder) itemView.getTag();
        }
        bindViewHolder(holder, position);
        return itemView;
    }

    @NonNull
    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public int getCount() {
        return this.attachments.size();
    }

    public Attachment getItem(int i) {
        return (Attachment) this.attachments.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }
}

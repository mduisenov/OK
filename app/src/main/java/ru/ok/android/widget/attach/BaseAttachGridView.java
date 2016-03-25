package ru.ok.android.widget.attach;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import java.util.List;
import ru.ok.model.messages.Attachment;

public abstract class BaseAttachGridView<A extends BaseAttachAdapter> extends GridView implements OnItemClickListener {
    protected A adapter;
    private DataSetObserver dataObserver;
    protected long messageId;
    private OnAttachClickListener onAttachClickListener;
    private int oneColumnSize;
    private int twoColumnsSize;

    public interface OnAttachClickListener {
        void onAttachClick(View view, List<Attachment> list, Attachment attachment);
    }

    /* renamed from: ru.ok.android.widget.attach.BaseAttachGridView.1 */
    class C14941 extends DataSetObserver {
        C14941() {
        }

        public void onChanged() {
            super.onChanged();
            BaseAttachGridView.this.setNumColumns(Math.min(BaseAttachGridView.this.adapter.getCount(), 2));
        }
    }

    protected BaseAttachGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dataObserver = new C14941();
        setFocusable(false);
        setOnItemClickListener(this);
        setStretchMode(2);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(536870911, LinearLayoutManager.INVALID_OFFSET));
        this.oneColumnSize = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        this.twoColumnsSize = this.oneColumnSize / 2;
        this.adapter.setColumnsSize(this.twoColumnsSize, this.oneColumnSize);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.onAttachClickListener != null) {
            List<Attachment> attachments = this.adapter.getAttachments();
            this.onAttachClickListener.onAttachClick(view, attachments, (Attachment) attachments.get(position));
        }
    }

    public void setOnAttachClickListener(OnAttachClickListener onAttachClickListener) {
        this.onAttachClickListener = onAttachClickListener;
    }

    public void setAttachesAdapter(A adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(this.dataObserver);
        }
        this.adapter = adapter;
        super.setAdapter(adapter);
        this.adapter.registerDataSetObserver(this.dataObserver);
    }

    public A getAttachesAdapter() {
        return this.adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        throw new NoSuchMethodError("Use setAttachesAdapter instead");
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}

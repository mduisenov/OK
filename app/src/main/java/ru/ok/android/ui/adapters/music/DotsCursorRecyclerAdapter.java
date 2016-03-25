package ru.ok.android.ui.adapters.music;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.utils.ViewUtil;

public abstract class DotsCursorRecyclerAdapter<T, VH extends ViewHolder> extends BaseCursorRecyclerAdapter<VH> implements OnClickListener {
    private int dotsTouchDelegateOffset;
    private OnDotsClickListener<T> onDotsClickListener;

    public DotsCursorRecyclerAdapter(Context context, Cursor c) {
        this(context, c, true);
    }

    public DotsCursorRecyclerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(c, autoRequery);
        this.dotsTouchDelegateOffset = context.getResources().getDimensionPixelSize(2131230952);
    }

    protected void onCreateDotsView(View dots) {
        dots.setOnClickListener(this);
    }

    protected void bindDots(View dots, T o) {
        ViewUtil.setTouchDelegate(dots, this.dotsTouchDelegateOffset);
        dots.setTag(o);
    }

    public void onClick(View v) {
        if (v.getId() == 2131624874 && v.getVisibility() == 0 && this.onDotsClickListener != null) {
            this.onDotsClickListener.onDotsClick(v.getTag(), v);
        }
    }

    public void setOnDotsClickListener(OnDotsClickListener onDotsClickListener) {
        this.onDotsClickListener = onDotsClickListener;
    }
}

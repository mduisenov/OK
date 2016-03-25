package ru.ok.android.ui.adapters.friends;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import ru.ok.android.ui.users.CursorSwapper;

public abstract class BaseCursorRecyclerAdapter<VH extends ViewHolder> extends Adapter<VH> implements CursorSwapper {
    protected boolean mAutoRequery;
    protected ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter$ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter.ChangeObserver mChangeObserver;
    protected Cursor mCursor;
    protected DataSetObserver mDataSetObserver;
    protected boolean mDataValid;
    protected int mRowIDColumn;

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean selfChange) {
            BaseCursorRecyclerAdapter.this.onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        private MyDataSetObserver() {
        }

        public void onChanged() {
            BaseCursorRecyclerAdapter.this.mDataValid = true;
            BaseCursorRecyclerAdapter.this.notifyDataSetChanged();
        }

        public void onInvalidated() {
            BaseCursorRecyclerAdapter.this.mDataValid = false;
            BaseCursorRecyclerAdapter.this.notifyDataSetChanged();
        }
    }

    public BaseCursorRecyclerAdapter(Cursor cursor) {
        this(cursor, false);
    }

    public BaseCursorRecyclerAdapter(Cursor cursor, boolean autoRequery) {
        this.mCursor = cursor;
        this.mDataValid = this.mCursor != null;
        this.mAutoRequery = autoRequery;
        this.mRowIDColumn = cursor != null ? cursor.getColumnIndexOrThrow("_id") : -1;
        if (autoRequery) {
            this.mChangeObserver = new ChangeObserver();
            this.mDataSetObserver = new MyDataSetObserver();
        } else {
            this.mChangeObserver = null;
            this.mDataSetObserver = null;
        }
        if (cursor != null && autoRequery) {
            if (this.mChangeObserver != null) {
                cursor.registerContentObserver(this.mChangeObserver);
            }
            if (this.mDataSetObserver != null) {
                cursor.registerDataSetObserver(this.mDataSetObserver);
            }
        }
    }

    public long getItemId(int position) {
        if (this.mCursor == null || !this.mCursor.moveToPosition(position)) {
            return 0;
        }
        return this.mCursor.getLong(this.mRowIDColumn);
    }

    public int getItemCount() {
        return this.mCursor == null ? 0 : this.mCursor.getCount();
    }

    public Object getItem(int position) {
        if (this.mCursor == null) {
            return null;
        }
        this.mCursor.moveToPosition(position);
        return this.mCursor;
    }

    public Cursor getItemCursor(int position) {
        if (this.mCursor == null) {
            return null;
        }
        this.mCursor.moveToPosition(position);
        return this.mCursor;
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == this.mCursor) {
            return null;
        }
        Cursor oldCursor = this.mCursor;
        if (oldCursor != null) {
            if (this.mChangeObserver != null) {
                oldCursor.unregisterContentObserver(this.mChangeObserver);
            }
            if (this.mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(this.mDataSetObserver);
            }
        }
        this.mCursor = newCursor;
        if (newCursor != null) {
            if (this.mChangeObserver != null) {
                newCursor.registerContentObserver(this.mChangeObserver);
            }
            if (this.mDataSetObserver != null) {
                newCursor.registerDataSetObserver(this.mDataSetObserver);
            }
            this.mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            this.mDataValid = true;
            notifyDataSetChanged();
            return oldCursor;
        }
        this.mRowIDColumn = -1;
        this.mDataValid = false;
        notifyDataSetChanged();
        return oldCursor;
    }

    protected void onContentChanged() {
        if (this.mAutoRequery && this.mCursor != null && !this.mCursor.isClosed()) {
            this.mDataValid = this.mCursor.requery();
        }
    }

    public Cursor getCursor() {
        return this.mCursor;
    }
}

package ru.ok.android.ui.adapters;

import android.widget.BaseAdapter;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.OnSelectionChangeListener;

public abstract class CheckChangeAdapter extends BaseAdapter {
    protected OnCheckStateChangeListener changeListener;
    protected CheckedChangeHolder checkedChangeHolder;

    protected class CheckedChangeHolder implements OnSelectionChangeListener {
        private int checkCount;

        public CheckedChangeHolder() {
            this.checkCount = 0;
        }

        public void clear() {
            this.checkCount = 0;
        }

        public void onSelectChange(boolean value, long trackId, int dataPosition) {
            if (value) {
                this.checkCount++;
                if (this.checkCount == 1) {
                    CheckChangeAdapter.this.notifyCheckStateChange(true);
                }
            } else if (this.checkCount > 0) {
                this.checkCount--;
                if (this.checkCount == 0) {
                    CheckChangeAdapter.this.notifyCheckStateChange(false);
                }
            }
        }
    }

    public interface OnCheckStateChangeListener {
        void onCheckStateChange(boolean z);
    }

    protected CheckChangeAdapter() {
        this.checkedChangeHolder = new CheckedChangeHolder();
    }

    protected void notifyCheckStateChange(boolean checkState) {
        if (this.changeListener != null) {
            this.changeListener.onCheckStateChange(checkState);
        }
    }

    public void clear() {
        this.checkedChangeHolder.clear();
    }

    public void setCheckStateChangeListener(OnCheckStateChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}

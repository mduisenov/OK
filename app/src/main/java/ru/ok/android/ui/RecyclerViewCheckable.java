package ru.ok.android.ui;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;

public class RecyclerViewCheckable extends RecyclerView {
    private SparseBooleanArray mCheckStates;
    private int mCheckedItemCount;
    private ActionMode mChoiceActionMode;
    private int mChoiceMode;

    public RecyclerViewCheckable(Context context) {
        super(context);
        this.mChoiceMode = 0;
    }

    public RecyclerViewCheckable(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChoiceMode = 0;
    }

    public RecyclerViewCheckable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChoiceMode = 0;
    }

    public void setChoiceMode(int choiceMode) {
        this.mChoiceMode = choiceMode;
        if (this.mChoiceActionMode != null) {
            this.mChoiceActionMode.finish();
            this.mChoiceActionMode = null;
        }
        if (this.mChoiceMode != 0) {
            if (this.mCheckStates == null) {
                this.mCheckStates = new SparseBooleanArray(0);
            }
            if (this.mChoiceMode == 3) {
                clearChoices();
                setLongClickable(true);
            }
        }
    }

    public boolean isItemChecked(int position) {
        if (this.mChoiceMode == 0 || this.mCheckStates == null) {
            return false;
        }
        return this.mCheckStates.get(position);
    }

    public int getCheckedItemPosition() {
        if (this.mChoiceMode == 1 && this.mCheckStates != null && this.mCheckStates.size() == 1) {
            return this.mCheckStates.keyAt(0);
        }
        return -1;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (this.mChoiceMode != 0) {
            return this.mCheckStates;
        }
        return null;
    }

    public void clearChoices() {
        if (this.mCheckStates != null) {
            this.mCheckStates.clear();
        }
        this.mCheckedItemCount = 0;
    }

    public void setItemChecked(int position, boolean value) {
        if (this.mChoiceMode != 0) {
            if (this.mChoiceMode == 2) {
                boolean oldValue = this.mCheckStates.get(position);
                this.mCheckStates.put(position, value);
                if (oldValue == value) {
                    return;
                }
                if (value) {
                    this.mCheckedItemCount++;
                    return;
                } else {
                    this.mCheckedItemCount--;
                    return;
                }
            }
            if (value || isItemChecked(position)) {
                this.mCheckStates.clear();
            }
            if (value) {
                this.mCheckStates.put(position, true);
                this.mCheckedItemCount = 1;
            } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                this.mCheckedItemCount = 0;
            }
        }
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }
}

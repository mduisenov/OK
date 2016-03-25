package ru.ok.android.ui.custom.layout.checkable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import java.util.ArrayList;
import java.util.List;

public final class CheckableLayoutUtils {
    private final List<Checkable> checkables;
    private boolean checked;

    public CheckableLayoutUtils() {
        this.checkables = new ArrayList();
    }

    public void onFinishInflate(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof Checkable) {
                this.checkables.add((Checkable) child);
            }
        }
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        for (Checkable checkable : this.checkables) {
            checkable.setChecked(this.checked);
        }
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void toggle() {
        this.checked = !this.checked;
        for (Checkable checkable : this.checkables) {
            checkable.toggle();
        }
    }
}

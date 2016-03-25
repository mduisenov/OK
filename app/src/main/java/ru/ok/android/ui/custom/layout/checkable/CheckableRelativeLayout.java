package ru.ok.android.ui.custom.layout.checkable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
    private final CheckableLayoutUtils utils;

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.utils = new CheckableLayoutUtils();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.utils.onFinishInflate(this);
    }

    public void setChecked(boolean checked) {
        this.utils.setChecked(checked);
    }

    public boolean isChecked() {
        return this.utils.isChecked();
    }

    public void toggle() {
        this.utils.toggle();
    }
}

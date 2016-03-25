package ru.ok.android.ui.custom.layout;

import android.view.View;
import android.view.ViewGroup;

final class SetPressedUtils {
    static void dispatchSetPressed(ViewGroup group, boolean pressed) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.isDuplicateParentStateEnabled() && !(pressed && (child.isClickable() || child.isLongClickable()))) {
                child.setPressed(pressed);
            }
        }
    }
}

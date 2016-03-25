package ru.ok.android.utils;

import android.view.View;
import android.view.ViewGroup;

public final class ViewTagUtils {

    public interface ViewTagVisitor {
        void visitViewTag(View view, int i, Object obj);
    }

    public static void traverseViewTags(View rootView, int key, ViewTagVisitor visitor) {
        Object tag = rootView.getTag(key);
        if (!(tag == null || visitor == null)) {
            visitor.visitViewTag(rootView, key, tag);
        }
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                traverseViewTags(viewGroup.getChildAt(i), key, visitor);
            }
        }
    }
}

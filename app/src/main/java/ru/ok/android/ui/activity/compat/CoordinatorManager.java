package ru.ok.android.ui.activity.compat;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel;

public class CoordinatorManager {
    public final List<Pair<View, String>> addedFabs;
    public final CoordinatorLayout coordinatorLayout;

    private class AddViewRunnable implements Runnable {
        private final View view;

        public AddViewRunnable(View view) {
            this.view = view;
        }

        public void run() {
            if (!CoordinatorManager.this.isAdded(this.view)) {
                boolean beforeTabbar = (this.view instanceof FloatingActionButton) || (this.view instanceof MediaComposerPanel);
                if (beforeTabbar) {
                    int tabbarIndex = -1;
                    int size = CoordinatorManager.this.coordinatorLayout.getChildCount();
                    for (int i = 0; i < size; i++) {
                        if (CoordinatorManager.this.coordinatorLayout.getChildAt(i).getId() == 2131624436) {
                            tabbarIndex = i;
                            break;
                        }
                    }
                    if (tabbarIndex == -1) {
                        CoordinatorManager.this.coordinatorLayout.addView(this.view);
                    } else {
                        CoordinatorManager.this.coordinatorLayout.addView(this.view, tabbarIndex);
                    }
                } else {
                    CoordinatorManager.this.coordinatorLayout.addView(this.view);
                }
                CoordinatorManager.this.forceCoordinatorLayoutPrepareChildren();
            }
        }
    }

    private class RemoveViewRunnable implements Runnable {
        private final View view;

        public RemoveViewRunnable(View view) {
            this.view = view;
        }

        public void run() {
            CoordinatorManager.this.coordinatorLayout.removeView(this.view);
            CoordinatorManager.this.forceCoordinatorLayoutPrepareChildren();
        }
    }

    public CoordinatorManager(CoordinatorLayout coordinator) {
        this.addedFabs = new ArrayList();
        this.coordinatorLayout = coordinator;
    }

    public void addFab(View view, String tag) {
        if (this.addedFabs.size() > 0) {
            int size = this.addedFabs.size();
            for (int i = 0; i < size; i++) {
                Pair<View, String> p = addedFabs.get(i);
                View v = p.first;
                String t = p.second;
                if (tag == null || t == null || !tag.equals(t)) {
                    this.coordinatorLayout.removeView(v);
                }
            }
            this.addedFabs.clear();
        }
        this.addedFabs.add(new Pair(view, tag));
        this.coordinatorLayout.post(new AddViewRunnable(view));
    }

    public void ensureFab(View view) {
        ensureFab(view, null);
    }

    public void ensureFab(View view, String tag) {
        addFab(view, tag);
    }

    public void remove(View view) {
        this.addedFabs.remove(view);
        if (isAdded(view)) {
            this.coordinatorLayout.post(new RemoveViewRunnable(view));
        }
    }

    public void forceCoordinatorLayoutPrepareChildren() {
        this.coordinatorLayout.measure(MeasureSpec.makeMeasureSpec(this.coordinatorLayout.getMeasuredWidth(), 1073741824), MeasureSpec.makeMeasureSpec(this.coordinatorLayout.getMeasuredHeight(), 1073741824));
    }

    public boolean isAdded(View view) {
        int size = this.coordinatorLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            if (this.coordinatorLayout.getChildAt(i) == view) {
                return true;
            }
        }
        return false;
    }

    public View getFabById(int id) {
        if (this.addedFabs.size() <= 0) {
            return null;
        }
        int size = this.addedFabs.size();
        for (int i = 0; i < size; i++) {
            View v = ((Pair) this.addedFabs.get(i)).first;
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }
}

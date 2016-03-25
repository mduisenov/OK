package ru.ok.android.ui.stream.viewcache;

import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class StreamViewCache {
    private final LinkedList<List<View>> children2Remove;
    private final Map<Integer, List<View>> layoutIdViewCorrelation;
    private final LayoutInflater layoutInflater;

    public StreamViewCache(LayoutInflater layoutInflater) {
        this.layoutIdViewCorrelation = new ArrayMap();
        this.children2Remove = new LinkedList();
        this.layoutInflater = layoutInflater;
    }

    public View getViewWithLayoutId(int layoutId, ViewGroup parentView) {
        List<View> views = (List) this.layoutIdViewCorrelation.get(Integer.valueOf(layoutId));
        if (!(views == null || views.isEmpty())) {
            View remove = (View) views.remove(views.size() - 1);
            if (remove.getParent() == null) {
                return remove;
            }
        }
        View result = this.layoutInflater.inflate(layoutId, parentView, false);
        result.setTag(2131624325, Integer.valueOf(layoutId));
        return result;
    }

    public void collectAndClearChildViews(ViewGroup parentView) {
        if (parentView != null) {
            List<View> children = (List) this.children2Remove.poll();
            if (children == null) {
                children = new ArrayList();
            }
            for (int i = 0; i < parentView.getChildCount(); i++) {
                View child = parentView.getChildAt(i);
                if (child instanceof ViewGroup) {
                    collectAndClearChildViews((ViewGroup) child);
                }
                if (collectThisView(child)) {
                    children.add(child);
                }
            }
            for (View view : children) {
                parentView.removeView(view);
            }
            children.clear();
            this.children2Remove.offer(children);
        }
    }

    public boolean collectThisView(View view) {
        Integer layoutId = (Integer) view.getTag(2131624325);
        if (layoutId == null) {
            return false;
        }
        List<View> views = (List) this.layoutIdViewCorrelation.get(layoutId);
        if (views == null) {
            views = new ArrayList();
            this.layoutIdViewCorrelation.put(layoutId, views);
        }
        if (!views.contains(view)) {
            if (view instanceof ViewPager) {
                ((ViewPager) view).setAdapter(null);
            }
            views.add(view);
        }
        return true;
    }
}

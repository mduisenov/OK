package ru.ok.android.ui.utils;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import ru.ok.android.utils.Logger;

public class ViewDrawObserver {
    private final ViewDrawListener listener;
    private final ArrayList<View> observedViews;
    private OnAttachStateChangeListener onAttachStateChangeListener;
    private OnPreDrawListener onPreDrawListener;
    private final View rootView;

    public interface ViewDrawListener {
        void onViewDraw(View view);
    }

    /* renamed from: ru.ok.android.ui.utils.ViewDrawObserver.1 */
    class C13511 implements OnPreDrawListener {
        C13511() {
        }

        public boolean onPreDraw() {
            ViewDrawListener listener = ViewDrawObserver.this.listener;
            ArrayList<View> observedViews = ViewDrawObserver.this.observedViews;
            if (listener != null) {
                int size = observedViews.size();
                for (int i = 0; i < size; i++) {
                    listener.onViewDraw((View) observedViews.get(i));
                }
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.utils.ViewDrawObserver.2 */
    class C13522 implements OnAttachStateChangeListener {
        C13522() {
        }

        public void onViewAttachedToWindow(View v) {
        }

        public void onViewDetachedFromWindow(View v) {
            if (v == ViewDrawObserver.this.rootView) {
                ViewDrawObserver.this.pause();
                return;
            }
            ArrayList<View> observedViews = ViewDrawObserver.this.observedViews;
            int i = 0;
            while (i < observedViews.size()) {
                if (observedViews.get(i) == v) {
                    observedViews.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    public ViewDrawObserver(View rootView, ViewDrawListener listener) {
        this.observedViews = new ArrayList();
        this.onPreDrawListener = new C13511();
        this.onAttachStateChangeListener = new C13522();
        if (rootView == null) {
            Logger.m184w("rootView param is null, will do nothing");
        }
        if (listener == null) {
            Logger.m184w("rootView param is null, will do nothing");
        }
        this.listener = listener;
        this.rootView = rootView;
    }

    public void resume() {
        if (this.rootView != null && this.listener != null) {
            ViewTreeObserver observer = this.rootView.getViewTreeObserver();
            if (observer != null) {
                observer.addOnPreDrawListener(this.onPreDrawListener);
            }
        }
    }

    public void pause() {
        if (this.rootView != null && this.listener != null) {
            ViewTreeObserver observer = this.rootView.getViewTreeObserver();
            if (observer != null) {
                observer.removeOnPreDrawListener(this.onPreDrawListener);
            }
        }
    }

    public void startObserving(View view) {
        this.observedViews.add(view);
        view.addOnAttachStateChangeListener(this.onAttachStateChangeListener);
    }
}

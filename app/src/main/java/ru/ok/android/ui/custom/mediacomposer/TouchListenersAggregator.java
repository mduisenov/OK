package ru.ok.android.ui.custom.mediacomposer;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import java.util.ArrayList;
import java.util.List;

public class TouchListenersAggregator implements OnTouchListener {
    private final List<OnTouchListener> listeners;

    public TouchListenersAggregator() {
        this.listeners = new ArrayList();
    }

    public void add(OnTouchListener listener) {
        this.listeners.add(listener);
    }

    public boolean onTouch(View v, MotionEvent event) {
        for (OnTouchListener listener : this.listeners) {
            if (listener.onTouch(v, event)) {
                return true;
            }
        }
        return false;
    }
}

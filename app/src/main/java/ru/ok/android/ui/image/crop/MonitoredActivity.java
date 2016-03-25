package ru.ok.android.ui.image.crop;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;

public class MonitoredActivity extends NoSearchActivity {
    private final ArrayList<LifeCycleListener> mListeners;

    public interface LifeCycleListener {
        void onActivityCreated(MonitoredActivity monitoredActivity);

        void onActivityDestroyed(MonitoredActivity monitoredActivity);

        void onActivityStarted(MonitoredActivity monitoredActivity);

        void onActivityStopped(MonitoredActivity monitoredActivity);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(MonitoredActivity activity) {
        }

        public void onActivityDestroyed(MonitoredActivity activity) {
        }

        public void onActivityStarted(MonitoredActivity activity) {
        }

        public void onActivityStopped(MonitoredActivity activity) {
        }
    }

    public MonitoredActivity() {
        this.mListeners = new ArrayList();
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        this.mListeners.remove(listener);
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            ((LifeCycleListener) i$.next()).onActivityCreated(this);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            ((LifeCycleListener) i$.next()).onActivityDestroyed(this);
        }
    }

    protected void onStart() {
        super.onStart();
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            ((LifeCycleListener) i$.next()).onActivityStarted(this);
        }
    }

    protected void onStop() {
        super.onStop();
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            ((LifeCycleListener) i$.next()).onActivityStopped(this);
        }
    }
}

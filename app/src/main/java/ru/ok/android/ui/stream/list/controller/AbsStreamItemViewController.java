package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.stream.StreamLayoutInflatorFactory;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.viewcache.StreamViewCache;

public abstract class AbsStreamItemViewController implements StreamItemViewController {
    private final Activity activity;
    private boolean debugMode;
    private final ScrollLoadBlocker imageLoadBlocker;
    private final LayoutInflater layoutInflater;
    private final String logContext;
    private NotifyContentChangeListener notifyContentChangeListener;
    private final StreamAdapterListener streamAdapterListener;
    private final StreamViewCache viewCache;

    public interface NotifyContentChangeListener {
        void onContentWithOptionsChanged();
    }

    public AbsStreamItemViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        this.imageLoadBlocker = ScrollLoadBlocker.forIdleOnly();
        this.activity = activity;
        this.streamAdapterListener = listener;
        this.logContext = logContext;
        this.layoutInflater = LayoutInflater.from(activity).cloneInContext(activity);
        this.layoutInflater.setFactory(new StreamLayoutInflatorFactory(activity, 2130772007, 2131296598));
        this.viewCache = new StreamViewCache(this.layoutInflater);
    }

    public Activity getActivity() {
        return this.activity;
    }

    public StreamAdapterListener getStreamAdapterListener() {
        return this.streamAdapterListener;
    }

    public LayoutInflater getLayoutInflater() {
        return this.layoutInflater;
    }

    public StreamViewCache getViewCache() {
        return this.viewCache;
    }

    public ScrollLoadBlocker getImageLoadBlocker() {
        return this.imageLoadBlocker;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getLogContext() {
        return this.logContext;
    }

    public void notifyContentWithOptionsChanged() {
        if (this.notifyContentChangeListener != null) {
            this.notifyContentChangeListener.onContentWithOptionsChanged();
        }
    }

    public void setNotifyContentChangeListener(NotifyContentChangeListener notifyContentChangeListener) {
        this.notifyContentChangeListener = notifyContentChangeListener;
    }
}

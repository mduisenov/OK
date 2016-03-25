package ru.ok.android.ui.custom.animations;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class OnlineAnimationManager {
    private static final OnlineAnimationManager instance;
    private final Handler handler;
    private int launchCount;
    private final List<WeakReference<OnlineAnimationObserver>> observers;
    private short reverse;
    private int value;

    /* renamed from: ru.ok.android.ui.custom.animations.OnlineAnimationManager.1 */
    class C06381 extends Handler {
        int count;

        C06381() {
            this.count = 0;
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (OnlineAnimationManager.this.value == 55) {
                OnlineAnimationManager.this.reverse = (short) -1;
            }
            if (OnlineAnimationManager.this.value == MotionEventCompat.ACTION_MASK) {
                if (this.count < 10) {
                    this.count++;
                }
                this.count = 0;
                OnlineAnimationManager.this.reverse = (short) 1;
            }
            OnlineAnimationManager.this.value = OnlineAnimationManager.this.value - (OnlineAnimationManager.this.reverse * 25);
            OnlineAnimationManager.this.notifyObservers(OnlineAnimationManager.this.value);
            sendEmptyMessageDelayed(1, 75);
        }
    }

    static {
        instance = new OnlineAnimationManager();
    }

    private OnlineAnimationManager() {
        this.observers = new ArrayList();
        this.value = MotionEventCompat.ACTION_MASK;
        this.reverse = (short) 1;
        this.handler = new C06381();
    }

    public static OnlineAnimationManager getInstance() {
        return instance;
    }

    public void onStartAnimation() {
        this.launchCount++;
        if (this.launchCount == 1) {
            this.handler.removeMessages(1);
            this.handler.sendEmptyMessageDelayed(1, 75);
        }
    }

    public void onStopAnimation() {
        this.launchCount--;
        if (this.launchCount == 0) {
            this.handler.removeMessages(1);
        }
    }

    public void addObserver(OnlineAnimationObserver observer) {
        this.observers.add(new WeakReference(observer));
    }

    private void notifyObservers(int alpha) {
        Iterator<WeakReference<OnlineAnimationObserver>> it = this.observers.iterator();
        while (it.hasNext()) {
            OnlineAnimationObserver observer = (OnlineAnimationObserver) ((WeakReference) it.next()).get();
            if (observer != null) {
                observer.handleAlpha(alpha);
            } else {
                it.remove();
            }
        }
    }
}

package ru.ok.android.ui.custom;

import android.os.Handler;
import android.view.MotionEvent;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;

public class GestureHandler {
    private int activeItemId;
    GestureHandlerCallback callback;
    private int eventOriginItemId;
    private State state;
    private boolean tapPossible;
    private Runnable timeoutRunnable;
    private Handler timeoutsHandler;

    interface GestureHandlerCallback {
        int getItemAtPoint(float f, float f2);

        void onCancelled(int i);

        void onItemEntered(int i);

        void onItemHeld(int i);

        void onItemTapped(int i);

        void onOutside(int i);

        void onTouchDown(int i);

        void onTouchUp(int i, int i2);
    }

    /* renamed from: ru.ok.android.ui.custom.GestureHandler.1 */
    class C06191 implements Runnable {
        C06191() {
        }

        public void run() {
            if (GestureHandler.this.state == State.STATE_DOWN) {
                GestureHandler.this.callback.onItemHeld(GestureHandler.this.activeItemId);
            }
        }
    }

    private enum State {
        STATE_INACTIVE,
        STATE_DOWN
    }

    public GestureHandler(GestureHandlerCallback callback) {
        this.tapPossible = false;
        this.state = State.STATE_INACTIVE;
        this.activeItemId = -1;
        this.timeoutsHandler = new Handler();
        this.timeoutRunnable = new C06191();
        this.callback = callback;
    }

    public boolean handleEvent(MotionEvent ev) {
        int i = 0;
        float x = ev.getRawX();
        float y = ev.getRawY();
        int eventItemId;
        switch (ev.getAction()) {
            case RECEIVED_VALUE:
                eventItemId = this.callback.getItemAtPoint(x, y);
                this.eventOriginItemId = eventItemId;
                if (-1 == eventItemId) {
                    return false;
                }
                if (this.state == State.STATE_DOWN) {
                    Logger.m184w("Double DOWN");
                } else {
                    this.tapPossible = true;
                }
                this.state = State.STATE_DOWN;
                this.timeoutsHandler.postDelayed(this.timeoutRunnable, 300);
                if (this.activeItemId != eventItemId) {
                    this.activeItemId = eventItemId;
                    this.callback.onTouchDown(this.activeItemId);
                }
                return true;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.state != State.STATE_DOWN) {
                    return false;
                }
                eventItemId = this.callback.getItemAtPoint(x, y);
                boolean z = this.tapPossible;
                if (eventItemId == this.activeItemId) {
                    i = 1;
                }
                this.tapPossible = i & z;
                this.timeoutsHandler.removeCallbacks(this.timeoutRunnable);
                if (!this.tapPossible || ev.getEventTime() - ev.getDownTime() >= 300) {
                    this.callback.onTouchUp(eventItemId, this.eventOriginItemId);
                } else {
                    this.callback.onItemTapped(eventItemId);
                }
                this.state = State.STATE_INACTIVE;
                this.activeItemId = -1;
                return true;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.state != State.STATE_DOWN) {
                    return false;
                }
                eventItemId = this.callback.getItemAtPoint(x, y);
                if (eventItemId != this.activeItemId) {
                    this.tapPossible = false;
                    this.timeoutsHandler.removeCallbacks(this.timeoutRunnable);
                    if (this.activeItemId != -1) {
                        this.callback.onOutside(this.activeItemId);
                    }
                    if (eventItemId != -1) {
                        this.callback.onItemEntered(eventItemId);
                        this.timeoutsHandler.removeCallbacks(this.timeoutRunnable);
                        this.timeoutsHandler.postDelayed(this.timeoutRunnable, 300);
                    }
                    this.activeItemId = eventItemId;
                }
                return true;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.state != State.STATE_DOWN) {
                    return false;
                }
                this.timeoutsHandler.removeCallbacks(this.timeoutRunnable);
                this.callback.onCancelled(this.activeItemId);
                this.state = State.STATE_INACTIVE;
                this.activeItemId = -1;
                return true;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (this.state != State.STATE_DOWN || this.activeItemId == -1) {
                    return false;
                }
                this.timeoutsHandler.removeCallbacks(this.timeoutRunnable);
                this.tapPossible = false;
                this.callback.onOutside(this.activeItemId);
                this.activeItemId = -1;
                return true;
            default:
                return false;
        }
    }
}

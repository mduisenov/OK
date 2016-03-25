package ru.ok.android.utils.music;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import ru.ok.android.proto.MessagesProto.Message;

public class SeekBarTouchDelegate extends TouchDelegate {
    private Rect mBounds;
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private int mSlop;
    private Rect mSlopBounds;

    public SeekBarTouchDelegate(Rect bounds, View delegateView) {
        super(bounds, delegateView);
        this.mBounds = bounds;
        this.mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
        this.mSlopBounds = new Rect(bounds);
        this.mSlopBounds.inset(-this.mSlop, -this.mSlop);
        this.mDelegateView = delegateView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean sendToDelegate = false;
        boolean hit = true;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                if (this.mBounds.contains(x, y)) {
                    this.mDelegateTargeted = true;
                    sendToDelegate = true;
                    break;
                }
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                sendToDelegate = this.mDelegateTargeted;
                if (sendToDelegate && !this.mSlopBounds.contains(x, y)) {
                    hit = false;
                    break;
                }
            case Message.TYPE_FIELD_NUMBER /*3*/:
                sendToDelegate = this.mDelegateTargeted;
                this.mDelegateTargeted = false;
                break;
        }
        if (!sendToDelegate) {
            return false;
        }
        View delegateView = this.mDelegateView;
        if (hit) {
            event.setLocation((float) x, (float) (delegateView.getHeight() / 2));
        } else {
            int slop = this.mSlop;
            event.setLocation((float) (-(slop * 2)), (float) (-(slop * 2)));
        }
        return delegateView.dispatchTouchEvent(event);
    }
}

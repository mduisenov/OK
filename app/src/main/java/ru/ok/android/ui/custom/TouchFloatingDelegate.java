package ru.ok.android.ui.custom;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import ru.ok.android.proto.MessagesProto.Message;

public final class TouchFloatingDelegate extends TouchDelegate {
    private final int bottomOffset;
    private boolean delegateTargeted;
    private final View delegateView;
    private final int leftOffset;
    private final int rightOffset;
    private final int slop;
    private final int topOffset;

    public TouchFloatingDelegate(View delegateView, int offset) {
        this(delegateView, offset, offset, offset, offset);
    }

    public TouchFloatingDelegate(View delegateView, int leftOffset, int topOffset, int rightOffset, int bottomOffset) {
        super(new Rect(), delegateView);
        this.delegateView = delegateView;
        this.slop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
        this.leftOffset = this.slop + leftOffset;
        this.topOffset = this.slop + topOffset;
        this.rightOffset = this.slop + rightOffset;
        this.bottomOffset = this.slop + bottomOffset;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.delegateView.getVisibility() != 0) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean sendToDelegate = false;
        boolean hit = true;
        int action = event.getAction();
        switch (action) {
            case RECEIVED_VALUE:
                if (isHit(x, y)) {
                    this.delegateTargeted = true;
                    sendToDelegate = true;
                    break;
                }
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                sendToDelegate = this.delegateTargeted;
                if (sendToDelegate && !isHit(x, y)) {
                    hit = false;
                }
                if (action == 1) {
                    this.delegateTargeted = false;
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                sendToDelegate = this.delegateTargeted;
                this.delegateTargeted = false;
                break;
        }
        if (!sendToDelegate) {
            return false;
        }
        View delegateView = this.delegateView;
        if (hit) {
            event.setLocation((float) (delegateView.getWidth() / 2), (float) (delegateView.getHeight() / 2));
        } else {
            event.setLocation((float) (-(this.slop * 2)), (float) (-(this.slop * 2)));
        }
        return delegateView.dispatchTouchEvent(event);
    }

    private boolean isHit(int x, int y) {
        return this.delegateView.getLeft() - this.leftOffset <= x && x <= this.delegateView.getRight() + this.rightOffset && this.delegateView.getTop() - this.topOffset <= y && y <= this.delegateView.getBottom() + this.bottomOffset;
    }
}

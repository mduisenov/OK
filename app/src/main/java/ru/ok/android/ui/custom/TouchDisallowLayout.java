package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import ru.ok.android.proto.MessagesProto.Message;

public class TouchDisallowLayout extends LinearLayout {
    private boolean disallow;

    public TouchDisallowLayout(Context context) {
        super(context);
        this.disallow = false;
    }

    public TouchDisallowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.disallow = false;
    }

    public TouchDisallowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.disallow = false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case RECEIVED_VALUE:
                requestDisallowInterceptTouchEvent(true);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                requestDisallowInterceptTouchEvent(true);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }
}

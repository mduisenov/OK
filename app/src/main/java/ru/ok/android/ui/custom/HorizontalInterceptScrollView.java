package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import ru.ok.android.proto.MessagesProto.Message;

public final class HorizontalInterceptScrollView extends HorizontalScrollView {
    public HorizontalInterceptScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case RECEIVED_VALUE:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return b;
    }
}

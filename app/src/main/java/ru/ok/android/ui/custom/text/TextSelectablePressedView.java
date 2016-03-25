package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.proto.MessagesProto.Message;

public final class TextSelectablePressedView extends TextView {
    public TextSelectablePressedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean unpress = false;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                ((ViewGroup) getParent()).setPressed(true);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                unpress = true;
                break;
        }
        boolean res = super.onTouchEvent(event);
        if (unpress) {
            ((ViewGroup) getParent()).setPressed(false);
        }
        return res;
    }
}

package ru.ok.android.ui.quickactions;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import ru.mail.libverify.C0176R;

public class ContextPopupWindow extends PopupWindow implements OnDismissListener {
    protected boolean hideOnMenuClick;
    protected Context mContext;
    protected WindowManager mWindowManager;
    protected View mainAnchor;

    /* renamed from: ru.ok.android.ui.quickactions.ContextPopupWindow.1 */
    class C11771 implements OnTouchListener {
        C11771() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != 4) {
                return false;
            }
            ContextPopupWindow.this.dismiss();
            return true;
        }
    }

    public ContextPopupWindow(Context context) {
        super(context);
        this.hideOnMenuClick = false;
        this.mContext = context;
        setTouchInterceptor(new C11771());
        this.mWindowManager = (WindowManager) context.getSystemService("window");
    }

    protected void preShow() {
        onShow();
        setBackgroundDrawable(getContext().getResources().getDrawable(C0176R.drawable.abc_popup_background_mtrl_mult));
        setWidth(-2);
        setHeight(-2);
        setTouchable(true);
        setOnDismissListener(this);
        setFocusable(getOutClickHide());
        setOutsideTouchable(getOutClickHide());
    }

    protected boolean getOutClickHide() {
        return true;
    }

    public void onDismiss() {
    }

    protected void onShow() {
    }

    protected Context getContext() {
        return this.mContext;
    }
}

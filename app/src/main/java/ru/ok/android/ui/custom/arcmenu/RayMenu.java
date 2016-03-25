package ru.ok.android.ui.custom.arcmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class RayMenu extends RelativeLayout {
    private ImageView mHintView;
    private RayLayout mRayLayout;

    /* renamed from: ru.ok.android.ui.custom.arcmenu.RayMenu.1 */
    class C06441 implements OnTouchListener {
        C06441() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                RayMenu.this.mHintView.startAnimation(RayMenu.createHintSwitchAnimation(RayMenu.this.mRayLayout.isExpanded()));
                RayMenu.this.mRayLayout.switchState(true);
            }
            return false;
        }
    }

    public RayMenu(Context context) {
        super(context);
        init(context);
    }

    public RayMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new LayoutParams(-1, -2));
        setClipChildren(false);
        LayoutInflater li = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mRayLayout = (RayLayout) findViewById(2131624619);
        ViewGroup controlLayout = (ViewGroup) findViewById(2131624620);
        controlLayout.setClickable(true);
        controlLayout.setOnTouchListener(new C06441());
        this.mHintView = (ImageView) findViewById(2131624621);
    }

    private static Animation createHintSwitchAnimation(boolean expanded) {
        float f;
        float f2 = 0.0f;
        if (expanded) {
            f = 45.0f;
        } else {
            f = 0.0f;
        }
        if (!expanded) {
            f2 = 45.0f;
        }
        Animation animation = new RotateAnimation(f, f2, 1, 0.5f, 1, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);
        return animation;
    }
}

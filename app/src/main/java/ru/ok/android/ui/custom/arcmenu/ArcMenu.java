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

public class ArcMenu extends RelativeLayout {
    private ArcLayout mArcLayout;
    private ImageView mHintView;

    /* renamed from: ru.ok.android.ui.custom.arcmenu.ArcMenu.1 */
    class C06411 implements OnTouchListener {
        C06411() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                ArcMenu.this.mHintView.startAnimation(ArcMenu.createHintSwitchAnimation(ArcMenu.this.mArcLayout.isExpanded()));
                ArcMenu.this.mArcLayout.switchState(true);
            }
            return false;
        }
    }

    public ArcMenu(Context context) {
        super(context);
        init(context);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(2130903103, this);
        this.mArcLayout = (ArcLayout) findViewById(2131624619);
        ViewGroup controlLayout = (ViewGroup) findViewById(2131624620);
        controlLayout.setClickable(true);
        controlLayout.setOnTouchListener(new C06411());
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

package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.ui.image.view.ActionToastManager;

public class ActionToastView extends LinearLayout {
    private View closeView;
    private TextView infoMessageView;

    /* renamed from: ru.ok.android.ui.custom.photo.ActionToastView.1 */
    class C07011 implements OnClickListener {
        C07011() {
        }

        public void onClick(View view) {
            ViewGroup parent = (ViewGroup) ActionToastView.this.getParent();
            if (parent != null) {
                ActionToastManager.hideToastFrom(parent, ActionToastView.this);
            }
        }
    }

    public ActionToastView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    public ActionToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public ActionToastView(Context context) {
        super(context);
        onCreate();
    }

    private void onCreate() {
        setBackgroundResource(2130838534);
        setGravity(16);
        int padding = getResources().getDimensionPixelSize(2131230867);
        setPadding(padding, padding, padding, padding);
        LayoutInflater.from(getContext()).inflate(2130903071, this, true);
        this.infoMessageView = (TextView) findViewById(2131624494);
        this.infoMessageView.setPaintFlags(this.infoMessageView.getPaintFlags() | 8);
        this.closeView = findViewById(2131624495);
        this.closeView.setOnClickListener(new C07011());
    }

    public void setInfoMessage(int resid) {
        this.infoMessageView.setText(resid);
    }

    public void setInfoMessage(CharSequence text) {
        this.infoMessageView.setText(text);
    }

    public final void fadeIn(AnimationListener listener) {
        animateAlpha(0.0f, 1.0f, listener);
    }

    public final void fadeOut(AnimationListener listener) {
        animateAlpha(1.0f, 0.0f, listener);
    }

    private final void animateAlpha(float from, float to, AnimationListener listener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setAnimationListener(listener);
        clearAnimation();
        startAnimation(alphaAnimation);
    }
}

package ru.ok.android.ui.custom.highlight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.afollestad.materialdialogs.C0047R;
import ru.mail.libverify.C0176R;
import ru.ok.android.utils.DimenUtils;

public class SimpleHighlightView extends HighlightOverlayView {
    private View closeView;
    private View contentView;
    private int gravity;
    private Interpolator interpolator;
    private TextView messageView;
    private TextView titleView;
    private int translation;
    private int verticalMargin;

    public SimpleHighlightView(Context context) {
        super(context);
        this.gravity = 0;
        this.interpolator = new DecelerateInterpolator();
        onCreate();
    }

    public SimpleHighlightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gravity = 0;
        this.interpolator = new DecelerateInterpolator();
        onCreate();
    }

    public SimpleHighlightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.gravity = 0;
        this.interpolator = new DecelerateInterpolator();
        onCreate();
    }

    private void onCreate() {
        this.translation = DimenUtils.getRealDisplayPixels(100, getContext());
        this.verticalMargin = getResources().getDimensionPixelSize(2131231029);
        LayoutInflater.from(getContext()).inflate(2130903574, this, true);
        this.contentView = findViewById(C0047R.id.content);
        this.titleView = (TextView) findViewById(C0176R.id.title);
        this.messageView = (TextView) findViewById(2131624538);
        this.closeView = findViewById(2131625435);
        updateContentGravity();
    }

    protected final void updateContentGravity() {
        LayoutParams params = (LayoutParams) this.contentView.getLayoutParams();
        int topMargin = 0;
        int bottomMargin = 0;
        if (this.gravity == 2) {
            topMargin = this.verticalMargin;
            params.addRule(13, 0);
            params.addRule(12, 0);
            params.addRule(10);
        } else if (this.gravity == 1) {
            params.addRule(13, 0);
            bottomMargin = this.verticalMargin;
            params.addRule(10, 0);
            params.addRule(12);
        } else {
            params.addRule(13);
            params.addRule(10, 0);
            params.addRule(12, 0);
        }
        params.setMargins(params.leftMargin, topMargin, params.rightMargin, bottomMargin);
        this.contentView.setLayoutParams(params);
    }

    protected void onShowAnimationStart(long duration) {
        this.contentView.setTranslationY((float) this.translation);
        animateShow(this.contentView, duration);
    }

    private void animateShow(View view, long duration) {
        view.animate().translationY(0.0f).setDuration(duration).setInterpolator(this.interpolator).start();
    }

    protected void onHideAnimationStart(long duration) {
        animateHide(this.contentView, duration);
    }

    private void animateHide(View view, long duration) {
        view.animate().translationY((float) this.translation).setDuration(duration).setInterpolator(this.interpolator).start();
    }

    public void setTitle(CharSequence title) {
        this.titleView.setText(title);
    }

    public void setMessage(CharSequence message) {
        this.messageView.setText(message);
    }

    public void setOnCloseButtonListener(OnClickListener onClickListener) {
        this.closeView.setOnClickListener(onClickListener);
    }

    public void setGravity(int gravity) {
        if (this.gravity != gravity) {
            this.gravity = gravity;
            updateContentGravity();
            invalidate();
        }
    }
}

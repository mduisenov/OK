package ru.ok.android.ui.custom.scroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.utils.localization.LocalizationManager;

public final class ScrollTopView extends FrameLayout implements OnClickListener {
    private ImageView actionView;
    private NewEventsMode mode;
    private NotificationsView notificationsView;
    private OnClickScrollListener onClickScrollListener;
    private Route route;
    private TextView textView;

    /* renamed from: ru.ok.android.ui.custom.scroll.ScrollTopView.1 */
    class C07511 implements AnimationListener {
        C07511() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            ScrollTopView.this.setRoute(ScrollTopView.this.route);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: ru.ok.android.ui.custom.scroll.ScrollTopView.2 */
    static /* synthetic */ class C07522 {
        static final /* synthetic */ int[] f99x616a098d;

        static {
            f99x616a098d = new int[NewEventsMode.values().length];
            try {
                f99x616a098d[NewEventsMode.ROUND_ARROW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f99x616a098d[NewEventsMode.STRAIGHT_ARROW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f99x616a098d[NewEventsMode.TEXT_AND_ARROW.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum NewEventsMode {
        STRAIGHT_ARROW,
        ROUND_ARROW,
        TEXT_AND_ARROW
    }

    public interface OnClickScrollListener {
        void onScrollTopClick(int i);
    }

    public enum Route {
        TOP,
        BOTTOM
    }

    public ScrollTopView(Context context) {
        this(context, null);
    }

    public ScrollTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public ScrollTopView(Context context, AttributeSet attrs, int defThemeAttr, int defStyle) {
        super(context, attrs, defThemeAttr);
        this.route = Route.TOP;
        this.mode = NewEventsMode.ROUND_ARROW;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.UpdateView, defThemeAttr, defStyle);
        int route = a.getInteger(0, 0);
        a.recycle();
        LocalizationManager.inflate(context, 2130903422, (ViewGroup) this, true);
        this.actionView = (ImageView) findViewById(2131625292);
        this.textView = (TextView) findViewById(C0263R.id.text);
        this.notificationsView = (NotificationsView) findViewById(2131624499);
        this.notificationsView.setSimpleBubble();
        Route routeEnum = route == 0 ? Route.TOP : Route.BOTTOM;
        LayoutParams lp = (LayoutParams) this.notificationsView.getLayoutParams();
        lp.gravity = 5;
        lp.gravity = (routeEnum == Route.TOP ? 48 : 80) | lp.gravity;
        setVisibility(8);
        setNewEventCount(0, true);
        setOnClickListener(this);
        setRoute(routeEnum);
    }

    public void setRoute(Route route) {
        this.route = route;
        if (route == Route.TOP) {
            setDrawableResourceForActionView(2130837953);
        } else {
            setDrawableResourceForActionView(2130837951);
        }
    }

    private void setDrawableResourceForActionView(int resource) {
        this.actionView.setImageResource(resource);
    }

    public void setNewEventsMode(NewEventsMode mode) {
        this.mode = mode;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            clearAnimation();
            setVisibility(8);
        }
    }

    public void setNewEventCount(int count, boolean autoHide) {
        updateBubble(count, autoHide);
    }

    public int getNewEventCount() {
        return this.notificationsView.getValue();
    }

    public void clearEvents(boolean autoHide) {
        setNewEventCount(0, autoHide);
    }

    public void setTextResourceId(int textResourceId) {
        this.textView.setText(LocalizationManager.getString(getContext(), textResourceId));
    }

    protected void updateBubble(int count, boolean autoHide) {
        this.notificationsView.setValue(count);
        if (count > 0) {
            switch (C07522.f99x616a098d[this.mode.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.notificationsView.setVisibility(0);
                    this.textView.setVisibility(8);
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.notificationsView.setVisibility(0);
                    this.textView.setVisibility(8);
                    setRoute(this.route);
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.textView.setVisibility(0);
                    setDrawableResourceForActionView(this.route == Route.BOTTOM ? 2130837952 : 2130837954);
                    break;
            }
            if (autoHide) {
                show();
                return;
            }
            return;
        }
        this.notificationsView.setVisibility(8);
        this.textView.setVisibility(8);
        setRoute(this.route);
        if (autoHide) {
            hide();
        }
    }

    public void show() {
        if (getVisibility() != 0) {
            clearAnimation();
            setVisibility(0);
            startAnimation(getAnimationIn());
        }
    }

    public void hide() {
        if (getVisibility() == 0) {
            clearAnimation();
            setVisibility(8);
            startAnimation(getAnimationOut());
        }
    }

    public void onClick(View v) {
        if (this.onClickScrollListener != null) {
            this.onClickScrollListener.onScrollTopClick(getNewEventCount());
        }
    }

    public void setOnClickScrollListener(OnClickScrollListener onClickScrollListener) {
        this.onClickScrollListener = onClickScrollListener;
    }

    public void onScroll(boolean isWantToShow, boolean isWantToHide) {
        if (!isEnabled()) {
            return;
        }
        if (getVisibility() == 8) {
            if (isWantToShow) {
                show();
            }
        } else if (isWantToHide && getNewEventCount() == 0) {
            hide();
        }
    }

    private Animation getAnimationOut() {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(100);
        fadeOut.setAnimationListener(new C07511());
        return fadeOut;
    }

    private Animation getAnimationIn() {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(100);
        return fadeIn;
    }
}

package ru.ok.android.ui.tabbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.WeakHashMap;

public abstract class Tabbar extends LinearLayout implements CurrentActionKeeper {
    private final LinearLayout actionsContainer;
    private Action currentAction;
    private Typeface font;
    private final OnClickListener mOnActionClickListener;
    private WeakHashMap<OnTranslationChangeListener, Boolean> translationListeners;

    public interface OnTranslationChangeListener {
        void onTranslationChanged(float f, float f2, float f3);
    }

    /* renamed from: ru.ok.android.ui.tabbar.Tabbar.1 */
    class C12831 implements OnClickListener {
        C12831() {
        }

        public void onClick(View view) {
            TabbarActionView tav = (TabbarActionView) view;
            Action action = tav.getAction();
            if (action.performAction(view)) {
                boolean needChangeAction;
                if (action == null || action.canBeSelected()) {
                    needChangeAction = true;
                } else {
                    needChangeAction = false;
                }
                for (int i = 0; i < Tabbar.this.actionsContainer.getChildCount() && needChangeAction; i++) {
                    boolean z;
                    TabbarActionView tavChild = Tabbar.this.getTabbarActionView(i);
                    if (tavChild == tav) {
                        z = true;
                    } else {
                        z = false;
                    }
                    tavChild.setSelected(z);
                }
            }
        }
    }

    protected abstract void buildActions();

    public Tabbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.translationListeners = new WeakHashMap();
        this.mOnActionClickListener = new C12831();
        LayoutInflater.from(context).inflate(2130903532, this, true);
        this.actionsContainer = (LinearLayout) findViewById(2131625394);
        this.actionsContainer.setOrientation(0);
        init();
        buildActions();
    }

    public void init() {
        for (int i = 0; i < this.actionsContainer.getChildCount(); i++) {
            prepareActionView(getTabbarActionView(i));
        }
        invalidate();
    }

    private TabbarActionView getTabbarActionView(int i) {
        return (TabbarActionView) this.actionsContainer.getChildAt(i);
    }

    public void setFont(Typeface font) {
        this.font = font;
        if (font != null) {
            int count = this.actionsContainer.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    getTabbarActionView(i).setTypeface(font);
                }
            }
        }
    }

    public void animateShow(AnimationListener animationListener) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), 2130968634);
        anim.setAnimationListener(animationListener);
        startAnimation(anim);
        setVisibility(0);
    }

    public void onSelectAction(Action action) {
        boolean needChangeAction;
        if (action == null || action.canBeSelected()) {
            needChangeAction = true;
        } else {
            needChangeAction = false;
        }
        for (int i = 0; i < this.actionsContainer.getChildCount() && needChangeAction; i++) {
            boolean z;
            TabbarActionView tavChild = getTabbarActionView(i);
            if (action == tavChild.getAction()) {
                z = true;
            } else {
                z = false;
            }
            tavChild.setSelected(z);
            this.currentAction = action;
        }
    }

    public void addAction(Action action) {
        TabbarActionView actionView = new TabbarActionView(getContext(), action);
        prepareActionView(actionView);
        if (this.font != null) {
            actionView.setTypeface(this.font);
        }
        actionView.setOnClickListener(this.mOnActionClickListener);
        addAction(this.actionsContainer, actionView);
        action.registerCurrentActionKeeper(this);
        action.setEventBubbleView(actionView.getNotificationsView());
    }

    public void clear() {
        for (int i = 0; i < this.actionsContainer.getChildCount(); i++) {
            TabbarActionView tavChild = getTabbarActionView(i);
            Action childAction = tavChild.getAction();
            tavChild.setSelected(false);
            childAction.hideBubble();
        }
    }

    public Action getCurrentAction() {
        return this.currentAction;
    }

    public void addAction(LinearLayout actionsView, View actionView) {
        actionsView.addView(actionView, new LayoutParams(-1, -1, 1.0f));
    }

    public void prepareActionView(TabbarActionView tav) {
        tav.setSelectorPosition(0);
        tav.getContentContainer().setPadding(0, tav.getContext().getResources().getDimensionPixelSize(2131231192), 0, 0);
    }

    public void invalidateLocale() {
        for (int i = 0; i < this.actionsContainer.getChildCount(); i++) {
            View child = this.actionsContainer.getChildAt(i);
            if (child instanceof TabbarActionView) {
                ((TabbarActionView) child).updateText();
            }
        }
    }

    public void setTranslationX(float translationX) {
        if (translationX != getTranslationX()) {
            super.setTranslationX(translationX);
            fireTranslationChanged();
        }
    }

    public void setTranslationY(float translationY) {
        if (translationY != getTranslationY()) {
            super.setTranslationY(translationY);
            fireTranslationChanged();
        }
    }

    @TargetApi(21)
    public void setTranslationZ(float translationZ) {
        if (translationZ != getTranslationZ()) {
            super.setTranslationZ(translationZ);
            fireTranslationChanged();
        }
    }

    @SuppressLint({"NewApi"})
    private void fireTranslationChanged() {
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        float translationZ = VERSION.SDK_INT >= 21 ? getTranslationZ() : 0.0f;
        for (OnTranslationChangeListener listener : this.translationListeners.keySet()) {
            if (listener != null) {
                listener.onTranslationChanged(translationX, translationY, translationZ);
            }
        }
    }

    public void addWeakTranslationListener(OnTranslationChangeListener listener) {
        this.translationListeners.put(listener, Boolean.valueOf(true));
    }
}

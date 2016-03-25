package ru.ok.android.ui.tabbar.actions;

import android.view.View;
import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;

public class MenuTabbarAction extends ResetNotificationsAction {
    public MenuTabbarAction(OnActionListener listener) {
        super(listener);
    }

    public boolean performAction(View view) {
        return notifyAction();
    }

    public int getTextRes() {
        return 2131166190;
    }

    public int getDrawable() {
        return 2130838263;
    }

    public boolean canBeSelected() {
        return false;
    }

    public void showBubble(int i) {
        this.bubble.setVisibility(0);
        this.bubble.setSimpleBubble();
    }

    public void hideBubble() {
        this.bubble.setVisibility(8);
    }
}

package ru.ok.android.ui.tabbar.actions;

import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;

public final class FeedPageAction extends ResetNotificationsAction {
    public FeedPageAction(OnActionListener listener) {
        super(listener);
    }

    public int getDrawable() {
        return 2130838259;
    }

    public int getTextRes() {
        return 2131165852;
    }

    public void showBubble(int i) {
        if (i == Integer.MAX_VALUE) {
            this.bubble.setVisibility(0);
            this.bubble.setSimpleBubble();
            return;
        }
        this.bubble.showText();
        super.showBubble(i);
    }
}

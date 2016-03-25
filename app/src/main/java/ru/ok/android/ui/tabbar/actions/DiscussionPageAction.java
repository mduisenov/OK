package ru.ok.android.ui.tabbar.actions;

import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;

public class DiscussionPageAction extends ResetNotificationsAction {
    public DiscussionPageAction(OnActionListener listener) {
        super(listener);
    }

    public int getTextRes() {
        return 2131165715;
    }

    public int getDrawable() {
        return 2130838256;
    }

    public void showLikeBubble() {
        this.bubble.setNotificationText("");
        this.bubble.setImage(2130838717);
        this.bubble.setVisibility(0);
        this.isBubbleShow = true;
    }

    public void showReplyBubble() {
        this.bubble.setNotificationText("");
        this.bubble.setImage(2130838719);
        this.bubble.setVisibility(0);
        this.isBubbleShow = true;
    }

    public void showBubble() {
        this.bubble.showText();
        super.showBubble();
    }

    public void showBubble(int count) {
        this.bubble.showText();
        super.showBubble(count);
    }

    public void hideBubble() {
        super.hideBubble();
        this.bubble.hideImage();
        this.bubble.setVisibility(8);
    }
}

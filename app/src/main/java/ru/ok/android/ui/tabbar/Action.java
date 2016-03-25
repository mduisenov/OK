package ru.ok.android.ui.tabbar;

import ru.ok.android.ui.custom.NotificationsView;

public abstract class Action implements ru.ok.android.ui.Action {
    protected NotificationsView bubble;
    protected boolean isBubbleShow;
    protected CurrentActionKeeper keeper;

    public abstract int getTextRes();

    public Action() {
        this.isBubbleShow = false;
    }

    public void registerCurrentActionKeeper(CurrentActionKeeper keeper) {
        this.keeper = keeper;
    }

    protected Action getCurrentActionFromKepper() {
        return this.keeper.getCurrentAction();
    }

    public void setEventBubbleView(NotificationsView view) {
        this.bubble = view;
    }

    public void showBubble(int count) {
        if (this.bubble != null) {
            this.bubble.setValue(count);
        }
        if (count > 0) {
            this.bubble.setVisibility(0);
            this.isBubbleShow = true;
            return;
        }
        this.bubble.setVisibility(4);
        this.isBubbleShow = false;
    }

    public void showBubble() {
        showBubble(1);
    }

    public void hideBubble() {
        if (this.bubble != null) {
            this.bubble.setVisibility(4);
            this.bubble.setValue(0);
            this.isBubbleShow = false;
        }
    }

    public boolean canBeSelected() {
        return true;
    }
}

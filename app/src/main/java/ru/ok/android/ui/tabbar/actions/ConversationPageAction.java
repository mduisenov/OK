package ru.ok.android.ui.tabbar.actions;

import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;

public final class ConversationPageAction extends ResetNotificationsAction {
    public ConversationPageAction(OnActionListener resetListener) {
        super(resetListener);
    }

    public int getTextRes() {
        return 2131165638;
    }

    public int getDrawable() {
        return 2130838266;
    }
}

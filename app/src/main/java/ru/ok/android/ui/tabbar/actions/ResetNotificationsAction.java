package ru.ok.android.ui.tabbar.actions;

import android.view.View;
import ru.ok.android.ui.tabbar.Action;

public abstract class ResetNotificationsAction extends Action {
    private final OnActionListener listener;

    public interface OnActionListener {
        boolean onPerformAction(Action action);

        void onResetNotification(Action action, Action action2);
    }

    protected ResetNotificationsAction(OnActionListener listener) {
        this.listener = listener;
    }

    public boolean performAction(View view) {
        Action predAction = getCurrentActionFromKepper();
        if (!(predAction == null || predAction == this)) {
            notifyReset(predAction);
        }
        return notifyAction();
    }

    private void notifyReset(Action action) {
        this.listener.onResetNotification(action, this);
    }

    protected boolean notifyAction() {
        return this.listener.onPerformAction(this);
    }
}

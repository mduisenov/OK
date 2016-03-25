package ru.ok.android.ui.activity;

import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public final class NotificationsActivity extends OdklSubActivity {
    protected Type getSlidingMenuSelectedItem() {
        return Type.notifications;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}

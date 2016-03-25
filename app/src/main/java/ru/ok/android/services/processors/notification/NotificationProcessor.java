package ru.ok.android.services.processors.notification;

import android.os.Bundle;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.processors.BackgroundProcessor;
import ru.ok.android.services.processors.notification.tasks.LoadGroupAvatarTask;
import ru.ok.android.services.processors.notification.tasks.LoadUserAvatarTask;

public final class NotificationProcessor extends BackgroundProcessor {
    @Subscribe(on = 2131623944, to = 2131624093)
    public void onNotificationReceived(BusEvent event) {
        Bundle data = event.bundleInput;
        if (data != null) {
            NotificationSignal notificationSignal = new NotificationSignalFactory(OdnoklassnikiApplication.getContext()).createNotificationSignalFromBundle(data);
            if (notificationSignal != null) {
                String senderId = data.getString("sender_id");
                String groupId = data.getString("group_id");
                if (groupId != null) {
                    doAsync(new LoadGroupAvatarTask(groupId, notificationSignal));
                } else if (senderId != null) {
                    doAsync(new LoadUserAvatarTask(senderId, notificationSignal));
                } else {
                    notificationSignal.performNotification();
                }
            }
        }
    }
}

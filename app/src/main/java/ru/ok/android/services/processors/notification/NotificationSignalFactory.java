package ru.ok.android.services.processors.notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.onelog.PushDeliveryLog;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.ui.activity.PresentsActivity;
import ru.ok.android.ui.activity.main.LinksActivity;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.presents.activity.PreloadPresentReceivedActivity;
import ru.ok.android.ui.stream.list.StreamItemAdapter;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.NotificationType;
import ru.ok.model.Discussion;

public class NotificationSignalFactory {
    @NonNull
    private Bundle bundle;
    @NonNull
    private final Context context;
    @NonNull
    private NotificationType notificationType;
    @NonNull
    private String tickerText;

    public NotificationSignalFactory(@NonNull Context context) {
        this.context = context;
    }

    @Nullable
    public NotificationSignal createNotificationSignalFromBundle(@NonNull Bundle bundle) {
        this.tickerText = bundle.getString(Message.ELEMENT);
        this.notificationType = NotifyReceiver.getNotificationsSettings(this.context);
        this.bundle = bundle;
        if (this.tickerText == null) {
            return null;
        }
        NotificationSignal signal = null;
        if (bundle.getBoolean("hidden", false)) {
            return null;
        }
        if (isOpenProfileForPresent()) {
            signal = createDiscussionSignal();
        } else if (isMakePresentToId()) {
            signal = createOpenProfileForPresentSignal(bundle.getString("present_to_id"), bundle.getString("present_holiday_id"));
        } else if (isReceivedPresent()) {
            signal = createReceivePresentSignal(bundle.getString("present_notification_id"));
        } else if (isOpenNotificationsPage()) {
            signal = createOpenNotificationsSignal();
            if (isReceivedPresent()) {
                signal.setPriority(1);
            }
        } else if (isOpenUri()) {
            signal = createOpenUriNotificationSignal(this.context, this.tickerText, bundle.getString("uri"), this.notificationType);
        } else if (!TextUtils.isEmpty(this.tickerText)) {
            String collapseKey = bundle.getString("key");
            signal = createDefaultNotificationSignal(collapseKey == null ? 138 : collapseKey.hashCode());
        }
        if (signal == null || !isHighPriority()) {
            return signal;
        }
        signal.setPriority(1);
        return signal;
    }

    public void logCreationTime(@NonNull Bundle bundle) {
        this.tickerText = bundle.getString(Message.ELEMENT);
        this.bundle = bundle;
        if (this.tickerText != null) {
            long creationTime = bundle.getLong("push_creation_date", 0);
            if (!bundle.getBoolean("hidden", false)) {
                if (isOpenProfileForPresent()) {
                    PushDeliveryLog.discussion(creationTime);
                } else if (isMakePresentToId()) {
                    PushDeliveryLog.presents(creationTime);
                } else if (isOpenNotificationsPage()) {
                    PushDeliveryLog.general(creationTime);
                } else if (isOpenUri()) {
                    PushDeliveryLog.openUri(creationTime);
                } else if (!TextUtils.isEmpty(this.tickerText)) {
                    PushDeliveryLog.dflt(creationTime);
                }
            }
        }
    }

    private boolean isHighPriority() {
        return isReceivedPresent();
    }

    private boolean isOpenProfileForPresent() {
        return this.bundle.getString("dsc_id") != null;
    }

    private boolean isMakePresentToId() {
        return this.bundle.getString("present_to_id") != null;
    }

    private boolean isOpenNotificationsPage() {
        return this.bundle.getString("open_notifications_page") != null;
    }

    private boolean isReceivedPresent() {
        return this.bundle.getString("present_notification_id") != null;
    }

    private boolean isOpenUri() {
        return this.bundle.getString("uri") != null;
    }

    @NonNull
    private NotificationSignal createOpenProfileForPresentSignal(@Nullable String userId, @Nullable String holidayId) {
        Intent notificationIntent;
        if (TextUtils.isEmpty(holidayId)) {
            notificationIntent = NavigationHelper.createIntentForShowUser(this.context, userId);
        } else {
            notificationIntent = NavigationHelper.createIntentForUrl(this.context, StreamItemAdapter.buildMakePresentRequest(userId, null, holidayId), PresentsActivity.class);
        }
        AppLaunchLog.fillPushPresent(notificationIntent);
        return new NotificationSignal(this.context, this.notificationType).setIntent(notificationIntent).setSmallIcon(2130838507).setTitle(LocalizationManager.getString(this.context, 2131166282)).setTickerText(this.tickerText).setContentText(this.tickerText).setNotificationTag(userId).setNotificationId(5);
    }

    @NonNull
    private NotificationSignal createOpenNotificationsSignal() {
        Intent notificationIntent = NavigationHelper.createIntentForNotificationPage(this.context);
        AppLaunchLog.fillPushGeneral(notificationIntent);
        return new NotificationSignal(this.context, this.notificationType).setIntent(notificationIntent).setSmallIcon(2130838498).setTitle(LocalizationManager.getString(this.context, 2131166282)).setTickerText(this.tickerText).setContentText(this.tickerText).setNotificationId(6);
    }

    @NonNull
    private NotificationSignal createReceivePresentSignal(@NonNull String presentNotificationId) {
        Intent notificationIntent = PreloadPresentReceivedActivity.createIntent(this.context, presentNotificationId);
        AppLaunchLog.fillPushGeneral(notificationIntent);
        return new NotificationSignal(this.context, this.notificationType).setIntent(notificationIntent).setSmallIcon(2130838498).setTitle(LocalizationManager.getString(this.context, 2131166282)).setTickerText(this.tickerText).setContentText(this.tickerText).setNotificationId(6);
    }

    @NonNull
    private NotificationSignal createDiscussionSignal() {
        boolean isError;
        if (this.bundle.getBoolean("general_error", false) || this.bundle.getBoolean("server_error", false)) {
            isError = true;
        } else {
            isError = false;
        }
        String str = this.bundle.getString("dsc_id");
        String[] param = str.split(":");
        Intent notificationIntent = NavigationHelper.createIntentForShowDiscussion(this.context, new Discussion(param[0], param[1]), TextUtils.isEmpty(this.bundle.getString("mediatopic_id")) ? Page.MESSAGES : Page.INFO);
        AppLaunchLog.fillPushDiscussion(notificationIntent);
        return new NotificationSignal(this.context, this.notificationType).setIntent(notificationIntent).setSmallIcon(isError ? 2130838516 : 2130838500).setTitle(this.context.getString(isError ? 2131165603 : 2131166282)).setTickerText(this.tickerText).setContentText(this.tickerText).setNotificationTag(str).setNotificationId(isError ? 3 : 2);
    }

    @NonNull
    private NotificationSignal createDefaultNotificationSignal(int notificationId) {
        Intent notificationIntent = new Intent(this.context, OdklActivity.class);
        notificationIntent.setAction("home");
        AppLaunchLog.fillPushDefaultNotification(notificationIntent);
        return new NotificationSignal(this.context, this.notificationType).setIntent(notificationIntent).setSmallIcon(2130838498).setTitle(LocalizationManager.getString(this.context, 2131165393)).setTickerText(this.tickerText).setContentText(this.tickerText).setNotificationId(notificationId);
    }

    private static NotificationSignal createOpenUriNotificationSignal(Context context, String tickerText, String uri, NotificationType notificationType) {
        Intent notificationIntent = new Intent(context, LinksActivity.class);
        notificationIntent.setData(Uri.parse(uri));
        AppLaunchLog.fillPushOpenUri(notificationIntent);
        return new NotificationSignal(context, notificationType).setIntent(notificationIntent).setSmallIcon(2130838498).setTitle(LocalizationManager.getString(context, 2131165393)).setTickerText(tickerText).setContentText(tickerText).setNotificationId(7);
    }
}

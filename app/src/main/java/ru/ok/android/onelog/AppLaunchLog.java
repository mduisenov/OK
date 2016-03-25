package ru.ok.android.onelog;

import android.content.Intent;
import android.os.SystemClock;
import ru.ok.onelog.app.launch.AppLaunchCommonFactory;
import ru.ok.onelog.app.launch.AppLaunchCommonSource;
import ru.ok.onelog.app.launch.AppLaunchLocalNotificationFactory;
import ru.ok.onelog.app.launch.AppLaunchLocalNotificationSubSource;
import ru.ok.onelog.app.launch.AppLaunchPushNotificationFactory;
import ru.ok.onelog.app.launch.AppLaunchPushNotificationSubSource;
import ru.ok.onelog.app.launch.AppLaunchShareFactory;
import ru.ok.onelog.app.launch.AppLaunchShareSubSource;
import ru.ok.onelog.app.launch.AppLaunchWidgetFactory;
import ru.ok.onelog.app.launch.AppLaunchWidgetSubSource;

public class AppLaunchLog {
    public static void fillPushConversation(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.conversation);
    }

    public static Intent fillPushConversationQuickReply(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.conversation_quick_reply);
        return intent;
    }

    public static void fillPushPresent(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.presents);
    }

    public static void fillPushDiscussion(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.discussion);
    }

    public static void fillPushOpenUri(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.open_uri);
    }

    public static void fillPushGeneral(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.general);
    }

    public static void fillPushDefaultNotification(Intent intent) {
        fillPushNotification(intent, AppLaunchPushNotificationSubSource.dflt);
    }

    public static void shortLinkInternal() {
        common(AppLaunchCommonSource.shortlink_odkl);
    }

    public static void contacts() {
        common(AppLaunchCommonSource.contacts);
    }

    public static void launchMessagingShortcut() {
        common(AppLaunchCommonSource.shortcut_messaging);
    }

    public static void localMusicPlayer() {
        localNotification(AppLaunchLocalNotificationSubSource.music_player);
    }

    public static void localVideoUploadComplete() {
        localNotification(AppLaunchLocalNotificationSubSource.video_upload_complete);
    }

    public static void fillLocalDbFailure(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.failure_db);
    }

    public static void fillLocalMediaTopicInProgress(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.media_topic_upload_in_progress);
    }

    public static void fillLocalImageUpload(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.image_upload);
    }

    public static void fillLocalVideoUploadCancel(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.video_upload_cancel);
    }

    public static void fillLocalVideoCall(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.video_call);
    }

    public static void fillLocalVideoUploadInProgress(Intent intent) {
        fillLocalNotification(intent, AppLaunchLocalNotificationSubSource.video_upload_in_progress);
    }

    public static void shareLink() {
        share(AppLaunchShareSubSource.link);
    }

    public static void shareVideo() {
        share(AppLaunchShareSubSource.video);
    }

    public static void sharePhoto() {
        share(AppLaunchShareSubSource.photo);
    }

    public static void fillWidgetMain(Intent intent) {
        fillWidget(intent, AppLaunchWidgetSubSource.main);
    }

    public static void fillWidgetMusic(Intent intent) {
        fillWidget(intent, AppLaunchWidgetSubSource.music);
    }

    public static void fillWidgetLogin(Intent intent) {
        fillWidget(intent, AppLaunchWidgetSubSource.login);
    }

    static void localNotification(AppLaunchLocalNotificationSubSource subSource) {
        AppLaunchMonitor.getInstance().reportLaunchConsumed();
        OneLog.log(AppLaunchLocalNotificationFactory.get(subSource));
    }

    static void common(AppLaunchCommonSource source) {
        AppLaunchMonitor.getInstance().reportLaunchConsumed();
        OneLog.log(AppLaunchCommonFactory.get(source));
    }

    static void share(AppLaunchShareSubSource subSource) {
        AppLaunchMonitor.getInstance().reportLaunchConsumed();
        OneLog.log(AppLaunchShareFactory.get(subSource));
    }

    static void fillPushNotification(Intent intent, AppLaunchPushNotificationSubSource subSource) {
        intent.putExtra("extra_push_delivery_time", SystemClock.elapsedRealtime());
        intent.putExtra("extra_push_delivery_type", subSource);
        AppLaunchLogHelper.fillIntent(intent, AppLaunchPushNotificationFactory.get(subSource));
    }

    static void fillLocalNotification(Intent intent, AppLaunchLocalNotificationSubSource subSource) {
        AppLaunchLogHelper.fillIntent(intent, AppLaunchLocalNotificationFactory.get(subSource));
    }

    static void fillWidget(Intent intent, AppLaunchWidgetSubSource subSource) {
        AppLaunchLogHelper.fillIntent(intent, AppLaunchWidgetFactory.get(subSource));
    }
}

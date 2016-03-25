package ru.ok.android.services.processors.photo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.ui.image.ImageUploadStatusActivity;
import ru.ok.android.utils.localization.LocalizationManager;

public final class ImageUploadNotificationProcessor {
    protected int errorsCount;

    public ImageUploadNotificationProcessor() {
        this.errorsCount = 0;
    }

    @Subscribe(on = 2131623944, to = 2131624008)
    public void onClearErrorsRequest(BusEvent event) {
        this.errorsCount = 0;
        getNotificationManager().cancel("ImageUploadNotificator", 987657);
    }

    @Subscribe(on = 2131623944, to = 2131624007)
    public void onClearAllRequest(BusEvent event) {
        getNotificationManager().cancel("ImageUploadNotificator", 987657);
        getNotificationManager().cancel("ImageUploadNotificator", 987656);
        getNotificationManager().cancel("ImageUploadNotificator", 987655);
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaderEvent(BusEvent event) {
        switch (event.resultCode) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                onUploadStatusChange(event.bundleOutput);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                onUploaderStatusChange(event.bundleOutput);
            default:
        }
    }

    private void onUploadStatusChange(Bundle bundle) {
        ImageForUpload changed = (ImageForUpload) bundle.getParcelable("img");
        if (changed.getCurrentStatus() == 1) {
            notifyTotalProgress(bundle.getInt("prcsd"), bundle.getInt("total"));
        } else if (changed.getCurrentStatus() == 8) {
            ImageUploadException error = changed.getError();
            int errorCode = error == null ? 0 : error.getErrorCode();
            if (errorCode != 11 && errorCode != 14) {
                this.errorsCount++;
                notifyOnError();
            }
        }
    }

    private void onUploaderStatusChange(Bundle bundle) {
        int status = bundle.getInt("upldrsts");
        switch (status) {
            case RECEIVED_VALUE:
                notifyUploadFinished(bundle.getInt("prcsd"), bundle.getInt("errs"));
            case Message.TEXT_FIELD_NUMBER /*1*/:
                notifyTotalProgress(bundle.getInt("prcsd"), bundle.getInt("total"));
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
                notifyOnPause(status, bundle.getInt("prcsd"), bundle.getInt("total"));
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                getNotificationManager().cancel("ImageUploadNotificator", 987657);
                getNotificationManager().cancel("ImageUploadNotificator", 987656);
                getNotificationManager().cancel("ImageUploadNotificator", 987655);
            default:
        }
    }

    protected final void notifyOnPause(int pauseStatus, int processed, int total) {
        int icon;
        String message;
        Context context = getContext();
        getNotificationManager().cancel("ImageUploadNotificator", 987655);
        long when = System.currentTimeMillis();
        String tickerText = LocalizationManager.getString(context, 2131165994);
        boolean vibrate = false;
        if (pauseStatus == 3) {
            icon = 2130838510;
            message = LocalizationManager.getString(context, 2131165995, Integer.valueOf(processed), Integer.valueOf(total));
        } else if (pauseStatus == 4) {
            icon = 2130838516;
            message = LocalizationManager.getString(context, 2131165812);
            vibrate = true;
        } else if (pauseStatus == 5) {
            icon = 2130838516;
            message = LocalizationManager.getString(context, 2131165816);
            vibrate = true;
        } else {
            return;
        }
        Builder builder = new Builder(context);
        builder.setSmallIcon(icon);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentText(message);
        builder.setContentTitle(tickerText);
        builder.setContentIntent(getPendingIntent());
        if (vibrate) {
            builder.setDefaults(2);
        }
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        getNotificationManager().notify("ImageUploadNotificator", 987655, builder.build());
    }

    protected final void notifyTotalProgress(int processed, int total) {
        Context context = getContext();
        getNotificationManager().cancel("ImageUploadNotificator", 987655);
        String tickerText = LocalizationManager.getString(context, 2131165995, Integer.valueOf(processed), Integer.valueOf(total));
        long when = System.currentTimeMillis();
        Builder builder = new Builder(context);
        builder.setSmallIcon(2130838515);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentTitle(LocalizationManager.getString(context, 2131166469));
        builder.setContentText(tickerText);
        builder.setContentIntent(getPendingIntent());
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        getNotificationManager().notify("ImageUploadNotificator", 987655, builder.build());
    }

    protected final void notifyUploadFinished(int processed, int errors) {
        Context context = getContext();
        getNotificationManager().cancel("ImageUploadNotificator", 987655);
        if (processed - errors != 0) {
            String tickerText = LocalizationManager.getString(context, 2131165993);
            long when = System.currentTimeMillis();
            String message = LocalizationManager.getString(context, 2131165995, Integer.valueOf(succeeded), Integer.valueOf(processed));
            Builder builder = new Builder(context);
            builder.setSmallIcon(2130838517);
            builder.setTicker(tickerText);
            builder.setWhen(when);
            builder.setContentTitle(tickerText);
            builder.setContentText(message);
            builder.setContentIntent(getPendingIntent());
            builder.setDefaults(2);
            builder.setAutoCancel(true);
            builder.setOngoing(false);
            getNotificationManager().notify("ImageUploadNotificator", 987656, builder.build());
        }
    }

    protected final void notifyOnError() {
        String tickerText = LocalizationManager.getString(getContext(), 2131165992);
        long when = System.currentTimeMillis();
        Builder builder = new Builder(getContext());
        builder.setSmallIcon(2130838516);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentTitle(tickerText);
        builder.setContentIntent(getPendingIntent());
        builder.setNumber(this.errorsCount);
        builder.setAutoCancel(true);
        getNotificationManager().notify("ImageUploadNotificator", 987657, builder.build());
    }

    private final PendingIntent getPendingIntent() {
        Intent notifyIntent = new Intent("android.intent.action.MAIN");
        notifyIntent.setClass(getContext(), ImageUploadStatusActivity.class);
        AppLaunchLog.fillLocalImageUpload(notifyIntent);
        return PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), notifyIntent, 134217728);
    }

    private NotificationManager getNotificationManager() {
        Context context = getContext();
        getContext();
        return (NotificationManager) context.getSystemService("notification");
    }

    private Context getContext() {
        return OdnoklassnikiApplication.getContext();
    }
}

package ru.ok.android.services.persistent;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import com.google.android.gms.ads.AdRequest;
import ru.ok.android.services.persistent.provider.PersistentTasksContract.PersistentTasks;
import ru.ok.android.utils.localization.LocalizationManager;

public final class PersistentTaskNotificationBuilder {
    private final Context context;
    private Builder currentBuilder;
    private int currentState;
    private Builder lastDisplayedBuilder;
    private int lastDisplayedState;
    private String title;

    PersistentTaskNotificationBuilder(Context context) {
        this.lastDisplayedState = 0;
        this.currentState = 0;
        this.context = context;
        this.currentBuilder = new Builder(context);
        this.lastDisplayedBuilder = new Builder(context);
        this.currentBuilder.setOnlyAlertOnce(true);
        this.lastDisplayedBuilder.setOnlyAlertOnce(true);
    }

    public void setTitle(String taskTitle) {
        this.title = taskTitle;
        this.currentBuilder.setContentTitle(taskTitle);
        this.lastDisplayedBuilder.setContentTitle(taskTitle);
        this.currentState |= 2;
    }

    public void setText(String taskText) {
        this.currentBuilder.setContentText(taskText);
        this.lastDisplayedBuilder.setContentText(taskText);
        this.currentState |= 1;
    }

    public void setSmallIcon(int iconResId) {
        this.currentBuilder.setSmallIcon(iconResId);
        this.lastDisplayedBuilder.setSmallIcon(iconResId);
        this.currentState |= 4;
    }

    public void setLargeIcon(Bitmap bitmap) {
        this.currentBuilder.setLargeIcon(bitmap);
        this.lastDisplayedBuilder.setLargeIcon(bitmap);
        this.currentState |= 8;
    }

    public void addCancelAction(Context context, LocalizationManager localizationManager, PendingIntent pendingCancel) {
        String text = localizationManager.getString(2131166337);
        this.currentBuilder.addAction(2130838572, text, pendingCancel);
        if ((this.lastDisplayedState & 32) == 0) {
            this.lastDisplayedBuilder.addAction(2130838572, text, pendingCancel);
        }
        this.currentState |= 32;
    }

    public void addPauseAction(Context context, LocalizationManager localizationManager, PersistentTask task) {
        PendingIntent pendingPause = PendingIntent.getService(context, 0, PersistentTaskService.createPauseTaskIntent(context, task), 134217728);
        String text = localizationManager.getString(2131166338);
        this.currentBuilder.addAction(2130838577, text, pendingPause);
        if ((this.lastDisplayedState & 16) == 0) {
            this.lastDisplayedBuilder.addAction(2130838577, text, pendingPause);
        }
        this.currentState |= 16;
    }

    public void addResumeAction(Context context, LocalizationManager localizationManager, PersistentTask task) {
        PendingIntent pendingResume = PendingIntent.getService(context, 0, PersistentTaskService.createResumeTaskIntent(context, task), 134217728);
        String text = localizationManager.getString(2131166339);
        this.currentBuilder.addAction(2130838579, text, pendingResume);
        if ((this.lastDisplayedState & 64) == 0) {
            this.lastDisplayedBuilder.addAction(2130838579, text, pendingResume);
        }
        this.currentState |= 64;
    }

    public void setIndeterminateProgress() {
        this.currentBuilder.setProgress(0, 0, true);
        this.lastDisplayedBuilder.setProgress(0, 0, true);
        this.currentState |= NotificationCompat.FLAG_HIGH_PRIORITY;
    }

    public void setProgress(int progress, int maxProgress) {
        this.currentBuilder.setProgress(maxProgress, progress, false);
        this.lastDisplayedBuilder.setProgress(maxProgress, progress, false);
        this.currentState |= NotificationCompat.FLAG_HIGH_PRIORITY;
    }

    public void setContentIntent(PendingIntent pendingIntent) {
        this.currentBuilder.setContentIntent(pendingIntent);
        this.lastDisplayedBuilder.setContentIntent(pendingIntent);
        this.currentState |= NotificationCompat.FLAG_LOCAL_ONLY;
    }

    public void setContentInfo(String contentInfo) {
        this.currentBuilder.setContentInfo(contentInfo);
        this.lastDisplayedBuilder.setContentInfo(contentInfo);
        this.currentState |= AdRequest.MAX_CONTENT_URL_LENGTH;
    }

    public Notification build() {
        Builder effectiveBuilder;
        int totalUploadsInQueue = PersistentTasks.queryCurrentUserTasksCount(this.context.getContentResolver());
        if (totalUploadsInQueue < 2) {
            setContentInfo(null);
        } else if (VERSION.SDK_INT >= 11) {
            setContentInfo(Integer.toString(totalUploadsInQueue));
        } else {
            String titleWithCount = this.title + " (" + totalUploadsInQueue + ")";
            this.currentBuilder.setContentTitle(titleWithCount);
            this.lastDisplayedBuilder.setContentTitle(titleWithCount);
        }
        if ((this.currentState & NotificationCompat.FLAG_HIGH_PRIORITY) < (this.lastDisplayedState & NotificationCompat.FLAG_HIGH_PRIORITY) || (this.currentState & 112) < (this.lastDisplayedState & 112)) {
            effectiveBuilder = this.currentBuilder;
        } else {
            if ((this.currentState & 1) < (this.lastDisplayedState & 1)) {
                this.lastDisplayedBuilder.setContentText("");
            }
            if ((this.currentState & 2) < (this.lastDisplayedState & 2)) {
                this.lastDisplayedBuilder.setContentTitle("");
            }
            if ((this.currentState & 4) < (this.lastDisplayedState & 4)) {
                this.lastDisplayedBuilder.setSmallIcon(0);
            }
            if ((this.currentState & 8) < (this.lastDisplayedState & 8)) {
                this.lastDisplayedBuilder.setLargeIcon(null);
            }
            if ((this.currentState & NotificationCompat.FLAG_LOCAL_ONLY) < (this.lastDisplayedState & NotificationCompat.FLAG_LOCAL_ONLY)) {
                this.lastDisplayedBuilder.setContentIntent(null);
            }
            if ((this.currentState & AdRequest.MAX_CONTENT_URL_LENGTH) < (this.lastDisplayedState & AdRequest.MAX_CONTENT_URL_LENGTH)) {
                this.lastDisplayedBuilder.setContentInfo(null);
            }
            effectiveBuilder = this.lastDisplayedBuilder;
        }
        this.lastDisplayedBuilder = effectiveBuilder;
        this.currentBuilder = new Builder(this.context);
        this.currentBuilder.setOnlyAlertOnce(true);
        this.lastDisplayedState = this.currentState;
        this.currentState = 0;
        return effectiveBuilder.build();
    }
}

package ru.ok.android.services.app.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;
import ru.ok.android.utils.AndroidResourceUris;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.failsafe.RingtoneUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.NotificationType;
import ru.ok.android.utils.settings.Settings;

public final class NotificationSignal {
    private static Map<String, Long> lastNoiseTimes;
    private static volatile int notificationIconSize;
    private final Builder builder;
    private CharSequence chatTopicText;
    private CharSequence contentText;
    private final Context context;
    private Intent intent;
    private int notificationId;
    private String notificationTag;
    private final NotificationType notificationType;
    private PendingIntent pendingIntent;
    private CharSequence titleText;

    static {
        lastNoiseTimes = new HashMap();
    }

    public NotificationSignal(Context context, NotificationType notificationType) {
        this.context = context;
        this.notificationType = notificationType;
        this.builder = new Builder(context).setAutoCancel(true);
    }

    public NotificationSignal setIntent(Intent intent) {
        this.intent = intent;
        this.intent.addFlags(603979776);
        return this;
    }

    public NotificationSignal setSmallIcon(int iconId) {
        this.builder.setSmallIcon(iconId);
        return this;
    }

    public NotificationSignal setTitle(CharSequence title) {
        Builder builder = this.builder;
        this.titleText = title;
        builder.setContentTitle(title);
        return this;
    }

    public NotificationSignal setTickerText(CharSequence tickerText) {
        this.builder.setTicker(tickerText);
        return this;
    }

    public NotificationSignal setContentText(CharSequence content) {
        Builder builder = this.builder;
        this.contentText = content;
        builder.setContentText(content);
        return this;
    }

    public NotificationSignal setLargeIcon(Bitmap icon) {
        Bitmap resultIcon = null;
        if (icon != null) {
            if (notificationIconSize == 0) {
                Resources resources = this.context.getResources();
                int notificationIconSize = Math.min(resources.getDimensionPixelSize(17104901), resources.getDimensionPixelSize(17104902));
                if (VERSION.SDK_INT >= 21) {
                    notificationIconSize++;
                }
                notificationIconSize = notificationIconSize;
            }
            resultIcon = drawableToBitmap(new RoundedBitmapDrawable(icon, 0), notificationIconSize, notificationIconSize);
        }
        this.builder.setLargeIcon(resultIcon);
        return this;
    }

    public NotificationSignal setNotificationTag(String notificationTag) {
        this.notificationTag = notificationTag;
        return this;
    }

    public NotificationSignal setNotificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public NotificationSignal setPriority(int priority) {
        this.builder.setPriority(priority);
        return this;
    }

    public NotificationSignal setVisibility(int visibility) {
        this.builder.setVisibility(visibility);
        return this;
    }

    public NotificationSignal setCategory(String category) {
        this.builder.setCategory(category);
        return this;
    }

    public NotificationSignal setCount(int count) {
        this.builder.setNumber(count);
        return this;
    }

    public NotificationSignal setConversationTopic(String topic) {
        this.chatTopicText = topic;
        return this;
    }

    public NotificationSignal addAction(int icon, int titleResId, PendingIntent pendingIntent) {
        this.builder.addAction(icon, titleResId != 0 ? LocalizationManager.getString(this.context, titleResId) : null, pendingIntent);
        return this;
    }

    public void performNotification() {
        long t = System.currentTimeMillis();
        Long lastTime = (Long) lastNoiseTimes.get(this.notificationTag);
        boolean noisePerformed = false;
        if (t - (lastTime != null ? lastTime.longValue() : 0) > 3000) {
            if (this.notificationType.vibrate) {
                noisePerformed = true;
                this.builder.setVibrate(new long[]{0, 150});
            }
            Uri uri = getRingtonePath(this.context);
            if (uri != null) {
                noisePerformed = true;
                this.builder.setSound(uri);
            }
            if (noisePerformed) {
                lastNoiseTimes.put(this.notificationTag, Long.valueOf(t));
            }
        }
        if (this.notificationType.led) {
            this.builder.setLights(16747520, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, 1200);
        }
        this.builder.setGroupSummary(true);
        BigTextStyle style = new BigTextStyle(this.builder).setBigContentTitle(this.titleText).bigText(this.contentText);
        if (!TextUtils.isEmpty(this.chatTopicText)) {
            style.setSummaryText(this.chatTopicText);
        }
        this.builder.setStyle(style);
        this.builder.setContentIntent(getPendingIntent());
        ((NotificationManager) this.context.getSystemService("notification")).notify(this.notificationTag, this.notificationId, this.builder.build());
    }

    public PendingIntent getPendingIntent() {
        if (this.pendingIntent == null) {
            this.pendingIntent = PendingIntent.getActivity(this.context.getApplicationContext(), System.identityHashCode(this.notificationTag) + this.notificationId, this.intent, 134217728);
        }
        return this.pendingIntent;
    }

    public static void hideNotification(Context context, String notificationTag, int notificationId) {
        ((NotificationManager) context.getSystemService("notification")).cancel(notificationTag, notificationId);
    }

    private static void performVibro(Context context) {
        int ringerMode = 0;
        AudioManager manager = (AudioManager) context.getSystemService("audio");
        if (manager != null) {
            ringerMode = manager.getRingerMode();
        }
        if (ringerMode == 2 || ringerMode == 1) {
            Vibrator v = (Vibrator) context.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(150);
            }
        }
    }

    public static Uri getRingtonePath(Context context) {
        String uri = Settings.getStrValueInvariable(context, context.getString(2131166288), null);
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        return Uri.parse(uri);
    }

    public static void notifyWithTypeNoNotification(Context context, NotificationType notificationType) {
        if (!notificationType.disableNotifications) {
            playRingtone(context, getRingtonePath(context));
            if (notificationType.vibrate) {
                performVibro(context);
            }
        }
    }

    public static void playSentSound(Context context) {
        NotificationType notificationType = NotifyReceiver.getNotificationsSettings(context);
        if (!notificationType.disableNotifications && notificationType.sentMessageSound) {
            Long lastNoiseTime = (Long) lastNoiseTimes.get("message-sent");
            if (lastNoiseTime == null || SystemClock.uptimeMillis() - lastNoiseTime.longValue() > 1500) {
                lastNoiseTimes.put("message-sent", Long.valueOf(SystemClock.uptimeMillis()));
                playRingtone(context, AndroidResourceUris.getAndroidResourceUri(context, 2131099656));
            }
        }
    }

    private static void playRingtone(Context context, Uri ringtonePath) {
        if (ringtonePath != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), ringtonePath);
            if (ringtone != null) {
                ringtone.setStreamType(5);
                RingtoneUtils.play(ringtone);
            }
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Logger.m184w("out of memory on creating notification icon");
            return bitmap;
        }
    }
}

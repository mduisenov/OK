package ru.ok.android.utils.settings;

public final class NotificationType {
    public final boolean disableNotifications;
    public final boolean led;
    public final boolean sentMessageSound;
    public final boolean simpleNotifications;
    public final boolean sound;
    public final boolean vibrate;

    public NotificationType(boolean disableNotifications, boolean sound, boolean sentMessageSound, boolean vibrate, boolean led, boolean simpleNotifications) {
        this.disableNotifications = disableNotifications;
        this.sound = sound;
        this.sentMessageSound = sentMessageSound;
        this.vibrate = vibrate;
        this.led = led;
        this.simpleNotifications = simpleNotifications;
    }
}

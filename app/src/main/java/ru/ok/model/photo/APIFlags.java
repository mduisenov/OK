package ru.ok.model.photo;

import android.support.v4.app.NotificationCompat;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import ru.ok.android.utils.Logger;

public enum APIFlags {
    CAN_LIKE("l", 1),
    CAN_COMMENT(EntityCapsManager.ELEMENT, 2),
    CAN_DELETE(Logger.METHOD_D, 4),
    CAN_MARK_AS_SPAM("s", 8),
    CAN_COMMENT_FRIEND("foc", 16),
    CAN_MODIFY("m", 32),
    CAN_MARK("ma", 64),
    CAN_TAG("ta", NotificationCompat.FLAG_HIGH_PRIORITY);
    
    final int mask;
    final String value;

    private APIFlags(String value, int mask) {
        this.value = value;
        this.mask = mask;
    }
}

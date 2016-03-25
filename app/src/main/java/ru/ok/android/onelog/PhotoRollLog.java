package ru.ok.android.onelog;

import android.support.annotation.NonNull;
import ru.ok.onelog.app.photo.PhotoRollEventFactory;
import ru.ok.onelog.app.photo.PhotoRollEventType;

public class PhotoRollLog {
    public static void logShow() {
        log(PhotoRollEventType.show);
    }

    public static void logClickOnPhoto() {
        log(PhotoRollEventType.click_on_photo);
    }

    public static void logUploadAttempt() {
        log(PhotoRollEventType.upload_attempt);
    }

    public static void logCloseNoUploadAttempt() {
        log(PhotoRollEventType.close_no_upload_attempt);
    }

    public static void logCloseAfterUploadAttempt() {
        log(PhotoRollEventType.close_after_upload_attempt);
    }

    private static void log(@NonNull PhotoRollEventType eventType) {
        OneLog.log(PhotoRollEventFactory.get(eventType));
    }
}

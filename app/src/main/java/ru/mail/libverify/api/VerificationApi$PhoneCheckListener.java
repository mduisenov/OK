package ru.mail.libverify.api;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import ru.mail.libverify.api.VerificationApi.PhoneCheckResult;

public interface VerificationApi$PhoneCheckListener {
    @WorkerThread
    void onCompleted(@NonNull String str, @NonNull PhoneCheckResult phoneCheckResult);
}

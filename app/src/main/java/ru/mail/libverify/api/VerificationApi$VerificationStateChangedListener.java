package ru.mail.libverify.api;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import ru.mail.libverify.api.VerificationApi.VerificationStateDescriptor;

public interface VerificationApi$VerificationStateChangedListener {
    @WorkerThread
    void onStateChanged(@NonNull String str, VerificationStateDescriptor verificationStateDescriptor);
}

package ru.ok.android.utils.controls.nativeregistration;

import android.support.annotation.NonNull;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public interface OnRegistrationListener {
    void onUserCreationError(String str, @NonNull ErrorType errorType);

    void onUserCreationSuccesfull(String str, boolean z, boolean z2);
}

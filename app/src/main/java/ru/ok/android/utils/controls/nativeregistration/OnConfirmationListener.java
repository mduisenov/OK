package ru.ok.android.utils.controls.nativeregistration;

import android.support.annotation.NonNull;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public interface OnConfirmationListener {
    void onUserConfirmationError(String str, @NonNull ErrorType errorType);

    void onUserConfirmationSuccessfull(String str);
}

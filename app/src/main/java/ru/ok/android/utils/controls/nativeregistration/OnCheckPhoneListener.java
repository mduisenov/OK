package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public interface OnCheckPhoneListener {
    void onCheckPhoneError(String str, @NonNull ErrorType errorType);

    void onCheckPhoneSuccessfull(Bundle bundle);
}

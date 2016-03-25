package ru.ok.android.utils.controls.nativeregistration;

import android.support.annotation.NonNull;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.model.UserInfo;

public interface UpdateInfoListener {
    void onUserInfoUpdateError(String str, @NonNull ErrorType errorType);

    void onUserInfoUpdateSuccessful(UserInfo userInfo);
}

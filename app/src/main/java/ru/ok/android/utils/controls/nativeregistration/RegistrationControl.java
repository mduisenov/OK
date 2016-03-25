package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.RegisterUserProcessor;
import ru.ok.android.utils.Utils;

public class RegistrationControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.RegistrationControl.1 */
    static /* synthetic */ class C14631 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode;

        static {
            $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode = new int[ResultCode.values().length];
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[ResultCode.FAILURE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    class CommandCallBack implements CommandListener {
        OnRegistrationListener listener;

        CommandCallBack(OnRegistrationListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (RegisterUserProcessor.isIt(commandName)) {
                switch (C14631.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        RegistrationControl.onRegistrationSuccess(data, this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        RegistrationControl.onRegistrationError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToRegisterUser(String login, OnRegistrationListener callback) {
        Utils.getServiceHelper().tryToRegisterUser(login, createCommandCallback(callback));
    }

    private static void onRegistrationSuccess(Bundle data, OnRegistrationListener listener) {
        if (listener != null) {
            listener.onUserCreationSuccesfull(data.getString(RegisterUserProcessor.KEY_UID), data.getBoolean(RegisterUserProcessor.KEY_PHONE_ALREADY_LOGIN), data.getBoolean(RegisterUserProcessor.KEY_ACCOUNT_RECOVERY));
        }
    }

    private static void onRegistrationError(Bundle data, OnRegistrationListener listener) {
        if (listener != null) {
            listener.onUserCreationError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnRegistrationListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

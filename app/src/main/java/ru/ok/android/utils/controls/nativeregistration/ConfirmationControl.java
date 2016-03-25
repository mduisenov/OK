package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.ConfirmUserProcessor;
import ru.ok.android.utils.Utils;

public class ConfirmationControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.ConfirmationControl.1 */
    static /* synthetic */ class C14581 {
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
        OnConfirmationListener listener;

        CommandCallBack(OnConfirmationListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (ConfirmUserProcessor.isIt(commandName)) {
                switch (C14581.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        ConfirmationControl.onConfirmationSuccess(data, this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        ConfirmationControl.onConfirmationError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToConfirmUser(String uid, String login, String pin, String newPassword, OnConfirmationListener callback) {
        Utils.getServiceHelper().tryToConfirmUser(uid, login, pin, newPassword, createCommandCallback(callback));
    }

    private static void onConfirmationSuccess(Bundle data, OnConfirmationListener listener) {
        if (listener != null) {
            listener.onUserConfirmationSuccessfull(data.getString(ConfirmUserProcessor.KEY_TOKEN));
        }
    }

    private static void onConfirmationError(Bundle data, OnConfirmationListener listener) {
        if (listener != null) {
            listener.onUserConfirmationError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnConfirmationListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.RecoverUserBySmsProcessor;
import ru.ok.android.utils.Utils;

public class RecoverUserBySmsControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.RecoverUserBySmsControl.1 */
    static /* synthetic */ class C14611 {
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
        OnRecoverUserBySmsListener listener;

        CommandCallBack(OnRecoverUserBySmsListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (RecoverUserBySmsProcessor.isIt(commandName)) {
                switch (C14611.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        RecoverUserBySmsControl.onRecoverSuccess(this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        RecoverUserBySmsControl.onRecoverError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToRecoverUserBySms(String uid, String pin, String password, OnRecoverUserBySmsListener callback) {
        Utils.getServiceHelper().tryToRecoverUserBySms(uid, pin, password, createCommandCallback(callback));
    }

    private static void onRecoverSuccess(OnRecoverUserBySmsListener listener) {
        if (listener != null) {
            listener.onRecoverPasswordSuccessful();
        }
    }

    private static void onRecoverError(Bundle data, OnRecoverUserBySmsListener listener) {
        if (listener != null) {
            listener.onRecoverPasswordError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnRecoverUserBySmsListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

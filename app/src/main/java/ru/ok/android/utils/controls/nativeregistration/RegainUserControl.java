package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.RegainUserProcessor;
import ru.ok.android.utils.Utils;

public class RegainUserControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.RegainUserControl.1 */
    static /* synthetic */ class C14621 {
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
        OnRegainUserListener listener;

        CommandCallBack(OnRegainUserListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (RegainUserProcessor.isIt(commandName)) {
                switch (C14621.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        RegainUserControl.onRegainUserSuccess(data, this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        RegainUserControl.onRegainUserError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToRegainUser(String uid, String regainUid, String pin, String newPassword, OnRegainUserListener callback) {
        Utils.getServiceHelper().tryToRegainUser(uid, regainUid, pin, newPassword, createCommandCallback(callback));
    }

    private static void onRegainUserSuccess(Bundle data, OnRegainUserListener listener) {
        if (listener != null) {
            listener.onRegainUserSuccessfull(data.getString(RegainUserProcessor.KEY_TOKEN));
        }
    }

    private static void onRegainUserError(Bundle data, OnRegainUserListener listener) {
        if (listener != null) {
            listener.onRegainUserError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnRegainUserListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

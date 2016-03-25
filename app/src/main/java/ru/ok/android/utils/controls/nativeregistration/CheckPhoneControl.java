package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.CheckPhoneProcessor;
import ru.ok.android.utils.Utils;

public class CheckPhoneControl {
    private CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.CheckPhoneControl.1 */
    static /* synthetic */ class C14571 {
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
        OnCheckPhoneListener listener;

        CommandCallBack(OnCheckPhoneListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (CheckPhoneProcessor.isIt(commandName)) {
                switch (C14571.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        CheckPhoneControl.onGettingSuccess(this.listener, data);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        CheckPhoneControl.onGettingError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToCheckPhone(String uid, String phone, String pin, OnCheckPhoneListener callback) {
        Utils.getServiceHelper().tryToGetUsers(uid, phone, pin, createCommandCallback(callback));
    }

    private static void onGettingSuccess(OnCheckPhoneListener listener, Bundle bundle) {
        if (listener != null) {
            listener.onCheckPhoneSuccessfull(bundle);
        }
    }

    private static void onGettingError(Bundle data, OnCheckPhoneListener listener) {
        if (listener != null) {
            listener.onCheckPhoneError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnCheckPhoneListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

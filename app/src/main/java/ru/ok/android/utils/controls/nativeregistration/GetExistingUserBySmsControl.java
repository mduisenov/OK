package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.GetExistingUserBySmsProcessor;
import ru.ok.android.utils.Utils;

public class GetExistingUserBySmsControl {
    private CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.GetExistingUserBySmsControl.1 */
    static /* synthetic */ class C14591 {
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
        OnGetExistingUserBySmsListener listener;

        CommandCallBack(OnGetExistingUserBySmsListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (GetExistingUserBySmsProcessor.isIt(commandName)) {
                switch (C14591.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        GetExistingUserBySmsControl.onGettingSuccess((UserWithLogin) data.getParcelable(GetExistingUserBySmsProcessor.KEY_USER), this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        GetExistingUserBySmsControl.onGettingError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void getExistingUserBySms(String phone, String pin, OnGetExistingUserBySmsListener callback) {
        Utils.getServiceHelper().getExistingUserBySms(phone, pin, createCommandCallback(callback));
    }

    private static void onGettingSuccess(UserWithLogin userInfo, OnGetExistingUserBySmsListener listener) {
        if (listener != null) {
            listener.onGetExistingUserSuccessful(userInfo);
        }
    }

    private static void onGettingError(Bundle data, OnGetExistingUserBySmsListener listener) {
        if (listener != null) {
            listener.onGetExistingUserError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(OnGetExistingUserBySmsListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.CompleteUserInfoProcessor;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;

public class UserInfoControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.UserInfoControl.1 */
    static /* synthetic */ class C14641 {
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
        UpdateInfoListener listener;

        CommandCallBack(UpdateInfoListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (CompleteUserInfoProcessor.isIt(commandName)) {
                switch (C14641.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        UserInfoControl.onUpdateSuccess(data, this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        UserInfoControl.onUpdateError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void tryToUpdateUserInfo(String oldPassword, String newPassword, UserInfo person, UpdateInfoListener callback) {
        Utils.getServiceHelper().tryToUpdateUserInfo(oldPassword, newPassword, person, createCommandCallback(callback));
    }

    private static void onUpdateSuccess(Bundle data, UpdateInfoListener listener) {
        if (listener != null) {
            listener.onUserInfoUpdateSuccessful((UserInfo) data.getParcelable(CompleteUserInfoProcessor.KEY_PERSON_INFO));
        }
    }

    private static void onUpdateError(Bundle data, UpdateInfoListener listener) {
        if (listener != null) {
            listener.onUserInfoUpdateError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(UpdateInfoListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

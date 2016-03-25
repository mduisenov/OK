package ru.ok.android.utils.controls.nativeregistration;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.services.processors.registration.ProfileActivityProcessor;
import ru.ok.android.utils.Utils;

public class PrepareProfileActivityControl {
    CommandCallBack commandCallBack;

    /* renamed from: ru.ok.android.utils.controls.nativeregistration.PrepareProfileActivityControl.1 */
    static /* synthetic */ class C14601 {
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
        PrepareProfileActivityListener listener;

        CommandCallBack(PrepareProfileActivityListener listener) {
            this.listener = listener;
        }

        public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
            if (ProfileActivityProcessor.isIt(commandName)) {
                switch (C14601.$SwitchMap$ru$ok$android$app$helper$ServiceHelper$ResultCode[resultCode.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        PrepareProfileActivityControl.onPrepareSuccess(data.getParcelableArrayList(ProfileActivityProcessor.KEY_LOCATION_LIST), (UpdateProfileFieldsFlags) data.getParcelable(ProfileActivityProcessor.KEY_FIELDS_FLAGS), this.listener);
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        PrepareProfileActivityControl.onPrepareError(data, this.listener);
                    default:
                }
            }
        }
    }

    public void prepareProfileActivity(PrepareProfileActivityListener callback) {
        Utils.getServiceHelper().prepareProfileActivity(createCommandCallback(callback));
    }

    private static void onPrepareSuccess(ArrayList<Location> locations, UpdateProfileFieldsFlags updateProfileFieldsFlags, PrepareProfileActivityListener listener) {
        if (listener != null) {
            listener.onPrepareProfileActivitySuccess(locations, updateProfileFieldsFlags);
        }
    }

    private static void onPrepareError(Bundle data, PrepareProfileActivityListener listener) {
        if (listener != null) {
            listener.onPrepareProfileActivityError(data.getString("errorMessage"), ErrorType.from(data));
        }
    }

    private CommandCallBack createCommandCallback(PrepareProfileActivityListener listener) {
        this.commandCallBack = new CommandCallBack(listener);
        return this.commandCallBack;
    }
}

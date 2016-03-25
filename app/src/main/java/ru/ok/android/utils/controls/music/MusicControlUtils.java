package ru.ok.android.utils.controls.music;

import android.content.Context;
import android.os.Message;
import ru.ok.android.C0206R;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.services.transport.exception.ServerNotFoundException;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.java.api.exceptions.ServerReturnErrorException;

public final class MusicControlUtils {
    public static void onError(Context context, Message msg) {
        if (context != null) {
            Object e = msg.obj;
            if (msg.obj instanceof NoConnectionException) {
                TimeToast.show(context, 2131166735, 1);
            } else if (msg.obj instanceof ServerNotFoundException) {
                TimeToast.show(context, 2131166539, 1);
            } else if (msg.obj instanceof NetworkException) {
                TimeToast.show(context, 2131165984, 1);
            } else if (e instanceof ServerReturnErrorException) {
                if (((ServerReturnErrorException) e).getErrorCode() == 59) {
                    TimeToast.show(context, 2131166071, 1);
                } else if (((ServerReturnErrorException) e).getErrorCode() != C0206R.styleable.Theme_checkedTextViewStyle) {
                    TimeToast.show(context, 2131166539, 1);
                }
            }
            if (e instanceof TransportLevelException) {
                TimeToast.show(context, 2131166735, 1);
            }
        }
    }
}

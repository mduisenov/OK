package ru.ok.android.services.processors.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.C0206R;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.gcm.GcmUnregisterProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.java.api.HttpResult;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.HttpStatusException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.LogOutRequest;

public class LogoutProcessorNew extends CommandProcessor {
    public static final String COMMAND_NAME;
    public static final String KEY_ERROR;

    static {
        COMMAND_NAME = LogoutProcessorNew.class.getName();
        KEY_ERROR = COMMAND_NAME + ":error";
    }

    public LogoutProcessorNew(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return onLogout(context, outBundle);
    }

    public static boolean isIt(String command) {
        return COMMAND_NAME.equals(command);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    private int onLogout(Context context, Bundle outBundle) {
        ServiceStateHolder serviceStateHolder = 2;
        try {
            GcmUnregisterProcessor.performUnregistering(context);
            logout();
        } catch (TransportLevelException en) {
            outBundle.putSerializable(KEY_ERROR, en);
            return r3;
        } catch (ServerReturnErrorException e) {
            if (e.getErrorCode() != C0206R.styleable.Theme_checkedTextViewStyle) {
                outBundle.putSerializable(KEY_ERROR, e);
                this._transportProvider.getStateHolder().clear();
                return 2;
            }
        } catch (BaseApiException e2) {
            outBundle.putSerializable(KEY_ERROR, e2);
            return serviceStateHolder;
        } finally {
            serviceStateHolder = this._transportProvider.getStateHolder();
            serviceStateHolder.clear();
        }
        return 1;
    }

    private void logout() throws BaseApiException {
        throwIfHttpStatusNotOk(this._transportProvider.execJsonHttpMethod(new LogOutRequest()));
    }

    private void throwIfHttpStatusNotOk(HttpResult result) throws HttpStatusException {
        if (result.getHttpStatus() != 200 || result.getHttpResponse() == null) {
            throw new HttpStatusException(result.getHttpStatus(), "Unexpected HTTP result");
        }
    }
}

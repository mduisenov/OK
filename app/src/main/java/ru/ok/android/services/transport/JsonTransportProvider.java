package ru.ok.android.services.transport;

import android.content.Context;
import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.C0206R;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.protocol.HttpCoreContext;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.exceptions.VerificationException;
import ru.ok.java.api.utils.JsonUtil;

public class JsonTransportProvider {
    private static volatile JsonTransportProvider instance;
    private final Context context;
    private final HttpTransportProvider httpProvider;

    private JsonTransportProvider(Context context) {
        this.context = context;
        this.httpProvider = HttpTransportProvider.getInstance();
    }

    public static JsonTransportProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (JsonTransportProvider.class) {
                if (instance == null) {
                    instance = new JsonTransportProvider(context);
                }
            }
        }
        return instance;
    }

    public JsonHttpResult execJsonHttpMethod(HttpUriRequest httpRequest) throws BaseApiException {
        return execJsonHttpMethod(httpRequest, null);
    }

    public JsonHttpResult execJsonHttpMethod(HttpUriRequest httpRequest, @Nullable HttpCoreContext logContext) throws BaseApiException {
        JsonHttpResult result = new JsonHttpResult(this.httpProvider.execute(this.context, httpRequest, logContext));
        try {
            throwIfContainsError(result.getResultAsObject());
        } catch (JSONException e) {
            Logger.m184w("Result is not JSON object, error checking is skipped");
        }
        return result;
    }

    private static void throwIfContainsError(JSONObject jsonObj) throws ServerReturnErrorException, JSONException {
        String errorMessage;
        int errorCode;
        if (JsonUtil.isErrorObj(jsonObj)) {
            errorMessage = jsonObj.getString("error_msg");
            errorCode = jsonObj.getInt("error_code");
            Logger.m177e("Error detected in JSON object: %d : %s", Integer.valueOf(errorCode), errorMessage);
            if (errorCode == 403 || errorCode == 1200) {
                throw new VerificationException(errorCode, errorMessage, jsonObj.optString("ver_redirect_url"));
            }
            throw new ServerReturnErrorException(errorCode, errorMessage);
        } else if (jsonObj.has("error")) {
            errorMessage = jsonObj.getString("error");
            if (errorMessage.equals("error.notloggedin")) {
                errorCode = C0206R.styleable.Theme_checkedTextViewStyle;
            } else if (errorMessage.equals("error.like.track.unplayable")) {
                errorCode = C0206R.styleable.Theme_editTextStyle;
            } else if (errorMessage.equals("Use of HTTPS is NOT ALLOWED for method")) {
                errorCode = 58;
            } else if (errorMessage.equals("error.playlists.size")) {
                errorCode = 59;
            } else {
                errorCode = C0206R.styleable.Theme_ratingBarStyle;
            }
            throw new ServerReturnErrorException(errorCode, errorMessage);
        }
    }
}

package ru.ok.android.services.transport;

import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.ServiceStateHolder.StateHolderChangeListener;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.BaseRequest;

public abstract class JsonSessionTransportProvider implements StateHolderChangeListener {
    private static volatile JsonSessionTransportProvider instance;

    public abstract JsonHttpResult execJsonHttpMethod(BaseRequest baseRequest, String str) throws BaseApiException;

    public abstract ServiceStateHolder getStateHolder();

    public abstract String getWebBaseUrl();

    public final JsonHttpResult execJsonHttpMethod(BaseRequest request) throws BaseApiException {
        return execJsonHttpMethod(request, null);
    }

    public static JsonSessionTransportProvider getInstance() {
        if (instance == null) {
            synchronized (JsonSessionTransportProvider.class) {
                if (instance == null) {
                    instance = createInstance();
                }
            }
        }
        return instance;
    }

    private static JsonSessionTransportProvider createInstance() {
        return new JsonSessionTransportProviderImpl(OdnoklassnikiApplication.getContext());
    }
}

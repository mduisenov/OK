package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;

public abstract class BaseRequest {
    protected final String logContext;

    public abstract String getMethodName();

    protected abstract void serializeInternal(RequestSerializer<?> requestSerializer) throws SerializeException;

    protected BaseRequest() {
        this(null);
    }

    protected BaseRequest(String logContext) {
        this.logContext = logContext;
    }

    public void serialize(RequestSerializer<?> serializer) throws SerializeException {
        serializeInternal(serializer);
        if (this.logContext != null) {
            serializer.add(SerializeParamName.LOG_CONTEXT, this.logContext);
        }
        if (!isMakeUserOnline()) {
            serializer.add(SerializeParamName.SET_ONLINE, false);
        }
    }

    public boolean isMakeUserOnline() {
        return true;
    }
}

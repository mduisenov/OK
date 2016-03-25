package ru.ok.java.api.request.batch;

import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasFormat = true, httpType = HttpMethodType.POST)
public class BatchRequest extends BaseRequest {
    private final BatchRequests requests;
    private final boolean setOnline;

    public BatchRequest(BatchRequests requests) {
        this(requests, true);
    }

    public BatchRequest(BatchRequests requests, boolean setOnline) {
        this.requests = requests;
        this.setOnline = setOnline;
    }

    public String getMethodName() {
        return "batch.execute";
    }

    public BatchRequests getBatchRequests() {
        return this.requests;
    }

    public boolean isMakeUserOnline() {
        return this.setOnline;
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.METHODS, this.requests.serialize());
    }

    public String toString() {
        return "BatchRequest{requests=" + this.requests + ",setOnline=" + this.setOnline + '}';
    }
}

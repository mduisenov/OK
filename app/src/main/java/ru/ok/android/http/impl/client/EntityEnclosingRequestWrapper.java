package ru.ok.android.http.impl.client;

import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpEntityEnclosingRequest;
import ru.ok.android.http.ProtocolException;

@Deprecated
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
    private boolean consumed;
    private HttpEntity entity;

    public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws ProtocolException {
        super(request);
        setEntity(request.getEntity());
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity != null ? new EntityWrapper(this, entity) : null;
        this.consumed = false;
    }

    public boolean expectContinue() {
        Header expect = getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }

    public boolean isRepeatable() {
        return this.entity == null || this.entity.isRepeatable() || !this.consumed;
    }
}

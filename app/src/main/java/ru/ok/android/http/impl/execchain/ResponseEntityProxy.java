package ru.ok.android.http.impl.execchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.conn.EofSensorInputStream;
import ru.ok.android.http.conn.EofSensorWatcher;
import ru.ok.android.http.entity.HttpEntityWrapper;

class ResponseEntityProxy extends HttpEntityWrapper implements EofSensorWatcher {
    private final ConnectionHolder connHolder;

    public static void enchance(HttpResponse response, ConnectionHolder connHolder) {
        HttpEntity entity = response.getEntity();
        if (entity != null && entity.isStreaming() && connHolder != null) {
            response.setEntity(new ResponseEntityProxy(entity, connHolder));
        }
    }

    ResponseEntityProxy(HttpEntity entity, ConnectionHolder connHolder) {
        super(entity);
        this.connHolder = connHolder;
    }

    private void cleanup() {
        if (this.connHolder != null) {
            this.connHolder.abortConnection();
        }
    }

    public void releaseConnection() throws IOException {
        if (this.connHolder != null) {
            try {
                if (this.connHolder.isReusable()) {
                    this.connHolder.releaseConnection();
                }
                cleanup();
            } catch (Throwable th) {
                cleanup();
            }
        }
    }

    public boolean isRepeatable() {
        return false;
    }

    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }

    @Deprecated
    public void consumeContent() throws IOException {
        releaseConnection();
    }

    public void writeTo(OutputStream outstream) throws IOException {
        try {
            this.wrappedEntity.writeTo(outstream);
            releaseConnection();
        } finally {
            cleanup();
        }
    }

    public boolean eofDetected(InputStream wrapped) throws IOException {
        try {
            wrapped.close();
            releaseConnection();
            return false;
        } finally {
            cleanup();
        }
    }

    public boolean streamClosed(InputStream wrapped) throws IOException {
        boolean open;
        try {
            if (this.connHolder == null || this.connHolder.isReleased()) {
                open = false;
            } else {
                open = true;
            }
            wrapped.close();
            releaseConnection();
        } catch (SocketException ex) {
            if (open) {
                throw ex;
            }
        } catch (Throwable th) {
            cleanup();
        }
        cleanup();
        return false;
    }

    public boolean streamAbort(InputStream wrapped) throws IOException {
        cleanup();
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ResponseEntityProxy{");
        sb.append(this.wrappedEntity);
        sb.append('}');
        return sb.toString();
    }
}

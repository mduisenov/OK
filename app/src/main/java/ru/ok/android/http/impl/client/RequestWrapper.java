package ru.ok.android.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.ProtocolException;
import ru.ok.android.http.ProtocolVersion;
import ru.ok.android.http.RequestLine;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.message.AbstractHttpMessage;
import ru.ok.android.http.message.BasicRequestLine;
import ru.ok.android.http.params.HttpProtocolParams;
import ru.ok.android.http.util.Args;

@Deprecated
public class RequestWrapper extends AbstractHttpMessage implements HttpUriRequest {
    private int execCount;
    private String method;
    private final HttpRequest original;
    private URI uri;
    private ProtocolVersion version;

    public RequestWrapper(HttpRequest request) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        this.original = request;
        setParams(request.getParams());
        setHeaders(request.getAllHeaders());
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest) request).getURI();
            this.method = ((HttpUriRequest) request).getMethod();
            this.version = null;
        } else {
            RequestLine requestLine = request.getRequestLine();
            try {
                this.uri = new URI(requestLine.getUri());
                this.method = requestLine.getMethod();
                this.version = request.getProtocolVersion();
            } catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid request URI: " + requestLine.getUri(), ex);
            }
        }
        this.execCount = 0;
    }

    public void resetHeaders() {
        this.headergroup.clear();
        setHeaders(this.original.getAllHeaders());
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        Args.notNull(method, "Method name");
        this.method = method;
    }

    public ProtocolVersion getProtocolVersion() {
        if (this.version == null) {
            this.version = HttpProtocolParams.getVersion(getParams());
        }
        return this.version;
    }

    public void setProtocolVersion(ProtocolVersion version) {
        this.version = version;
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public RequestLine getRequestLine() {
        ProtocolVersion ver = getProtocolVersion();
        String uritext = null;
        if (this.uri != null) {
            uritext = this.uri.toASCIIString();
        }
        if (uritext == null || uritext.isEmpty()) {
            uritext = "/";
        }
        return new BasicRequestLine(getMethod(), uritext, ver);
    }

    public void abort() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean isAborted() {
        return false;
    }

    public HttpRequest getOriginal() {
        return this.original;
    }

    public boolean isRepeatable() {
        return true;
    }

    public int getExecCount() {
        return this.execCount;
    }

    public void incrementExecCount() {
        this.execCount++;
    }
}

package ru.ok.android.http.message;

import ru.ok.android.http.Header;
import ru.ok.android.http.HeaderIterator;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.params.BasicHttpParams;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

public abstract class AbstractHttpMessage implements HttpMessage {
    protected HeaderGroup headergroup;
    @Deprecated
    protected HttpParams params;

    @Deprecated
    protected AbstractHttpMessage(HttpParams params) {
        this.headergroup = new HeaderGroup();
        this.params = params;
    }

    protected AbstractHttpMessage() {
        this(null);
    }

    public boolean containsHeader(String name) {
        return this.headergroup.containsHeader(name);
    }

    public Header[] getHeaders(String name) {
        return this.headergroup.getHeaders(name);
    }

    public Header getFirstHeader(String name) {
        return this.headergroup.getFirstHeader(name);
    }

    public Header getLastHeader(String name) {
        return this.headergroup.getLastHeader(name);
    }

    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }

    public void addHeader(Header header) {
        this.headergroup.addHeader(header);
    }

    public void addHeader(String name, String value) {
        Args.notNull(name, "Header name");
        this.headergroup.addHeader(new BasicHeader(name, value));
    }

    public void setHeader(Header header) {
        this.headergroup.updateHeader(header);
    }

    public void setHeader(String name, String value) {
        Args.notNull(name, "Header name");
        this.headergroup.updateHeader(new BasicHeader(name, value));
    }

    public void setHeaders(Header[] headers) {
        this.headergroup.setHeaders(headers);
    }

    public void removeHeader(Header header) {
        this.headergroup.removeHeader(header);
    }

    public void removeHeaders(String name) {
        if (name != null) {
            HeaderIterator i = this.headergroup.iterator();
            while (i.hasNext()) {
                if (name.equalsIgnoreCase(i.nextHeader().getName())) {
                    i.remove();
                }
            }
        }
    }

    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }

    public HeaderIterator headerIterator(String name) {
        return this.headergroup.iterator(name);
    }

    @Deprecated
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }

    @Deprecated
    public void setParams(HttpParams params) {
        this.params = (HttpParams) Args.notNull(params, "HTTP parameters");
    }
}

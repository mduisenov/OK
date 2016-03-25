package ru.ok.android.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.ProtocolException;
import ru.ok.android.http.client.CircularRedirectException;
import ru.ok.android.http.client.RedirectStrategy;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.client.methods.HttpGet;
import ru.ok.android.http.client.methods.HttpHead;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.client.methods.RequestBuilder;
import ru.ok.android.http.client.protocol.HttpClientContext;
import ru.ok.android.http.client.utils.URIBuilder;
import ru.ok.android.http.client.utils.URIUtils;
import ru.ok.android.http.commons.logging.Log;
import ru.ok.android.http.commons.logging.LogFactory;
import ru.ok.android.http.protocol.HttpContext;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;
import ru.ok.android.http.util.TextUtils;

public class DefaultRedirectStrategy implements RedirectStrategy {
    public static final DefaultRedirectStrategy INSTANCE;
    @Deprecated
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private static final String[] REDIRECT_METHODS;
    private final Log log;

    static {
        INSTANCE = new DefaultRedirectStrategy();
        REDIRECT_METHODS = new String[]{"GET", "HEAD"};
    }

    public DefaultRedirectStrategy() {
        this.log = LogFactory.getLog(getClass());
    }

    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");
        int statusCode = response.getStatusLine().getStatusCode();
        String method = request.getRequestLine().getMethod();
        Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
            case 301:
            case 307:
                return isRedirectable(method);
            case 302:
                if (!isRedirectable(method) || locationHeader == null) {
                    return false;
                }
                return true;
            case 303:
                return true;
            default:
                return false;
        }
    }

    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        String location = locationHeader.getValue();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location '" + location + "'");
        }
        RequestConfig config = clientContext.getRequestConfig();
        URI uri = createLocationURI(location);
        try {
            if (!uri.isAbsolute()) {
                if (config.isRelativeRedirectsAllowed()) {
                    HttpHost target = clientContext.getTargetHost();
                    Asserts.notNull(target, "Target host");
                    uri = URIUtils.resolve(URIUtils.rewriteURI(new URI(request.getRequestLine().getUri()), target, false), uri);
                } else {
                    throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                }
            }
            RedirectLocations redirectLocations = (RedirectLocations) clientContext.getAttribute(REDIRECT_LOCATIONS);
            if (redirectLocations == null) {
                redirectLocations = new RedirectLocations();
                context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
            }
            if (config.isCircularRedirectsAllowed() || !redirectLocations.contains(uri)) {
                redirectLocations.add(uri);
                return uri;
            }
            throw new CircularRedirectException("Circular redirect to '" + uri + "'");
        } catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
    }

    protected URI createLocationURI(String location) throws ProtocolException {
        try {
            URIBuilder b = new URIBuilder(new URI(location).normalize());
            String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.ROOT));
            }
            if (TextUtils.isEmpty(b.getPath())) {
                b.setPath("/");
            }
            return b.build();
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
    }

    protected boolean isRedirectable(String method) {
        for (String m : REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = getLocationURI(request, response, context);
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        if (response.getStatusLine().getStatusCode() == 307) {
            return RequestBuilder.copy(request).setUri(uri).build();
        }
        return new HttpGet(uri);
    }
}

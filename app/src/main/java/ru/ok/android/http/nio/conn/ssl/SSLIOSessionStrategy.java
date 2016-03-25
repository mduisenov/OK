package ru.ok.android.http.nio.conn.ssl;

import java.io.IOException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.conn.ssl.AllowAllHostnameVerifier;
import ru.ok.android.http.conn.ssl.BrowserCompatHostnameVerifier;
import ru.ok.android.http.conn.ssl.DefaultHostnameVerifier;
import ru.ok.android.http.conn.ssl.StrictHostnameVerifier;
import ru.ok.android.http.conn.ssl.X509HostnameVerifier;
import ru.ok.android.http.conn.util.PublicSuffixMatcherLoader;
import ru.ok.android.http.nio.conn.SchemeIOSessionStrategy;
import ru.ok.android.http.nio.reactor.IOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLIOSession;
import ru.ok.android.http.nio.reactor.ssl.SSLMode;
import ru.ok.android.http.ssl.SSLContexts;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.Asserts;

public class SSLIOSessionStrategy implements SchemeIOSessionStrategy {
    @Deprecated
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER;
    @Deprecated
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    @Deprecated
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER;
    private final HostnameVerifier hostnameVerifier;
    private final SSLContext sslContext;
    private final String[] supportedCipherSuites;
    private final String[] supportedProtocols;

    static {
        ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
        STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
    }

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
    }

    public static SSLIOSessionStrategy getDefaultStrategy() {
        return new SSLIOSessionStrategy(SSLContexts.createDefault(), getDefaultHostnameVerifier());
    }

    public SSLIOSessionStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        this.sslContext = (SSLContext) Args.notNull(sslContext, "SSL context");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        if (hostnameVerifier == null) {
            hostnameVerifier = getDefaultHostnameVerifier();
        }
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLIOSessionStrategy(SSLContext sslcontext, HostnameVerifier hostnameVerifier) {
        this(sslcontext, null, null, hostnameVerifier);
    }

    public SSLIOSession upgrade(HttpHost host, IOSession iosession) throws IOException {
        Asserts.check(!(iosession instanceof SSLIOSession), "I/O session is already upgraded to TLS/SSL");
        SSLIOSession ssliosession = new SSLIOSession(iosession, SSLMode.CLIENT, host, this.sslContext, new 1(this, host));
        iosession.setAttribute("http.session.ssl", ssliosession);
        ssliosession.initialize();
        return ssliosession;
    }

    protected void initializeEngine(SSLEngine engine) {
    }

    protected void verifySession(HttpHost host, IOSession iosession, SSLSession sslsession) throws SSLException {
        if (!this.hostnameVerifier.verify(host.getHostName(), sslsession)) {
            throw new SSLPeerUnverifiedException("Host name '" + host.getHostName() + "' does not match " + "the certificate subject provided by the peer (" + sslsession.getPeerCertificates()[0].getSubjectX500Principal().toString() + ")");
        }
    }

    public boolean isLayeringRequired() {
        return true;
    }
}

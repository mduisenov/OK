package ru.ok.android.http.impl.nio.client;

import java.net.ProxySelector;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import ru.ok.android.http.ConnectionReuseStrategy;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequestInterceptor;
import ru.ok.android.http.HttpResponseInterceptor;
import ru.ok.android.http.auth.AuthSchemeProvider;
import ru.ok.android.http.client.AuthenticationStrategy;
import ru.ok.android.http.client.CookieStore;
import ru.ok.android.http.client.CredentialsProvider;
import ru.ok.android.http.client.RedirectStrategy;
import ru.ok.android.http.client.UserTokenHandler;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.client.protocol.RequestAddCookies;
import ru.ok.android.http.client.protocol.RequestAuthCache;
import ru.ok.android.http.client.protocol.RequestClientConnControl;
import ru.ok.android.http.client.protocol.RequestDefaultHeaders;
import ru.ok.android.http.client.protocol.RequestExpectContinue;
import ru.ok.android.http.client.protocol.ResponseProcessCookies;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.config.Lookup;
import ru.ok.android.http.config.RegistryBuilder;
import ru.ok.android.http.conn.ConnectionKeepAliveStrategy;
import ru.ok.android.http.conn.SchemePortResolver;
import ru.ok.android.http.conn.routing.HttpRoutePlanner;
import ru.ok.android.http.conn.ssl.DefaultHostnameVerifier;
import ru.ok.android.http.conn.ssl.X509HostnameVerifier;
import ru.ok.android.http.conn.util.PublicSuffixMatcher;
import ru.ok.android.http.conn.util.PublicSuffixMatcherLoader;
import ru.ok.android.http.cookie.CookieSpecProvider;
import ru.ok.android.http.impl.DefaultConnectionReuseStrategy;
import ru.ok.android.http.impl.NoConnectionReuseStrategy;
import ru.ok.android.http.impl.auth.BasicSchemeFactory;
import ru.ok.android.http.impl.auth.DigestSchemeFactory;
import ru.ok.android.http.impl.auth.KerberosSchemeFactory;
import ru.ok.android.http.impl.auth.NTLMSchemeFactory;
import ru.ok.android.http.impl.auth.SPNegoSchemeFactory;
import ru.ok.android.http.impl.client.BasicCookieStore;
import ru.ok.android.http.impl.client.BasicCredentialsProvider;
import ru.ok.android.http.impl.client.DefaultConnectionKeepAliveStrategy;
import ru.ok.android.http.impl.client.DefaultRedirectStrategy;
import ru.ok.android.http.impl.client.NoopUserTokenHandler;
import ru.ok.android.http.impl.client.ProxyAuthenticationStrategy;
import ru.ok.android.http.impl.client.TargetAuthenticationStrategy;
import ru.ok.android.http.impl.conn.DefaultProxyRoutePlanner;
import ru.ok.android.http.impl.conn.DefaultRoutePlanner;
import ru.ok.android.http.impl.conn.DefaultSchemePortResolver;
import ru.ok.android.http.impl.conn.SystemDefaultRoutePlanner;
import ru.ok.android.http.impl.cookie.DefaultCookieSpecProvider;
import ru.ok.android.http.impl.cookie.IgnoreSpecProvider;
import ru.ok.android.http.impl.cookie.NetscapeDraftSpecProvider;
import ru.ok.android.http.impl.cookie.RFC6265CookieSpecProvider;
import ru.ok.android.http.impl.cookie.RFC6265CookieSpecProvider.CompatibilityLevel;
import ru.ok.android.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import ru.ok.android.http.impl.nio.reactor.IOReactorConfig;
import ru.ok.android.http.nio.NHttpClientEventHandler;
import ru.ok.android.http.nio.conn.NHttpClientConnectionManager;
import ru.ok.android.http.nio.conn.NoopIOSessionStrategy;
import ru.ok.android.http.nio.conn.SchemeIOSessionStrategy;
import ru.ok.android.http.nio.conn.ssl.SSLIOSessionStrategy;
import ru.ok.android.http.nio.protocol.HttpAsyncRequestExecutor;
import ru.ok.android.http.protocol.HttpProcessor;
import ru.ok.android.http.protocol.HttpProcessorBuilder;
import ru.ok.android.http.protocol.RequestContent;
import ru.ok.android.http.protocol.RequestTargetHost;
import ru.ok.android.http.protocol.RequestUserAgent;
import ru.ok.android.http.ssl.SSLContexts;
import ru.ok.android.http.util.TextUtils;
import ru.ok.android.http.util.VersionInfo;

public class HttpAsyncClientBuilder {
    private boolean authCachingDisabled;
    private Lookup<AuthSchemeProvider> authSchemeRegistry;
    private NHttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private boolean connectionStateDisabled;
    private boolean cookieManagementDisabled;
    private Lookup<CookieSpecProvider> cookieSpecRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private ConnectionConfig defaultConnectionConfig;
    private Collection<? extends Header> defaultHeaders;
    private IOReactorConfig defaultIOReactorConfig;
    private RequestConfig defaultRequestConfig;
    private NHttpClientEventHandler eventHandler;
    private HostnameVerifier hostnameVerifier;
    private HttpProcessor httpprocessor;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private int maxConnPerRoute;
    private int maxConnTotal;
    private HttpHost proxy;
    private AuthenticationStrategy proxyAuthStrategy;
    private PublicSuffixMatcher publicSuffixMatcher;
    private RedirectStrategy redirectStrategy;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private ConnectionReuseStrategy reuseStrategy;
    private HttpRoutePlanner routePlanner;
    private SchemePortResolver schemePortResolver;
    private SchemeIOSessionStrategy sslStrategy;
    private SSLContext sslcontext;
    private boolean systemProperties;
    private AuthenticationStrategy targetAuthStrategy;
    private ThreadFactory threadFactory;
    private String userAgent;
    private UserTokenHandler userTokenHandler;

    public static HttpAsyncClientBuilder create() {
        return new HttpAsyncClientBuilder();
    }

    protected HttpAsyncClientBuilder() {
        this.maxConnTotal = 0;
        this.maxConnPerRoute = 0;
    }

    public final HttpAsyncClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher) {
        this.publicSuffixMatcher = publicSuffixMatcher;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManager(NHttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpAsyncClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final HttpAsyncClientBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public final HttpAsyncClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
        this.userTokenHandler = userTokenHandler;
        return this;
    }

    public final HttpAsyncClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
        this.httpprocessor = httpprocessor;
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseFirst == null) {
                this.responseFirst = new LinkedList();
            }
            this.responseFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseLast == null) {
                this.responseLast = new LinkedList();
            }
            this.responseLast.addLast(itcp);
        }
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestFirst == null) {
                this.requestFirst = new LinkedList();
            }
            this.requestFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestLast == null) {
                this.requestLast = new LinkedList();
            }
            this.requestLast.addLast(itcp);
        }
        return this;
    }

    public final HttpAsyncClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final HttpAsyncClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final HttpAsyncClientBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLStrategy(SchemeIOSessionStrategy strategy) {
        this.sslStrategy = strategy;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLContext(SSLContext sslcontext) {
        this.sslcontext = sslcontext;
        return this;
    }

    @Deprecated
    public final HttpAsyncClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultIOReactorConfig(IOReactorConfig config) {
        this.defaultIOReactorConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.defaultConnectionConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public final HttpAsyncClientBuilder setEventHandler(NHttpClientEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public final HttpAsyncClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public CloseableHttpAsyncClient build() {
        PublicSuffixMatcher publicSuffixMatcher = this.publicSuffixMatcher;
        if (publicSuffixMatcher == null) {
            publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        }
        NHttpClientConnectionManager connManager = this.connManager;
        if (connManager == null) {
            SchemeIOSessionStrategy sslStrategy = this.sslStrategy;
            if (sslStrategy == null) {
                SSLContext sslcontext = this.sslcontext;
                if (sslcontext == null) {
                    if (this.systemProperties) {
                        sslcontext = SSLContexts.createDefault();
                    } else {
                        sslcontext = SSLContexts.createSystemDefault();
                    }
                }
                String[] supportedProtocols = this.systemProperties ? split(System.getProperty("https.protocols")) : null;
                String[] supportedCipherSuites = this.systemProperties ? split(System.getProperty("https.cipherSuites")) : null;
                HostnameVerifier hostnameVerifier = this.hostnameVerifier;
                if (hostnameVerifier == null) {
                    DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
                }
                SSLIOSessionStrategy sSLIOSessionStrategy = new SSLIOSessionStrategy(sslcontext, supportedProtocols, supportedCipherSuites, hostnameVerifier);
            }
            PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager = new PoolingNHttpClientConnectionManager(IOReactorUtils.create(this.defaultIOReactorConfig != null ? this.defaultIOReactorConfig : IOReactorConfig.DEFAULT), RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE).register("https", sslStrategy).build());
            if (this.defaultConnectionConfig != null) {
                poolingNHttpClientConnectionManager.setDefaultConnectionConfig(this.defaultConnectionConfig);
            }
            if (this.systemProperties) {
                if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
                    int max = Integer.parseInt(System.getProperty("http.maxConnections", "5"));
                    poolingNHttpClientConnectionManager.setDefaultMaxPerRoute(max);
                    poolingNHttpClientConnectionManager.setMaxTotal(max * 2);
                }
            } else {
                if (this.maxConnTotal > 0) {
                    poolingNHttpClientConnectionManager.setMaxTotal(this.maxConnTotal);
                }
                if (this.maxConnPerRoute > 0) {
                    poolingNHttpClientConnectionManager.setDefaultMaxPerRoute(this.maxConnPerRoute);
                }
            }
            connManager = poolingNHttpClientConnectionManager;
        }
        ConnectionReuseStrategy reuseStrategy = this.reuseStrategy;
        if (reuseStrategy == null) {
            if (this.systemProperties) {
                if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
                    reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
                } else {
                    reuseStrategy = NoConnectionReuseStrategy.INSTANCE;
                }
            } else {
                reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
            }
        }
        ConnectionKeepAliveStrategy keepAliveStrategy = this.keepAliveStrategy;
        if (keepAliveStrategy == null) {
            keepAliveStrategy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        AuthenticationStrategy targetAuthStrategy = this.targetAuthStrategy;
        if (targetAuthStrategy == null) {
            targetAuthStrategy = TargetAuthenticationStrategy.INSTANCE;
        }
        AuthenticationStrategy proxyAuthStrategy = this.proxyAuthStrategy;
        if (proxyAuthStrategy == null) {
            proxyAuthStrategy = ProxyAuthenticationStrategy.INSTANCE;
        }
        UserTokenHandler userTokenHandler = this.userTokenHandler;
        if (userTokenHandler == null) {
            if (this.connectionStateDisabled) {
                userTokenHandler = NoopUserTokenHandler.INSTANCE;
            } else {
                userTokenHandler = DefaultAsyncUserTokenHandler.INSTANCE;
            }
        }
        SchemePortResolver schemePortResolver = this.schemePortResolver;
        if (schemePortResolver == null) {
            schemePortResolver = DefaultSchemePortResolver.INSTANCE;
        }
        HttpProcessor httpprocessor = this.httpprocessor;
        if (httpprocessor == null) {
            Iterator i$;
            String userAgent = this.userAgent;
            if (userAgent == null) {
                if (this.systemProperties) {
                    userAgent = System.getProperty("http.agent");
                }
                if (userAgent == null) {
                    userAgent = VersionInfo.getUserAgent("Apache-HttpAsyncClient", "ru.ok.android.http.nio.client", getClass());
                }
            }
            HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (this.requestFirst != null) {
                i$ = this.requestFirst.iterator();
                while (i$.hasNext()) {
                    b.addFirst((HttpRequestInterceptor) i$.next());
                }
            }
            if (this.responseFirst != null) {
                i$ = this.responseFirst.iterator();
                while (i$.hasNext()) {
                    b.addFirst((HttpResponseInterceptor) i$.next());
                }
            }
            r12 = new HttpRequestInterceptor[6];
            r12[0] = new RequestDefaultHeaders(this.defaultHeaders);
            r12[1] = new RequestContent();
            r12[2] = new RequestTargetHost();
            r12[3] = new RequestClientConnControl();
            r12[4] = new RequestUserAgent(userAgent);
            r12[5] = new RequestExpectContinue();
            b.addAll(r12);
            if (!this.cookieManagementDisabled) {
                b.add(new RequestAddCookies());
            }
            if (!this.authCachingDisabled) {
                b.add(new RequestAuthCache());
            }
            if (!this.cookieManagementDisabled) {
                b.add(new ResponseProcessCookies());
            }
            if (this.requestLast != null) {
                i$ = this.requestLast.iterator();
                while (i$.hasNext()) {
                    b.addLast((HttpRequestInterceptor) i$.next());
                }
            }
            if (this.responseLast != null) {
                i$ = this.responseLast.iterator();
                while (i$.hasNext()) {
                    b.addLast((HttpResponseInterceptor) i$.next());
                }
            }
            httpprocessor = b.build();
        }
        HttpRoutePlanner routePlanner = this.routePlanner;
        if (routePlanner == null) {
            if (this.proxy != null) {
                routePlanner = new DefaultProxyRoutePlanner(this.proxy, schemePortResolver);
            } else if (this.systemProperties) {
                routePlanner = new SystemDefaultRoutePlanner(schemePortResolver, ProxySelector.getDefault());
            } else {
                routePlanner = new DefaultRoutePlanner(schemePortResolver);
            }
        }
        Lookup<AuthSchemeProvider> authSchemeRegistry = this.authSchemeRegistry;
        if (authSchemeRegistry == null) {
            BasicSchemeFactory basicSchemeFactory = new BasicSchemeFactory();
            DigestSchemeFactory digestSchemeFactory = new DigestSchemeFactory();
            NTLMSchemeFactory nTLMSchemeFactory = new NTLMSchemeFactory();
            SPNegoSchemeFactory sPNegoSchemeFactory = new SPNegoSchemeFactory();
            String str = "Kerberos";
            KerberosSchemeFactory kerberosSchemeFactory = new KerberosSchemeFactory();
            authSchemeRegistry = RegistryBuilder.create().register("Basic", r42).register("Digest", r42).register("NTLM", r42).register("Negotiate", r42).register(r18, r42).build();
        }
        Lookup<CookieSpecProvider> cookieSpecRegistry = this.cookieSpecRegistry;
        if (cookieSpecRegistry == null) {
            DefaultCookieSpecProvider defaultCookieSpecProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
            str = "standard-strict";
            str = "netscape";
            NetscapeDraftSpecProvider netscapeDraftSpecProvider = new NetscapeDraftSpecProvider();
            str = "ignoreCookies";
            IgnoreSpecProvider ignoreSpecProvider = new IgnoreSpecProvider();
            cookieSpecRegistry = RegistryBuilder.create().register("default", defaultCookieSpecProvider).register("best-match", defaultCookieSpecProvider).register("compatibility", defaultCookieSpecProvider).register("standard", new RFC6265CookieSpecProvider(CompatibilityLevel.RELAXED, publicSuffixMatcher)).register(r18, new RFC6265CookieSpecProvider(CompatibilityLevel.STRICT, publicSuffixMatcher)).register(r18, r42).register(r18, r42).build();
        }
        CookieStore defaultCookieStore = this.cookieStore;
        if (defaultCookieStore == null) {
            defaultCookieStore = new BasicCookieStore();
        }
        CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
        if (defaultCredentialsProvider == null) {
            defaultCredentialsProvider = new BasicCredentialsProvider();
        }
        RedirectStrategy redirectStrategy = this.redirectStrategy;
        if (redirectStrategy == null) {
            redirectStrategy = DefaultRedirectStrategy.INSTANCE;
        }
        RequestConfig defaultRequestConfig = this.defaultRequestConfig;
        if (defaultRequestConfig == null) {
            defaultRequestConfig = RequestConfig.DEFAULT;
        }
        MainClientExec exec = new MainClientExec(httpprocessor, routePlanner, redirectStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
        ThreadFactory threadFactory = null;
        NHttpClientEventHandler eventHandler = null;
        if (!this.connManagerShared) {
            threadFactory = this.threadFactory;
            if (threadFactory == null) {
                threadFactory = Executors.defaultThreadFactory();
            }
            eventHandler = this.eventHandler;
            if (eventHandler == null) {
                eventHandler = new HttpAsyncRequestExecutor();
            }
        }
        return new InternalHttpAsyncClient(connManager, reuseStrategy, keepAliveStrategy, threadFactory, eventHandler, exec, cookieSpecRegistry, authSchemeRegistry, defaultCookieStore, defaultCredentialsProvider, defaultRequestConfig);
    }
}

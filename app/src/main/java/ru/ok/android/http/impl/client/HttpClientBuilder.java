package ru.ok.android.http.impl.client;

import java.io.Closeable;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import ru.ok.android.http.ConnectionReuseStrategy;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequestInterceptor;
import ru.ok.android.http.HttpResponseInterceptor;
import ru.ok.android.http.auth.AuthSchemeProvider;
import ru.ok.android.http.client.AuthenticationStrategy;
import ru.ok.android.http.client.BackoffManager;
import ru.ok.android.http.client.ConnectionBackoffStrategy;
import ru.ok.android.http.client.CookieStore;
import ru.ok.android.http.client.CredentialsProvider;
import ru.ok.android.http.client.HttpRequestRetryHandler;
import ru.ok.android.http.client.RedirectStrategy;
import ru.ok.android.http.client.ServiceUnavailableRetryStrategy;
import ru.ok.android.http.client.UserTokenHandler;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.client.entity.InputStreamFactory;
import ru.ok.android.http.client.protocol.RequestAcceptEncoding;
import ru.ok.android.http.client.protocol.RequestAddCookies;
import ru.ok.android.http.client.protocol.RequestAuthCache;
import ru.ok.android.http.client.protocol.RequestClientConnControl;
import ru.ok.android.http.client.protocol.RequestDefaultHeaders;
import ru.ok.android.http.client.protocol.RequestExpectContinue;
import ru.ok.android.http.client.protocol.ResponseContentEncoding;
import ru.ok.android.http.client.protocol.ResponseProcessCookies;
import ru.ok.android.http.config.ConnectionConfig;
import ru.ok.android.http.config.Lookup;
import ru.ok.android.http.config.RegistryBuilder;
import ru.ok.android.http.config.SocketConfig;
import ru.ok.android.http.conn.ConnectionKeepAliveStrategy;
import ru.ok.android.http.conn.HttpClientConnectionManager;
import ru.ok.android.http.conn.SchemePortResolver;
import ru.ok.android.http.conn.routing.HttpRoutePlanner;
import ru.ok.android.http.conn.socket.LayeredConnectionSocketFactory;
import ru.ok.android.http.conn.socket.PlainConnectionSocketFactory;
import ru.ok.android.http.conn.ssl.DefaultHostnameVerifier;
import ru.ok.android.http.conn.ssl.SSLConnectionSocketFactory;
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
import ru.ok.android.http.impl.conn.DefaultProxyRoutePlanner;
import ru.ok.android.http.impl.conn.DefaultRoutePlanner;
import ru.ok.android.http.impl.conn.DefaultSchemePortResolver;
import ru.ok.android.http.impl.conn.PoolingHttpClientConnectionManager;
import ru.ok.android.http.impl.conn.SystemDefaultRoutePlanner;
import ru.ok.android.http.impl.cookie.DefaultCookieSpecProvider;
import ru.ok.android.http.impl.cookie.IgnoreSpecProvider;
import ru.ok.android.http.impl.cookie.NetscapeDraftSpecProvider;
import ru.ok.android.http.impl.cookie.RFC6265CookieSpecProvider;
import ru.ok.android.http.impl.cookie.RFC6265CookieSpecProvider.CompatibilityLevel;
import ru.ok.android.http.impl.execchain.BackoffStrategyExec;
import ru.ok.android.http.impl.execchain.ClientExecChain;
import ru.ok.android.http.impl.execchain.MainClientExec;
import ru.ok.android.http.impl.execchain.ProtocolExec;
import ru.ok.android.http.impl.execchain.RedirectExec;
import ru.ok.android.http.impl.execchain.RetryExec;
import ru.ok.android.http.impl.execchain.ServiceUnavailableRetryExec;
import ru.ok.android.http.protocol.HttpProcessor;
import ru.ok.android.http.protocol.HttpProcessorBuilder;
import ru.ok.android.http.protocol.HttpRequestExecutor;
import ru.ok.android.http.protocol.ImmutableHttpProcessor;
import ru.ok.android.http.protocol.RequestContent;
import ru.ok.android.http.protocol.RequestTargetHost;
import ru.ok.android.http.protocol.RequestUserAgent;
import ru.ok.android.http.ssl.SSLContexts;
import ru.ok.android.http.util.TextUtils;
import ru.ok.android.http.util.VersionInfo;

public class HttpClientBuilder {
    private boolean authCachingDisabled;
    private Lookup<AuthSchemeProvider> authSchemeRegistry;
    private boolean automaticRetriesDisabled;
    private BackoffManager backoffManager;
    private List<Closeable> closeables;
    private HttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private long connTimeToLive;
    private TimeUnit connTimeToLiveTimeUnit;
    private ConnectionBackoffStrategy connectionBackoffStrategy;
    private boolean connectionStateDisabled;
    private boolean contentCompressionDisabled;
    private Map<String, InputStreamFactory> contentDecoderMap;
    private boolean cookieManagementDisabled;
    private Lookup<CookieSpecProvider> cookieSpecRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private ConnectionConfig defaultConnectionConfig;
    private Collection<? extends Header> defaultHeaders;
    private RequestConfig defaultRequestConfig;
    private SocketConfig defaultSocketConfig;
    private boolean evictExpiredConnections;
    private boolean evictIdleConnections;
    private HostnameVerifier hostnameVerifier;
    private HttpProcessor httpprocessor;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private int maxConnPerRoute;
    private int maxConnTotal;
    private long maxIdleTime;
    private TimeUnit maxIdleTimeUnit;
    private HttpHost proxy;
    private AuthenticationStrategy proxyAuthStrategy;
    private PublicSuffixMatcher publicSuffixMatcher;
    private boolean redirectHandlingDisabled;
    private RedirectStrategy redirectStrategy;
    private HttpRequestExecutor requestExec;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private HttpRequestRetryHandler retryHandler;
    private ConnectionReuseStrategy reuseStrategy;
    private HttpRoutePlanner routePlanner;
    private SchemePortResolver schemePortResolver;
    private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
    private LayeredConnectionSocketFactory sslSocketFactory;
    private SSLContext sslcontext;
    private boolean systemProperties;
    private AuthenticationStrategy targetAuthStrategy;
    private String userAgent;
    private UserTokenHandler userTokenHandler;

    public static HttpClientBuilder create() {
        return new HttpClientBuilder();
    }

    protected HttpClientBuilder() {
        this.maxConnTotal = 0;
        this.maxConnPerRoute = 0;
        this.connTimeToLive = -1;
        this.connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;
    }

    public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec) {
        this.requestExec = requestExec;
        return this;
    }

    @Deprecated
    public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher) {
        this.publicSuffixMatcher = publicSuffixMatcher;
        return this;
    }

    public final HttpClientBuilder setSslcontext(SSLContext sslcontext) {
        this.sslcontext = sslcontext;
        return this;
    }

    public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public final HttpClientBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public final HttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public final HttpClientBuilder setDefaultSocketConfig(SocketConfig config) {
        this.defaultSocketConfig = config;
        return this;
    }

    public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.defaultConnectionConfig = config;
        return this;
    }

    public final HttpClientBuilder setConnectionTimeToLive(long connTimeToLive, TimeUnit connTimeToLiveTimeUnit) {
        this.connTimeToLive = connTimeToLive;
        this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
        return this;
    }

    public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final HttpClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
        this.userTokenHandler = userTokenHandler;
        return this;
    }

    public final HttpClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final HttpClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseFirst == null) {
                this.responseFirst = new LinkedList();
            }
            this.responseFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseLast == null) {
                this.responseLast = new LinkedList();
            }
            this.responseLast.addLast(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestFirst == null) {
                this.requestFirst = new LinkedList();
            }
            this.requestFirst.addFirst(itcp);
        }
        return this;
    }

    public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestLast == null) {
                this.requestLast = new LinkedList();
            }
            this.requestLast.addLast(itcp);
        }
        return this;
    }

    public final HttpClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableContentCompression() {
        this.contentCompressionDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
        this.httpprocessor = httpprocessor;
        return this;
    }

    public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
        return this;
    }

    public final HttpClientBuilder disableAutomaticRetries() {
        this.automaticRetriesDisabled = true;
        return this;
    }

    public final HttpClientBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final HttpClientBuilder disableRedirectHandling() {
        this.redirectHandlingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy) {
        this.connectionBackoffStrategy = connectionBackoffStrategy;
        return this;
    }

    public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager) {
        this.backoffManager = backoffManager;
        return this;
    }

    public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
        this.serviceUnavailStrategy = serviceUnavailStrategy;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final HttpClientBuilder setContentDecoderRegistry(Map<String, InputStreamFactory> contentDecoderMap) {
        this.contentDecoderMap = contentDecoderMap;
        return this;
    }

    public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public final HttpClientBuilder evictExpiredConnections() {
        this.evictExpiredConnections = true;
        return this;
    }

    public final HttpClientBuilder evictIdleConnections(Long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this.evictIdleConnections = true;
        this.maxIdleTime = maxIdleTime.longValue();
        this.maxIdleTimeUnit = maxIdleTimeUnit;
        return this;
    }

    protected ClientExecChain createMainExec(HttpRequestExecutor requestExec, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
        return new MainClientExec(requestExec, connManager, reuseStrategy, keepAliveStrategy, proxyHttpProcessor, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
    }

    protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
        return mainExec;
    }

    protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return protocolExec;
    }

    protected void addCloseable(Closeable closeable) {
        if (closeable != null) {
            if (this.closeables == null) {
                this.closeables = new ArrayList();
            }
            this.closeables.add(closeable);
        }
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public CloseableHttpClient build() {
        List<Closeable> closeablesCopy;
        RequestConfig requestConfig;
        PublicSuffixMatcher publicSuffixMatcherCopy = this.publicSuffixMatcher;
        if (publicSuffixMatcherCopy == null) {
            publicSuffixMatcherCopy = PublicSuffixMatcherLoader.getDefault();
        }
        HttpRequestExecutor requestExecCopy = this.requestExec;
        if (requestExecCopy == null) {
            requestExecCopy = new HttpRequestExecutor();
        }
        HttpClientConnectionManager connManagerCopy = this.connManager;
        if (connManagerCopy == null) {
            LayeredConnectionSocketFactory sslSocketFactoryCopy = this.sslSocketFactory;
            if (sslSocketFactoryCopy == null) {
                String[] supportedProtocols = this.systemProperties ? split(System.getProperty("https.protocols")) : null;
                String[] supportedCipherSuites = this.systemProperties ? split(System.getProperty("https.cipherSuites")) : null;
                HostnameVerifier hostnameVerifierCopy = this.hostnameVerifier;
                if (hostnameVerifierCopy == null) {
                    DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcherCopy);
                }
                SSLConnectionSocketFactory sSLConnectionSocketFactory;
                if (this.sslcontext != null) {
                    sSLConnectionSocketFactory = new SSLConnectionSocketFactory(this.sslcontext, supportedProtocols, supportedCipherSuites, hostnameVerifierCopy);
                } else if (this.systemProperties) {
                    sSLConnectionSocketFactory = new SSLConnectionSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault(), supportedProtocols, supportedCipherSuites, hostnameVerifierCopy);
                } else {
                    sSLConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.createDefault(), hostnameVerifierCopy);
                }
            }
            PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactoryCopy).build(), null, null, null, this.connTimeToLive, this.connTimeToLiveTimeUnit != null ? this.connTimeToLiveTimeUnit : TimeUnit.MILLISECONDS);
            if (this.defaultSocketConfig != null) {
                poolingmgr.setDefaultSocketConfig(this.defaultSocketConfig);
            }
            if (this.defaultConnectionConfig != null) {
                poolingmgr.setDefaultConnectionConfig(this.defaultConnectionConfig);
            }
            if (this.systemProperties) {
                if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
                    int max = Integer.parseInt(System.getProperty("http.maxConnections", "5"));
                    poolingmgr.setDefaultMaxPerRoute(max);
                    poolingmgr.setMaxTotal(max * 2);
                }
            }
            if (this.maxConnTotal > 0) {
                poolingmgr.setMaxTotal(this.maxConnTotal);
            }
            if (this.maxConnPerRoute > 0) {
                poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute);
            }
            connManagerCopy = poolingmgr;
        }
        ConnectionReuseStrategy reuseStrategyCopy = this.reuseStrategy;
        if (reuseStrategyCopy == null) {
            if (this.systemProperties) {
                if ("true".equalsIgnoreCase(System.getProperty("http.keepAlive", "true"))) {
                    reuseStrategyCopy = DefaultConnectionReuseStrategy.INSTANCE;
                } else {
                    reuseStrategyCopy = NoConnectionReuseStrategy.INSTANCE;
                }
            } else {
                reuseStrategyCopy = DefaultConnectionReuseStrategy.INSTANCE;
            }
        }
        ConnectionKeepAliveStrategy keepAliveStrategyCopy = this.keepAliveStrategy;
        if (keepAliveStrategyCopy == null) {
            keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy;
        if (targetAuthStrategyCopy == null) {
            targetAuthStrategyCopy = TargetAuthenticationStrategy.INSTANCE;
        }
        AuthenticationStrategy proxyAuthStrategyCopy = this.proxyAuthStrategy;
        if (proxyAuthStrategyCopy == null) {
            proxyAuthStrategyCopy = ProxyAuthenticationStrategy.INSTANCE;
        }
        UserTokenHandler userTokenHandlerCopy = this.userTokenHandler;
        if (userTokenHandlerCopy == null) {
            if (this.connectionStateDisabled) {
                userTokenHandlerCopy = NoopUserTokenHandler.INSTANCE;
            } else {
                userTokenHandlerCopy = DefaultUserTokenHandler.INSTANCE;
            }
        }
        String userAgentCopy = this.userAgent;
        if (userAgentCopy == null) {
            if (this.systemProperties) {
                userAgentCopy = System.getProperty("http.agent");
            }
            if (userAgentCopy == null) {
                userAgentCopy = VersionInfo.getUserAgent("Apache-HttpClient", "ru.ok.android.http.client", getClass());
            }
        }
        ClientExecChain execChain = decorateMainExec(createMainExec(requestExecCopy, connManagerCopy, reuseStrategyCopy, keepAliveStrategyCopy, new ImmutableHttpProcessor(new HttpRequestInterceptor[]{new RequestTargetHost(), new RequestUserAgent(userAgentCopy)}), targetAuthStrategyCopy, proxyAuthStrategyCopy, userTokenHandlerCopy));
        HttpProcessor httpprocessorCopy = this.httpprocessor;
        if (httpprocessorCopy == null) {
            Iterator i$;
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
            b.addAll(new HttpRequestInterceptor[]{new RequestDefaultHeaders(this.defaultHeaders), new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(userAgentCopy), new RequestExpectContinue()});
            if (!this.cookieManagementDisabled) {
                b.add(new RequestAddCookies());
            }
            if (!this.contentCompressionDisabled) {
                if (this.contentDecoderMap != null) {
                    List<String> arrayList = new ArrayList(this.contentDecoderMap.keySet());
                    Collections.sort(arrayList);
                    b.add(new RequestAcceptEncoding(arrayList));
                } else {
                    b.add(new RequestAcceptEncoding());
                }
            }
            if (!this.authCachingDisabled) {
                b.add(new RequestAuthCache());
            }
            if (!this.cookieManagementDisabled) {
                b.add(new ResponseProcessCookies());
            }
            if (!this.contentCompressionDisabled) {
                if (this.contentDecoderMap != null) {
                    RegistryBuilder<InputStreamFactory> b2 = RegistryBuilder.create();
                    for (Entry<String, InputStreamFactory> entry : this.contentDecoderMap.entrySet()) {
                        b2.register((String) entry.getKey(), entry.getValue());
                    }
                    b.add(new ResponseContentEncoding(b2.build()));
                } else {
                    b.add(new ResponseContentEncoding());
                }
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
            httpprocessorCopy = b.build();
        }
        execChain = decorateProtocolExec(new ProtocolExec(execChain, httpprocessorCopy));
        if (!this.automaticRetriesDisabled) {
            HttpRequestRetryHandler retryHandlerCopy = this.retryHandler;
            if (retryHandlerCopy == null) {
                retryHandlerCopy = DefaultHttpRequestRetryHandler.INSTANCE;
            }
            execChain = new RetryExec(execChain, retryHandlerCopy);
        }
        HttpRoutePlanner routePlannerCopy = this.routePlanner;
        if (routePlannerCopy == null) {
            SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
            if (schemePortResolverCopy == null) {
                schemePortResolverCopy = DefaultSchemePortResolver.INSTANCE;
            }
            if (this.proxy != null) {
                DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(this.proxy, schemePortResolverCopy);
            } else if (this.systemProperties) {
                SystemDefaultRoutePlanner systemDefaultRoutePlanner = new SystemDefaultRoutePlanner(schemePortResolverCopy, ProxySelector.getDefault());
            } else {
                DefaultRoutePlanner defaultRoutePlanner = new DefaultRoutePlanner(schemePortResolverCopy);
            }
        }
        if (!this.redirectHandlingDisabled) {
            RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
            if (redirectStrategyCopy == null) {
                redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
            }
            execChain = new RedirectExec(execChain, routePlannerCopy, redirectStrategyCopy);
        }
        ServiceUnavailableRetryStrategy serviceUnavailStrategyCopy = this.serviceUnavailStrategy;
        if (serviceUnavailStrategyCopy != null) {
            execChain = new ServiceUnavailableRetryExec(execChain, serviceUnavailStrategyCopy);
        }
        if (!(this.backoffManager == null || this.connectionBackoffStrategy == null)) {
            execChain = new BackoffStrategyExec(execChain, this.connectionBackoffStrategy, this.backoffManager);
        }
        Lookup<AuthSchemeProvider> authSchemeRegistryCopy = this.authSchemeRegistry;
        if (authSchemeRegistryCopy == null) {
            authSchemeRegistryCopy = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).register("Digest", new DigestSchemeFactory()).register("NTLM", new NTLMSchemeFactory()).register("Negotiate", new SPNegoSchemeFactory()).register("Kerberos", new KerberosSchemeFactory()).build();
        }
        Lookup<CookieSpecProvider> cookieSpecRegistryCopy = this.cookieSpecRegistry;
        if (cookieSpecRegistryCopy == null) {
            DefaultCookieSpecProvider defaultCookieSpecProvider = new DefaultCookieSpecProvider(publicSuffixMatcherCopy);
            cookieSpecRegistryCopy = RegistryBuilder.create().register("default", defaultCookieSpecProvider).register("best-match", defaultCookieSpecProvider).register("compatibility", defaultCookieSpecProvider).register("standard", new RFC6265CookieSpecProvider(CompatibilityLevel.RELAXED, publicSuffixMatcherCopy)).register("standard-strict", new RFC6265CookieSpecProvider(CompatibilityLevel.STRICT, publicSuffixMatcherCopy)).register("netscape", new NetscapeDraftSpecProvider()).register("ignoreCookies", new IgnoreSpecProvider()).build();
        }
        CookieStore defaultCookieStore = this.cookieStore;
        if (defaultCookieStore == null) {
            defaultCookieStore = new BasicCookieStore();
        }
        CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
        if (defaultCredentialsProvider == null) {
            if (this.systemProperties) {
                defaultCredentialsProvider = new SystemDefaultCredentialsProvider();
            } else {
                defaultCredentialsProvider = new BasicCredentialsProvider();
            }
        }
        if (this.closeables != null) {
            List<Closeable> arrayList2 = new ArrayList(this.closeables);
        } else {
            closeablesCopy = null;
        }
        if (!this.connManagerShared) {
            if (closeablesCopy == null) {
                arrayList2 = new ArrayList(1);
            }
            HttpClientConnectionManager cm = connManagerCopy;
            if (this.evictExpiredConnections || this.evictIdleConnections) {
                TimeUnit timeUnit;
                long j = this.maxIdleTime > 0 ? this.maxIdleTime : 10;
                if (this.maxIdleTimeUnit != null) {
                    timeUnit = this.maxIdleTimeUnit;
                } else {
                    timeUnit = TimeUnit.SECONDS;
                }
                IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(cm, j, timeUnit);
                closeablesCopy.add(new 1(this, idleConnectionEvictor));
                idleConnectionEvictor.start();
            }
            closeablesCopy.add(new 2(this, cm));
        }
        if (this.defaultRequestConfig != null) {
            requestConfig = this.defaultRequestConfig;
        } else {
            requestConfig = RequestConfig.DEFAULT;
        }
        return new InternalHttpClient(execChain, connManagerCopy, routePlannerCopy, cookieSpecRegistryCopy, authSchemeRegistryCopy, defaultCookieStore, defaultCredentialsProvider, requestConfig, closeablesCopy);
    }
}

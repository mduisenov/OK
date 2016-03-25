package ru.ok.android.services.transport;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.UnknownHostException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.impl.client.CloseableHttpClient;
import ru.ok.android.http.impl.client.HttpClientBuilder;
import ru.ok.android.http.impl.conn.SystemDefaultRoutePlanner;
import ru.ok.android.http.protocol.HttpCoreContext;
import ru.ok.android.http.support.v1.CheckThreadHttpRequestInterceptor;
import ru.ok.android.http.support.v1.SSLCertificateSocketFactoryBuilder;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.services.transport.exception.ServerNotFoundException;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.HttpResult;

public class HttpTransportProvider {
    private static volatile HttpTransportProvider instance;
    private final CloseableHttpClient client;

    private HttpTransportProvider() {
        this.client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(30000).setSocketTimeout(30000).build()).setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).setSSLSocketFactory(SSLCertificateSocketFactoryBuilder.create().setHandshakeTimeout(30000).setCacheContext(OdnoklassnikiApplication.getContext()).setUseSessionTickets(VERSION.SDK_INT >= 17).buildConnectionSocketFactory()).addInterceptorFirst(CheckThreadHttpRequestInterceptor.getInstance()).build();
    }

    public static HttpTransportProvider getInstance() {
        if (instance == null) {
            synchronized (HttpTransportProvider.class) {
                if (instance == null) {
                    instance = new HttpTransportProvider();
                }
            }
        }
        return instance;
    }

    public HttpResult execute(Context context, HttpUriRequest httpRequest, @Nullable HttpCoreContext logContext) throws TransportLevelException {
        if (NetUtils.isConnectionAvailable(context, false)) {
            HttpResponse httpResponse;
            if (logContext != null) {
                try {
                    httpResponse = this.client.execute(httpRequest, logContext);
                } catch (UnknownHostException e) {
                    Logger.m177e("execHttpMethod failed: %s", e.getMessage());
                    throw new ServerNotFoundException();
                } catch (IOException e2) {
                    Logger.m177e("execHttpMethod failed: %s", e2.getMessage());
                    throw new NetworkException(e2);
                }
            }
            httpResponse = this.client.execute(httpRequest);
            return new HttpResult(httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
        }
        throw new NoConnectionException();
    }
}

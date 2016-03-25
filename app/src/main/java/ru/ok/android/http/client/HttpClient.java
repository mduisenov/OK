package ru.ok.android.http.client;

import java.io.IOException;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.conn.ClientConnectionManager;
import ru.ok.android.http.protocol.HttpContext;

public interface HttpClient {
    <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException;

    HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException;

    HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException;

    @Deprecated
    ClientConnectionManager getConnectionManager();
}

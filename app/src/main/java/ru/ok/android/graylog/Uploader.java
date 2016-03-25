package ru.ok.android.graylog;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.ByteArrayInputStream;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.StatusLine;
import ru.ok.android.http.impl.client.CloseableHttpClient;
import ru.ok.android.http.impl.client.HttpClientBuilder;
import ru.ok.android.http.support.v1.SSLCertificateSocketFactoryBuilder;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.onelog.api.ApiConfig;
import ru.ok.android.onelog.api.ApiException;
import ru.ok.android.onelog.api.ApiParam;
import ru.ok.android.onelog.api.ApiRequest;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.transport.TransportUtils;

final class Uploader {
    private final CloseableHttpClient client;
    private int countSinceLastTime;
    private volatile Handler handler;
    private long lastTimeMillis;

    private class Callback implements android.os.Handler.Callback {
        private Callback() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    Uploader.this.upload((Item) msg.obj);
                    return true;
                default:
                    return false;
            }
        }
    }

    public Uploader(Context context) {
        this.lastTimeMillis = Long.MIN_VALUE;
        this.client = HttpClientBuilder.create().setSSLSocketFactory(SSLCertificateSocketFactoryBuilder.create().setCacheContext(context).setUseSessionTickets(VERSION.SDK_INT >= 17).buildConnectionSocketFactory()).build();
    }

    public void post(Item item) {
        Message.obtain(obtainHandler(), 1, item).sendToTarget();
    }

    private void upload(Item item) {
        if (isEnough()) {
            Log.w("gray-log", "Too many calls");
            return;
        }
        ApiConfig config = GrayLog.getApiConfig();
        if (config.getUri() == null || config.getApplicationKey() == null || config.getSessionKey() == null) {
            Log.w("gray-log", "Api config incomplete");
            return;
        }
        try {
            ApiRequest request = new ApiRequest(config);
            request.addParam(createMethodParam());
            request.addParam(new ApiParam("code", Integer.toString(item.getCode())));
            request.addParam(new ApiParam("time", Long.toString(item.getTime())));
            request.addParam(new ApiParam("comment", item.getComment()));
            HttpRequest httpRequest = request.createHttpRequest();
            TransportUtils.addGeneralHeaders(httpRequest);
            HttpResponse httpResponse = this.client.execute(httpRequest);
            EntityUtils.consume(httpResponse.getEntity());
            StatusLine httpStatus = httpResponse.getStatusLine();
            if (httpStatus.getStatusCode() != 200) {
                throw new ApiException("Unexpected response " + httpStatus.getStatusCode() + " " + httpStatus.getReasonPhrase());
            }
        } catch (Throwable ignore) {
            Log.e("gray-log", "Upload failed", ignore);
        }
    }

    @NonNull
    private Handler obtainHandler() {
        if (this.handler == null) {
            synchronized (this) {
                if (this.handler == null) {
                    HandlerThread thread = new HandlerThread("gray-log");
                    thread.start();
                    this.handler = new Handler(thread.getLooper(), new Callback());
                }
            }
        }
        return this.handler;
    }

    public boolean isEnough() {
        long currentTime = SystemClock.elapsedRealtime();
        if (this.lastTimeMillis + 600000 < currentTime) {
            this.lastTimeMillis = currentTime;
            this.countSinceLastTime = 0;
        }
        int i = this.countSinceLastTime;
        this.countSinceLastTime = i + 1;
        if (i > 10) {
            return true;
        }
        return false;
    }

    private static ApiParam<?> createMethodParam() {
        return new ApiParam("method", new ByteArrayInputStream("log.clientLog".getBytes()));
    }
}

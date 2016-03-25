package ru.ok.android.onelog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import ru.ok.android.C0206R;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.StatusLine;
import ru.ok.android.http.client.HttpClient;
import ru.ok.android.http.impl.client.HttpClientBuilder;
import ru.ok.android.http.support.v1.SSLCertificateSocketFactoryBuilder;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.onelog.api.ApiConfig;
import ru.ok.android.onelog.api.ApiException;
import ru.ok.android.onelog.api.ApiInvocationErrorResponse;
import ru.ok.android.onelog.api.ApiParam;
import ru.ok.android.onelog.api.ApiRequest;
import ru.ok.android.proto.MessagesProto.Message;

final class Uploader {
    private static final byte[] BRACKET_CLOSE_BYTES;
    private static final ApiParam<byte[]> METHOD_PARAM;
    private final ByteArrayInputStream bracketClose;
    private final ByteArrayInputStream bracketOpen;
    private final HttpClient client;
    private final String collector;
    private final ApiParam<byte[]> collectorParam;
    private final File file;
    private final Lock lock;

    static {
        BRACKET_CLOSE_BYTES = new byte[]{(byte) 93, (byte) 125};
        METHOD_PARAM = new ApiParam("method", "log.externalLog".getBytes());
    }

    public Uploader(Context context, File file, Lock lock, String collector) {
        boolean z = true;
        this.file = file;
        this.lock = lock;
        this.collector = collector;
        String application = composeApplicationString(context);
        String platform = composePlatformString(context);
        this.bracketOpen = new ByteArrayInputStream(String.format("{\"application\":\"%s\",\"platform\":\"%s\",\"items\":[", new Object[]{application, platform}).getBytes());
        this.bracketClose = new ByteArrayInputStream(BRACKET_CLOSE_BYTES);
        this.collectorParam = new ApiParam("collector", collector.getBytes());
        SSLCertificateSocketFactoryBuilder cacheContext = SSLCertificateSocketFactoryBuilder.create().setCacheContext(context);
        if (VERSION.SDK_INT < 17) {
            z = false;
        }
        this.client = HttpClientBuilder.create().setSSLSocketFactory(cacheContext.setUseSessionTickets(z).buildConnectionSocketFactory()).build();
    }

    public void upload() {
        Exception e;
        FileInputStream fileIn;
        InputStream dataIn;
        try {
            this.lock.lock();
            if (!this.file.exists() || this.file.length() == 0) {
                this.lock.unlock();
                return;
            }
            ApiConfig config = OneLog.getApiConfig();
            if (config.getUri() == null || config.getApplicationKey() == null || config.getSessionKey() == null) {
                throw new ApiException("upload config incomplete, will retry");
            }
            fileIn = new FileInputStream(this.file);
            dataIn = createSequenceInputStream(this.bracketOpen, fileIn, this.bracketClose);
            ApiParam<InputStream> dataParam = createDataParam(dataIn);
            ApiRequest request = createRequest(config);
            request.addParam(METHOD_PARAM);
            request.addParam(this.collectorParam);
            request.addParam(dataParam);
            Log.d("one-log", "upload to collector " + this.collector + " @ " + config.getUri());
            HttpResponse httpResponse = this.client.execute(request.createHttpRequest());
            StatusLine httpStatus = httpResponse.getStatusLine();
            if (httpStatus.getStatusCode() != 200) {
                EntityUtils.consume(httpResponse.getEntity());
                throw new ApiException("Unexpected response " + httpStatus.getStatusCode() + " " + httpStatus.getReasonPhrase());
            }
            ApiInvocationErrorResponse errResponse = ApiInvocationErrorResponse.parseIfPresent(httpResponse);
            EntityUtils.consume(httpResponse.getEntity());
            if (errResponse != null) {
                switch (errResponse.getCode()) {
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    case C0206R.styleable.Theme_checkedTextViewStyle /*102*/:
                    case C0206R.styleable.Theme_editTextStyle /*103*/:
                    case 453:
                        Log.w("one-log", "recoverable invocation error occurred, will retry");
                        throw new ApiException(errResponse.getMessage());
                    default:
                        Log.e("one-log", "upload error " + errResponse.getMessage());
                        Log.e("one-log", "upload failed, removing possibly broken logs");
                        break;
                }
            }
            dataIn.close();
            fileIn.close();
            this.bracketOpen.reset();
            this.bracketClose.reset();
            Files.delete(this.file);
            this.lock.unlock();
        } catch (Exception e2) {
            e = e2;
            try {
                Log.e("one-log", "upload failed", e);
            } finally {
                this.lock.unlock();
            }
        } catch (ApiException e3) {
            e = e3;
            Log.e("one-log", "upload failed", e);
        } catch (Throwable th) {
            dataIn.close();
            fileIn.close();
            this.bracketOpen.reset();
            this.bracketClose.reset();
        }
    }

    private static String composeApplicationString(Context context) {
        try {
            return composeApplicationString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0));
        } catch (NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static String composeApplicationString(PackageInfo pacInfo) {
        return pacInfo.packageName + ":" + pacInfo.versionName + ":" + pacInfo.versionCode;
    }

    private static String composePlatformString(Context context) {
        return "android:" + (getSmallestScreenWidthDp(context) < 600 ? "phone" : "tablet") + ":" + VERSION.RELEASE;
    }

    @TargetApi(13)
    private static int getSmallestScreenWidthDp(Context context) {
        if (VERSION.SDK_INT >= 13) {
            return context.getResources().getConfiguration().smallestScreenWidthDp;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.min((int) (((float) metrics.widthPixels) / metrics.density), (int) (((float) metrics.heightPixels) / metrics.density));
    }

    private static InputStream createSequenceInputStream(InputStream... ins) {
        Vector<InputStream> vec = new Vector(ins.length);
        for (InputStream in : ins) {
            vec.addElement(in);
        }
        return new SequenceInputStream(vec.elements());
    }

    private static ApiParam<InputStream> createDataParam(InputStream val) {
        return new ApiParam("data", val);
    }

    private static ApiRequest createRequest(ApiConfig config) {
        return new ApiRequest(config);
    }
}

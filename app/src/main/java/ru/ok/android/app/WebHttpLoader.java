package ru.ok.android.app;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.fragments.web.WebViewUtil;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpException;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.HttpClient;
import ru.ok.android.http.client.methods.HttpGet;
import ru.ok.android.http.client.methods.HttpHead;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.support.v1.AndroidHttpClients;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.utils.Constants.Api;

public class WebHttpLoader {
    protected final ThreadLocal<SoftReference<byte[]>> bufferThreadLocal;
    protected final Context context;
    protected final HttpClient httpClient;
    protected volatile boolean isDisposed;
    protected final BlockingQueue<Runnable> sPoolWorkQueue;
    protected final ThreadFactory sThreadFactory;
    protected final ThreadPoolExecutor threadPoolExecutor;

    /* renamed from: ru.ok.android.app.WebHttpLoader.1 */
    class C02181 implements Runnable {
        final /* synthetic */ LoadUrlTaskCommon val$task;

        C02181(LoadUrlTaskCommon loadUrlTaskCommon) {
            this.val$task = loadUrlTaskCommon;
        }

        public void run() {
            WebHttpLoader.this.loadUrl(this.val$task);
        }
    }

    /* renamed from: ru.ok.android.app.WebHttpLoader.2 */
    class C02192 implements ThreadFactory {
        private final AtomicInteger mCount;

        C02192() {
            this.mCount = new AtomicInteger(1);
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, "WebHttpLoader #" + this.mCount.getAndIncrement());
        }
    }

    public static abstract class LoadUrlTaskCommon {
        final StackTraceElement[] ctorTrace;
        final boolean failOnError;
        private Handler handler;
        public final RequestType type;
        public final String url;

        /* renamed from: ru.ok.android.app.WebHttpLoader.LoadUrlTaskCommon.1 */
        class C02201 extends Handler {
            C02201() {
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        LoadUrlTaskCommon.this.onFailed(msg.arg1);
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                        LoadUrlTaskCommon.this.onRedirect((String) msg.obj);
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        LoadUrlTaskCommon.this.onLoadedContent((String) msg.obj);
                    case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        LoadUrlTaskCommon.this.onLoadedContent(((WebResponse) msg.obj).url);
                    default:
                }
            }
        }

        public abstract void onFailed(int i);

        public abstract void onLoadedContent(String str);

        public abstract void onRedirect(String str);

        public LoadUrlTaskCommon(String url, RequestType type) {
            this(url, type, true);
        }

        public LoadUrlTaskCommon(String url, RequestType type, boolean failOnError) {
            this.handler = new C02201();
            this.url = url;
            this.type = type;
            this.ctorTrace = Thread.currentThread().getStackTrace();
            this.failOnError = failOnError;
        }

        void postFailed(int errorCode) {
            this.handler.sendMessage(Message.obtain(this.handler, 1, errorCode, 0));
        }

        void postRedirect(String newUrl) {
            this.handler.sendMessage(Message.obtain(this.handler, 2, newUrl));
        }

        public void postLoadedContent(String url) {
            this.handler.sendMessage(Message.obtain(this.handler, 3, url));
        }

        void postLoadedContent(WebResponse webResponse) {
            this.handler.sendMessage(Message.obtain(this.handler, 4, webResponse));
        }
    }

    public enum RequestType {
        GET(true) {
            HttpUriRequest createRequest(String url) {
                return new HttpGet(url);
            }
        },
        HEAD(false) {
            HttpUriRequest createRequest(String url) {
                return new HttpHead(url);
            }
        },
        GET_RANGE_0_1(true) {
            HttpUriRequest createRequest(String url) {
                HttpGet get = new HttpGet(url);
                get.addHeader("Range", "bytes=0-1");
                return get;
            }

            boolean isResponseStatusOk(int status) {
                return super.isResponseStatusOk(status) || status == 206;
            }
        };
        
        public final boolean doReceiveContent;

        abstract HttpUriRequest createRequest(String str);

        private RequestType(boolean doReceiveContent) {
            this.doReceiveContent = doReceiveContent;
        }

        boolean isResponseStatusOk(int status) {
            return status == 200;
        }
    }

    static class WebResponse {
        final byte[] content;
        final String encoding;
        final String mimeType;
        final String url;

        WebResponse(byte[] content, String mimeType, String encoding, String url) {
            this.content = content;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.url = url;
        }
    }

    public WebHttpLoader(Context context) {
        this.bufferThreadLocal = new ThreadLocal();
        this.isDisposed = false;
        this.sThreadFactory = new C02192();
        this.sPoolWorkQueue = new LinkedBlockingQueue();
        this.threadPoolExecutor = new ThreadPoolExecutor(5, 32, 10, TimeUnit.SECONDS, this.sPoolWorkQueue, this.sThreadFactory);
        this.context = context;
        this.httpClient = createHttpClient(context);
        initCookies();
    }

    protected HttpClient createHttpClient(Context context) {
        String userAgent = WebViewUtil.getWebViewUserAgent(context);
        Logger.m172d("Create web view http client, User-Agent: " + userAgent);
        return AndroidHttpClients.create(userAgent);
    }

    public synchronized void postLoadUrl(LoadUrlTaskCommon task) {
        if (this.isDisposed) {
            throw new IllegalStateException("Attempt to use disposed WebHttpLoader");
        } else if (isUrlValid(task.url)) {
            this.threadPoolExecutor.execute(new C02181(task));
        } else {
            task.onFailed(1);
        }
    }

    protected HttpUriRequest createRequest(String url, RequestType requestType) throws MalformedURLException {
        try {
            HttpUriRequest httpUriRequest = requestType.createRequest(url);
            String cookie = getCookie(url);
            Logger.m173d("Cookie: %s for url: %s", cookie, url);
            if (cookie != null) {
                httpUriRequest.setHeader("Cookie", cookie);
            }
            return httpUriRequest;
        } catch (IllegalArgumentException e) {
            throw new MalformedURLException("Invalid url: " + url);
        }
    }

    public static WebHttpLoader from(Context context) {
        return ((OdnoklassnikiApplication) context.getApplicationContext()).getWebHttpLoader();
    }

    protected void handleContent(HttpEntity entity, LoadUrlTaskCommon task) throws IOException {
        Throwable th;
        if (task.type.doReceiveContent) {
            Header contentEncodingHeader = entity.getContentType();
            String mimeType = null;
            String encoding = null;
            if (contentEncodingHeader != null) {
                Pair<String, String> mimeTypeEncoding = WebViewUtil.parseMimeTypeAndEncoding(contentEncodingHeader.getValue());
                if (mimeTypeEncoding != null) {
                    mimeType = mimeTypeEncoding.first;
                    encoding = mimeTypeEncoding.second;
                }
            }
            Logger.m172d("mimeType=" + mimeType + " encoding=" + encoding);
            Closeable closeable = null;
            Closeable out = null;
            try {
                closeable = entity.getContent();
                Closeable out2 = new ByteArrayOutputStream();
                try {
                    byte[] buffer = obtainIOBuffer();
                    while (true) {
                        int readBytes = closeable.read(buffer);
                        if (readBytes != -1) {
                            out2.write(buffer, 0, readBytes);
                        } else {
                            IOUtils.closeSilently(closeable);
                            IOUtils.closeSilently(out2);
                            byte[] data = out2.toByteArray();
                            logContent("HTTP Content: ", data, encoding);
                            task.postLoadedContent(new WebResponse(data, mimeType, encoding, task.url));
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    out = out2;
                    IOUtils.closeSilently(closeable);
                    IOUtils.closeSilently(out);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                IOUtils.closeSilently(closeable);
                IOUtils.closeSilently(out);
                throw th;
            }
        }
        task.postLoadedContent(task.url);
    }

    protected void handleRedirect(HttpResponse response, LoadUrlTaskCommon task) throws HttpException {
        Header locationHeader = response.getFirstHeader("Location");
        String location = locationHeader == null ? null : locationHeader.getValue();
        if (location == null) {
            Logger.m176e("HTTP redirect with no Location header specified");
            throw new HttpException("Missing Location header");
        }
        Logger.m172d("HTTP redirect to: " + decodeUrl(location));
        if (equalUrls(task.url, location)) {
            Logger.m184w("Cyclic redirect detected!");
            throw new HttpException("Cyclic redirect detected: " + decodeUrl(location));
        } else {
            task.postRedirect(location);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void loadUrl(ru.ok.android.app.WebHttpLoader.LoadUrlTaskCommon r13) {
        /*
        r12 = this;
        r3 = 0;
        r2 = 0;
        r8 = r13.url;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "";
        r9 = r9.append(r10);
        r10 = decodeUrl(r8);
        r9 = r9.append(r10);
        r9 = r9.toString();
        ru.ok.android.utils.Logger.m172d(r9);
        r1 = 0;
        r9 = r13.type;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r4 = r12.createRequest(r8, r9);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r12.httpClient;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r5 = r9.execute(r4);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r12.processCookies(r8, r5);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r7 = r5.getStatusLine();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r6 = r7.getStatusCode();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r1 = r5.getEntity();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r13.type;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r9.isResponseStatusOk(r6);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        if (r9 != 0) goto L_0x00be;
    L_0x0043:
        r9 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9.<init>();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = "HTTP response: ";
        r9 = r9.append(r10);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r9.append(r6);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = " : ";
        r9 = r9.append(r10);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = r7.getReasonPhrase();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r9.append(r10);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = r9.toString();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        ru.ok.android.utils.Logger.m184w(r9);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        if (r6 == r9) goto L_0x0079;
    L_0x006d:
        r9 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        if (r6 == r9) goto L_0x0079;
    L_0x0071:
        r9 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        if (r6 == r9) goto L_0x0079;
    L_0x0075:
        r9 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        if (r6 != r9) goto L_0x0087;
    L_0x0079:
        r12.handleRedirect(r5, r13);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
    L_0x007c:
        if (r1 == 0) goto L_0x0081;
    L_0x007e:
        r1.consumeContent();	 Catch:{ Throwable -> 0x012a }
    L_0x0081:
        if (r2 == 0) goto L_0x0086;
    L_0x0083:
        r13.postFailed(r3);
    L_0x0086:
        return;
    L_0x0087:
        r9 = new ru.ok.android.http.HttpException;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10.<init>();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r11 = "Bad response: ";
        r10 = r10.append(r11);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = r10.append(r6);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r11 = " : ";
        r10 = r10.append(r11);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r11 = r7.getReasonPhrase();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = r10.append(r11);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r10 = r10.toString();	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        r9.<init>(r10);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        throw r9;	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
    L_0x00b0:
        r0 = move-exception;
        r3 = 1;
        r2 = r0;
        if (r1 == 0) goto L_0x00b8;
    L_0x00b5:
        r1.consumeContent();	 Catch:{ Throwable -> 0x012d }
    L_0x00b8:
        if (r2 == 0) goto L_0x0086;
    L_0x00ba:
        r13.postFailed(r3);
        goto L_0x0086;
    L_0x00be:
        r12.handleContent(r1, r13);	 Catch:{ MalformedURLException -> 0x00b0, IOException -> 0x00c2, IllegalStateException -> 0x00d0, HttpException -> 0x00de, Throwable -> 0x00ec }
        goto L_0x007c;
    L_0x00c2:
        r0 = move-exception;
        r3 = 2;
        r2 = r0;
        if (r1 == 0) goto L_0x00ca;
    L_0x00c7:
        r1.consumeContent();	 Catch:{ Throwable -> 0x012f }
    L_0x00ca:
        if (r2 == 0) goto L_0x0086;
    L_0x00cc:
        r13.postFailed(r3);
        goto L_0x0086;
    L_0x00d0:
        r0 = move-exception;
        r3 = 2;
        r2 = r0;
        if (r1 == 0) goto L_0x00d8;
    L_0x00d5:
        r1.consumeContent();	 Catch:{ Throwable -> 0x0131 }
    L_0x00d8:
        if (r2 == 0) goto L_0x0086;
    L_0x00da:
        r13.postFailed(r3);
        goto L_0x0086;
    L_0x00de:
        r0 = move-exception;
        r3 = 3;
        r2 = r0;
        if (r1 == 0) goto L_0x00e6;
    L_0x00e3:
        r1.consumeContent();	 Catch:{ Throwable -> 0x0133 }
    L_0x00e6:
        if (r2 == 0) goto L_0x0086;
    L_0x00e8:
        r13.postFailed(r3);
        goto L_0x0086;
    L_0x00ec:
        r0 = move-exception;
        r9 = r13.failOnError;	 Catch:{ all -> 0x0110 }
        if (r9 == 0) goto L_0x011c;
    L_0x00f1:
        r9 = r13.ctorTrace;	 Catch:{ all -> 0x0110 }
        appendTrace(r0, r9);	 Catch:{ all -> 0x0110 }
        r9 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0110 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0110 }
        r10.<init>();	 Catch:{ all -> 0x0110 }
        r11 = "Failed to load url: ";
        r10 = r10.append(r11);	 Catch:{ all -> 0x0110 }
        r10 = r10.append(r8);	 Catch:{ all -> 0x0110 }
        r10 = r10.toString();	 Catch:{ all -> 0x0110 }
        r9.<init>(r10, r0);	 Catch:{ all -> 0x0110 }
        throw r9;	 Catch:{ all -> 0x0110 }
    L_0x0110:
        r9 = move-exception;
        if (r1 == 0) goto L_0x0116;
    L_0x0113:
        r1.consumeContent();	 Catch:{ Throwable -> 0x0137 }
    L_0x0116:
        if (r2 == 0) goto L_0x011b;
    L_0x0118:
        r13.postFailed(r3);
    L_0x011b:
        throw r9;
    L_0x011c:
        r3 = 4;
        r2 = r0;
        if (r1 == 0) goto L_0x0123;
    L_0x0120:
        r1.consumeContent();	 Catch:{ Throwable -> 0x0135 }
    L_0x0123:
        if (r2 == 0) goto L_0x0086;
    L_0x0125:
        r13.postFailed(r3);
        goto L_0x0086;
    L_0x012a:
        r9 = move-exception;
        goto L_0x0081;
    L_0x012d:
        r9 = move-exception;
        goto L_0x00b8;
    L_0x012f:
        r9 = move-exception;
        goto L_0x00ca;
    L_0x0131:
        r9 = move-exception;
        goto L_0x00d8;
    L_0x0133:
        r9 = move-exception;
        goto L_0x00e6;
    L_0x0135:
        r9 = move-exception;
        goto L_0x0123;
    L_0x0137:
        r10 = move-exception;
        goto L_0x0116;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.app.WebHttpLoader.loadUrl(ru.ok.android.app.WebHttpLoader$LoadUrlTaskCommon):void");
    }

    private static boolean isUrlValid(String url) {
        return URLUtil.isValidUrl(url);
    }

    public synchronized void dispose() {
        this.isDisposed = true;
        this.threadPoolExecutor.shutdownNow();
        this.httpClient.getConnectionManager().shutdown();
    }

    protected void processCookies(String url, HttpResponse response) {
        Header[] allHeaders = response.getAllHeaders();
        if (allHeaders != null) {
            for (Header header : allHeaders) {
                if (TextUtils.equals(header.getName(), "Set-Cookie")) {
                    Logger.m172d("Set-Cookie: " + header.getValue());
                    setCookie(url, header.getValue());
                }
            }
        }
    }

    protected byte[] obtainIOBuffer() {
        byte[] buffer = null;
        SoftReference<byte[]> bufferRef = (SoftReference) this.bufferThreadLocal.get();
        if (bufferRef != null) {
            buffer = (byte[]) bufferRef.get();
        }
        if (buffer != null) {
            return buffer;
        }
        buffer = new byte[16384];
        this.bufferThreadLocal.set(new SoftReference(buffer));
        return buffer;
    }

    protected String getCookie(String url) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this.context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieSyncManager.sync();
        return cookieManager.getCookie(url);
    }

    protected void setCookie(String url, String value) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this.context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, value);
        cookieSyncManager.sync();
    }

    protected static boolean equalUrls(String url1, String url2) {
        try {
            return new URL(url1).equals(new URL(url2));
        } catch (Throwable th) {
            return false;
        }
    }

    protected void initCookies() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this.context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String appcapsCookie = "APPCAPS=" + Api.CLIENT_NAME;
        for (String domain : WebViewUtil.getOkCookieDomainUrls()) {
            cookieManager.setCookie(domain, appcapsCookie);
            Logger.m173d("Set cookie %s for domain %s", appcapsCookie, domain);
        }
        cookieSyncManager.sync();
    }

    public static String decodeUrl(String url) {
        try {
            url = URLDecoder.decode(url, null);
        } catch (Throwable th) {
        }
        return url;
    }

    protected static void logContent(String prefix, byte[] data, String encoding) {
        if (Logger.isLoggingEnable()) {
            String content = null;
            if (data != null) {
                if (encoding == null) {
                    try {
                        encoding = StringUtils.UTF8;
                    } catch (UnsupportedEncodingException e) {
                        content = new String(data);
                    }
                }
                content = new String(data, encoding);
            }
            if (content == null) {
                Logger.m172d(prefix + "null");
                return;
            }
            Logger.m172d(prefix + "--------------- BEGIN -----------------");
            for (int off = 0; off < content.length(); off += 100) {
                Logger.m172d(prefix + content.substring(off, Math.min(off + 100, content.length())));
            }
            Logger.m172d(prefix + "--------------- END -----------------");
        }
    }

    private static void appendTrace(Throwable e, StackTraceElement[] trace) {
        Throwable rootCause = findRootCause(e);
        StackTraceElement[] errorTrace = rootCause.getStackTrace();
        StackTraceElement[] newTrace = new StackTraceElement[(((trace == null ? 0 : trace.length) + (errorTrace == null ? 0 : errorTrace.length)) + 1)];
        int offset = 0;
        if (errorTrace != null) {
            System.arraycopy(errorTrace, 0, newTrace, 0, errorTrace.length);
            offset = 0 + errorTrace.length;
        }
        int offset2 = offset + 1;
        newTrace[offset] = new StackTraceElement("FakeTrace", "StartsBelow", "java", 0);
        if (trace != null) {
            System.arraycopy(trace, 0, newTrace, offset2, trace.length);
            offset = offset2 + trace.length;
        } else {
            offset = offset2;
        }
        rootCause.setStackTrace(newTrace);
    }

    private static Throwable findRootCause(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            Throwable parent = cause.getCause();
            if (parent == null) {
                break;
            }
            cause = parent;
        }
        return cause;
    }
}

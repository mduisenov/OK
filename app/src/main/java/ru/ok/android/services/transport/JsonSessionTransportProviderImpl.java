package ru.ok.android.services.transport;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.HttpEntityEnclosingRequest;
import ru.ok.android.http.HttpMessage;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.protocol.HttpCoreContext;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.onelog.api.ApiRequestsReporter;
import ru.ok.android.services.processors.login.LoginByTokenProcessorNew;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.services.utils.users.LocationUtils;
import ru.ok.android.ui.activity.BaseActivity.ErrorType;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ReferrerStorage;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.HttpResult;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.HttpSessionCreateException;
import ru.ok.java.api.exceptions.HttpStatusException;
import ru.ok.java.api.exceptions.InvalidTokenException;
import ru.ok.java.api.exceptions.LogicLevelException;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.json.JsonResultLoginParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.LoginTokenRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.LogSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer;
import ru.ok.java.api.request.serializer.http.RequestHttpSerializer.GeolocationFactory;
import ru.ok.model.login.ResultLogin;

public final class JsonSessionTransportProviderImpl extends JsonSessionTransportProvider {
    private final Context context;
    private GeolocationFactory geolocationFactory;
    private final ReadWriteLock reloginLock;
    private final AtomicInteger requestCount;
    private long requestTime;
    private ServiceStateHolder stateHolder;
    AtomicInteger successfulReLoginCount;
    private final JsonTransportProvider transportProvider;

    /* renamed from: ru.ok.android.services.transport.JsonSessionTransportProviderImpl.1 */
    class C05201 implements GeolocationFactory {
        C05201() {
        }

        public Location getGeolocation() {
            return LocationUtils.getLastLocationIfPermitted(JsonSessionTransportProviderImpl.this.context);
        }
    }

    JsonSessionTransportProviderImpl(Context context) {
        this.reloginLock = new ReentrantReadWriteLock();
        this.stateHolder = null;
        this.requestCount = new AtomicInteger(0);
        this.successfulReLoginCount = new AtomicInteger(0);
        this.geolocationFactory = new C05201();
        Logger.m172d(">>> Initializing JsonSessionTransportProvider...");
        this.context = context;
        this.transportProvider = JsonTransportProvider.getInstance(context);
        this.stateHolder = AuthSessionDataStore.getDefault(context);
        this.stateHolder.setListener(this);
        this.requestTime = Settings.getLastRequestTime(context);
        Logger.m172d("<<< Initialization of JsonSessionTransportProvider done.");
    }

    public JsonHttpResult execJsonHttpMethod(BaseRequest request, String apiUrl) throws BaseApiException {
        int requestId = this.requestCount.incrementAndGet();
        String baseUrl = this.stateHolder.getBaseUrl().toString();
        boolean loginNeeded = request != null && request.getClass().getAnnotation(NoLoginNeeded.class) == null;
        if (loginNeeded && urlRequiresLogin(baseUrl)) {
            Logger.m173d("(%d) performing login...", Integer.valueOf(requestId));
            performLogin(requestId);
        }
        if (apiUrl != null) {
            ServiceStateHolder tempStateHolder = new ServiceStateHolder(this.stateHolder);
            tempStateHolder.setBaseUrl(apiUrl);
            return execJsonHttpMethod(requestId, request, loginNeeded, true, 0, tempStateHolder);
        }
        return execJsonHttpMethod(requestId, request, loginNeeded, true, 0, this.stateHolder);
    }

    private boolean urlRequiresLogin(String url) {
        return TextUtils.equals(url, "https://api.odnoklassniki.ru") || TextUtils.equals(url, "https://apitest.odnoklassniki.ru");
    }

    private synchronized void performLogin(int requestId) throws BaseApiException {
        Logger.m173d("(%d) >>> ", Integer.valueOf(requestId));
        if (Settings.hasLoginData(this.context)) {
            Logger.m173d("(%d) performing login with token=%s", Integer.valueOf(requestId), Logger.logSecret(Settings.getToken(this.context)));
            try {
                new LoginByTokenProcessorNew(this).login(Settings.getToken(this.context), null);
            } catch (BaseApiException ex) {
                if (ex instanceof ServerReturnErrorException) {
                    ServerReturnErrorException se = (ServerReturnErrorException) ex;
                    if (se.getErrorCode() == 401) {
                        ErrorType type = ErrorType.BLOCKED;
                        if (se.getErrorMessage().equals("AUTH_LOGIN : BLOCKED")) {
                            type = ErrorType.BLOCKED;
                        } else if (se.getErrorMessage().equals("AUTH_LOGIN : INVALID_CREDENTIALS")) {
                            type = ErrorType.INVALID_CREDENTIALS;
                        } else if (se.getErrorMessage().equals("AUTH_LOGIN : LOGOUT_ALL")) {
                            type = ErrorType.LOGOUT_ALL;
                        }
                        sendErrorBroadcast(type);
                    } else {
                        throw ex;
                    }
                }
                throw ex;
            }
            Logger.m173d("(%d) <<<", Integer.valueOf(requestId));
        } else {
            Logger.m173d("(%d) <<< don't have login data", Integer.valueOf(requestId));
            throw new InvalidTokenException();
        }
    }

    private void sendErrorBroadcast(ErrorType type) {
        Intent intent = new Intent("error_user_action");
        intent.putExtra("type_extras", type);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

    private void setLastRequestTime(long time) {
        this.requestTime = time;
        Settings.setHttpRequestTime(OdnoklassnikiApplication.getContext(), time);
    }

    private long getLastRequestTime() {
        return this.requestTime;
    }

    private JsonHttpResult execJsonHttpMethod(int requestId, BaseRequest request, boolean loginNeeded, boolean reLogin, int count, ServiceStateHolder stateHolder) throws BaseApiException {
        HttpRequest httpRequest;
        Logger.m173d("(%d,%d) >>> request=%s reLogin=%s", Integer.valueOf(requestId), Integer.valueOf(count), request, Boolean.valueOf(reLogin));
        JsonHttpResult result = null;
        boolean hasError = true;
        HttpCoreContext logContext = new HttpCoreContext();
        if (loginNeeded) {
            try {
                if (getLastRequestTime() + 1800000 <= System.currentTimeMillis()) {
                    throw new ServerReturnErrorException(C0206R.styleable.Theme_checkedTextViewStyle, "reLogin time");
                }
            } catch (ServerReturnErrorException ex) {
                if (isLoggableServerError(ex) && request != null) {
                    grayLogRequest(request, logContext, ex, "api_request_server_error");
                }
                if (count <= 2 || !(ex.getErrorCode() == 10 || ex.getErrorCode() == 58 || ex.getErrorCode() == 300)) {
                    Logger.m185w("(%d,%d) request failed: %s", Integer.valueOf(requestId), Integer.valueOf(count), ex);
                    if ((ex.getErrorCode() == C0206R.styleable.Theme_checkedTextViewStyle || ex.getErrorCode() == 10 || ex.getErrorCode() == 58 || ex.getErrorCode() == 300) && (loginNeeded || reLogin)) {
                        Logger.m173d("(%d,%d) trying to re-login...", Integer.valueOf(requestId), Integer.valueOf(count));
                        if (TextUtils.isEmpty(stateHolder.getAuthenticationToken())) {
                            throw new InvalidTokenException();
                        }
                        reLogin(stateHolder.getAuthenticationToken(), request.isMakeUserOnline());
                        result = execJsonHttpMethod(requestId, request, loginNeeded, false, count + 1, stateHolder);
                        if (false) {
                            Logger.m185w("(%d,%d) <<< result=ERROR: %s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                        } else {
                            Logger.m173d("(%d,%d) <<< result=%s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                        }
                    } else if (ex.getErrorCode() == 401) {
                        ErrorType type = ErrorType.BLOCKED;
                        if (ex.getErrorMessage().equals("AUTH_LOGIN : errors.user.password.wrong")) {
                            throw new ServerReturnErrorException(40101, "error password");
                        }
                        if (ex.getErrorMessage().equals("AUTH_LOGIN : BLOCKED")) {
                            type = ErrorType.BLOCKED;
                        } else if (ex.getErrorMessage().equals("AUTH_LOGIN : INVALID_CREDENTIALS")) {
                            type = ErrorType.INVALID_CREDENTIALS;
                        } else if (ex.getErrorMessage().equals("AUTH_LOGIN : LOGOUT_ALL")) {
                            type = ErrorType.LOGOUT_ALL;
                        }
                        sendErrorBroadcast(type);
                        throw ex;
                    } else if (ex.getErrorCode() == 403) {
                        Logger.m177e("verification throwing exception: %s %s", Integer.valueOf(requestId), Integer.valueOf(count));
                        throw ex;
                    } else {
                        throw ex;
                    }
                }
                Logger.m177e("(%d,%d) reached max attempts limit, throwing exception", Integer.valueOf(requestId), Integer.valueOf(count));
                throw ex;
            } catch (ServerReturnErrorException ex2) {
                Logger.m185w("(%d,%d) request failed: %s", Integer.valueOf(requestId), Integer.valueOf(count), ex2);
                throw ex2;
            } catch (NetworkException ex3) {
                Logger.m185w("(%d,%d) request failed: %s", Integer.valueOf(requestId), Integer.valueOf(count), ex3);
                if (count > 2) {
                    Logger.m176e("reached max attempts limit, throwing exception");
                    throw ex3;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                result = execJsonHttpMethod(requestId, request, loginNeeded, false, count + 1, stateHolder);
                if (false) {
                    Logger.m185w("(%d,%d) <<< result=ERROR: %s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                } else {
                    Logger.m173d("(%d,%d) <<< result=%s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                }
            } catch (HttpSessionCreateException sessionEx) {
                if (sessionEx.getCause() instanceof NoConnectionException) {
                    throw ((NoConnectionException) sessionEx.getCause());
                }
                throw ex2;
            } catch (Throwable th) {
                if (hasError) {
                    Logger.m185w("(%d,%d) <<< result=ERROR: %s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                } else {
                    Logger.m173d("(%d,%d) <<< result=%s", Integer.valueOf(requestId), Integer.valueOf(count), result);
                }
            }
        }
        this.reloginLock.readLock().lock();
        try {
            RequestHttpSerializer requestHttpSerializer = new RequestHttpSerializer(stateHolder);
            requestHttpSerializer.setGeolocationFactory(this.geolocationFactory);
            httpRequest = requestHttpSerializer.serialize(request);
            TransportUtils.addGeneralHeaders(httpRequest);
            if (Logger.isLoggingEnable()) {
                Logger.m173d("(%d,%d) httpRequest=%s", Integer.valueOf(requestId), Integer.valueOf(count), Utils.toString(httpRequest));
                if (httpRequest instanceof HttpEntityEnclosingRequest) {
                    HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
                    if (entity.isRepeatable()) {
                        String entityString = EntityUtils.toString(entity);
                        if (entity.getContentType().getValue().startsWith("application/x-www-form-urlencoded")) {
                            for (String param : entityString.split("&")) {
                                Object[] objArr = new Object[3];
                                objArr[0] = Integer.valueOf(requestId);
                                objArr[1] = Integer.valueOf(count);
                                objArr[2] = URLDecoder.decode(param, StringUtils.UTF8);
                                Logger.m173d("(%d,%d) param %s", objArr);
                            }
                        } else {
                            Logger.m173d("(%d,%d) body %s", Integer.valueOf(requestId), Integer.valueOf(count), entityString);
                        }
                    } else {
                        Logger.m173d("(%d,%d) cannot get body", Integer.valueOf(requestId), Integer.valueOf(count));
                    }
                }
            }
        } catch (IOException e2) {
            Logger.m173d("(%d,%d) cannot get body", Integer.valueOf(requestId), Integer.valueOf(count));
        } catch (Throwable th2) {
            this.reloginLock.readLock().unlock();
        }
        long t = SystemClock.elapsedRealtime();
        result = this.transportProvider.execJsonHttpMethod(httpRequest, logContext);
        ApiRequestsReporter.report(request.getMethodName(), SystemClock.elapsedRealtime() - t);
        throwIfResponseEmpty(result);
        throwIfHttpStatusNotOk(result);
        hasError = false;
        setLastRequestTime(System.currentTimeMillis());
        this.reloginLock.readLock().unlock();
        if (null != null) {
            Logger.m185w("(%d,%d) <<< result=ERROR: %s", Integer.valueOf(requestId), Integer.valueOf(count), result);
        } else {
            Logger.m173d("(%d,%d) <<< result=%s", Integer.valueOf(requestId), Integer.valueOf(count), result);
        }
        return result;
    }

    private void throwIfHttpStatusNotOk(HttpResult result) throws HttpStatusException {
        if (result.getHttpStatus() != 200) {
            throw new HttpStatusException(result.getHttpStatus(), "Unexpected HTTP result");
        }
    }

    private void throwIfResponseEmpty(HttpResult result) throws HttpStatusException {
        if (result.getHttpStatus() == 200 && result.getHttpResponse() == null) {
            throw new HttpStatusException(result.getHttpStatus(), "Unexpected empty response body");
        }
    }

    private void reLogin(String token, boolean setOnline) throws LogicLevelException, TransportLevelException, HttpSessionCreateException {
        BaseRequest request;
        NetworkException e;
        Throwable th;
        Throwable e2;
        ServerReturnErrorException se;
        ErrorType type;
        int currentReLoginCount = this.successfulReLoginCount.get();
        this.reloginLock.writeLock().lock();
        if (this.successfulReLoginCount.get() > currentReLoginCount) {
            this.reloginLock.writeLock().unlock();
            return;
        }
        Logger.m173d(">>> token=%s setOnline=%s", Logger.logSecret(token), Boolean.valueOf(setOnline));
        boolean hasError = true;
        HttpCoreContext logContext = new HttpCoreContext();
        try {
            Context context = OdnoklassnikiApplication.getContext();
            request = new LoginTokenRequest(token, null, DeviceUtils.getDeviceId(context), ReferrerStorage.getReferrer(context), setOnline, "1");
            try {
                HttpRequest method = new RequestHttpSerializer(JsonSessionTransportProvider.getInstance().getStateHolder()).serialize(request);
                TransportUtils.addGeneralHeaders(method);
                Logger.m173d("method=%s", Utils.toString(method));
                Logger.m173d("result=%s", this.transportProvider.execJsonHttpMethod(method, logContext));
                ResultLogin resultLogin = new JsonResultLoginParser(result).parse();
                Settings.storeStrValue(context, "authHash", resultLogin.authenticationHash);
                getStateHolder().setLoginInfo(resultLogin, false);
                hasError = false;
                setLastRequestTime(System.currentTimeMillis());
                String str = "<<< %s";
                Object[] objArr = new Object[1];
                objArr[0] = null != null ? "ERROR" : "OK";
                Logger.m173d(str, objArr);
                if (null == null) {
                    this.successfulReLoginCount.incrementAndGet();
                }
                this.reloginLock.writeLock().unlock();
            } catch (NetworkException e3) {
                e = e3;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e4) {
                e2 = e4;
                Logger.m177e("Failed create session fo login: %s", e2);
                Logger.m178e(e2);
                if (e2 instanceof ServerReturnErrorException) {
                    se = (ServerReturnErrorException) e2;
                    if (isLoggableServerError(se) && request != null) {
                        grayLogRequest(request, logContext, se, "api_request_server_error");
                    }
                    if (se.getErrorCode() == 401) {
                        type = ErrorType.BLOCKED;
                        if (se.getErrorMessage().equals("AUTH_LOGIN : BLOCKED")) {
                            type = ErrorType.BLOCKED;
                        } else if (se.getErrorMessage().equals("AUTH_LOGIN : INVALID_CREDENTIALS")) {
                            type = ErrorType.INVALID_CREDENTIALS;
                        } else if (se.getErrorMessage().equals("AUTH_LOGIN : LOGOUT_ALL")) {
                            type = ErrorType.LOGOUT_ALL;
                        }
                        sendErrorBroadcast(type);
                    }
                }
                throw new HttpSessionCreateException("Failed create session fo login", e2);
            }
        } catch (NetworkException e5) {
            e = e5;
            request = null;
            throw e;
        } catch (Exception e6) {
            e2 = e6;
            request = null;
            Logger.m177e("Failed create session fo login: %s", e2);
            Logger.m178e(e2);
            if (e2 instanceof ServerReturnErrorException) {
                se = (ServerReturnErrorException) e2;
                grayLogRequest(request, logContext, se, "api_request_server_error");
                if (se.getErrorCode() == 401) {
                    type = ErrorType.BLOCKED;
                    if (se.getErrorMessage().equals("AUTH_LOGIN : BLOCKED")) {
                        type = ErrorType.BLOCKED;
                    } else if (se.getErrorMessage().equals("AUTH_LOGIN : INVALID_CREDENTIALS")) {
                        type = ErrorType.INVALID_CREDENTIALS;
                    } else if (se.getErrorMessage().equals("AUTH_LOGIN : LOGOUT_ALL")) {
                        type = ErrorType.LOGOUT_ALL;
                    }
                    sendErrorBroadcast(type);
                }
            }
            throw new HttpSessionCreateException("Failed create session fo login", e2);
        } catch (Throwable th22) {
            th = th22;
            request = null;
            String str2 = "<<< %s";
            Object[] objArr2 = new Object[1];
            objArr2[0] = hasError ? "ERROR" : "OK";
            Logger.m173d(str2, objArr2);
            if (!hasError) {
                this.successfulReLoginCount.incrementAndGet();
            }
            this.reloginLock.writeLock().unlock();
            throw th;
        }
    }

    public ServiceStateHolder getStateHolder() {
        return this.stateHolder;
    }

    public synchronized String getWebBaseUrl() {
        Logger.m173d("result: %s", ConfigurationPreferences.getInstance().getWebServer());
        return ConfigurationPreferences.getInstance().getWebServer();
    }

    public void onStateHolderChange(ServiceStateHolder holder) {
        AuthSessionDataStore.saveDefault(this.context, holder);
    }

    private boolean isLoggableServerError(@NonNull ServerReturnErrorException e) {
        int code = e.getErrorCode();
        return code == C0206R.styleable.Theme_radioButtonStyle || code == C0206R.styleable.Theme_editTextStyle || code == 100;
    }

    private boolean isExtraLoggableServerError(@NonNull ServerReturnErrorException e) {
        int code = e.getErrorCode();
        return code == C0206R.styleable.Theme_radioButtonStyle || code == C0206R.styleable.Theme_checkedTextViewStyle || code == C0206R.styleable.Theme_editTextStyle;
    }

    private void grayLogRequest(BaseRequest request, HttpCoreContext httpContext, @Nullable ServerReturnErrorException e, @Nullable String firstLineMessage) {
        if (GrayLog.isEnabled()) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append('\n');
                if (firstLineMessage != null) {
                    sb.append(firstLineMessage).append('\n');
                }
                if (httpContext != null) {
                    writeHttpRequestLog(httpContext, sb);
                }
                writeApiRequestLog(request, sb);
                if (e != null && isExtraLoggableServerError(e)) {
                    writeExtraLog(sb);
                }
                GrayLog.log(sb, e);
            } catch (Throwable ex) {
                Logger.m179e(ex, "Failed to gray-log request");
            }
        }
    }

    private void writeHttpRequestLog(HttpCoreContext httpContext, StringBuilder out) {
        out.append("----- HTTP Request -----").append('\n');
        HttpRequest httpRequest = httpContext.getRequest();
        if (httpRequest instanceof HttpUriRequest) {
            out.append(Utils.toString((HttpUriRequest) httpRequest)).append('\n');
        }
        writeHttpHeaders(httpRequest, out);
        out.append('\n');
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            if (entity.isRepeatable()) {
                try {
                    String entityString = EntityUtils.toString(entity);
                    if (entity.getContentType().getValue().startsWith("application/x-www-form-urlencoded")) {
                        for (String param : entityString.split("&")) {
                            out.append("param: ").append(param).append('\n');
                        }
                    } else {
                        out.append("body: ").append(entityString).append('\n');
                    }
                } catch (IOException ex) {
                    out.append("Failed to get body: ").append(ex).append('\n');
                }
            } else {
                out.append("Cannot get body: ").append('\n');
            }
        }
        HttpResponse response = httpContext.getResponse();
        if (response != null) {
            out.append("----- HTTP Response -----").append('\n');
            out.append(response.getStatusLine());
            writeHttpHeaders(response, out);
        }
    }

    private void writeHttpHeaders(HttpMessage httpRequest, StringBuilder out) {
        Header[] headers = httpRequest.getAllHeaders();
        if (headers != null) {
            for (Header header : headers) {
                if (header != null) {
                    out.append(header.getName()).append(": ").append(header.getValue()).append('\n');
                }
            }
        }
    }

    private void writeApiRequestLog(BaseRequest request, StringBuilder out) throws SerializeException, NotSessionKeyException {
        out.append("----- API Request -----").append('\n');
        new LogSerializer(out).serialize(request);
    }

    private void writeExtraLog(StringBuilder out) {
        out.append("----- Extras -----").append('\n');
        ServiceStateHolder stateHolder = getStateHolder();
        out.append("lat=").append(stateHolder.getSecretSessionKey()).append('\n');
        out.append("lon=").append(stateHolder.getSessionKey()).append('\n');
    }
}

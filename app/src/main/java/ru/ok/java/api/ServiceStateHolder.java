package ru.ok.java.api;

import android.os.Bundle;
import android.text.TextUtils;
import java.net.URI;
import java.net.URISyntaxException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.login.ResultLogin;

public class ServiceStateHolder {
    private final transient String appKey;
    private String authenticationHash;
    private volatile String authenticationToken;
    private String baseUrl;
    private final transient String defUrl;
    private final transient String secretAppKey;
    private String secretSessionKey;
    private String sessionKey;
    private transient StateHolderChangeListener stateHolderChangeListener;
    private String userId;

    public interface StateHolderChangeListener {
        void onStateHolderChange(ServiceStateHolder serviceStateHolder);
    }

    public ServiceStateHolder(String appKey, String secretAppKey, String defaultUrl) {
        this.stateHolderChangeListener = null;
        this.appKey = appKey;
        this.secretAppKey = secretAppKey;
        this.defUrl = defaultUrl;
    }

    public ServiceStateHolder(ServiceStateHolder stateHolder) {
        this.stateHolderChangeListener = null;
        this.appKey = stateHolder.appKey;
        this.secretAppKey = stateHolder.secretAppKey;
        this.defUrl = stateHolder.defUrl;
        this.baseUrl = stateHolder.baseUrl;
        this.userId = stateHolder.userId;
        this.sessionKey = stateHolder.sessionKey;
        this.secretSessionKey = stateHolder.secretSessionKey;
        this.authenticationToken = stateHolder.authenticationToken;
        this.authenticationHash = stateHolder.authenticationHash;
    }

    public void setListener(StateHolderChangeListener listener) {
        this.stateHolderChangeListener = listener;
    }

    public void legacyReadFromBundle(Bundle bundle, String tokenOld) {
        this.userId = bundle.getString("uid");
        this.sessionKey = bundle.getString("session_key");
        this.secretSessionKey = bundle.getString("session_secret_key");
        this.authenticationToken = bundle.getString("auth_token");
        this.authenticationHash = bundle.getString("auth_hash");
        if (TextUtils.isEmpty(this.authenticationToken)) {
            this.authenticationToken = tokenOld;
        }
    }

    private void onStateHolderBeChanged() {
        if (this.stateHolderChangeListener != null) {
            this.stateHolderChangeListener.onStateHolderChange(this);
        }
    }

    public void clear() {
        this.sessionKey = null;
        this.secretSessionKey = null;
        this.userId = null;
        this.baseUrl = null;
        onStateHolderBeChanged();
    }

    public void setLoginInfo(ResultLogin result, boolean tokenRequired) throws ResultParsingException {
        this.sessionKey = result.sessionKey;
        this.secretSessionKey = result.secretSessionKey;
        this.authenticationHash = result.authenticationHash;
        if (tokenRequired && !TextUtils.isEmpty(result.authenticationToken)) {
            this.authenticationToken = result.authenticationToken;
        }
        this.userId = result.uid;
        checkUrl(result.apiServer);
        setBaseUrl(result.apiServer);
    }

    private static void checkUrl(String url) throws ResultParsingException {
        if (url == null) {
            throw new ResultParsingException("URL is null");
        }
        try {
            URI uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new ResultParsingException("Not a valid url: " + url);
        }
    }

    public void setBaseUrl(String baseApiUrl) {
        this.baseUrl = baseApiUrl;
        onStateHolderBeChanged();
    }

    public void setAuthenticationHash(String authenticationHash) {
        this.authenticationHash = authenticationHash;
    }

    public String toString() {
        return "ServiceStateHolderImpl[appKey=" + this.appKey + " secretAppKey=" + (this.secretAppKey == null ? "null" : "<" + Utils.md5(this.secretAppKey) + ">") + " userId=" + this.userId + " sessionKey=" + this.sessionKey + " secretSessionKey=" + (this.secretSessionKey == null ? "null" : "<" + Utils.md5(this.secretSessionKey) + ">") + " authToken=" + (this.authenticationToken == null ? "null" : "<" + Utils.md5(this.authenticationToken) + ">") + " authHash=" + this.authenticationHash + " baseUrl=" + this.baseUrl + " defUrl=" + this.defUrl + "]";
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public String getAuthenticationToken() {
        return this.authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
        onStateHolderBeChanged();
    }

    public String getSecretAppKey() {
        return this.secretAppKey;
    }

    public String getSecretSessionKey() {
        return this.secretSessionKey;
    }

    public void clearSession() {
        this.sessionKey = null;
        this.secretSessionKey = null;
    }

    public String getBaseUrl() {
        if (this.baseUrl != null) {
            return this.baseUrl;
        }
        return this.defUrl;
    }
}

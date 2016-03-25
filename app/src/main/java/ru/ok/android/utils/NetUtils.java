package ru.ok.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ru.ok.android.http.HttpEntity;
import ru.ok.android.http.NameValuePair;
import ru.ok.android.http.client.HttpClient;
import ru.ok.android.http.client.entity.UrlEncodedFormEntity;
import ru.ok.android.http.client.methods.HttpPost;
import ru.ok.android.http.message.BasicNameValuePair;
import ru.ok.android.http.support.v1.SupportHttpClients;

public final class NetUtils {
    public static boolean isConnectionAvailable(Context context, boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            return defaultValue;
        }
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        boolean z = ni != null && ni.isConnected();
        return z;
    }

    public static final String performRequest(String url, Map<String, String> params) {
        String result = null;
        HttpClient httpClient = SupportHttpClients.createMinimal();
        HttpPost request = new HttpPost(url);
        if (!(params == null || params.isEmpty())) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList(params.size());
            for (Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            } catch (Exception e) {
            }
        }
        try {
            HttpEntity httpEntity = httpClient.execute(request).getEntity();
            if (httpEntity != null) {
                result = IOUtils.inputStreamToString(httpEntity.getContent());
            }
        } catch (Exception e2) {
        }
        return result;
    }

    public static Proxy getProxyForUrl(URL url) {
        try {
            return getProxyForUrl(url.toURI());
        } catch (URISyntaxException e) {
            Logger.m187w(e, "Failed to create URI for proxy selection: %s", url);
            return Proxy.NO_PROXY;
        }
    }

    public static Proxy getProxyForUrl(String urlStr) {
        Exception e;
        try {
            URL url = new URL(urlStr);
            return getProxyForUrl(new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()));
        } catch (URISyntaxException e2) {
            e = e2;
            Logger.m187w(e, "Failed to create URI for proxy selection: %s", urlStr);
            return Proxy.NO_PROXY;
        } catch (MalformedURLException e3) {
            e = e3;
            Logger.m187w(e, "Failed to create URI for proxy selection: %s", urlStr);
            return Proxy.NO_PROXY;
        }
    }

    public static Proxy getProxyForUrl(URI url) {
        Proxy proxy = null;
        ProxySelector selector = ProxySelector.getDefault();
        if (selector != null) {
            List<Proxy> proxyList = selector.select(url);
            if (proxyList != null && proxyList.size() > 0) {
                proxy = (Proxy) proxyList.get(0);
            }
        }
        if (proxy == null) {
            return Proxy.NO_PROXY;
        }
        return proxy;
    }
}

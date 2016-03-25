package ru.ok.android.web;

import android.content.Context;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.HttpClient;
import ru.ok.android.http.client.methods.HttpGet;
import ru.ok.android.http.support.v1.SupportHttpClients;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.utils.NetUtils;

public final class WebHelper {
    static HttpClient client;

    static {
        client = SupportHttpClients.createMinimal();
    }

    public static byte[] performGet(Context context, String url) throws WebHelperException {
        if (NetUtils.isConnectionAvailable(context, false)) {
            try {
                HttpResponse response = client.execute(new HttpGet(url));
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    return EntityUtils.toByteArray(response.getEntity());
                }
                throw new WebHelperException("Error " + statusCode + " while performing get request: " + url);
            } catch (Exception e) {
                throw new WebHelperException("Failed to perform GET request: " + url, e);
            }
        }
        throw new WebHelperException();
    }
}

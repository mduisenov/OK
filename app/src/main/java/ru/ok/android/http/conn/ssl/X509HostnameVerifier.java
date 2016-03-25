package ru.ok.android.http.conn.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;

@Deprecated
public interface X509HostnameVerifier extends HostnameVerifier {
    void verify(String str, String[] strArr, String[] strArr2) throws SSLException;
}

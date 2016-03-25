package ru.ok.android.http.conn.ssl;

import javax.net.ssl.SSLException;

@Deprecated
public class StrictHostnameVerifier extends AbstractVerifier {
    public static final StrictHostnameVerifier INSTANCE;

    static {
        INSTANCE = new StrictHostnameVerifier();
    }

    public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        verify(host, cns, subjectAlts, true);
    }

    public final String toString() {
        return "STRICT";
    }
}

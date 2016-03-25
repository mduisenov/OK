package ru.ok.android.http.params;

import ru.ok.android.http.HttpVersion;
import ru.ok.android.http.ProtocolVersion;
import ru.ok.android.http.util.Args;

@Deprecated
public final class HttpProtocolParams {
    public static ProtocolVersion getVersion(HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        Object param = params.getParameter("http.protocol.version");
        if (param == null) {
            return HttpVersion.HTTP_1_1;
        }
        return (ProtocolVersion) param;
    }
}

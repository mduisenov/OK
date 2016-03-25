package ru.ok.android.http.params;

import java.util.Set;

@Deprecated
public abstract class AbstractHttpParams implements HttpParams, HttpParamsNames {
    protected AbstractHttpParams() {
    }

    public long getLongParameter(String name, long defaultValue) {
        Object param = getParameter(name);
        return param == null ? defaultValue : ((Long) param).longValue();
    }

    public int getIntParameter(String name, int defaultValue) {
        Object param = getParameter(name);
        return param == null ? defaultValue : ((Integer) param).intValue();
    }

    public boolean getBooleanParameter(String name, boolean defaultValue) {
        Object param = getParameter(name);
        return param == null ? defaultValue : ((Boolean) param).booleanValue();
    }

    public Set<String> getNames() {
        throw new UnsupportedOperationException();
    }
}

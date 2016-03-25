package ru.ok.android.http.params;

@Deprecated
public interface HttpParams {
    HttpParams copy();

    boolean getBooleanParameter(String str, boolean z);

    int getIntParameter(String str, int i);

    long getLongParameter(String str, long j);

    Object getParameter(String str);

    HttpParams setParameter(String str, Object obj);
}

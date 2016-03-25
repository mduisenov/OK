package ru.ok.android.http.auth;

import ru.ok.android.http.params.HttpParams;

@Deprecated
public interface AuthSchemeFactory {
    AuthScheme newInstance(HttpParams httpParams);
}

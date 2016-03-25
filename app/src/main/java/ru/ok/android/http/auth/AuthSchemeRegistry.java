package ru.ok.android.http.auth;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import ru.ok.android.http.config.Lookup;
import ru.ok.android.http.params.HttpParams;
import ru.ok.android.http.util.Args;

@Deprecated
public final class AuthSchemeRegistry implements Lookup<AuthSchemeProvider> {
    private final ConcurrentHashMap<String, AuthSchemeFactory> registeredSchemes;

    public AuthSchemeRegistry() {
        this.registeredSchemes = new ConcurrentHashMap();
    }

    public void register(String name, AuthSchemeFactory factory) {
        Args.notNull(name, "Name");
        Args.notNull(factory, "Authentication scheme factory");
        this.registeredSchemes.put(name.toLowerCase(Locale.ENGLISH), factory);
    }

    public AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
        Args.notNull(name, "Name");
        AuthSchemeFactory factory = (AuthSchemeFactory) this.registeredSchemes.get(name.toLowerCase(Locale.ENGLISH));
        if (factory != null) {
            return factory.newInstance(params);
        }
        throw new IllegalStateException("Unsupported authentication scheme: " + name);
    }

    public AuthSchemeProvider lookup(String name) {
        return new 1(this, name);
    }
}

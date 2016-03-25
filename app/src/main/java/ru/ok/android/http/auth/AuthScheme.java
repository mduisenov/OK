package ru.ok.android.http.auth;

import ru.ok.android.http.Header;
import ru.ok.android.http.HttpRequest;

public interface AuthScheme {
    @Deprecated
    Header authenticate(Credentials credentials, HttpRequest httpRequest) throws AuthenticationException;

    String getRealm();

    String getSchemeName();

    boolean isComplete();

    boolean isConnectionBased();

    void processChallenge(Header header) throws MalformedChallengeException;
}

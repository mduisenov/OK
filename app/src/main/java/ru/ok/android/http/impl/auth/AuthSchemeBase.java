package ru.ok.android.http.impl.auth;

import java.util.Locale;
import ru.ok.android.http.FormattedHeader;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.auth.AuthenticationException;
import ru.ok.android.http.auth.ChallengeState;
import ru.ok.android.http.auth.ContextAwareAuthScheme;
import ru.ok.android.http.auth.Credentials;
import ru.ok.android.http.auth.MalformedChallengeException;
import ru.ok.android.http.protocol.HTTP;
import ru.ok.android.http.protocol.HttpContext;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;

public abstract class AuthSchemeBase implements ContextAwareAuthScheme {
    private ChallengeState challengeState;

    protected abstract void parseChallenge(CharArrayBuffer charArrayBuffer, int i, int i2) throws MalformedChallengeException;

    @Deprecated
    public AuthSchemeBase(ChallengeState challengeState) {
        this.challengeState = challengeState;
    }

    public void processChallenge(Header header) throws MalformedChallengeException {
        CharArrayBuffer buffer;
        int pos;
        String s;
        Args.notNull(header, "Header");
        String authheader = header.getName();
        if (authheader.equalsIgnoreCase("WWW-Authenticate")) {
            this.challengeState = ChallengeState.TARGET;
        } else if (authheader.equalsIgnoreCase("Proxy-Authenticate")) {
            this.challengeState = ChallengeState.PROXY;
        } else {
            throw new MalformedChallengeException("Unexpected header name: " + authheader);
        }
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader) header).getBuffer();
            pos = ((FormattedHeader) header).getValuePos();
        } else {
            s = header.getValue();
            if (s == null) {
                throw new MalformedChallengeException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            pos = 0;
        }
        while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        int beginIndex = pos;
        while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        s = buffer.substring(beginIndex, pos);
        if (s.equalsIgnoreCase(getSchemeName())) {
            parseChallenge(buffer, pos, buffer.length());
            return;
        }
        throw new MalformedChallengeException("Invalid scheme identifier: " + s);
    }

    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        return authenticate(credentials, request);
    }

    public boolean isProxy() {
        return this.challengeState != null && this.challengeState == ChallengeState.PROXY;
    }

    public ChallengeState getChallengeState() {
        return this.challengeState;
    }

    public String toString() {
        String name = getSchemeName();
        if (name != null) {
            return name.toUpperCase(Locale.ROOT);
        }
        return super.toString();
    }
}

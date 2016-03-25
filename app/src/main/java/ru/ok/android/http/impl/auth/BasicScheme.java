package ru.ok.android.http.impl.auth;

import java.nio.charset.Charset;
import ru.ok.android.http.Consts;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.auth.AuthenticationException;
import ru.ok.android.http.auth.ChallengeState;
import ru.ok.android.http.auth.Credentials;
import ru.ok.android.http.auth.MalformedChallengeException;
import ru.ok.android.http.commons.codec.binary.Base64;
import ru.ok.android.http.message.BufferedHeader;
import ru.ok.android.http.protocol.BasicHttpContext;
import ru.ok.android.http.protocol.HttpContext;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;
import ru.ok.android.http.util.EncodingUtils;

public class BasicScheme extends RFC2617Scheme {
    private static final long serialVersionUID = -1931571557597830536L;
    private boolean complete;

    public BasicScheme(Charset credentialsCharset) {
        super(credentialsCharset);
        this.complete = false;
    }

    @Deprecated
    public BasicScheme(ChallengeState challengeState) {
        super(challengeState);
    }

    public BasicScheme() {
        this(Consts.ASCII);
    }

    public String getSchemeName() {
        return "basic";
    }

    public void processChallenge(Header header) throws MalformedChallengeException {
        super.processChallenge(header);
        this.complete = true;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean isConnectionBased() {
        return false;
    }

    @Deprecated
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return authenticate(credentials, request, new BasicHttpContext());
    }

    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        Args.notNull(credentials, "Credentials");
        Args.notNull(request, "HTTP request");
        StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
        byte[] base64password = new Base64(0).encode(EncodingUtils.getBytes(tmp.toString(), getCredentialsCharset(request)));
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (isProxy()) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }

    @Deprecated
    public static Header authenticate(Credentials credentials, String charset, boolean proxy) {
        Args.notNull(credentials, "Credentials");
        Args.notNull(charset, "charset");
        StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
        byte[] base64password = Base64.encodeBase64(EncodingUtils.getBytes(tmp.toString(), charset), false);
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (proxy) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BASIC [complete=").append(this.complete).append("]");
        return builder.toString();
    }
}

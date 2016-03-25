package ru.ok.android.http.impl.auth;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import ru.ok.android.http.Header;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.auth.AuthenticationException;
import ru.ok.android.http.auth.Credentials;
import ru.ok.android.http.auth.InvalidCredentialsException;
import ru.ok.android.http.auth.KerberosCredentials;
import ru.ok.android.http.auth.MalformedChallengeException;
import ru.ok.android.http.commons.codec.binary.Base64;
import ru.ok.android.http.commons.logging.Log;
import ru.ok.android.http.commons.logging.LogFactory;
import ru.ok.android.http.conn.routing.HttpRoute;
import ru.ok.android.http.message.BufferedHeader;
import ru.ok.android.http.protocol.HttpContext;
import ru.ok.android.http.util.Args;
import ru.ok.android.http.util.CharArrayBuffer;
import ru.ok.android.proto.MessagesProto.Message;

public abstract class GGSSchemeBase extends AuthSchemeBase {
    private final Base64 base64codec;
    private final Log log;
    private State state;
    private final boolean stripPort;
    private byte[] token;
    private final boolean useCanonicalHostname;

    GGSSchemeBase(boolean stripPort, boolean useCanonicalHostname) {
        this.log = LogFactory.getLog(getClass());
        this.base64codec = new Base64(0);
        this.stripPort = stripPort;
        this.useCanonicalHostname = useCanonicalHostname;
        this.state = State.UNINITIATED;
    }

    GGSSchemeBase(boolean stripPort) {
        this(stripPort, true);
    }

    GGSSchemeBase() {
        this(true, true);
    }

    protected GSSManager getManager() {
        return GSSManager.getInstance();
    }

    protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer) throws GSSException {
        return generateGSSToken(input, oid, authServer, null);
    }

    protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer, Credentials credentials) throws GSSException {
        GSSCredential gssCredential;
        byte[] inputBuff = input;
        if (inputBuff == null) {
            inputBuff = new byte[0];
        }
        GSSManager manager = getManager();
        GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
        if (credentials instanceof KerberosCredentials) {
            gssCredential = ((KerberosCredentials) credentials).getGSSCredential();
        } else {
            gssCredential = null;
        }
        GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, gssCredential, 0);
        gssContext.requestMutualAuth(true);
        gssContext.requestCredDeleg(true);
        return gssContext.initSecContext(inputBuff, 0, inputBuff.length);
    }

    @Deprecated
    protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
        return null;
    }

    protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
        return generateToken(input, authServer);
    }

    public boolean isComplete() {
        return this.state == State.TOKEN_GENERATED || this.state == State.FAILED;
    }

    @Deprecated
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return authenticate(credentials, request, null);
    }

    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        Args.notNull(request, "HTTP request");
        switch (1.$SwitchMap$org$apache$http$impl$auth$GGSSchemeBase$State[this.state.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                throw new AuthenticationException(getSchemeName() + " authentication has not been initiated");
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                throw new AuthenticationException(getSchemeName() + " authentication has failed");
            case Message.TYPE_FIELD_NUMBER /*3*/:
                try {
                    HttpRoute route = (HttpRoute) context.getAttribute("http.route");
                    if (route != null) {
                        HttpHost host;
                        String authServer;
                        if (isProxy()) {
                            host = route.getProxyHost();
                            if (host == null) {
                                host = route.getTargetHost();
                            }
                        } else {
                            host = route.getTargetHost();
                        }
                        String hostname = host.getHostName();
                        if (this.useCanonicalHostname) {
                            try {
                                hostname = resolveCanonicalHostname(hostname);
                            } catch (UnknownHostException e) {
                            }
                        }
                        if (this.stripPort) {
                            authServer = hostname;
                        } else {
                            authServer = hostname + ":" + host.getPort();
                        }
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("init " + authServer);
                        }
                        this.token = generateToken(this.token, authServer, credentials);
                        this.state = State.TOKEN_GENERATED;
                        break;
                    }
                    throw new AuthenticationException("Connection route is not available");
                } catch (GSSException gsse) {
                    this.state = State.FAILED;
                    if (gsse.getMajor() == 9 || gsse.getMajor() == 8) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    } else if (gsse.getMajor() == 13) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    } else if (gsse.getMajor() == 10 || gsse.getMajor() == 19 || gsse.getMajor() == 20) {
                        throw new AuthenticationException(gsse.getMessage(), gsse);
                    } else {
                        throw new AuthenticationException(gsse.getMessage());
                    }
                }
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                break;
            default:
                throw new IllegalStateException("Illegal state: " + this.state);
        }
        String tokenstr = new String(this.base64codec.encode(this.token));
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending response '" + tokenstr + "' back to the auth server");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (isProxy()) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Negotiate ");
        buffer.append(tokenstr);
        return new BufferedHeader(buffer);
    }

    protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
        String challenge = buffer.substringTrimmed(beginIndex, endIndex);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Received challenge '" + challenge + "' from the auth server");
        }
        if (this.state == State.UNINITIATED) {
            this.token = Base64.decodeBase64(challenge.getBytes());
            this.state = State.CHALLENGE_RECEIVED;
            return;
        }
        this.log.debug("Authentication already attempted");
        this.state = State.FAILED;
    }

    private String resolveCanonicalHostname(String host) throws UnknownHostException {
        InetAddress in = InetAddress.getByName(host);
        String canonicalServer = in.getCanonicalHostName();
        return in.getHostAddress().contentEquals(canonicalServer) ? host : canonicalServer;
    }
}

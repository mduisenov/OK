package ru.ok.android.http.impl.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ru.ok.android.http.Consts;
import ru.ok.android.http.HeaderElement;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.http.auth.ChallengeState;
import ru.ok.android.http.auth.MalformedChallengeException;
import ru.ok.android.http.message.BasicHeaderValueParser;
import ru.ok.android.http.message.ParserCursor;
import ru.ok.android.http.util.CharArrayBuffer;
import ru.ok.android.http.util.CharsetUtils;

public abstract class RFC2617Scheme extends AuthSchemeBase implements Serializable {
    private static final long serialVersionUID = -2845454858205884623L;
    private transient Charset credentialsCharset;
    private final Map<String, String> params;

    @Deprecated
    public RFC2617Scheme(ChallengeState challengeState) {
        super(challengeState);
        this.params = new HashMap();
        this.credentialsCharset = Consts.ASCII;
    }

    public RFC2617Scheme(Charset credentialsCharset) {
        this.params = new HashMap();
        if (credentialsCharset == null) {
            credentialsCharset = Consts.ASCII;
        }
        this.credentialsCharset = credentialsCharset;
    }

    public RFC2617Scheme() {
        this(Consts.ASCII);
    }

    public Charset getCredentialsCharset() {
        return this.credentialsCharset != null ? this.credentialsCharset : Consts.ASCII;
    }

    String getCredentialsCharset(HttpRequest request) {
        String charset = (String) request.getParams().getParameter("http.auth.credential-charset");
        if (charset == null) {
            return getCredentialsCharset().name();
        }
        return charset;
    }

    protected void parseChallenge(CharArrayBuffer buffer, int pos, int len) throws MalformedChallengeException {
        HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buffer, new ParserCursor(pos, buffer.length()));
        this.params.clear();
        for (HeaderElement element : elements) {
            this.params.put(element.getName().toLowerCase(Locale.ROOT), element.getValue());
        }
    }

    protected Map<String, String> getParameters() {
        return this.params;
    }

    public String getParameter(String name) {
        if (name == null) {
            return null;
        }
        return (String) this.params.get(name.toLowerCase(Locale.ROOT));
    }

    public String getRealm() {
        return getParameter("realm");
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.credentialsCharset.name());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.credentialsCharset = CharsetUtils.get(in.readUTF());
        if (this.credentialsCharset == null) {
            this.credentialsCharset = Consts.ASCII;
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
    }
}

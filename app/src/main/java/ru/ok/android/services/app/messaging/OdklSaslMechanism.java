package ru.ok.android.services.app.messaging;

import java.security.MessageDigest;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.utils.Constants.Api;

public class OdklSaslMechanism extends SASLMechanism {
    private String AppPublicKey;
    private String AppSecretKey;

    public OdklSaslMechanism() {
        this.AppPublicKey = "CBAFJIICABABABABA";
        this.AppSecretKey = Api.m193k();
    }

    protected void authenticateInternal(CallbackHandler cbh) throws SmackException {
    }

    protected byte[] getAuthenticationText() throws SmackException {
        return null;
    }

    public String getName() {
        return "X-ODKL-API";
    }

    public int getPriority() {
        return 1;
    }

    public OdklSaslMechanism newInstance() {
        return new OdklSaslMechanism();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance(StringUtils.MD5);
            digest.update(s.getBytes());
            byte[] result = digest.digest();
            StringBuffer res = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                res = res.append(String.format("%02X", new Object[]{Byte.valueOf(result[i])}));
            }
            return res.toString().toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    protected byte[] evaluateChallenge(byte[] challenge) throws SmackException {
        if (challenge.length == 0) {
            throw new SmackException("Initial challenge has zero length");
        }
        challenge = Base64.encode(challenge);
        return ("application_key=" + this.AppPublicKey + "&sig=" + md5(new String(challenge) + this.AppSecretKey) + "&token=" + JsonSessionTransportProvider.getInstance().getStateHolder().getAuthenticationToken()).getBytes();
    }
}

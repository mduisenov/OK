package ru.ok.android.billingUtils;

import android.text.TextUtils;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
        if (!TextUtils.isEmpty(signedData) && !TextUtils.isEmpty(base64PublicKey) && !TextUtils.isEmpty(signature)) {
            return verify(generatePublicKey(base64PublicKey), signedData, signature);
        }
        Log.e("IABUtil/Security", "Purchase verification failed: missing data.");
        return false;
    }

    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(encodedPublicKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e2) {
            Log.e("IABUtil/Security", "Invalid key specification.");
            throw new IllegalArgumentException(e2);
        } catch (Base64DecoderException e3) {
            Log.e("IABUtil/Security", "Base64 decoding failed.");
            throw new IllegalArgumentException(e3);
        }
    }

    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (sig.verify(Base64.decode(signature))) {
                return true;
            }
            Log.e("IABUtil/Security", "Signature verification failed.");
            return false;
        } catch (NoSuchAlgorithmException e) {
            Log.e("IABUtil/Security", "NoSuchAlgorithmException.");
            return false;
        } catch (InvalidKeyException e2) {
            Log.e("IABUtil/Security", "Invalid key specification.");
            return false;
        } catch (SignatureException e3) {
            Log.e("IABUtil/Security", "Signature exception.");
            return false;
        } catch (Base64DecoderException e4) {
            Log.e("IABUtil/Security", "Base64 decoding failed.");
            return false;
        }
    }
}

package ru.ok.android.utils;

import java.security.MessageDigest;
import org.jivesoftware.smack.util.StringUtils;

public final class CommonUtils {
    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance(StringUtils.MD5);
            digest.update(s.getBytes());
            byte[] result = digest.digest();
            return String.format("%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X", new Object[]{Byte.valueOf(result[0]), Byte.valueOf(result[1]), Byte.valueOf(result[2]), Byte.valueOf(result[3]), Byte.valueOf(result[4]), Byte.valueOf(result[5]), Byte.valueOf(result[6]), Byte.valueOf(result[7]), Byte.valueOf(result[8]), Byte.valueOf(result[9]), Byte.valueOf(result[10]), Byte.valueOf(result[11]), Byte.valueOf(result[12]), Byte.valueOf(result[13]), Byte.valueOf(result[14]), Byte.valueOf(result[15])}).toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }
}

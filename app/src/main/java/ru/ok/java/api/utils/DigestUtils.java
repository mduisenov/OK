package ru.ok.java.api.utils;

import android.support.v4.view.MotionEventCompat;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import org.jivesoftware.smack.util.StringUtils;

public final class DigestUtils {
    public static void addInt(MessageDigest digest, int value, byte[] buffer) {
        digest.update(buffer, 0, writeInt(buffer, 0, value));
    }

    public static void addLong(MessageDigest digest, long value, byte[] buffer) {
        digest.update(buffer, 0, writeLong(buffer, 0, value));
    }

    public static void addString(MessageDigest digest, String s) {
        if (s == null) {
            digest.update((byte) 0);
            return;
        }
        digest.update((byte) 1);
        try {
            digest.update(s.getBytes(StringUtils.UTF8));
        } catch (UnsupportedEncodingException e) {
        }
    }

    public static int writeInt(byte[] buffer, int off, int value) {
        int i = off + 1;
        buffer[off] = (byte) (value & MotionEventCompat.ACTION_MASK);
        value >>= 8;
        off = i + 1;
        buffer[i] = (byte) (value & MotionEventCompat.ACTION_MASK);
        value >>= 8;
        i = off + 1;
        buffer[off] = (byte) (value & MotionEventCompat.ACTION_MASK);
        off = i + 1;
        buffer[i] = (byte) ((value >> 8) & MotionEventCompat.ACTION_MASK);
        return 4;
    }

    public static int writeLong(byte[] buffer, int off, long value) {
        int i = off + 1;
        buffer[off] = (byte) ((int) (value & 255));
        value >>= 8;
        off = i + 1;
        buffer[i] = (byte) ((int) (value & 255));
        value >>= 8;
        i = off + 1;
        buffer[off] = (byte) ((int) (value & 255));
        value >>= 8;
        off = i + 1;
        buffer[i] = (byte) ((int) (value & 255));
        value >>= 8;
        i = off + 1;
        buffer[off] = (byte) ((int) (value & 255));
        value >>= 8;
        off = i + 1;
        buffer[i] = (byte) ((int) (value & 255));
        value >>= 8;
        i = off + 1;
        buffer[off] = (byte) ((int) (value & 255));
        off = i + 1;
        buffer[i] = (byte) ((int) ((value >> 8) & 255));
        return 8;
    }
}

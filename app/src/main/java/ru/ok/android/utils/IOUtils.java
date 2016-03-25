package ru.ok.android.utils;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentTransaction;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import org.jivesoftware.smack.util.StringUtils;

public class IOUtils {
    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeSilently(Cursor closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeSilently(AssetFileDescriptor afd) {
        if (afd != null) {
            try {
                afd.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeSilently(ParcelFileDescriptor pfd) {
        if (pfd != null) {
            try {
                pfd.close();
            } catch (Exception e) {
            }
        }
    }

    public static void disconnectSilently(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
            }
        }
    }

    public static String serializableToBase64String(Serializable serializable) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(serializable);
        byte[] data = out.toByteArray();
        out.close();
        return Base64.encodeBytes(data, 2);
    }

    public static Object base64SerializedToObject(String serialized) throws ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(serialized)));
        Object result = ois.readObject();
        ois.close();
        return result;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, StringUtils.UTF8));
        try {
            String separator = System.getProperty("line.separator");
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                out.append(line).append(separator);
            }
            return out.toString();
        } finally {
            in.close();
        }
    }

    public static void copyStreams(OutputStream out, InputStream in) throws IOException {
        try {
            byte[] buf = new byte[FragmentTransaction.TRANSIT_EXIT_MASK];
            while (true) {
                int length = in.read(buf);
                if (length < 0) {
                    break;
                }
                out.write(buf, 0, length);
            }
        } finally {
            closeSilently((Closeable) in);
            closeSilently((Closeable) out);
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        copyStreams(baout, in);
        return baout.toByteArray();
    }

    public static int read(InputStream in, byte[] buffer, int offset, int length) throws IOException {
        if (length < 0) {
            throw new IndexOutOfBoundsException("len is negative");
        }
        int total = 0;
        while (total < length) {
            int result = in.read(buffer, offset + total, length - total);
            if (result == -1) {
                break;
            }
            total += result;
        }
        return total;
    }
}

package ru.ok.java.api.utils;

import android.support.v4.app.NotificationCompat;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.model.photo.PhotoSize;

public class Utils {
    private static final Pattern entityIdPattern;

    public static boolean presentIsBig(TreeSet<PhotoSize> sizes) {
        return !sizes.isEmpty() && ((PhotoSize) sizes.first()).getWidth() >= NotificationCompat.FLAG_HIGH_PRIORITY && ((PhotoSize) sizes.first()).getHeight() == ((PhotoSize) sizes.first()).getWidth();
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

    public static String join(String delimiter, Collection<?> objects) {
        StringBuilder sb = new StringBuilder();
        for (Object value : objects) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(value.toString());
        }
        return sb.toString();
    }

    public static Set<String> extractUserIds(String str) {
        return extractEntityIds(str, "user");
    }

    public static Set<String> extractGroupIds(String str) {
        return extractEntityIds(str, "group");
    }

    static {
        entityIdPattern = Pattern.compile("\\{([^}]*)\\}");
    }

    public static Set<String> extractEntityIds(String str, String entityName) {
        Matcher matcher = entityIdPattern.matcher(str);
        Set<String> result = new HashSet();
        while (matcher.find()) {
            String[] chunks = str.substring(matcher.start() + 1, matcher.end() - 1).split(":");
            if (chunks != null && chunks.length == 2 && entityName.equals(chunks[0])) {
                result.add(chunks[1]);
            }
        }
        return result;
    }

    public static String getXoredIdSafe(String id) {
        try {
            id = Long.toString(xorId(id));
        } catch (Exception e) {
        }
        return id;
    }

    public static long xorId(String id) throws NumberFormatException {
        return xorId(Long.parseLong(id));
    }

    public static long xorId(long id) {
        return id == 265224201205L ? id : id ^ 265224201205L;
    }
}

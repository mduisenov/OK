package ru.mail.libverify.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.Base64;
import com.google.gson.Gson;
import java.io.File;
import java.security.MessageDigest;
import java.util.Locale;
import org.jivesoftware.smack.util.StringUtils;
import ru.mail.libverify.utils.bouncycastle.c;

/* renamed from: ru.mail.libverify.utils.j */
public final class C0205j {
    private static final Gson f64a;

    static {
        f64a = new Gson();
    }

    public static int m142a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static Gson m143a() {
        return f64a;
    }

    public static String m144a(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : bundle.keySet()) {
            if (!(str == null || bundle.get(str) == null)) {
                stringBuilder.append(str).append("='").append(bundle.get(str).toString()).append("', ");
            }
        }
        return stringBuilder.toString();
    }

    public static String m145a(@NonNull String str) {
        byte[] bytes = str.getBytes();
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.reset();
            instance.update(bytes);
            return C0205j.m147a(instance.digest());
        } catch (Throwable e) {
            C0204d.m130a("Utils", "stringToSHA256", e);
            return C0205j.m147a(c.a(bytes));
        }
    }

    public static String m146a(@NonNull Locale locale) {
        String language = locale.getLanguage();
        Object country = locale.getCountry();
        return TextUtils.isEmpty(language) ? "en_US" : !TextUtils.isEmpty(country) ? language + '_' + country : language;
    }

    public static String m147a(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bArr) {
            if ((b & MotionEventCompat.ACTION_MASK) < 16) {
                stringBuilder.append('0');
            }
            stringBuilder.append(Integer.toHexString(b & MotionEventCompat.ACTION_MASK));
        }
        return String.valueOf(stringBuilder);
    }

    public static void m148a(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static boolean m149a(@NonNull Context context, @NonNull String str) {
        boolean z;
        try {
            CharSequence installerPackageName = context.getPackageManager().getInstallerPackageName(str);
            z = !TextUtils.isEmpty(installerPackageName) && TextUtils.equals(installerPackageName, context.getPackageManager().getInstallerPackageName(context.getPackageName()));
            if (!z) {
                try {
                    C0204d.m137b("Utils", "Package %s was installed manually or installer is not equal to %s", str, r3);
                } catch (Throwable th) {
                    C0204d.m131a("Utils", "Failed to get package %s installer name", str);
                    return z;
                }
            }
        } catch (Throwable th2) {
            z = false;
            C0204d.m131a("Utils", "Failed to get package %s installer name", str);
            return z;
        }
        return z;
    }

    @TargetApi(16)
    public static boolean m150a(@NonNull Context context, @NonNull String str, @NonNull String str2) {
        if (VERSION.SDK_INT < 16) {
            return false;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, FragmentTransaction.TRANSIT_ENTER_MASK);
            int i = 0;
            while (i < packageInfo.requestedPermissions.length) {
                if (TextUtils.equals(packageInfo.requestedPermissions[i], str2) && (packageInfo.requestedPermissionsFlags[i] & 2) == 2) {
                    return true;
                }
                i++;
            }
            C0204d.m137b("Utils", "Package %s hasn't permission %s", str, str2);
            return false;
        } catch (Throwable th) {
            C0204d.m131a("Utils", "Failed to check package %s permission %s", str, str2);
            return false;
        }
    }

    public static File m151b(@NonNull Context context) {
        return VERSION.SDK_INT >= 21 ? context.getNoBackupFilesDir() : context.getFilesDir();
    }

    public static String m152b(@NonNull String str) {
        byte[] bytes = str.getBytes();
        try {
            MessageDigest instance = MessageDigest.getInstance(StringUtils.MD5);
            instance.reset();
            instance.update(bytes);
            return C0205j.m147a(instance.digest());
        } catch (Throwable e) {
            C0204d.m130a("Utils", "stringToMD5", e);
            bytes = str.getBytes();
            int[] iArr = new int[16];
            int[] iArr2 = new int[]{1732584193, -271733879, -1732584194, 271733878};
            int[] iArr3 = new int[2];
            byte[] bArr = new byte[64];
            g.a(bytes, bytes.length, iArr, iArr2, iArr3, bArr);
            byte[] a = g.a(new byte[16], iArr3, 8);
            bytes = new byte[64];
            bytes[0] = Byte.MIN_VALUE;
            int i = (iArr3[0] >>> 3) & 63;
            g.a(bytes, i < 56 ? 56 - i : 120 - i, iArr, iArr2, iArr3, bArr);
            g.a(a, 8, iArr, iArr2, iArr3, bArr);
            return C0205j.m147a(g.a(a, iArr2, 16));
        }
    }

    public static Locale m153b() {
        return Locale.getDefault();
    }

    public static boolean m154b(@NonNull Context context, @NonNull String str) {
        return ContextCompat.checkSelfPermission(context, str) == 0;
    }

    public static String m155c() {
        String str = Build.MANUFACTURER;
        String str2 = Build.MODEL;
        return str2.startsWith(str) ? C0205j.m161h(str2) : str.equalsIgnoreCase("HTC") ? "HTC " + str2 : C0205j.m161h(str) + " " + str2;
    }

    public static String m156c(String str) {
        String str2 = "";
        int i = 0;
        while (i < str.length()) {
            try {
                str2 = str2 + Integer.toHexString(str.charAt(i));
                i++;
            } catch (Exception e) {
            }
        }
        return str2;
    }

    public static boolean m157d(String str) {
        for (char isDigit : str.toCharArray()) {
            if (!Character.isDigit(isDigit)) {
                return false;
            }
        }
        return true;
    }

    public static String m158e(String str) {
        return Base64.encodeToString(str.getBytes(), 2);
    }

    public static Locale m159f(String str) {
        if (TextUtils.isEmpty(str)) {
            return Locale.getDefault();
        }
        String str2 = "";
        int indexOf = str.indexOf(95);
        if (indexOf >= 0) {
            String substring = str.substring(0, indexOf);
            str2 = str.substring(indexOf + 1);
            str = substring;
        }
        return new Locale(str.length() == 2 ? str.toLowerCase(Locale.US) : "", str2.length() == 2 ? str2.toUpperCase(Locale.US) : "");
    }

    public static String m160g(@NonNull String str) {
        if (str.length() <= 4) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder(str.length());
        int length = str.length() - 4;
        for (int i = 0; i < length; i++) {
            stringBuilder.append('*');
        }
        stringBuilder.append(str.substring(length));
        return stringBuilder.toString();
    }

    private static String m161h(String str) {
        if (!TextUtils.isEmpty(str)) {
            char[] toCharArray = str.toCharArray();
            str = "";
            int length = toCharArray.length;
            int i = 0;
            Object obj = 1;
            while (i < length) {
                String str2;
                char c = toCharArray[i];
                if (obj == null || !Character.isLetter(c)) {
                    if (Character.isWhitespace(c)) {
                        obj = 1;
                    }
                    str2 = str + c;
                } else {
                    str2 = str + Character.toUpperCase(c);
                    obj = null;
                }
                i++;
                str = str2;
            }
        }
        return str;
    }
}

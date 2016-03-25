package ru.ok.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.slidingmenu.SlidingMenuStrategy;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestField;

public final class DeviceUtils {
    private static String cpuModel;
    private static DeviceLayoutType deviceLayoutTypeBuffer;
    private static Pattern pattern;

    public enum DeviceLayoutType {
        SMALL("small"),
        BIG("big"),
        LARGE("large");
        
        private final String value;

        private DeviceLayoutType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    static {
        deviceLayoutTypeBuffer = null;
        cpuModel = null;
    }

    public static DeviceLayoutType getType(Context context) {
        if (deviceLayoutTypeBuffer == null) {
            deviceLayoutTypeBuffer = getTypeFromConfig(context);
        }
        return deviceLayoutTypeBuffer;
    }

    private static DeviceLayoutType getTypeFromConfig(Context context) {
        if (context == null) {
            context = OdnoklassnikiApplication.getContext();
        }
        try {
            String value = context.getResources().getString(2131165261);
            for (DeviceLayoutType type : DeviceLayoutType.values()) {
                if (TextUtils.equals(value, type.value)) {
                    return type;
                }
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        return DeviceLayoutType.SMALL;
    }

    public static int getStreamHighQualityPhotoWidth() {
        DisplayMetrics displayMetrics = OdnoklassnikiApplication.getContext().getResources().getDisplayMetrics();
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static float getDeviceAspectRatio() {
        DisplayMetrics displayMetrics = OdnoklassnikiApplication.getContext().getResources().getDisplayMetrics();
        return (((float) Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels)) * 1.0f) / ((float) Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    public static int getStreamLowQualityPhotoWidth() {
        return getStreamHighQualityPhotoWidth() / 5;
    }

    public static boolean isTablet(Context context) {
        return getType(context) == DeviceLayoutType.LARGE;
    }

    public static boolean isSmall(Context context) {
        return getType(context) == DeviceLayoutType.SMALL;
    }

    public static boolean hasNavigationBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return resources.getBoolean(id);
        }
        return (KeyCharacterMap.deviceHasKey(4) && KeyCharacterMap.deviceHasKey(3)) ? false : true;
    }

    public static final int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService("activity")).getMemoryClass();
    }

    public static boolean isSonyDevice() {
        return Build.MANUFACTURER.toLowerCase().contains("sony");
    }

    public static final boolean isShowTabbar() {
        return SlidingMenuStrategy.isNeedShowTabbar();
    }

    public static final boolean hasSdk(int sdkVer) {
        return VERSION.SDK_INT >= sdkVer;
    }

    public static RequestField getUserAvatarPicFieldName() {
        Context context = OdnoklassnikiApplication.getContext();
        if (context == null) {
            return FIELDS.PIC_190x190;
        }
        Resources resources = context.getResources();
        if (resources == null) {
            return FIELDS.PIC_190x190;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return FIELDS.PIC_190x190;
        }
        int densityDpi = displayMetrics.densityDpi;
        if (densityDpi < 240) {
            return FIELDS.PIC_190x190;
        }
        if (densityDpi < 320) {
            return FIELDS.PIC_190x190;
        }
        if (densityDpi < 480) {
            return FIELDS.PIC_190x190;
        }
        return FIELDS.PIC_190x190;
    }

    public static String getOSVersion() {
        return VERSION.RELEASE;
    }

    private static UUID getGenerateDeviceId(Context context) {
        UUID uuid;
        synchronized (DeviceUtils.class) {
            SharedPreferences prefs = context.getSharedPreferences("device_id.xml", 0);
            String id = prefs.getString("device_id", null);
            if (id != null) {
                uuid = UUID.fromString(id);
            } else {
                uuid = UUID.randomUUID();
                prefs.edit().putString("device_id", uuid.toString()).apply();
            }
        }
        return uuid;
    }

    private static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getSystemDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }

    public static String getTrackId(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getDeviceId(Context context) {
        StringBuilder builder = new StringBuilder();
        try {
            UUID uuid = getGenerateDeviceId(context);
            if (uuid != null) {
                builder.append("INSTALL_ID=").append(uuid);
                builder.append(';');
            }
        } catch (Exception e) {
            Logger.m172d("errror");
        }
        try {
            String deviceId = getSystemDeviceId(context);
            if (!TextUtils.isEmpty(deviceId)) {
                builder.append("DEVICE_ID=").append(deviceId);
                builder.append(';');
            }
        } catch (Exception e2) {
            Logger.m172d("error");
        }
        try {
            String androidId = getAndroidId(context);
            if (!TextUtils.isEmpty(androidId)) {
                builder.append("ANDROID_ID=").append(androidId);
                builder.append(';');
            }
        } catch (Exception e3) {
            Logger.m172d("error");
        }
        return builder.toString();
    }

    public static boolean getScreenSize(Activity activity, Point outSize) {
        if (activity == null || outSize == null) {
            return false;
        }
        WindowManager wm = activity.getWindowManager();
        if (wm == null) {
            return false;
        }
        Display display = wm.getDefaultDisplay();
        if (display == null) {
            return false;
        }
        boolean hasSize = false;
        if (VERSION.SDK_INT >= 17) {
            display.getRealSize(outSize);
            hasSize = true;
        } else if (VERSION.SDK_INT >= 14) {
            try {
                Method getRawH = Display.class.getMethod("getRawHeight", new Class[0]);
                outSize.x = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(display, new Object[0])).intValue();
                outSize.y = ((Integer) getRawH.invoke(display, new Object[0])).intValue();
                hasSize = true;
            } catch (Throwable e) {
                Logger.m186w(e, "Failed to get raw display size");
            }
        }
        if (!hasSize) {
            display.getSize(outSize);
        }
        return true;
    }

    public static boolean isLG() {
        return "LGE".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isE6710() {
        return "E6710".equalsIgnoreCase(Build.DEVICE);
    }

    public static boolean needSlidingMenuFixForWebView(Context context) {
        return VERSION.SDK_INT < 17 && !isSmall(context);
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == 1) {
            if (rotation == 0 || rotation == 3) {
                return 1;
            }
            return 9;
        } else if (orientation != 2) {
            return -1;
        } else {
            if (rotation == 0 || rotation == 1) {
                return 0;
            }
            return 8;
        }
    }

    public static String getCPUModel() {
        if (cpuModel == null) {
            cpuModel = getCPUModelValue();
        }
        return cpuModel;
    }

    private static Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile("Hardware\\s*:(.*)$");
        }
        return pattern;
    }

    private static String getCPUModelValue() {
        IOException e;
        Throwable th;
        if (new File("/proc/cpuinfo").exists()) {
            BufferedReader br = null;
            try {
                BufferedReader br2 = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                try {
                    Matcher matcher;
                    Pattern pattern = getPattern();
                    do {
                        String aLine = br2.readLine();
                        if (aLine != null) {
                            matcher = pattern.matcher(aLine);
                        } else if (br2 != null) {
                            try {
                                br2.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    } while (!matcher.matches());
                    String trim = matcher.group(1).trim();
                    if (br2 == null) {
                        return trim;
                    }
                    try {
                        br2.close();
                        return trim;
                    } catch (IOException e22) {
                        e22.printStackTrace();
                        return trim;
                    }
                } catch (IOException e3) {
                    e22 = e3;
                    br = br2;
                    try {
                        e22.printStackTrace();
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        return "";
                    } catch (Throwable th2) {
                        th = th2;
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e2222) {
                                e2222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    br = br2;
                    if (br != null) {
                        br.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e2222 = e4;
                e2222.printStackTrace();
                if (br != null) {
                    br.close();
                }
                return "";
            }
        }
        return "";
    }

    public static boolean isLargeTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 720;
    }
}

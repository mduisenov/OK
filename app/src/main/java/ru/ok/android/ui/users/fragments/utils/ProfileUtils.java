package ru.ok.android.ui.users.fragments.utils;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo.Location;

public final class ProfileUtils {
    public static void appendLocation(StringBuilder sb, Location location) {
        if (location != null) {
            if (!TextUtils.isEmpty(location.city)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(location.city);
            }
            if (!TextUtils.isEmpty(location.country)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(location.country);
            }
        }
    }

    public static void appendAgeString(Context context, StringBuilder sb, int age) {
        if (sb != null && age > 0) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(LocalizationManager.getString(context, StringUtils.plural((long) age, 2131165364, 2131165365, 2131165366), Integer.valueOf(age)));
        }
    }
}

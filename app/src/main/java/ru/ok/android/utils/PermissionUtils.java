package ru.ok.android.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public final class PermissionUtils {

    public interface Requester {
        void requestPermissions(@NonNull String... strArr);
    }

    private static final class FragmentRequester implements Requester {
        private final Fragment fragment;
        private final int requestCode;

        public FragmentRequester(Fragment fragment, int requestCode) {
            this.fragment = fragment;
            this.requestCode = requestCode;
        }

        public void requestPermissions(@NonNull String... permissions) {
            this.fragment.requestPermissions(permissions, this.requestCode);
        }
    }

    public static int checkSelfPermission(@NonNull Context context, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                return -1;
            }
        }
        return 0;
    }

    public static int checkAnySelfPermission(@NonNull Context context, @NonNull String... permissions) {
        if (permissions.length == 0) {
            return 0;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == 0) {
                return 0;
            }
        }
        return -1;
    }

    public static int getGrantResult(@NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return 0;
        }
        for (int grantResult : grantResults) {
            if (grantResult != 0) {
                return -1;
            }
        }
        return 0;
    }

    public static Requester createRequester(Fragment fragment, int requestCode) {
        return new FragmentRequester(fragment, requestCode);
    }
}

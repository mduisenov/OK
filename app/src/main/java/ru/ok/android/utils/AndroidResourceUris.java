package ru.ok.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

public final class AndroidResourceUris {
    public static Uri getAndroidResourceUri(@NonNull Context context, @AnyRes int resId) {
        return new Builder().scheme("android.resource").authority(context.getResources().getResourcePackageName(resId)).appendEncodedPath(Integer.toString(resId)).build();
    }

    public static Uri getSymbolicAndroidResourceUri(@NonNull Context context, @AnyRes int resId) {
        Resources res = context.getResources();
        String packageName = res.getResourcePackageName(resId);
        String typeName = res.getResourceTypeName(resId);
        return new Builder().scheme("android.resource").authority(packageName).appendEncodedPath(typeName).appendEncodedPath(res.getResourceEntryName(resId)).build();
    }
}

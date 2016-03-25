package ru.ok.android.services.utils.users;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.RequiresPermission;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;

public class LocationUtils {
    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public static Location getLastLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService("location");
        if (manager == null) {
            Logger.m184w("LocationManager is null");
            return null;
        }
        long bestTime = 0;
        Location bestLocation = null;
        String bestProvider = null;
        for (String provider : manager.getAllProviders()) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null && location.getTime() > bestTime) {
                bestTime = location.getTime();
                bestLocation = location;
                bestProvider = provider;
            }
        }
        Logger.m173d("Best location: %s by provider: %s", bestLocation, bestProvider);
        return bestLocation;
    }

    public static Location getLastLocationIfPermitted(Context context) {
        if (PermissionUtils.checkAnySelfPermission(context, "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            return null;
        }
        return getLastLocation(context);
    }
}

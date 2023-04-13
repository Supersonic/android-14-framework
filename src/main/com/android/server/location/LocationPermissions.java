package com.android.server.location;

import android.content.Context;
import android.os.Binder;
/* loaded from: classes.dex */
public final class LocationPermissions {
    public static boolean checkLocationPermission(int i, int i2) {
        return i >= i2;
    }

    public static String asPermission(int i) {
        if (i != 1) {
            if (i == 2) {
                return "android.permission.ACCESS_FINE_LOCATION";
            }
            throw new IllegalArgumentException();
        }
        return "android.permission.ACCESS_COARSE_LOCATION";
    }

    public static int asAppOp(int i) {
        if (i != 1) {
            if (i == 2) {
                return 1;
            }
            throw new IllegalArgumentException();
        }
        return 0;
    }

    public static void enforceCallingOrSelfLocationPermission(Context context, int i) {
        enforceLocationPermission(Binder.getCallingUid(), getPermissionLevel(context, Binder.getCallingUid(), Binder.getCallingPid()), i);
    }

    public static void enforceLocationPermission(int i, int i2, int i3) {
        if (checkLocationPermission(i2, i3)) {
            return;
        }
        if (i3 != 1) {
            if (i3 != 2) {
                return;
            }
            throw new SecurityException("uid " + i + " does not have android.permission.ACCESS_FINE_LOCATION.");
        }
        throw new SecurityException("uid " + i + " does not have android.permission.ACCESS_COARSE_LOCATION or android.permission.ACCESS_FINE_LOCATION.");
    }

    public static void enforceCallingOrSelfBypassPermission(Context context) {
        enforceBypassPermission(context, Binder.getCallingUid(), Binder.getCallingPid());
    }

    public static void enforceBypassPermission(Context context, int i, int i2) {
        if (context.checkPermission("android.permission.LOCATION_BYPASS", i2, i) == 0) {
            return;
        }
        throw new SecurityException("uid" + i + " does not have android.permission.LOCATION_BYPASS.");
    }

    public static boolean checkCallingOrSelfLocationPermission(Context context, int i) {
        return checkLocationPermission(getCallingOrSelfPermissionLevel(context), i);
    }

    public static int getCallingOrSelfPermissionLevel(Context context) {
        return getPermissionLevel(context, Binder.getCallingUid(), Binder.getCallingPid());
    }

    public static int getPermissionLevel(Context context, int i, int i2) {
        if (context.checkPermission("android.permission.ACCESS_FINE_LOCATION", i2, i) == 0) {
            return 2;
        }
        return context.checkPermission("android.permission.ACCESS_COARSE_LOCATION", i2, i) == 0 ? 1 : 0;
    }
}

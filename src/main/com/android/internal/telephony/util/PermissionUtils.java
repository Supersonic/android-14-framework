package com.android.internal.telephony.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Binder;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class PermissionUtils {
    public static boolean checkAnyPermissionOf(Context context, String... strArr) {
        for (String str : strArr) {
            if (context.checkCallingOrSelfPermission(str) == 0) {
                return true;
            }
        }
        return false;
    }

    public static void enforceAnyPermissionOf(Context context, String... strArr) {
        if (checkAnyPermissionOf(context, strArr)) {
            return;
        }
        throw new SecurityException("Requires one of the following permissions: " + String.join(", ", strArr) + ".");
    }

    public static void enforceNetworkStackPermission(Context context) {
        enforceNetworkStackPermissionOr(context, new String[0]);
    }

    public static void enforceNetworkStackPermissionOr(Context context, String... strArr) {
        ArrayList arrayList = new ArrayList(Arrays.asList(strArr));
        arrayList.add("android.permission.NETWORK_STACK");
        arrayList.add("android.permission.MAINLINE_NETWORK_STACK");
        enforceAnyPermissionOf(context, (String[]) arrayList.toArray(new String[0]));
    }

    public static void enforceRestrictedNetworkPermission(Context context, String str) {
        context.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS", str);
    }

    public static void enforceAccessNetworkStatePermission(Context context, String str) {
        context.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", str);
    }

    public static boolean checkDumpPermission(Context context, String str, PrintWriter printWriter) {
        if (context.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            printWriter.println("Permission Denial: can't dump " + str + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.DUMP permission");
            return false;
        }
        return true;
    }

    public static void enforceSystemFeature(Context context, String str, String str2) {
        if (context.getPackageManager().hasSystemFeature(str)) {
            return;
        }
        if (str2 == null) {
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException(str2);
    }

    public static List<String> getGrantedPermissions(PackageInfo packageInfo) {
        if (packageInfo.requestedPermissions == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(packageInfo.requestedPermissions.length);
        int i = 0;
        while (true) {
            String[] strArr = packageInfo.requestedPermissions;
            if (i >= strArr.length) {
                return arrayList;
            }
            if ((packageInfo.requestedPermissionsFlags[i] & 2) != 0) {
                arrayList.add(strArr[i]);
            }
            i++;
        }
    }
}

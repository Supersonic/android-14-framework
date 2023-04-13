package com.android.net.module.util;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.location.LocationManager;
import android.net.NetworkStack;
import android.p008os.Binder;
import android.p008os.UserHandle;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes5.dex */
public class LocationPermissionChecker {
    public static final int ERROR_LOCATION_MODE_OFF = 1;
    public static final int ERROR_LOCATION_PERMISSION_MISSING = 2;
    public static final int SUCCEEDED = 0;
    private static final String TAG = "LocationPermissionChecker";
    private final AppOpsManager mAppOpsManager;
    private final Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface LocationPermissionCheckStatus {
    }

    public LocationPermissionChecker(Context context) {
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    public boolean checkLocationPermission(String pkgName, String featureId, int uid, String message) {
        return checkLocationPermissionInternal(pkgName, featureId, uid, message) == 0;
    }

    public int checkLocationPermissionWithDetailInfo(String pkgName, String featureId, int uid, String message) {
        int result = checkLocationPermissionInternal(pkgName, featureId, uid, message);
        switch (result) {
            case 1:
                Log.m110e(TAG, "Location mode is disabled for the device");
                break;
            case 2:
                Log.m110e(TAG, "UID " + uid + " has no location permission");
                break;
        }
        return result;
    }

    public void enforceLocationPermission(String pkgName, String featureId, int uid, String message) throws SecurityException {
        int result = checkLocationPermissionInternal(pkgName, featureId, uid, message);
        switch (result) {
            case 1:
                throw new SecurityException("Location mode is disabled for the device");
            case 2:
                throw new SecurityException("UID " + uid + " has no location permission");
            default:
                return;
        }
    }

    private int checkLocationPermissionInternal(String pkgName, String featureId, int uid, String message) {
        checkPackage(uid, pkgName);
        if (checkNetworkSettingsPermission(uid) || checkNetworkSetupWizardPermission(uid) || checkNetworkStackPermission(uid) || checkMainlineNetworkStackPermission(uid)) {
            return 0;
        }
        if (isLocationModeEnabled()) {
            return !checkCallersLocationPermission(pkgName, featureId, uid, true, message) ? 2 : 0;
        }
        return 1;
    }

    public boolean checkCallersLocationPermission(String pkgName, String featureId, int uid, boolean coarseForTargetSdkLessThanQ, String message) {
        String permissionType;
        boolean isTargetSdkLessThanQ = isTargetSdkLessThan(pkgName, 29, uid);
        if (coarseForTargetSdkLessThanQ && isTargetSdkLessThanQ) {
            permissionType = Manifest.C0000permission.ACCESS_COARSE_LOCATION;
        } else {
            permissionType = Manifest.C0000permission.ACCESS_FINE_LOCATION;
        }
        if (getUidPermission(permissionType, uid) == -1) {
            return false;
        }
        boolean isFineLocationAllowed = noteAppOpAllowed(AppOpsManager.OPSTR_FINE_LOCATION, pkgName, featureId, uid, message);
        if (isFineLocationAllowed) {
            return true;
        }
        if (coarseForTargetSdkLessThanQ && isTargetSdkLessThanQ) {
            return noteAppOpAllowed(AppOpsManager.OPSTR_COARSE_LOCATION, pkgName, featureId, uid, message);
        }
        return false;
    }

    public boolean isLocationModeEnabled() {
        LocationManager LocationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
        try {
            return LocationManager.isLocationEnabledForUser(UserHandle.m145of(getCurrentUser()));
        } catch (Exception e) {
            Log.m109e(TAG, "Failure to get location mode via API, falling back to settings", e);
            return false;
        }
    }

    private boolean isTargetSdkLessThan(String packageName, int versionCode, int callingUid) {
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, 0, UserHandle.getUserHandleForUid(callingUid)).targetSdkVersion < versionCode) {
                Binder.restoreCallingIdentity(ident);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
        Binder.restoreCallingIdentity(ident);
        return false;
    }

    private boolean noteAppOpAllowed(String op, String pkgName, String featureId, int uid, String message) {
        return this.mAppOpsManager.noteOp(op, uid, pkgName, featureId, message) == 0;
    }

    private void checkPackage(int uid, String pkgName) throws SecurityException {
        if (pkgName == null) {
            throw new SecurityException("Checking UID " + uid + " but Package Name is Null");
        }
        this.mAppOpsManager.checkPackage(uid, pkgName);
    }

    protected int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }

    private int getUidPermission(String permissionType, int uid) {
        return this.mContext.checkPermission(permissionType, -1, uid);
    }

    public boolean checkNetworkSettingsPermission(int uid) {
        return getUidPermission(Manifest.C0000permission.NETWORK_SETTINGS, uid) == 0;
    }

    public boolean checkNetworkSetupWizardPermission(int uid) {
        return getUidPermission(Manifest.C0000permission.NETWORK_SETUP_WIZARD, uid) == 0;
    }

    public boolean checkNetworkStackPermission(int uid) {
        return getUidPermission(Manifest.C0000permission.NETWORK_STACK, uid) == 0;
    }

    public boolean checkMainlineNetworkStackPermission(int uid) {
        return getUidPermission(NetworkStack.PERMISSION_MAINLINE_NETWORK_STACK, uid) == 0;
    }
}

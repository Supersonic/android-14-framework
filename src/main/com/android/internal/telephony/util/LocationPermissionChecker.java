package com.android.internal.telephony.util;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.UserHandle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.android.net.module.annotation.VisibleForTesting;
@RequiresApi(30)
/* loaded from: classes.dex */
public class LocationPermissionChecker {
    public static final int ERROR_LOCATION_MODE_OFF = 1;
    public static final int ERROR_LOCATION_PERMISSION_MISSING = 2;
    public static final int SUCCEEDED = 0;
    private final AppOpsManager mAppOpsManager;
    private final Context mContext;

    public LocationPermissionChecker(Context context) {
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    public boolean checkLocationPermission(String str, String str2, int i, String str3) {
        return checkLocationPermissionInternal(str, str2, i, str3) == 0;
    }

    public int checkLocationPermissionWithDetailInfo(String str, String str2, int i, String str3) {
        int checkLocationPermissionInternal = checkLocationPermissionInternal(str, str2, i, str3);
        if (checkLocationPermissionInternal == 1) {
            Log.e("LocationPermissionChecker", "Location mode is disabled for the device");
        } else if (checkLocationPermissionInternal == 2) {
            Log.e("LocationPermissionChecker", "UID " + i + " has no location permission");
        }
        return checkLocationPermissionInternal;
    }

    public void enforceLocationPermission(String str, String str2, int i, String str3) throws SecurityException {
        int checkLocationPermissionInternal = checkLocationPermissionInternal(str, str2, i, str3);
        if (checkLocationPermissionInternal == 1) {
            throw new SecurityException("Location mode is disabled for the device");
        }
        if (checkLocationPermissionInternal != 2) {
            return;
        }
        throw new SecurityException("UID " + i + " has no location permission");
    }

    private int checkLocationPermissionInternal(String str, String str2, int i, String str3) {
        checkPackage(i, str);
        if (!checkNetworkSettingsPermission(i) && !checkNetworkSetupWizardPermission(i) && !checkNetworkStackPermission(i) && !checkMainlineNetworkStackPermission(i)) {
            if (!isLocationModeEnabled()) {
                return 1;
            }
            if (!checkCallersLocationPermission(str, str2, i, true, str3)) {
                return 2;
            }
        }
        return 0;
    }

    public boolean checkCallersLocationPermission(String str, String str2, int i, boolean z, String str3) {
        boolean isTargetSdkLessThan = isTargetSdkLessThan(str, 29, i);
        if (getUidPermission((z && isTargetSdkLessThan) ? "android.permission.ACCESS_COARSE_LOCATION" : "android.permission.ACCESS_FINE_LOCATION", i) == -1) {
            return false;
        }
        if (noteAppOpAllowed("android:fine_location", str, str2, i, str3)) {
            return true;
        }
        if (z && isTargetSdkLessThan) {
            return noteAppOpAllowed("android:coarse_location", str, str2, i, str3);
        }
        return false;
    }

    public boolean isLocationModeEnabled() {
        try {
            return ((LocationManager) this.mContext.getSystemService(LocationManager.class)).isLocationEnabledForUser(UserHandle.of(getCurrentUser()));
        } catch (Exception e) {
            Log.e("LocationPermissionChecker", "Failure to get location mode via API, falling back to settings", e);
            return false;
        }
    }

    private boolean isTargetSdkLessThan(String str, int i, int i2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserHandleForUid(i2)).targetSdkVersion < i) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
        return false;
    }

    private boolean noteAppOpAllowed(String str, String str2, String str3, int i, String str4) {
        return this.mAppOpsManager.noteOp(str, i, str2, str3, str4) == 0;
    }

    private void checkPackage(int i, String str) throws SecurityException {
        if (str == null) {
            throw new SecurityException("Checking UID " + i + " but Package Name is Null");
        }
        this.mAppOpsManager.checkPackage(i, str);
    }

    @VisibleForTesting
    protected int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }

    private int getUidPermission(String str, int i) {
        return this.mContext.checkPermission(str, -1, i);
    }

    public boolean checkNetworkSettingsPermission(int i) {
        return getUidPermission("android.permission.NETWORK_SETTINGS", i) == 0;
    }

    public boolean checkNetworkSetupWizardPermission(int i) {
        return getUidPermission("android.permission.NETWORK_SETUP_WIZARD", i) == 0;
    }

    public boolean checkNetworkStackPermission(int i) {
        return getUidPermission("android.permission.NETWORK_STACK", i) == 0;
    }

    public boolean checkMainlineNetworkStackPermission(int i) {
        return getUidPermission("android.permission.MAINLINE_NETWORK_STACK", i) == 0;
    }
}

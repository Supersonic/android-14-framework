package android.telephony;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.location.LocationManager;
import android.p008os.Binder;
import android.p008os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.telephony.util.TelephonyUtils;
/* loaded from: classes3.dex */
public final class LocationAccessPolicy {
    private static final boolean DBG = false;
    public static final int MAX_SDK_FOR_ANY_ENFORCEMENT = 10000;
    private static final String TAG = "LocationAccessPolicy";

    /* loaded from: classes3.dex */
    public enum LocationPermissionResult {
        ALLOWED,
        DENIED_SOFT,
        DENIED_HARD
    }

    /* loaded from: classes3.dex */
    public static class LocationPermissionQuery {
        public final String callingFeatureId;
        public final String callingPackage;
        public final int callingPid;
        public final int callingUid;
        public final boolean logAsInfo;
        public final String method;
        public final int minSdkVersionForCoarse;
        public final int minSdkVersionForFine;

        private LocationPermissionQuery(String callingPackage, String callingFeatureId, int callingUid, int callingPid, int minSdkVersionForCoarse, int minSdkVersionForFine, boolean logAsInfo, String method) {
            this.callingPackage = callingPackage;
            this.callingFeatureId = callingFeatureId;
            this.callingUid = callingUid;
            this.callingPid = callingPid;
            this.minSdkVersionForCoarse = minSdkVersionForCoarse;
            this.minSdkVersionForFine = minSdkVersionForFine;
            this.logAsInfo = logAsInfo;
            this.method = method;
        }

        /* loaded from: classes3.dex */
        public static class Builder {
            private String mCallingFeatureId;
            private String mCallingPackage;
            private int mCallingPid;
            private int mCallingUid;
            private String mMethod;
            private int mMinSdkVersionForCoarse = -1;
            private int mMinSdkVersionForFine = -1;
            private int mMinSdkVersionForEnforcement = -1;
            private boolean mLogAsInfo = false;

            public Builder setCallingPackage(String callingPackage) {
                this.mCallingPackage = callingPackage;
                return this;
            }

            public Builder setCallingFeatureId(String callingFeatureId) {
                this.mCallingFeatureId = callingFeatureId;
                return this;
            }

            public Builder setCallingUid(int callingUid) {
                this.mCallingUid = callingUid;
                return this;
            }

            public Builder setCallingPid(int callingPid) {
                this.mCallingPid = callingPid;
                return this;
            }

            public Builder setMinSdkVersionForCoarse(int minSdkVersionForCoarse) {
                this.mMinSdkVersionForCoarse = minSdkVersionForCoarse;
                return this;
            }

            public Builder setMinSdkVersionForFine(int minSdkVersionForFine) {
                this.mMinSdkVersionForFine = minSdkVersionForFine;
                return this;
            }

            public Builder setMinSdkVersionForEnforcement(int minSdkVersionForEnforcement) {
                this.mMinSdkVersionForEnforcement = minSdkVersionForEnforcement;
                return this;
            }

            public Builder setMethod(String method) {
                this.mMethod = method;
                return this;
            }

            public Builder setLogAsInfo(boolean logAsInfo) {
                this.mLogAsInfo = logAsInfo;
                return this;
            }

            public LocationPermissionQuery build() {
                int i;
                int i2 = this.mMinSdkVersionForCoarse;
                if (i2 < 0 || (i = this.mMinSdkVersionForFine) < 0) {
                    throw new IllegalArgumentException("Must specify min sdk versions for enforcement for both coarse and fine permissions");
                }
                if (i > 1 && i2 > 1 && this.mMinSdkVersionForEnforcement != Math.min(i2, i)) {
                    throw new IllegalArgumentException("setMinSdkVersionForEnforcement must be called.");
                }
                int i3 = this.mMinSdkVersionForFine;
                int i4 = this.mMinSdkVersionForCoarse;
                if (i3 < i4) {
                    throw new IllegalArgumentException("Since fine location permission includes access to coarse location, the min sdk level for enforcement of the fine location permission must not be less than the min sdk level for enforcement of the coarse location permission.");
                }
                return new LocationPermissionQuery(this.mCallingPackage, this.mCallingFeatureId, this.mCallingUid, this.mCallingPid, i4, i3, this.mLogAsInfo, this.mMethod);
            }
        }
    }

    private static void logError(Context context, LocationPermissionQuery query, String errorMsg) {
        if (query.logAsInfo) {
            Log.m108i(TAG, errorMsg);
            return;
        }
        Log.m110e(TAG, errorMsg);
        try {
            if (TelephonyUtils.IS_DEBUGGABLE) {
                Toast.makeText(context, errorMsg, 0).show();
            }
        } catch (Throwable th) {
        }
    }

    private static LocationPermissionResult appOpsModeToPermissionResult(int appOpsMode) {
        switch (appOpsMode) {
            case 0:
                return LocationPermissionResult.ALLOWED;
            case 1:
            default:
                return LocationPermissionResult.DENIED_SOFT;
            case 2:
                return LocationPermissionResult.DENIED_HARD;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static String getAppOpsString(String manifestPermission) {
        char c;
        switch (manifestPermission.hashCode()) {
            case -1888586689:
                if (manifestPermission.equals(Manifest.C0000permission.ACCESS_FINE_LOCATION)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -63024214:
                if (manifestPermission.equals(Manifest.C0000permission.ACCESS_COARSE_LOCATION)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return AppOpsManager.OPSTR_FINE_LOCATION;
            case 1:
                return AppOpsManager.OPSTR_COARSE_LOCATION;
            default:
                return null;
        }
    }

    private static LocationPermissionResult checkAppLocationPermissionHelper(Context context, LocationPermissionQuery query, String permissionToCheck) {
        String locationTypeForLog = Manifest.C0000permission.ACCESS_FINE_LOCATION.equals(permissionToCheck) ? "fine" : "coarse";
        boolean hasManifestPermission = checkManifestPermission(context, query.callingPid, query.callingUid, permissionToCheck);
        if (hasManifestPermission) {
            int appOpMode = ((AppOpsManager) context.getSystemService(AppOpsManager.class)).noteOpNoThrow(getAppOpsString(permissionToCheck), query.callingUid, query.callingPackage, query.callingFeatureId, (String) null);
            if (appOpMode == 0) {
                return LocationPermissionResult.ALLOWED;
            }
            Log.m108i(TAG, query.callingPackage + " is aware of " + locationTypeForLog + " but the app-ops permission is specifically denied.");
            return appOpsModeToPermissionResult(appOpMode);
        }
        int minSdkVersion = Manifest.C0000permission.ACCESS_FINE_LOCATION.equals(permissionToCheck) ? query.minSdkVersionForFine : query.minSdkVersionForCoarse;
        if (minSdkVersion > 10000) {
            String errorMsg = "Allowing " + query.callingPackage + " " + locationTypeForLog + " because we're not enforcing API " + minSdkVersion + " yet. Please fix this app because it will break in the future. Called from " + query.method;
            logError(context, query, errorMsg);
            return null;
        }
        String errorMsg2 = query.callingPackage;
        if (!isAppAtLeastSdkVersion(context, errorMsg2, minSdkVersion)) {
            String errorMsg3 = "Allowing " + query.callingPackage + " " + locationTypeForLog + " because it doesn't target API " + minSdkVersion + " yet. Please fix this app. Called from " + query.method;
            logError(context, query, errorMsg3);
            return null;
        }
        return LocationPermissionResult.DENIED_HARD;
    }

    public static LocationPermissionResult checkLocationPermission(Context context, LocationPermissionQuery query) {
        LocationPermissionResult resultForCoarse;
        LocationPermissionResult resultForFine;
        if (query.callingUid == 1001 || query.callingUid == 1000 || query.callingUid == 1073 || query.callingUid == 0) {
            return LocationPermissionResult.ALLOWED;
        }
        if (!checkSystemLocationAccess(context, query.callingUid, query.callingPid, query.callingPackage)) {
            return LocationPermissionResult.DENIED_SOFT;
        }
        if (query.minSdkVersionForFine < Integer.MAX_VALUE && (resultForFine = checkAppLocationPermissionHelper(context, query, Manifest.C0000permission.ACCESS_FINE_LOCATION)) != null) {
            return resultForFine;
        }
        if (query.minSdkVersionForCoarse < Integer.MAX_VALUE && (resultForCoarse = checkAppLocationPermissionHelper(context, query, Manifest.C0000permission.ACCESS_COARSE_LOCATION)) != null) {
            return resultForCoarse;
        }
        return LocationPermissionResult.ALLOWED;
    }

    private static boolean checkManifestPermission(Context context, int pid, int uid, String permissionToCheck) {
        return context.checkPermission(permissionToCheck, pid, uid) == 0;
    }

    private static boolean checkSystemLocationAccess(Context context, int uid, int pid, String callingPackage) {
        if (isLocationModeEnabled(context, UserHandle.getUserHandleForUid(uid).getIdentifier()) || isLocationBypassAllowed(context, callingPackage)) {
            return isCurrentProfile(context, uid) || checkInteractAcrossUsersFull(context, pid, uid);
        }
        return false;
    }

    public static boolean isLocationModeEnabled(Context context, int userId) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LocationManager.class);
        if (locationManager == null) {
            Log.m104w(TAG, "Couldn't get location manager, denying location access");
            return false;
        }
        return locationManager.isLocationEnabledForUser(UserHandle.m145of(userId));
    }

    private static boolean isLocationBypassAllowed(Context context, String callingPackage) {
        String[] locationBypassPackages;
        for (String bypassPackage : getLocationBypassPackages(context)) {
            if (callingPackage.equals(bypassPackage)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getLocationBypassPackages(Context context) {
        return context.getResources().getStringArray(C4057R.array.config_serviceStateLocationAllowedPackages);
    }

    private static boolean checkInteractAcrossUsersFull(Context context, int pid, int uid) {
        return checkManifestPermission(context, pid, uid, Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL);
    }

    private static boolean isCurrentProfile(Context context, int uid) {
        long token = Binder.clearCallingIdentity();
        try {
            if (UserHandle.getUserHandleForUid(uid).getIdentifier() == ActivityManager.getCurrentUser()) {
                Binder.restoreCallingIdentity(token);
                return true;
            }
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
            if (activityManager != null) {
                return activityManager.isProfileForeground(UserHandle.getUserHandleForUid(ActivityManager.getCurrentUser()));
            }
            Binder.restoreCallingIdentity(token);
            return false;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private static boolean isAppAtLeastSdkVersion(Context context, String pkgName, int sdkVersion) {
        if (context.getPackageManager().getApplicationInfo(pkgName, 0).targetSdkVersion < sdkVersion) {
            return false;
        }
        return true;
    }
}

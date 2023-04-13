package com.android.internal.telephony;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.p008os.Binder;
import android.p008os.UserHandle;
import android.permission.LegacyPermissionManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public final class TelephonyPermissions {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "TelephonyPermissions";
    private static final String PROPERTY_DEVICE_IDENTIFIER_ACCESS_RESTRICTIONS_DISABLED = "device_identifier_access_restrictions_disabled";
    private static final Map<String, Set<String>> sReportedDeviceIDPackages = new HashMap();

    private TelephonyPermissions() {
    }

    public static boolean checkCallingOrSelfReadPhoneState(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        return checkReadPhoneState(context, subId, Binder.getCallingPid(), Binder.getCallingUid(), callingPackage, callingFeatureId, message);
    }

    public static boolean checkCallingOrSelfReadPhoneStateNoThrow(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        try {
            return checkCallingOrSelfReadPhoneState(context, subId, callingPackage, callingFeatureId, message);
        } catch (SecurityException e) {
            return false;
        }
    }

    public static boolean checkInternetPermissionNoThrow(Context context, String message) {
        try {
            context.enforcePermission(Manifest.C0000permission.INTERNET, Binder.getCallingPid(), Binder.getCallingUid(), message);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    public static boolean checkCallingOrSelfReadNonDangerousPhoneStateNoThrow(Context context, String message) {
        try {
            context.enforcePermission(Manifest.C0000permission.READ_BASIC_PHONE_STATE, Binder.getCallingPid(), Binder.getCallingUid(), message);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    public static boolean checkReadPhoneState(Context context, int subId, int pid, int uid, String callingPackage, String callingFeatureId, String message) {
        try {
            context.enforcePermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE, pid, uid, message);
            return true;
        } catch (SecurityException e) {
            try {
                context.enforcePermission(Manifest.C0000permission.READ_PHONE_STATE, pid, uid, message);
                AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                return appOps.noteOp(AppOpsManager.OPSTR_READ_PHONE_STATE, uid, callingPackage, callingFeatureId, (String) null) == 0;
            } catch (SecurityException phoneStateException) {
                if (SubscriptionManager.isValidSubscriptionId(subId)) {
                    enforceCarrierPrivilege(context, subId, uid, message);
                    return true;
                }
                throw phoneStateException;
            }
        }
    }

    public static boolean checkCarrierPrivilegeForSubId(Context context, int subId) {
        return SubscriptionManager.isValidSubscriptionId(subId) && getCarrierPrivilegeStatus(context, subId, Binder.getCallingUid()) == 1;
    }

    public static boolean checkReadPhoneStateOnAnyActiveSub(Context context, int pid, int uid, String callingPackage, String callingFeatureId, String message) {
        try {
            context.enforcePermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE, pid, uid, message);
            return true;
        } catch (SecurityException e) {
            try {
                context.enforcePermission(Manifest.C0000permission.READ_PHONE_STATE, pid, uid, message);
                AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                return appOps.noteOp(AppOpsManager.OPSTR_READ_PHONE_STATE, uid, callingPackage, callingFeatureId, (String) null) == 0;
            } catch (SecurityException e2) {
                return checkCarrierPrivilegeForAnySubId(context, uid);
            }
        }
    }

    public static boolean checkCallingOrSelfReadDeviceIdentifiers(Context context, String callingPackage, String callingFeatureId, String message) {
        return checkCallingOrSelfReadDeviceIdentifiers(context, -1, callingPackage, callingFeatureId, message);
    }

    public static boolean checkCallingOrSelfReadDeviceIdentifiers(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        if (checkCallingOrSelfUseIccAuthWithDeviceIdentifier(context, callingPackage, callingFeatureId, message)) {
            return true;
        }
        return checkPrivilegedReadPermissionOrCarrierPrivilegePermission(context, subId, callingPackage, callingFeatureId, message, true, true);
    }

    public static boolean checkCallingOrSelfReadSubscriberIdentifiers(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        return checkCallingOrSelfReadSubscriberIdentifiers(context, subId, callingPackage, callingFeatureId, message, true);
    }

    public static boolean checkCallingOrSelfReadSubscriberIdentifiers(Context context, int subId, String callingPackage, String callingFeatureId, String message, boolean reportFailure) {
        if (checkCallingOrSelfUseIccAuthWithDeviceIdentifier(context, callingPackage, callingFeatureId, message)) {
            return true;
        }
        return checkPrivilegedReadPermissionOrCarrierPrivilegePermission(context, subId, callingPackage, callingFeatureId, message, false, reportFailure);
    }

    private static void throwSecurityExceptionAsUidDoesNotHaveAccess(String message, int uid) {
        throw new SecurityException(message + ": The uid " + uid + " does not meet the requirements to access device identifiers.");
    }

    private static boolean checkPrivilegedReadPermissionOrCarrierPrivilegePermission(Context context, int subId, String callingPackage, String callingFeatureId, String message, boolean allowCarrierPrivilegeOnAnySub, boolean reportFailure) {
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        if (checkCarrierPrivilegeForSubId(context, subId)) {
            return true;
        }
        if (allowCarrierPrivilegeOnAnySub && checkCarrierPrivilegeForAnySubId(context, uid)) {
            return true;
        }
        LegacyPermissionManager permissionManager = (LegacyPermissionManager) context.getSystemService(Context.LEGACY_PERMISSION_SERVICE);
        try {
        } catch (SecurityException e) {
            throwSecurityExceptionAsUidDoesNotHaveAccess(message, uid);
        }
        if (permissionManager.checkDeviceIdentifierAccess(callingPackage, message, callingFeatureId, pid, uid) == 0) {
            return true;
        }
        if (reportFailure) {
            return reportAccessDeniedToReadIdentifiers(context, subId, pid, uid, callingPackage, message);
        }
        return false;
    }

    private static boolean reportAccessDeniedToReadIdentifiers(Context context, int subId, int pid, int uid, String callingPackage, String message) {
        Set invokedMethods;
        ApplicationInfo callingPackageInfo = null;
        try {
            callingPackageInfo = context.getPackageManager().getApplicationInfoAsUser(callingPackage, 0, UserHandle.getUserHandleForUid(uid));
        } catch (PackageManager.NameNotFoundException e) {
            Log.m109e(LOG_TAG, "Exception caught obtaining package info for package " + callingPackage, e);
        }
        Map<String, Set<String>> map = sReportedDeviceIDPackages;
        boolean packageReported = map.containsKey(callingPackage);
        if (!packageReported || !map.get(callingPackage).contains(message)) {
            if (!packageReported) {
                invokedMethods = new HashSet();
                map.put(callingPackage, invokedMethods);
            } else {
                invokedMethods = map.get(callingPackage);
            }
            invokedMethods.add(message);
            TelephonyCommonStatsLog.write(172, callingPackage, message, false, false);
        }
        Log.m104w(LOG_TAG, "reportAccessDeniedToReadIdentifiers:" + callingPackage + ":" + message + ":" + subId);
        if (callingPackageInfo != null && callingPackageInfo.targetSdkVersion < 29 && (context.checkPermission(Manifest.C0000permission.READ_PHONE_STATE, pid, uid) == 0 || checkCarrierPrivilegeForSubId(context, subId))) {
            return false;
        }
        throwSecurityExceptionAsUidDoesNotHaveAccess(message, uid);
        return true;
    }

    public static boolean checkCallingOrSelfUseIccAuthWithDeviceIdentifier(Context context, String callingPackage, String callingFeatureId, String message) {
        if (callingPackage == null) {
            return false;
        }
        int callingUid = Binder.getCallingUid();
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int opMode = appOps.noteOpNoThrow(AppOpsManager.OPSTR_USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER, callingUid, callingPackage, callingFeatureId, message);
        switch (opMode) {
            case 0:
            case 4:
                return true;
            case 1:
            case 2:
            default:
                return false;
            case 3:
                if (context.checkCallingOrSelfPermission(Manifest.C0000permission.USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER) != 0) {
                    return false;
                }
                return true;
        }
    }

    public static boolean checkReadCallLog(Context context, int subId, int pid, int uid, String callingPackage, String callingPackageName) {
        if (context.checkPermission(Manifest.C0000permission.READ_CALL_LOG, pid, uid) != 0) {
            if (SubscriptionManager.isValidSubscriptionId(subId)) {
                enforceCarrierPrivilege(context, subId, uid, "readCallLog");
                return true;
            }
            return false;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return appOps.noteOp(AppOpsManager.OPSTR_READ_CALL_LOG, uid, callingPackage, callingPackageName, (String) null) == 0;
    }

    public static boolean checkCallingOrSelfReadPhoneNumber(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        return checkReadPhoneNumber(context, subId, Binder.getCallingPid(), Binder.getCallingUid(), callingPackage, callingFeatureId, message);
    }

    public static boolean checkReadPhoneNumber(Context context, int subId, int pid, int uid, String callingPackage, String callingFeatureId, String message) {
        LegacyPermissionManager permissionManager = (LegacyPermissionManager) context.getSystemService(Context.LEGACY_PERMISSION_SERVICE);
        int permissionResult = permissionManager.checkPhoneNumberAccess(callingPackage, message, callingFeatureId, pid, uid);
        if (permissionResult == 0) {
            return true;
        }
        if (SubscriptionManager.isValidSubscriptionId(subId) && getCarrierPrivilegeStatus(context, subId, uid) == 1) {
            return true;
        }
        if (permissionResult == 1) {
            return false;
        }
        throw new SecurityException(message + ": Neither user " + uid + " nor current process has " + Manifest.C0000permission.READ_PHONE_STATE + ", " + Manifest.C0000permission.READ_SMS + ", or " + Manifest.C0000permission.READ_PHONE_NUMBERS);
    }

    public static void enforceCallingOrSelfModifyPermissionOrCarrierPrivilege(Context context, int subId, String message) {
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.MODIFY_PHONE_STATE) == 0) {
            return;
        }
        enforceCallingOrSelfCarrierPrivilege(context, subId, message);
    }

    public static boolean checkLastKnownCellIdAccessPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_LAST_KNOWN_CELL_ID") == 0;
    }

    public static void enforceCallingOrSelfReadPhoneStatePermissionOrCarrierPrivilege(Context context, int subId, String message) {
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_PHONE_STATE) == 0) {
            return;
        }
        enforceCallingOrSelfCarrierPrivilege(context, subId, message);
    }

    /* renamed from: enforceCallingOrSelfReadPrivilegedPhoneStatePermissionOrCarrierPrivilege */
    public static void m20x50818a28(Context context, int subId, String message) {
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE) == 0) {
            return;
        }
        enforceCallingOrSelfCarrierPrivilege(context, subId, message);
    }

    /* renamed from: enforceCallingOrSelfReadPrecisePhoneStatePermissionOrCarrierPrivilege */
    public static void m21xbd698382(Context context, int subId, String message) {
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE) == 0 || context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_PRECISE_PHONE_STATE) == 0) {
            return;
        }
        enforceCallingOrSelfCarrierPrivilege(context, subId, message);
    }

    public static void enforceCallingOrSelfCarrierPrivilege(Context context, int subId, String message) {
        enforceCarrierPrivilege(context, subId, Binder.getCallingUid(), message);
    }

    private static void enforceCarrierPrivilege(Context context, int subId, int uid, String message) {
        if (getCarrierPrivilegeStatus(context, subId, uid) != 1) {
            throw new SecurityException(message);
        }
    }

    private static boolean checkCarrierPrivilegeForAnySubId(Context context, int uid) {
        SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        long identity = Binder.clearCallingIdentity();
        try {
            int[] activeSubIds = sm.getCompleteActiveSubscriptionIdList();
            Binder.restoreCallingIdentity(identity);
            for (int activeSubId : activeSubIds) {
                if (getCarrierPrivilegeStatus(context, activeSubId, uid) == 1) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    private static int getCarrierPrivilegeStatus(Context context, int subId, int uid) {
        if (uid == 1000 || uid == 1001) {
            return 1;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            return telephonyManager.createForSubscriptionId(subId).getCarrierPrivilegeStatus(uid);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public static void enforceAnyPermissionGranted(Context context, int uid, String message, String... permissions) {
        if (permissions.length == 0) {
            return;
        }
        boolean isGranted = false;
        int length = permissions.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String perm = permissions[i];
            if (context.checkCallingOrSelfPermission(perm) != 0) {
                i++;
            } else {
                isGranted = true;
                break;
            }
        }
        if (isGranted) {
            return;
        }
        StringBuilder b = new StringBuilder(message);
        b.append(": Neither user ");
        b.append(uid);
        b.append(" nor current process has ");
        b.append(permissions[0]);
        for (int i2 = 1; i2 < permissions.length; i2++) {
            b.append(" or ");
            b.append(permissions[i2]);
        }
        throw new SecurityException(b.toString());
    }

    public static void enforceAnyPermissionGrantedOrCarrierPrivileges(Context context, int subId, int uid, String message, String... permissions) {
        enforceAnyPermissionGrantedOrCarrierPrivileges(context, subId, uid, false, message, permissions);
    }

    public static void enforceAnyPermissionGrantedOrCarrierPrivileges(Context context, int subId, int uid, boolean allowCarrierPrivilegeOnAnySub, String message, String... permissions) {
        if (permissions.length == 0) {
            return;
        }
        boolean isGranted = false;
        int length = permissions.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String perm = permissions[i];
            if (context.checkCallingOrSelfPermission(perm) != 0) {
                i++;
            } else {
                isGranted = true;
                break;
            }
        }
        if (isGranted) {
            return;
        }
        if (allowCarrierPrivilegeOnAnySub) {
            if (checkCarrierPrivilegeForAnySubId(context, Binder.getCallingUid())) {
                return;
            }
        } else if (checkCarrierPrivilegeForSubId(context, subId)) {
            return;
        }
        StringBuilder b = new StringBuilder(message);
        b.append(": Neither user ");
        b.append(uid);
        b.append(" nor current process has ");
        b.append(permissions[0]);
        for (int i2 = 1; i2 < permissions.length; i2++) {
            b.append(" or ");
            b.append(permissions[i2]);
        }
        b.append(" or carrier privileges. subId=" + subId + ", allowCarrierPrivilegeOnAnySub=" + allowCarrierPrivilegeOnAnySub);
        throw new SecurityException(b.toString());
    }

    public static void enforceShellOnly(int callingUid, String message) {
        if (callingUid == 2000 || callingUid == 0) {
            return;
        }
        throw new SecurityException(message + ": Only shell user can call it");
    }

    public static int getTargetSdk(Context c, String packageName) {
        try {
            ApplicationInfo ai = c.getPackageManager().getApplicationInfoAsUser(packageName, 0, UserHandle.getUserHandleForUid(Binder.getCallingUid()));
            if (ai != null) {
                return ai.targetSdkVersion;
            }
            return Integer.MAX_VALUE;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(LOG_TAG, "Failed to get package info for pkg=" + packageName + ", uid=" + Binder.getCallingUid());
            return Integer.MAX_VALUE;
        }
    }

    public static boolean checkSubscriptionAssociatedWithUser(Context context, int subId, UserHandle callerUserHandle) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
            long token = Binder.clearCallingIdentity();
            if (subManager != null) {
                try {
                    if (!subManager.isSubscriptionAssociatedWithUser(subId, callerUserHandle)) {
                        Log.m110e(LOG_TAG, "User[User ID:" + callerUserHandle.getIdentifier() + "] is not associated with Subscription ID:" + subId);
                        Binder.restoreCallingIdentity(token);
                        return false;
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
            return true;
        }
        return true;
    }

    /* renamed from: checkCallingOrSelfReadPrivilegedPhoneStatePermissionOrReadPhoneNumber */
    public static boolean m22xdd54da98(Context context, int subId, String callingPackage, String callingFeatureId, String message) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            return context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE) == 0 || checkCallingOrSelfReadPhoneNumber(context, subId, callingPackage, callingFeatureId, message);
        }
        return false;
    }
}

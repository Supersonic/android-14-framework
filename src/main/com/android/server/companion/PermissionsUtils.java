package com.android.server.companion;

import android.companion.AssociationInfo;
import android.companion.AssociationRequest;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.android.internal.app.IAppOpsService;
import java.util.Collections;
import java.util.Map;
/* loaded from: classes.dex */
public final class PermissionsUtils {
    public static final Map<String, String> DEVICE_PROFILE_TO_PERMISSION;
    public static IAppOpsService sAppOpsService;

    static {
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put("android.app.role.COMPANION_DEVICE_WATCH", "android.permission.REQUEST_COMPANION_PROFILE_WATCH");
        arrayMap.put("android.app.role.COMPANION_DEVICE_APP_STREAMING", "android.permission.REQUEST_COMPANION_PROFILE_APP_STREAMING");
        arrayMap.put("android.app.role.SYSTEM_AUTOMOTIVE_PROJECTION", "android.permission.REQUEST_COMPANION_PROFILE_AUTOMOTIVE_PROJECTION");
        arrayMap.put("android.app.role.COMPANION_DEVICE_COMPUTER", "android.permission.REQUEST_COMPANION_PROFILE_COMPUTER");
        arrayMap.put("android.app.role.COMPANION_DEVICE_GLASSES", "android.permission.REQUEST_COMPANION_PROFILE_GLASSES");
        arrayMap.put("android.app.role.COMPANION_DEVICE_NEARBY_DEVICE_STREAMING", "android.permission.REQUEST_COMPANION_PROFILE_NEARBY_DEVICE_STREAMING");
        DEVICE_PROFILE_TO_PERMISSION = Collections.unmodifiableMap(arrayMap);
        sAppOpsService = null;
    }

    public static void enforcePermissionsForAssociation(Context context, AssociationRequest associationRequest, int i) {
        enforceRequestDeviceProfilePermissions(context, associationRequest.getDeviceProfile(), i);
        if (associationRequest.isSelfManaged()) {
            enforceRequestSelfManagedPermission(context, i);
        }
    }

    public static void enforceRequestDeviceProfilePermissions(Context context, String str, int i) {
        if (str == null) {
            return;
        }
        Map<String, String> map = DEVICE_PROFILE_TO_PERMISSION;
        if (!map.containsKey(str)) {
            throw new IllegalArgumentException("Unsupported device profile: " + str);
        }
        String str2 = map.get(str);
        if (context.checkPermission(str2, Binder.getCallingPid(), i) == 0) {
            return;
        }
        throw new SecurityException("Application must hold " + str2 + " to associate with a device with " + str + " profile.");
    }

    public static void enforceRequestSelfManagedPermission(Context context, int i) {
        if (context.checkPermission("android.permission.REQUEST_COMPANION_SELF_MANAGED", Binder.getCallingPid(), i) != 0) {
            throw new SecurityException("Application does not hold android.permission.REQUEST_COMPANION_SELF_MANAGED");
        }
    }

    public static boolean checkCallerCanInteractWithUserId(Context context, int i) {
        return UserHandle.getCallingUserId() == i || context.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") == 0;
    }

    public static void enforceCallerCanInteractWithUserId(Context context, int i) {
        if (UserHandle.getCallingUserId() == i) {
            return;
        }
        context.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS", null);
    }

    public static void enforceCallerIsSystemOrCanInteractWithUserId(Context context, int i) {
        if (Binder.getCallingUid() == 1000) {
            return;
        }
        enforceCallerCanInteractWithUserId(context, i);
    }

    public static boolean checkCallerIsSystemOr(int i, String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000) {
            return true;
        }
        return UserHandle.getCallingUserId() == i && checkPackage(callingUid, str);
    }

    public static void enforceCallerIsSystemOr(int i, String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000) {
            return;
        }
        int callingUserId = UserHandle.getCallingUserId();
        if (UserHandle.getCallingUserId() != i) {
            throw new SecurityException("Calling UserId (" + callingUserId + ") does not match the expected UserId (" + i + ")");
        } else if (checkPackage(callingUid, str)) {
        } else {
            throw new SecurityException(str + " doesn't belong to calling uid (" + callingUid + ")");
        }
    }

    public static boolean checkCallerCanManageCompanionDevice(Context context) {
        return Binder.getCallingUid() == 1000 || context.checkCallingPermission("android.permission.MANAGE_COMPANION_DEVICES") == 0;
    }

    public static void enforceCallerCanManageCompanionDevice(Context context, String str) {
        if (Binder.getCallingUid() == 1000) {
            return;
        }
        context.enforceCallingPermission("android.permission.MANAGE_COMPANION_DEVICES", str);
    }

    public static void enforceCallerCanManageAssociationsForPackage(Context context, int i, String str, String str2) {
        if (checkCallerCanManageAssociationsForPackage(context, i, str)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Caller (uid=");
        sb.append(Binder.getCallingUid());
        sb.append(") does not have permissions to ");
        if (str2 == null) {
            str2 = "manage associations";
        }
        sb.append(str2);
        sb.append(" for u");
        sb.append(i);
        sb.append("/");
        sb.append(str);
        throw new SecurityException(sb.toString());
    }

    public static boolean checkCallerCanManageAssociationsForPackage(Context context, int i, String str) {
        if (checkCallerIsSystemOr(i, str)) {
            return true;
        }
        if (checkCallerCanInteractWithUserId(context, i)) {
            return checkCallerCanManageCompanionDevice(context);
        }
        return false;
    }

    public static AssociationInfo sanitizeWithCallerChecks(Context context, AssociationInfo associationInfo) {
        if (associationInfo != null && checkCallerCanManageAssociationsForPackage(context, associationInfo.getUserId(), associationInfo.getPackageName())) {
            return associationInfo;
        }
        return null;
    }

    public static boolean checkPackage(int i, String str) {
        try {
            return getAppOpsService().checkPackage(i, str) == 0;
        } catch (RemoteException unused) {
            return true;
        }
    }

    public static IAppOpsService getAppOpsService() {
        if (sAppOpsService == null) {
            synchronized (PermissionsUtils.class) {
                if (sAppOpsService == null) {
                    sAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
                }
            }
        }
        return sAppOpsService;
    }
}

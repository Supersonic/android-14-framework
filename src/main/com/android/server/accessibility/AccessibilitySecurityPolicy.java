package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManagerInternal;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Slog;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.util.ArrayUtils;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class AccessibilitySecurityPolicy {
    public static final int OWN_PROCESS_ID = Process.myPid();
    public final AccessibilityUserManager mAccessibilityUserManager;
    public AccessibilityWindowManager mAccessibilityWindowManager;
    public final AppOpsManager mAppOpsManager;
    public AppWidgetManagerInternal mAppWidgetService;
    public final Context mContext;
    public final PackageManager mPackageManager;
    public final PolicyWarningUIController mPolicyWarningUIController;
    public final UserManager mUserManager;
    public final ArraySet<ComponentName> mNonA11yCategoryServices = new ArraySet<>();
    public int mCurrentUserId = -10000;
    public boolean mSendNonA11yToolNotificationEnabled = false;

    /* loaded from: classes.dex */
    public interface AccessibilityUserManager {
        int getCurrentUserIdLocked();
    }

    public AccessibilitySecurityPolicy(PolicyWarningUIController policyWarningUIController, Context context, AccessibilityUserManager accessibilityUserManager) {
        this.mContext = context;
        this.mAccessibilityUserManager = accessibilityUserManager;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPolicyWarningUIController = policyWarningUIController;
    }

    public void setSendingNonA11yToolNotificationLocked(boolean z) {
        if (z == this.mSendNonA11yToolNotificationEnabled) {
            return;
        }
        this.mSendNonA11yToolNotificationEnabled = z;
        this.mPolicyWarningUIController.enableSendingNonA11yToolNotification(z);
        if (z) {
            for (int i = 0; i < this.mNonA11yCategoryServices.size(); i++) {
                this.mPolicyWarningUIController.onNonA11yCategoryServiceBound(this.mCurrentUserId, this.mNonA11yCategoryServices.valueAt(i));
            }
        }
    }

    public void setAccessibilityWindowManager(AccessibilityWindowManager accessibilityWindowManager) {
        this.mAccessibilityWindowManager = accessibilityWindowManager;
    }

    public void setAppWidgetManager(AppWidgetManagerInternal appWidgetManagerInternal) {
        this.mAppWidgetService = appWidgetManagerInternal;
    }

    public boolean canDispatchAccessibilityEventLocked(int i, AccessibilityEvent accessibilityEvent) {
        switch (accessibilityEvent.getEventType()) {
            case 32:
            case 64:
            case 128:
            case 256:
            case 512:
            case 1024:
            case 16384:
            case 262144:
            case 524288:
            case 1048576:
            case 2097152:
            case 4194304:
            case 16777216:
                return true;
            default:
                return isRetrievalAllowingWindowLocked(i, accessibilityEvent.getWindowId());
        }
    }

    public String resolveValidReportedPackageLocked(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence == null) {
            return null;
        }
        if (i == 1000) {
            return charSequence.toString();
        }
        String charSequence2 = charSequence.toString();
        int uid = UserHandle.getUid(i2, i);
        if (isValidPackageForUid(charSequence2, uid)) {
            return charSequence.toString();
        }
        AppWidgetManagerInternal appWidgetManagerInternal = this.mAppWidgetService;
        if (appWidgetManagerInternal != null && ArrayUtils.contains(appWidgetManagerInternal.getHostedWidgetPackages(uid), charSequence2)) {
            return charSequence.toString();
        }
        if (this.mContext.checkPermission("android.permission.ACT_AS_PACKAGE_FOR_ACCESSIBILITY", i3, uid) == 0) {
            return charSequence.toString();
        }
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(uid);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return null;
        }
        return packagesForUid[0];
    }

    public String[] computeValidReportedPackages(String str, int i) {
        ArraySet hostedWidgetPackages;
        if (UserHandle.getAppId(i) == 1000) {
            return EmptyArray.STRING;
        }
        String[] strArr = {str};
        AppWidgetManagerInternal appWidgetManagerInternal = this.mAppWidgetService;
        if (appWidgetManagerInternal == null || (hostedWidgetPackages = appWidgetManagerInternal.getHostedWidgetPackages(i)) == null || hostedWidgetPackages.isEmpty()) {
            return strArr;
        }
        String[] strArr2 = new String[hostedWidgetPackages.size() + 1];
        int i2 = 0;
        System.arraycopy(strArr, 0, strArr2, 0, 1);
        int size = hostedWidgetPackages.size();
        while (i2 < size) {
            int i3 = 1 + i2;
            strArr2[i3] = (String) hostedWidgetPackages.valueAt(i2);
            i2 = i3;
        }
        return strArr2;
    }

    public void updateEventSourceLocked(AccessibilityEvent accessibilityEvent) {
        if ((accessibilityEvent.getEventType() & 71547327) == 0) {
            accessibilityEvent.setSource(null);
        }
    }

    public boolean canGetAccessibilityNodeInfoLocked(int i, AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection, int i2) {
        return canRetrieveWindowContentLocked(abstractAccessibilityServiceConnection) && isRetrievalAllowingWindowLocked(i, i2);
    }

    public boolean canRetrieveWindowsLocked(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        return canRetrieveWindowContentLocked(abstractAccessibilityServiceConnection) && abstractAccessibilityServiceConnection.mRetrieveInteractiveWindows;
    }

    public boolean canRetrieveWindowContentLocked(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        return (abstractAccessibilityServiceConnection.getCapabilities() & 1) != 0;
    }

    public boolean canControlMagnification(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        return (abstractAccessibilityServiceConnection.getCapabilities() & 16) != 0;
    }

    public boolean canPerformGestures(AccessibilityServiceConnection accessibilityServiceConnection) {
        return (accessibilityServiceConnection.getCapabilities() & 32) != 0;
    }

    public boolean canCaptureFingerprintGestures(AccessibilityServiceConnection accessibilityServiceConnection) {
        return (accessibilityServiceConnection.getCapabilities() & 64) != 0;
    }

    public boolean canTakeScreenshotLocked(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        return (abstractAccessibilityServiceConnection.getCapabilities() & 128) != 0;
    }

    public int canEnableDisableInputMethod(String str, AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) throws SecurityException {
        InputMethodInfo inputMethodInfo;
        String packageName = abstractAccessibilityServiceConnection.getComponentName().getPackageName();
        int callingUserId = UserHandle.getCallingUserId();
        List<InputMethodInfo> inputMethodListAsUser = InputMethodManagerInternal.get().getInputMethodListAsUser(callingUserId);
        if (inputMethodListAsUser != null) {
            Iterator<InputMethodInfo> it = inputMethodListAsUser.iterator();
            while (it.hasNext()) {
                inputMethodInfo = it.next();
                if (inputMethodInfo.getId().equals(str)) {
                    break;
                }
            }
        }
        inputMethodInfo = null;
        if (inputMethodInfo == null || !inputMethodInfo.getPackageName().equals(packageName)) {
            throw new SecurityException("The input method is in a different package with the accessibility service");
        }
        return checkIfInputMethodDisallowed(this.mContext, inputMethodInfo.getPackageName(), callingUserId) != null ? 1 : 0;
    }

    public static UserHandle getUserHandleOf(int i) {
        if (i == -10000) {
            return null;
        }
        return UserHandle.of(i);
    }

    public static int getManagedProfileId(Context context, int i) {
        for (UserInfo userInfo : ((UserManager) context.getSystemService(UserManager.class)).getProfiles(i)) {
            if (userInfo.id != i && userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfInputMethodDisallowed(Context context, String str, int i) {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        if (devicePolicyManager == null) {
            return null;
        }
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        boolean z = true;
        boolean isInputMethodPermittedByAdmin = profileOrDeviceOwner != null ? devicePolicyManager.isInputMethodPermittedByAdmin(profileOrDeviceOwner.component, str, i) : true;
        int managedProfileId = getManagedProfileId(context, i);
        if (managedProfileId != -10000) {
            enforcedAdmin = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(managedProfileId));
            if (enforcedAdmin != null && devicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile()) {
                z = devicePolicyManager.getParentProfileInstance(UserManager.get(context).getUserInfo(managedProfileId)).isInputMethodPermittedByAdmin(enforcedAdmin.component, str, managedProfileId);
            }
        } else {
            enforcedAdmin = null;
        }
        if (isInputMethodPermittedByAdmin || z) {
            if (isInputMethodPermittedByAdmin) {
                if (z) {
                    return null;
                }
                return enforcedAdmin;
            }
            return profileOrDeviceOwner;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    public int resolveProfileParentLocked(int i) {
        if (i != this.mAccessibilityUserManager.getCurrentUserIdLocked()) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                UserInfo profileParent = this.mUserManager.getProfileParent(i);
                if (profileParent != null) {
                    return profileParent.getUserHandle().getIdentifier();
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return i;
    }

    public int resolveCallingUserIdEnforcingPermissionsLocked(int i) {
        int callingUid = Binder.getCallingUid();
        int currentUserIdLocked = this.mAccessibilityUserManager.getCurrentUserIdLocked();
        if (callingUid == 0 || callingUid == 1000 || callingUid == 2000) {
            return (i == -2 || i == -3) ? currentUserIdLocked : resolveProfileParentLocked(i);
        }
        int userId = UserHandle.getUserId(callingUid);
        if (userId == i) {
            return resolveProfileParentLocked(i);
        }
        if (resolveProfileParentLocked(userId) == currentUserIdLocked && (i == -2 || i == -3)) {
            return currentUserIdLocked;
        }
        if (hasPermission("android.permission.INTERACT_ACROSS_USERS") || hasPermission("android.permission.INTERACT_ACROSS_USERS_FULL")) {
            return (i == -2 || i == -3) ? currentUserIdLocked : resolveProfileParentLocked(i);
        }
        throw new SecurityException("Call from user " + userId + " as user " + i + " without permission INTERACT_ACROSS_USERS or INTERACT_ACROSS_USERS_FULL not allowed.");
    }

    public boolean isCallerInteractingAcrossUsers(int i) {
        return Binder.getCallingPid() == Process.myPid() || Binder.getCallingUid() == 2000 || i == -2 || i == -3;
    }

    public final boolean isValidPackageForUid(String str, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return i == this.mPackageManager.getPackageUidAsUser(str, 4194304, UserHandle.getUserId(i));
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean isRetrievalAllowingWindowLocked(int i, int i2) {
        if (Binder.getCallingUid() == 1000) {
            return true;
        }
        if (Binder.getCallingUid() != 2000 || isShellAllowedToRetrieveWindowLocked(i, i2)) {
            return this.mAccessibilityWindowManager.resolveParentWindowIdLocked(i2) == this.mAccessibilityWindowManager.getActiveWindowId(i) || this.mAccessibilityWindowManager.findA11yWindowInfoByIdLocked(i2) != null;
        }
        return false;
    }

    public final boolean isShellAllowedToRetrieveWindowLocked(int i, int i2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IBinder windowTokenForUserAndWindowIdLocked = this.mAccessibilityWindowManager.getWindowTokenForUserAndWindowIdLocked(i, i2);
            if (windowTokenForUserAndWindowIdLocked == null) {
                return false;
            }
            int windowOwnerUserId = this.mAccessibilityWindowManager.getWindowOwnerUserId(windowTokenForUserAndWindowIdLocked);
            if (windowOwnerUserId == -10000) {
                return false;
            }
            return !this.mUserManager.hasUserRestriction("no_debugging_features", UserHandle.of(windowOwnerUserId));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void enforceCallingPermission(String str, String str2) {
        if (OWN_PROCESS_ID == Binder.getCallingPid() || hasPermission(str)) {
            return;
        }
        throw new SecurityException("You do not have " + str + " required to call " + str2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    }

    public boolean hasPermission(String str) {
        return this.mContext.checkCallingPermission(str) == 0;
    }

    public boolean canRegisterService(ServiceInfo serviceInfo) {
        if (!"android.permission.BIND_ACCESSIBILITY_SERVICE".equals(serviceInfo.permission)) {
            Slog.w("AccessibilitySecurityPolicy", "Skipping accessibility service " + new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString() + ": it does not require the permission android.permission.BIND_ACCESSIBILITY_SERVICE");
            return false;
        } else if ((serviceInfo.flags & 4) != 0) {
            Slog.w("AccessibilitySecurityPolicy", "Skipping accessibility service " + new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString() + ": the service is the external one and doesn't allow to register as an accessibility service ");
            return false;
        } else {
            if (this.mAppOpsManager.noteOpNoThrow("android:bind_accessibility_service", serviceInfo.applicationInfo.uid, serviceInfo.packageName, null, null) != 0) {
                Slog.w("AccessibilitySecurityPolicy", "Skipping accessibility service " + new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString() + ": disallowed by AppOps");
                return false;
            }
            return true;
        }
    }

    public boolean checkAccessibilityAccess(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        String packageName = abstractAccessibilityServiceConnection.getComponentName().getPackageName();
        ResolveInfo resolveInfo = abstractAccessibilityServiceConnection.getServiceInfo().getResolveInfo();
        if (resolveInfo == null) {
            return true;
        }
        int i = resolveInfo.serviceInfo.applicationInfo.uid;
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        String attributionTag = abstractAccessibilityServiceConnection.getAttributionTag();
        try {
            if (OWN_PROCESS_ID == callingPid) {
                return this.mAppOpsManager.noteOpNoThrow("android:access_accessibility", i, packageName, attributionTag, null) == 0;
            }
            return this.mAppOpsManager.noteOp("android:access_accessibility", i, packageName, attributionTag, null) == 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void enforceCallingOrSelfPermission(String str) {
        if (this.mContext.checkCallingOrSelfPermission(str) == 0) {
            return;
        }
        throw new SecurityException("Caller does not hold permission " + str);
    }

    public void onBoundServicesChangedLocked(int i, ArrayList<AccessibilityServiceConnection> arrayList) {
        if (this.mAccessibilityUserManager.getCurrentUserIdLocked() != i) {
            return;
        }
        ArraySet<? extends ComponentName> arraySet = new ArraySet<>();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            AccessibilityServiceInfo serviceInfo = arrayList.get(i2).getServiceInfo();
            ComponentName clone = serviceInfo.getComponentName().clone();
            if (!serviceInfo.isAccessibilityTool()) {
                arraySet.add(clone);
                if (this.mNonA11yCategoryServices.contains(clone)) {
                    this.mNonA11yCategoryServices.remove(clone);
                } else if (this.mSendNonA11yToolNotificationEnabled) {
                    this.mPolicyWarningUIController.onNonA11yCategoryServiceBound(i, clone);
                }
            }
        }
        for (int i3 = 0; i3 < this.mNonA11yCategoryServices.size(); i3++) {
            this.mPolicyWarningUIController.onNonA11yCategoryServiceUnbound(i, this.mNonA11yCategoryServices.valueAt(i3));
        }
        this.mNonA11yCategoryServices.clear();
        this.mNonA11yCategoryServices.addAll(arraySet);
    }

    public void onSwitchUserLocked(int i, Set<ComponentName> set) {
        if (this.mCurrentUserId == i) {
            return;
        }
        this.mPolicyWarningUIController.onSwitchUser(i, new ArraySet(set));
        for (int i2 = 0; i2 < this.mNonA11yCategoryServices.size(); i2++) {
            this.mPolicyWarningUIController.onNonA11yCategoryServiceUnbound(this.mCurrentUserId, this.mNonA11yCategoryServices.valueAt(i2));
        }
        this.mNonA11yCategoryServices.clear();
        this.mCurrentUserId = i;
    }

    public void onEnabledServicesChangedLocked(int i, Set<ComponentName> set) {
        if (this.mAccessibilityUserManager.getCurrentUserIdLocked() != i) {
            return;
        }
        this.mPolicyWarningUIController.onEnabledServicesChanged(i, new ArraySet(set));
    }
}

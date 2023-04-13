package com.android.server.restrictions;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IRestrictionsManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IUserManager;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import com.android.internal.util.ArrayUtils;
import com.android.server.SystemService;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class RestrictionsManagerService extends SystemService {
    public final RestrictionsManagerImpl mRestrictionsManagerImpl;

    public RestrictionsManagerService(Context context) {
        super(context);
        this.mRestrictionsManagerImpl = new RestrictionsManagerImpl(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("restrictions", this.mRestrictionsManagerImpl);
    }

    /* loaded from: classes2.dex */
    public class RestrictionsManagerImpl extends IRestrictionsManager.Stub {
        public final Context mContext;
        public final IDevicePolicyManager mDpm;
        public final DevicePolicyManagerInternal mDpmInternal;
        public final IUserManager mUm;

        public RestrictionsManagerImpl(Context context) {
            this.mContext = context;
            this.mUm = RestrictionsManagerService.this.getBinderService("user");
            this.mDpm = RestrictionsManagerService.this.getBinderService("device_policy");
            this.mDpmInternal = (DevicePolicyManagerInternal) RestrictionsManagerService.this.getLocalService(DevicePolicyManagerInternal.class);
        }

        @Deprecated
        public Bundle getApplicationRestrictions(String str) throws RemoteException {
            return this.mUm.getApplicationRestrictions(str);
        }

        public List<Bundle> getApplicationRestrictionsPerAdminForUser(int i, String str) throws RemoteException {
            DevicePolicyManagerInternal devicePolicyManagerInternal = this.mDpmInternal;
            if (devicePolicyManagerInternal != null) {
                return devicePolicyManagerInternal.getApplicationRestrictionsPerAdminForUser(str, i);
            }
            return new ArrayList();
        }

        public boolean hasRestrictionsProvider() throws RemoteException {
            int callingUserId = UserHandle.getCallingUserId();
            if (this.mDpm != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return this.mDpm.getRestrictionsProvider(callingUserId) != null;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return false;
        }

        public void requestPermission(String str, String str2, String str3, PersistableBundle persistableBundle) throws RemoteException {
            int callingUid = Binder.getCallingUid();
            int userId = UserHandle.getUserId(callingUid);
            if (this.mDpm != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    ComponentName restrictionsProvider = this.mDpm.getRestrictionsProvider(userId);
                    if (restrictionsProvider == null) {
                        throw new IllegalStateException("Cannot request permission without a restrictions provider registered");
                    }
                    enforceCallerMatchesPackage(callingUid, str, "Package name does not match caller ");
                    Intent intent = new Intent("android.content.action.REQUEST_PERMISSION");
                    intent.setComponent(restrictionsProvider);
                    intent.putExtra("android.content.extra.PACKAGE_NAME", str);
                    intent.putExtra("android.content.extra.REQUEST_TYPE", str2);
                    intent.putExtra("android.content.extra.REQUEST_ID", str3);
                    intent.putExtra("android.content.extra.REQUEST_BUNDLE", persistableBundle);
                    this.mContext.sendBroadcastAsUser(intent, new UserHandle(userId));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public Intent createLocalApprovalIntent() throws RemoteException {
            ActivityInfo activityInfo;
            int callingUserId = UserHandle.getCallingUserId();
            if (this.mDpm != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    ComponentName restrictionsProvider = this.mDpm.getRestrictionsProvider(callingUserId);
                    if (restrictionsProvider == null) {
                        throw new IllegalStateException("Cannot request permission without a restrictions provider registered");
                    }
                    String packageName = restrictionsProvider.getPackageName();
                    Intent intent = new Intent("android.content.action.REQUEST_LOCAL_APPROVAL");
                    intent.setPackage(packageName);
                    ResolveInfo resolveIntent = AppGlobals.getPackageManager().resolveIntent(intent, (String) null, 0L, callingUserId);
                    if (resolveIntent == null || (activityInfo = resolveIntent.activityInfo) == null || !activityInfo.exported) {
                        return null;
                    }
                    ActivityInfo activityInfo2 = resolveIntent.activityInfo;
                    intent.setComponent(new ComponentName(activityInfo2.packageName, activityInfo2.name));
                    return intent;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        public void notifyPermissionResponse(String str, PersistableBundle persistableBundle) throws RemoteException {
            int callingUid = Binder.getCallingUid();
            int userId = UserHandle.getUserId(callingUid);
            if (this.mDpm != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    ComponentName restrictionsProvider = this.mDpm.getRestrictionsProvider(userId);
                    if (restrictionsProvider == null) {
                        throw new SecurityException("No restrictions provider registered for user");
                    }
                    enforceCallerMatchesPackage(callingUid, restrictionsProvider.getPackageName(), "Restrictions provider does not match caller ");
                    Intent intent = new Intent("android.content.action.PERMISSION_RESPONSE_RECEIVED");
                    intent.setPackage(str);
                    intent.putExtra("android.content.extra.RESPONSE_BUNDLE", persistableBundle);
                    this.mContext.sendBroadcastAsUser(intent, new UserHandle(userId));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public final void enforceCallerMatchesPackage(int i, String str, String str2) {
            try {
                String[] packagesForUid = AppGlobals.getPackageManager().getPackagesForUid(i);
                if (packagesForUid != null && !ArrayUtils.contains(packagesForUid, str)) {
                    throw new SecurityException(str2 + i);
                }
            } catch (RemoteException unused) {
            }
        }
    }
}

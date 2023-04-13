package com.android.server.oemlock;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.oemlock.IOemLockService;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.PersistentDataBlockManagerInternal;
import com.android.server.SystemService;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserRestrictionsUtils;
/* loaded from: classes2.dex */
public class OemLockService extends SystemService {
    public Context mContext;
    public OemLock mOemLock;
    public final IBinder mService;
    public final UserManagerInternal.UserRestrictionsListener mUserRestrictionsListener;

    public static boolean isHalPresent() {
        return (VendorLockHidl.getOemLockHalService() == null && VendorLockAidl.getOemLockHalService() == null) ? false : true;
    }

    public static OemLock getOemLock(Context context) {
        if (VendorLockAidl.getOemLockHalService() != null) {
            Slog.i("OemLock", "Using vendor lock via the HAL(aidl)");
            return new VendorLockAidl(context);
        } else if (VendorLockHidl.getOemLockHalService() != null) {
            Slog.i("OemLock", "Using vendor lock via the HAL(hidl)");
            return new VendorLockHidl(context);
        } else {
            Slog.i("OemLock", "Using persistent data block based lock");
            return new PersistentDataBlockLock(context);
        }
    }

    public OemLockService(Context context) {
        this(context, getOemLock(context));
    }

    public OemLockService(Context context, OemLock oemLock) {
        super(context);
        UserManagerInternal.UserRestrictionsListener userRestrictionsListener = new UserManagerInternal.UserRestrictionsListener() { // from class: com.android.server.oemlock.OemLockService.1
            @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
            public void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2) {
                if (!UserRestrictionsUtils.restrictionsChanged(bundle2, bundle, "no_factory_reset") || (!bundle.getBoolean("no_factory_reset"))) {
                    return;
                }
                OemLockService.this.mOemLock.setOemUnlockAllowedByDevice(false);
                OemLockService.this.setPersistentDataBlockOemUnlockAllowedBit(false);
            }
        };
        this.mUserRestrictionsListener = userRestrictionsListener;
        this.mService = new IOemLockService.Stub() { // from class: com.android.server.oemlock.OemLockService.2
            @EnforcePermission("android.permission.MANAGE_CARRIER_OEM_UNLOCK_STATE")
            public String getLockName() {
                super.getLockName_enforcePermission();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return OemLockService.this.mOemLock.getLockName();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission("android.permission.MANAGE_CARRIER_OEM_UNLOCK_STATE")
            public void setOemUnlockAllowedByCarrier(boolean z, byte[] bArr) {
                super.setOemUnlockAllowedByCarrier_enforcePermission();
                OemLockService.this.enforceUserIsAdmin();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OemLockService.this.mOemLock.setOemUnlockAllowedByCarrier(z, bArr);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission("android.permission.MANAGE_CARRIER_OEM_UNLOCK_STATE")
            public boolean isOemUnlockAllowedByCarrier() {
                super.isOemUnlockAllowedByCarrier_enforcePermission();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return OemLockService.this.mOemLock.isOemUnlockAllowedByCarrier();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission("android.permission.MANAGE_USER_OEM_UNLOCK_STATE")
            public void setOemUnlockAllowedByUser(boolean z) {
                super.setOemUnlockAllowedByUser_enforcePermission();
                if (ActivityManager.isUserAMonkey()) {
                    return;
                }
                OemLockService.this.enforceUserIsAdmin();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (!OemLockService.this.isOemUnlockAllowedByAdmin()) {
                        throw new SecurityException("Admin does not allow OEM unlock");
                    }
                    if (!OemLockService.this.mOemLock.isOemUnlockAllowedByCarrier()) {
                        throw new SecurityException("Carrier does not allow OEM unlock");
                    }
                    OemLockService.this.mOemLock.setOemUnlockAllowedByDevice(z);
                    OemLockService.this.setPersistentDataBlockOemUnlockAllowedBit(z);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission("android.permission.MANAGE_USER_OEM_UNLOCK_STATE")
            public boolean isOemUnlockAllowedByUser() {
                super.isOemUnlockAllowedByUser_enforcePermission();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return OemLockService.this.mOemLock.isOemUnlockAllowedByDevice();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission(anyOf = {"android.permission.READ_OEM_UNLOCK_STATE", "android.permission.OEM_UNLOCK_STATE"})
            public boolean isOemUnlockAllowed() {
                super.isOemUnlockAllowed_enforcePermission();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    boolean z = OemLockService.this.mOemLock.isOemUnlockAllowedByCarrier() && OemLockService.this.mOemLock.isOemUnlockAllowedByDevice();
                    OemLockService.this.setPersistentDataBlockOemUnlockAllowedBit(z);
                    return z;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            @EnforcePermission(anyOf = {"android.permission.READ_OEM_UNLOCK_STATE", "android.permission.OEM_UNLOCK_STATE"})
            public boolean isDeviceOemUnlocked() {
                super.isDeviceOemUnlocked_enforcePermission();
                String str = SystemProperties.get("ro.boot.flash.locked");
                str.hashCode();
                return str.equals("0");
            }
        };
        this.mContext = context;
        this.mOemLock = oemLock;
        ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).addUserRestrictionsListener(userRestrictionsListener);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("oem_lock", this.mService);
    }

    public final void setPersistentDataBlockOemUnlockAllowedBit(boolean z) {
        PersistentDataBlockManagerInternal persistentDataBlockManagerInternal = (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        if (persistentDataBlockManagerInternal == null || (this.mOemLock instanceof PersistentDataBlockLock)) {
            return;
        }
        Slog.i("OemLock", "Update OEM Unlock bit in pst partition to " + z);
        persistentDataBlockManagerInternal.forceOemUnlockEnabled(z);
    }

    public final boolean isOemUnlockAllowedByAdmin() {
        return !UserManager.get(this.mContext).hasUserRestriction("no_factory_reset", UserHandle.SYSTEM);
    }

    public final void enforceUserIsAdmin() {
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (UserManager.get(this.mContext).isUserAdmin(callingUserId)) {
                return;
            }
            throw new SecurityException("Must be an admin user");
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}

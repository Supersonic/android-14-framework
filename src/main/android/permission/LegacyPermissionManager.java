package android.permission;

import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.permission.ILegacyPermissionManager;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public final class LegacyPermissionManager {
    private final ILegacyPermissionManager mLegacyPermissionManager;

    public LegacyPermissionManager() throws ServiceManager.ServiceNotFoundException {
        this(ILegacyPermissionManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.LEGACY_PERMISSION_SERVICE)));
    }

    public LegacyPermissionManager(ILegacyPermissionManager legacyPermissionManager) {
        this.mLegacyPermissionManager = legacyPermissionManager;
    }

    public int checkDeviceIdentifierAccess(String packageName, String message, String callingFeatureId, int pid, int uid) {
        try {
            return this.mLegacyPermissionManager.checkDeviceIdentifierAccess(packageName, message, callingFeatureId, pid, uid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int checkPhoneNumberAccess(String packageName, String message, String callingFeatureId, int pid, int uid) {
        try {
            return this.mLegacyPermissionManager.checkPhoneNumberAccess(packageName, message, callingFeatureId, pid, uid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void grantDefaultPermissionsToLuiApp(String packageName, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.grantDefaultPermissionsToActiveLuiApp(packageName, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void revokeDefaultPermissionsFromLuiApps(String[] packageNames, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.revokeDefaultPermissionsFromLuiApps(packageNames, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void grantDefaultPermissionsToEnabledImsServices(String[] packageNames, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.grantDefaultPermissionsToEnabledImsServices(packageNames, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void grantDefaultPermissionsToEnabledTelephonyDataServices(String[] packageNames, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.grantDefaultPermissionsToEnabledTelephonyDataServices(packageNames, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void revokeDefaultPermissionsFromDisabledTelephonyDataServices(String[] packageNames, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.revokeDefaultPermissionsFromDisabledTelephonyDataServices(packageNames, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void grantDefaultPermissionsToEnabledCarrierApps(String[] packageNames, UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        try {
            this.mLegacyPermissionManager.grantDefaultPermissionsToEnabledCarrierApps(packageNames, user.getIdentifier());
            executor.execute(new Runnable() { // from class: android.permission.LegacyPermissionManager$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void grantDefaultPermissionsToCarrierServiceApp(String packageName, int userId) {
        try {
            this.mLegacyPermissionManager.grantDefaultPermissionsToCarrierServiceApp(packageName, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}

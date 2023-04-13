package android.permission;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.UserHandle;
import android.permission.IPermissionController;
import android.permission.PermissionControllerService;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class PermissionControllerService extends Service {
    private static final long CAMERA_MIC_INDICATORS_NOT_PRESENT = 162547999;
    private static final String LOG_TAG = PermissionControllerService.class.getSimpleName();
    public static final String SERVICE_INTERFACE = "android.permission.PermissionControllerService";

    public abstract void onCountPermissionApps(List<String> list, int i, IntConsumer intConsumer);

    public abstract void onGetAppPermissions(String str, Consumer<List<RuntimePermissionPresentationInfo>> consumer);

    public abstract void onGetPermissionUsages(boolean z, long j, Consumer<List<RuntimePermissionUsageInfo>> consumer);

    public abstract void onGetRuntimePermissionsBackup(UserHandle userHandle, OutputStream outputStream, Runnable runnable);

    public abstract void onGrantOrUpgradeDefaultRuntimePermissions(Runnable runnable);

    public abstract void onRevokeRuntimePermission(String str, String str2, Runnable runnable);

    public abstract void onRevokeRuntimePermissions(Map<String, List<String>> map, boolean z, int i, String str, Consumer<Map<String, List<String>>> consumer);

    @Deprecated
    public abstract void onSetRuntimePermissionGrantStateByDeviceAdmin(String str, String str2, String str3, int i, Consumer<Boolean> consumer);

    @Deprecated
    public void onRestoreRuntimePermissionsBackup(UserHandle user, InputStream backup, Runnable callback) {
    }

    public void onStageAndApplyRuntimePermissionsBackup(UserHandle user, InputStream backup, Runnable callback) {
        onRestoreRuntimePermissionsBackup(user, backup, callback);
    }

    @Deprecated
    public void onRestoreDelayedRuntimePermissionsBackup(String packageName, UserHandle user, Consumer<Boolean> callback) {
    }

    public void onApplyStagedRuntimePermissionBackup(String packageName, UserHandle user, Consumer<Boolean> callback) {
        onRestoreDelayedRuntimePermissionsBackup(packageName, user, callback);
    }

    public void onUpdateUserSensitivePermissionFlags(int uid, Executor executor, Runnable callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    public void onUpdateUserSensitivePermissionFlags(int uid, Runnable callback) {
        onUpdateUserSensitivePermissionFlags(uid, getMainExecutor(), callback);
    }

    public void onSetRuntimePermissionGrantStateByDeviceAdmin(String callerPackageName, AdminPermissionControlParams params, Consumer<Boolean> callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    public void onOneTimePermissionSessionTimeout(String packageName) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    public void onGetPlatformPermissionsForGroup(String permissionGroupName, Consumer<List<String>> callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    public void onGetGroupOfPlatformPermission(String permissionName, Consumer<String> callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    public void onRevokeSelfPermissionsOnKill(String packageName, List<String> permissions, Runnable callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    @SystemApi
    public String getPrivilegesDescriptionStringForProfile(String deviceProfileName) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    @SystemApi
    public void onGetUnusedAppCount(IntConsumer callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    @SystemApi
    public void onGetHibernationEligibility(String packageName, IntConsumer callback) {
        throw new AbstractMethodError("Must be overridden in implementing class");
    }

    /* renamed from: android.permission.PermissionControllerService$1 */
    /* loaded from: classes3.dex */
    class BinderC23061 extends IPermissionController.Stub {
        BinderC23061() {
        }

        @Override // android.permission.IPermissionController
        public void revokeRuntimePermissions(Bundle bundleizedRequest, boolean doDryRun, int reason, String callerPackageName, final AndroidFuture callback) {
            Preconditions.checkNotNull(bundleizedRequest, "bundleizedRequest");
            Preconditions.checkNotNull(callerPackageName);
            Preconditions.checkNotNull(callback);
            Map<String, List<String>> request = new ArrayMap<>();
            for (String packageName : bundleizedRequest.keySet()) {
                Preconditions.checkNotNull(packageName);
                ArrayList<String> permissions = bundleizedRequest.getStringArrayList(packageName);
                Preconditions.checkCollectionElementsNotNull(permissions, "permissions");
                request.put(packageName, permissions);
            }
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
            try {
                PackageInfo pkgInfo = PermissionControllerService.this.getPackageManager().getPackageInfo(callerPackageName, 0);
                Preconditions.checkArgument(getCallingUid() == pkgInfo.applicationInfo.uid);
                PermissionControllerService.this.onRevokeRuntimePermissions(request, doDryRun, reason, callerPackageName, new Consumer() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda9
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        PermissionControllerService.BinderC23061.lambda$revokeRuntimePermissions$1(AndroidFuture.this, (Map) obj);
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$revokeRuntimePermissions$1(AndroidFuture callback, Map revoked) {
            CollectionUtils.forEach(revoked, new BiConsumer() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda3
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    PermissionControllerService.BinderC23061.lambda$revokeRuntimePermissions$0((String) obj, (List) obj2);
                }
            });
            callback.complete(revoked);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$revokeRuntimePermissions$0(String pkg, List perms) {
            Preconditions.checkNotNull(pkg);
            Preconditions.checkCollectionElementsNotNull(perms, "permissions");
        }

        private void enforceSomePermissionsGrantedToCaller(String... requiredPermissions) {
            for (String requiredPermission : requiredPermissions) {
                if (PermissionControllerService.this.checkCallingPermission(requiredPermission) == 0) {
                    return;
                }
            }
            throw new SecurityException("At lest one of the following permissions is required: " + Arrays.toString(requiredPermissions));
        }

        @Override // android.permission.IPermissionController
        public void getRuntimePermissionBackup(UserHandle user, ParcelFileDescriptor pipe) {
            Preconditions.checkNotNull(user);
            Preconditions.checkNotNull(pipe);
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
            try {
                OutputStream backup = new ParcelFileDescriptor.AutoCloseOutputStream(pipe);
                try {
                    CountDownLatch latch = new CountDownLatch(1);
                    PermissionControllerService permissionControllerService = PermissionControllerService.this;
                    Objects.requireNonNull(latch);
                    permissionControllerService.onGetRuntimePermissionsBackup(user, backup, new PermissionControllerService$1$$ExternalSyntheticLambda4(latch));
                    latch.await();
                    backup.close();
                } catch (Throwable th) {
                    try {
                        backup.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException e) {
                Log.m109e(PermissionControllerService.LOG_TAG, "Could not open pipe to write backup to", e);
            } catch (InterruptedException e2) {
                Log.m109e(PermissionControllerService.LOG_TAG, "getRuntimePermissionBackup timed out", e2);
            }
        }

        @Override // android.permission.IPermissionController
        public void stageAndApplyRuntimePermissionsBackup(UserHandle user, ParcelFileDescriptor pipe) {
            Preconditions.checkNotNull(user);
            Preconditions.checkNotNull(pipe);
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GRANT_RUNTIME_PERMISSIONS, Manifest.C0000permission.RESTORE_RUNTIME_PERMISSIONS);
            try {
                InputStream backup = new ParcelFileDescriptor.AutoCloseInputStream(pipe);
                try {
                    CountDownLatch latch = new CountDownLatch(1);
                    PermissionControllerService permissionControllerService = PermissionControllerService.this;
                    Objects.requireNonNull(latch);
                    permissionControllerService.onStageAndApplyRuntimePermissionsBackup(user, backup, new PermissionControllerService$1$$ExternalSyntheticLambda4(latch));
                    latch.await();
                    backup.close();
                } catch (Throwable th) {
                    try {
                        backup.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException e) {
                Log.m109e(PermissionControllerService.LOG_TAG, "Could not open pipe to read backup from", e);
            } catch (InterruptedException e2) {
                Log.m109e(PermissionControllerService.LOG_TAG, "restoreRuntimePermissionBackup timed out", e2);
            }
        }

        @Override // android.permission.IPermissionController
        public void applyStagedRuntimePermissionBackup(String packageName, UserHandle user, AndroidFuture callback) {
            Preconditions.checkNotNull(packageName);
            Preconditions.checkNotNull(user);
            Preconditions.checkNotNull(callback);
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GRANT_RUNTIME_PERMISSIONS, Manifest.C0000permission.RESTORE_RUNTIME_PERMISSIONS);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(callback);
            permissionControllerService.onApplyStagedRuntimePermissionBackup(packageName, user, new PermissionControllerService$1$$ExternalSyntheticLambda6(callback));
        }

        @Override // android.permission.IPermissionController
        public void getAppPermissions(String packageName, AndroidFuture callback) {
            Preconditions.checkNotNull(packageName, "packageName");
            Preconditions.checkNotNull(callback, "callback");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(callback);
            permissionControllerService.onGetAppPermissions(packageName, new PermissionControllerService$1$$ExternalSyntheticLambda5(callback));
        }

        @Override // android.permission.IPermissionController
        public void revokeRuntimePermission(String packageName, String permissionName) {
            Preconditions.checkNotNull(packageName, "packageName");
            Preconditions.checkNotNull(permissionName, "permissionName");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
            CountDownLatch latch = new CountDownLatch(1);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(latch);
            permissionControllerService.onRevokeRuntimePermission(packageName, permissionName, new PermissionControllerService$1$$ExternalSyntheticLambda4(latch));
            try {
                latch.await();
            } catch (InterruptedException e) {
                Log.m109e(PermissionControllerService.LOG_TAG, "revokeRuntimePermission timed out", e);
            }
        }

        @Override // android.permission.IPermissionController
        public void countPermissionApps(List<String> permissionNames, int flags, AndroidFuture callback) {
            Preconditions.checkCollectionElementsNotNull(permissionNames, "permissionNames");
            Preconditions.checkFlagsArgument(flags, 3);
            Preconditions.checkNotNull(callback, "callback");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(callback);
            permissionControllerService.onCountPermissionApps(permissionNames, flags, new PermissionControllerService$1$$ExternalSyntheticLambda2(callback));
        }

        @Override // android.permission.IPermissionController
        public void getPermissionUsages(boolean countSystem, long numMillis, AndroidFuture callback) {
            Preconditions.checkArgumentNonnegative(numMillis);
            Preconditions.checkNotNull(callback, "callback");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(callback);
            permissionControllerService.onGetPermissionUsages(countSystem, numMillis, new PermissionControllerService$1$$ExternalSyntheticLambda5(callback));
        }

        @Override // android.permission.IPermissionController
        public void setRuntimePermissionGrantStateByDeviceAdminFromParams(String callerPackageName, AdminPermissionControlParams params, AndroidFuture callback) {
            Preconditions.checkStringNotEmpty(callerPackageName);
            if (params.getGrantState() == 1) {
                enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GRANT_RUNTIME_PERMISSIONS);
            }
            if (params.getGrantState() == 2) {
                enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
            }
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.ADJUST_RUNTIME_PERMISSIONS_POLICY);
            Preconditions.checkNotNull(callback);
            PermissionControllerService permissionControllerService = PermissionControllerService.this;
            Objects.requireNonNull(callback);
            permissionControllerService.onSetRuntimePermissionGrantStateByDeviceAdmin(callerPackageName, params, new PermissionControllerService$1$$ExternalSyntheticLambda6(callback));
        }

        @Override // android.permission.IPermissionController
        public void grantOrUpgradeDefaultRuntimePermissions(final AndroidFuture callback) {
            Preconditions.checkNotNull(callback, "callback");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.ADJUST_RUNTIME_PERMISSIONS_POLICY);
            PermissionControllerService.this.onGrantOrUpgradeDefaultRuntimePermissions(new Runnable() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidFuture.this.complete(true);
                }
            });
        }

        @Override // android.permission.IPermissionController
        public void updateUserSensitiveForApp(int uid, final AndroidFuture callback) {
            Preconditions.checkNotNull(callback, "callback cannot be null");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.ADJUST_RUNTIME_PERMISSIONS_POLICY);
            try {
                PermissionControllerService.this.onUpdateUserSensitivePermissionFlags(uid, new Runnable() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidFuture.this.complete(null);
                    }
                });
            } catch (Exception e) {
                callback.completeExceptionally(e);
            }
        }

        @Override // android.permission.IPermissionController
        public void notifyOneTimePermissionSessionTimeout(String packageName) {
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
            PermissionControllerService.this.onOneTimePermissionSessionTimeout((String) Preconditions.checkNotNull(packageName, "packageName cannot be null"));
        }

        @Override // android.p008os.Binder
        protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
            Preconditions.checkNotNull(fd, "fd");
            Preconditions.checkNotNull(writer, "writer");
            enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
            PermissionControllerService.this.dump(fd, writer, args);
        }

        @Override // android.permission.IPermissionController
        public void getPrivilegesDescriptionStringForProfile(String deviceProfileName, AndroidFuture<String> callback) {
            try {
                Preconditions.checkStringNotEmpty(deviceProfileName);
                Objects.requireNonNull(callback);
                enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.MANAGE_COMPANION_DEVICES);
                callback.complete(PermissionControllerService.this.getPrivilegesDescriptionStringForProfile(deviceProfileName));
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }

        @Override // android.permission.IPermissionController
        public void getPlatformPermissionsForGroup(String permissionName, AndroidFuture<List<String>> callback) {
            try {
                Objects.requireNonNull(permissionName);
                Objects.requireNonNull(callback);
                PermissionControllerService permissionControllerService = PermissionControllerService.this;
                Objects.requireNonNull(callback);
                permissionControllerService.onGetPlatformPermissionsForGroup(permissionName, new PermissionControllerService$1$$ExternalSyntheticLambda5(callback));
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }

        @Override // android.permission.IPermissionController
        public void getGroupOfPlatformPermission(String permissionGroupName, final AndroidFuture<String> callback) {
            try {
                Objects.requireNonNull(permissionGroupName);
                Objects.requireNonNull(callback);
                PermissionControllerService permissionControllerService = PermissionControllerService.this;
                Objects.requireNonNull(callback);
                permissionControllerService.onGetGroupOfPlatformPermission(permissionGroupName, new Consumer() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda8
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AndroidFuture.this.complete((String) obj);
                    }
                });
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }

        @Override // android.permission.IPermissionController
        public void getUnusedAppCount(AndroidFuture callback) {
            try {
                Objects.requireNonNull(callback);
                enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.MANAGE_APP_HIBERNATION);
                PermissionControllerService permissionControllerService = PermissionControllerService.this;
                Objects.requireNonNull(callback);
                permissionControllerService.onGetUnusedAppCount(new PermissionControllerService$1$$ExternalSyntheticLambda2(callback));
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }

        @Override // android.permission.IPermissionController
        public void getHibernationEligibility(String packageName, AndroidFuture callback) {
            try {
                Objects.requireNonNull(callback);
                enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.MANAGE_APP_HIBERNATION);
                PermissionControllerService permissionControllerService = PermissionControllerService.this;
                Objects.requireNonNull(callback);
                permissionControllerService.onGetHibernationEligibility(packageName, new PermissionControllerService$1$$ExternalSyntheticLambda2(callback));
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }

        @Override // android.permission.IPermissionController
        public void revokeSelfPermissionsOnKill(String packageName, List<String> permissions, final AndroidFuture callback) {
            try {
                Objects.requireNonNull(callback);
                int callingUid = Binder.getCallingUid();
                int targetPackageUid = PermissionControllerService.this.getPackageManager().getPackageUid(packageName, PackageManager.PackageInfoFlags.m189of(0L));
                if (targetPackageUid != callingUid) {
                    enforceSomePermissionsGrantedToCaller(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
                }
                PermissionControllerService.this.onRevokeSelfPermissionsOnKill(packageName, permissions, new Runnable() { // from class: android.permission.PermissionControllerService$1$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidFuture.this.complete(null);
                    }
                });
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new BinderC23061();
    }
}
